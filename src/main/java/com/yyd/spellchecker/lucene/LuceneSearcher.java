package com.yyd.spellchecker.lucene;

import java.util.Map;

import org.apache.lucene.search.Query;

public interface LuceneSearcher {
	Map<String,String>  search(Query query, int n)throws Exception;
	
	void close();
}
