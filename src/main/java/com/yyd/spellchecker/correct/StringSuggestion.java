package com.yyd.spellchecker.correct;

import java.util.List;

/**
 * 字符串相似度匹配
 * @author pc
 *
 */
public interface StringSuggestion {
	List<MatchTerm> suggestSimilar(String text, int numSug,float accuracy,String scene)throws Exception;
	
	void close();
}
