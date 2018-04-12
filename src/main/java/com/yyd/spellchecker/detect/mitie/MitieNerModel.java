package com.yyd.spellchecker.detect.mitie;

import java.util.ArrayList;
import java.util.List;

import edu.mit.ll.mitie.EntityMention;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.NamedEntityExtractor;
import edu.mit.ll.mitie.StringVector;
import edu.mit.ll.mitie.TotalWordFeatureExtractor;

public class MitieNerModel {
	  private NamedEntityExtractor ner;
	  private TotalWordFeatureExtractor totalWordFeatureExtractor;

	  public MitieNerModel(String modelPath,TotalWordFeatureExtractor totalWordFeatureExtractor){
		  ner = new NamedEntityExtractor(modelPath);
		  this.totalWordFeatureExtractor = totalWordFeatureExtractor;
	  }
	  
	  /**
	   * 中文识别
	   * @param text:句子，未分词的
	   * @return
	   */
	  public List<EntityTerm> extractEntities(String text){
		  StringVector textStringVector = new StringVector();
		  char[] seg = text.toCharArray();
		  for(int i=0; i < seg.length;i++){
			  textStringVector.add(String.valueOf(seg[i]));
		  }
		  
		 return extract(textStringVector);  
	 }
	  
	  /**
	   * 英文实体识别
	   * @param text
	   * @return
	   */
	 public List<EntityTerm> extractENEntities(String text){
		 StringVector textStringVector = new StringVector();
		  String[] seg = text.split(" ");
		  for(int i=0; i < seg.length;i++){
			  textStringVector.add(seg[i]);
		  }
		  
		 return extract(textStringVector);  
	 }
	  
	  public List<EntityTerm> extract(StringVector textStringVector){
		  List<EntityTerm> entityList = new ArrayList<EntityTerm>();
		  EntityMentionVector entities = ner.extractEntities(textStringVector,totalWordFeatureExtractor);
		  StringVector possibleTags = ner.getPossibleNerTags();
		  for (int i = 0; i < entities.size(); ++i)
	      {	
			  	EntityMention entity = entities.get(i);
			  	double score = entity.getScore();	           
	            String tag = possibleTags.get(entity.getTag());  
	            
	            StringBuilder build = new StringBuilder();
	            for (int j = entity.getStart(); j < entity.getEnd(); ++j)
	            {
	            	build.append(textStringVector.get(j));
	            }	            
	            String word = build.toString();
	            
	            EntityTerm term = new EntityTerm(word,tag);
	            term.setScore(score);
	            term.setStart(entity.getStart());
	            term.setEnd(entity.getEnd()-1);
	            entityList.add(term);
	       }
		  
		  return entityList;	
	  }
}
