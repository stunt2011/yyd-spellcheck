package com.yyd.spellchecker.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneFileSearcher implements LuceneSearcher{
	private Object synchronized_r = new Object();
	private IndexReader indexReader = null;
	private IndexSearcher indexSearcher = null;
	private String indexPath;
	
	public LuceneFileSearcher(String indexPath){
		this.indexPath = indexPath;
		File file = new File(indexPath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		File[] listFiles = file.listFiles();
		if(listFiles.length > 0){
			//认为索引文件存在，因此加载索引文件
			try {
				getIndexSearcher();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.indexReader = null;
				this.indexSearcher = null;
			}
		}
		
	}
	
	private IndexSearcher getIndexSearcher() throws IOException {	
		synchronized (synchronized_r) {
			if(this.indexReader==null) {  
				Directory dir=FSDirectory.open(new File(indexPath).toPath());  
				indexReader = DirectoryReader.open(dir);  
	        }
		   
		    if(this.indexSearcher == null){
			   this.indexSearcher = new IndexSearcher(indexReader);
		    }
		}

	   return this.indexSearcher;
	      	      
	}
	public Map<String,String>  search(Query query, int n)throws Exception{
		IndexSearcher searcher=getIndexSearcher(); 	
		if(null == searcher){
			return null;
		}
		
		TopDocs topdocs=searcher.search(query, n);  
        ScoreDoc[] scoreDocs=topdocs.scoreDocs;  
        
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
		
	}
}
