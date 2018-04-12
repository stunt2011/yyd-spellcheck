package com.yyd.spellchecker.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.yyd.spellchecker.correct.PinyinTerm;
import com.yyd.spellchecker.util.FileUtil;
import com.yyd.spellchecker.util.PinyinUtil;

public class LuceneFileWriter {

	/**
	 * 为词典建立索引，纠错时会词典中的词作为参考的正确词
	 * @param fileName
	 */
	public void indexDictionary(String indexPath, String dictFilePath,String scene)throws Exception {
		//1.读取字典文件
		List<String> dict = FileUtil.read(dictFilePath);
		if(null == dict ||dict.isEmpty()){
			return;
		}
		
		//2.将中文转为拼音
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
		
		//3. 写入文件索引
		writeIndex(indexPath,termList,scene);
	     
	}
	
	private void writeIndex(String indexPath,List<PinyinTerm> termList,String scene)throws Exception{
		if(null == indexPath){
			return;
		}
		
		Directory dir= FSDirectory.open(new File(indexPath).toPath()); 		
		Analyzer analyzer = new StandardAnalyzer();
	    IndexWriterConfig iwc=new IndexWriterConfig(analyzer);    
	    IndexWriter writer = new IndexWriter(dir, iwc);  
	    
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
            doc.add(new StringField("id", id.toString(), Store.YES)); 
            doc.add(new StringField("content", text, Store.YES)); 
            doc.add(new StringField("scene", scene, Store.YES)); 
            doc.add(new TextField("pinyinContent", pinyin, Store.YES));    
            writer.addDocument(doc);                
        }   
        
       writer.close(); 
	    
	}
}
