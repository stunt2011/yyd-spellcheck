package com.yyd.spellchecker.correct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yyd.spellchecker.detect.SpellError;
import com.yyd.spellchecker.lucene.LuceneSearcher;
import com.yyd.spellchecker.segment.WordTerm;

public class PinyinSpellErrorCorrector implements SpellErrorCorrector{
	/**
	 * 词语相似度匹配
	 */
	private StringSuggestion suggestion;
		
	public PinyinSpellErrorCorrector(LuceneSearcher searcher){
		this.suggestion = new CnPinyinSuggestion(searcher);
	}
	
	/**
	 * 
	 * @param errorList:拼写错误
	 * @param sentence ：原始句子(分词后结果)
	 * @param scene    :场景（或待纠正的词典库）
	 * @return
	 */
	@Override
	public List<WordTerm> correct(List<SpellError> errorList,List<WordTerm> sentence,String scene){
		int lastEnd = -1;
		
		List<MatchTerm> matchList = new  ArrayList<MatchTerm>();
		for(int i=0 ; i < errorList.size();i++){
			String word = errorList.get(i).build();
			int length = word.length();
			float accuracy = 5.0f;//未来对不同长度的字，再从字形上进行约束
			 //还需要对不同长度的字符串调整阈值。
			 if(length == 2){
				 accuracy = 0.9f;
			 }
			 else if(length == 3){
				 accuracy = 0.65f;
			 }
			 else if(length ==4 || length ==6){
				 accuracy = 0.73f;
			 }
			 else
			 {
				 accuracy = 0.6f;
			 }
			 
			 try {
				 String nature = errorList.get(i).getNature();
				 String totalScene = (nature!=null)?(scene+nature):scene;
				
				List<MatchTerm> correct = suggestion.suggestSimilar(word, 1, accuracy,totalScene);
				if(null != correct && correct.size() > 0){
					//TODO:
					if(errorList.get(i).getStart()-1 > lastEnd){
						MatchTerm term = correct.get(0);
						term.setStart(errorList.get(i).getStart());
						term.setEnd(errorList.get(i).getEnd());
						matchList.add(term);	
						lastEnd = errorList.get(i).getEnd();
					}
								
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			 
			 
		}
		
		
		List<WordTerm> correctSentence = new ArrayList<WordTerm>();
		if(matchList.isEmpty()){
			correctSentence.addAll(sentence);
		}
		else
		{
			Iterator<MatchTerm> iter = matchList.iterator();
			MatchTerm curTerm = null;
			if(iter.hasNext()){
				curTerm = iter.next();
			}
			
			int index = 0;
			while(index < sentence.size()){
				if(null !=curTerm){
					if(index >=curTerm.getStart() && index <= curTerm.getEnd()){
						correctSentence.add(new WordTerm(curTerm.getText(),"n"));
						index = curTerm.getEnd() + 1;
						if(iter.hasNext()){
							curTerm = iter.next();
						}
						else
						{
							curTerm = null;
						}
						continue;
					}
				}
				
				correctSentence.add(sentence.get(index));
				index += 1;
			}
			
		}
		
		return correctSentence;
	}
	
	@Override
	public void close() {
		
	}
}
