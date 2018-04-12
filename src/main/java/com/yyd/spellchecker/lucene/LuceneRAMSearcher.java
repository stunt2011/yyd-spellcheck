package com.yyd.spellchecker.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.yyd.spellchecker.correct.PinyinTerm;
import com.yyd.spellchecker.util.PinyinUtil;

public class LuceneRAMSearcher implements LuceneSearcher{
	private Directory directory = new RAMDirectory();  	
	//private Object synchronized_r = new Object();
	private IndexReader indexReader = null;
	private IndexSearcher indexSearcher = null;
	private IndexWriter indexWriter;
	
	public boolean indexDictionary(Map<String,Map<String,List<String>>> entityMap) {
		if(entityMap.isEmpty()) {
			return false;
		}
		
		Map<String,List<PinyinTerm>> pyMap = new HashMap<String,List<PinyinTerm>>();
		for(Map.Entry<String, Map<String,List<String>>> entry:entityMap.entrySet()) {
			String scene = entry.getKey();
			for(Map.Entry<String, List<String>> nerEntry:entry.getValue().entrySet()) {
				String entity = nerEntry.getKey();
				List<String> wordList = nerEntry.getValue();
				// 使用场景+实体 的组作为索引，应该可以减少索引时间
				List<PinyinTerm> pinyinList = covertToPinyin(wordList);
				pyMap.put(scene+entity, pinyinList);
			}
		}	
		
		try {
			writeIndex(pyMap);
			//写完后立即打开读索引
			getIndexSearcher();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private void writeIndex(Map<String,List<PinyinTerm>> pyMap)throws Exception{
		Directory dir= directory; 		
	    if(null == indexWriter){
	    	Analyzer analyzer = new StandardAnalyzer();
		    IndexWriterConfig iwc=new IndexWriterConfig(analyzer);  
	    	indexWriter = new IndexWriter(dir, iwc);  
	    }	    
	    
	    for(Map.Entry<String, List<PinyinTerm>> entry:pyMap.entrySet()) {
	    	String scene = entry.getKey();
	    	List<PinyinTerm> termList = entry.getValue();
	    	for(int i=0; i < termList.size(); i++) {   
		    	PinyinTerm term = termList.get(i);
		    	String text = term.getText();
		    	List<String> pinyinList = term.getPinyin();
		    	
		    	StringBuilder build = new StringBuilder();
		    	for(int j =0; j < pinyinList.size();j++){
		    		build.append(pinyinList.get(j));
		    		if(j != pinyinList.size() -1){
		    			build.append(" ");
		    		}
		    	}
		    	String pinyin = build.toString();
		    	
	            Document doc=new Document();  
	            Integer id = i + 1;
	            //居然没有加入实体类型
	            doc.add(new StringField("id", id.toString(), Store.YES)); 
	            doc.add(new StringField("content", text, Store.YES)); 
	            doc.add(new StringField("scene", scene, Store.YES)); 
	            doc.add(new TextField("pinyinContent", pinyin, Store.YES));    
	            indexWriter.addDocument(doc);                
	        }  
	    }
	     
        
	    indexWriter.close(); 	    
	}
	
	
	/**
	 * 将汉字转化为拼音
	 * @param dict
	 * @return
	 */
	private List<PinyinTerm> covertToPinyin(List<String> dict){
		List<PinyinTerm> termList = new ArrayList<PinyinTerm>();
		for(int i=0; i < dict.size();i++){
			String text = dict.get(i).trim();
			List<String> pinyin = PinyinUtil.getWrodsPinYin(text);
			if(null == pinyin || pinyin.isEmpty()){
				continue;
			}
			
			PinyinTerm term = new PinyinTerm(text,pinyin);
			termList.add(term);
		}
		
		return termList;
	}
	
	private IndexSearcher getIndexSearcher() throws IOException {	
		//TODO 暂时不使用锁
		//synchronized (synchronized_r) {
			if(this.indexReader==null) {  
				Directory dir=directory;  
				indexReader = DirectoryReader.open(dir);  
	        }
		   
		    if(this.indexSearcher == null){
			   this.indexSearcher = new IndexSearcher(indexReader);
		    }
		//}

	   return this.indexSearcher;	      	      
	}
	
	public Map<String,String>  search(Query query, int n)throws Exception{
		IndexSearcher searcher=getIndexSearcher(); 	
		if(null == searcher){
			return null;
		}
		
		TopDocs topdocs=searcher.search(query, n);  		
		
        ScoreDoc[] scoreDocs = topdocs.scoreDocs;         
       	Map<String,String> searchResult = new HashMap<String,String>();	       	
        for(int i=0; i < scoreDocs.length; i++) {  
            int doc = scoreDocs[i].doc;  
            Document document = searcher.doc(doc); 
            String content = document.get("content");
            String pinyinContent = document.get("pinyinContent");
            searchResult.put(content, pinyinContent);
         }
		return searchResult;
	}
	
	
	@Override
	public void close() {
		try {
			indexReader.close();
			indexReader = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
