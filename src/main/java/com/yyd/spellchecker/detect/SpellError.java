package com.yyd.spellchecker.detect;

import java.util.List;

/**
 * 句中的拼写检查错误
 * @author pc
 *
 */
public class SpellError {
	/**
	 * 错误类型，目前主要是依据语言模型划分的
	 * @author pc
	 *
	 */
	public enum SpellErrorType{
		//基于语言模型识别的错误
		/**
		 * 非词错误
		 */
		NO_WORD,  
		
		/**
		 * 连续单字词序列
		 */
		CONTINU_SINGLE_WORD,
		
		/**
		 * 词性模型不匹配
		 */
		NGRAM_NO_NATURE,
		
		/**
		 * 词模型不匹配
		 */
		NGRAM_NO_WORD
	}
	
	/**
	 * 错字的在句子中的开始位置(通常以词为单位)
	 */
	private Integer start;
	/**
	 * 错字的在句子中的结束位置(通常以词为单位)
	 */
	private Integer end;
	/**
	 * 错误得分，分数越高错误可能性越大。(暂未使用)
	 */
	private Integer score; 
	/**
	 * 错误类型
	 */
	private SpellErrorType type;
	/**
	 * 可能的的拼写错误的字符串,即连续相连的一段词语
	 */
	private List<String> character;
	
	/**
	 * 词性，只有基于实体抽取的检测才有此数据
	 */
	private String nature;	
	
	public SpellError(SpellErrorType type,Integer start,Integer end,List<String> character){
		this.start = start;
		this.end = end;
		this.character = character;
		this.type = type;
		
		setScore(character.size());
	}
	
	public String getNature() {
		return this.nature;
	}
	
	public void setNature(String nature) {
		this.nature = nature;
	}
	
	public SpellErrorType getType() {
		return type;
	}

	public void setType(SpellErrorType type) {
		this.type = type;
	}
	
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}
	public List<String> getCharacter() {
		return character;
	}
	public void setCharacter(List<String> character) {
		this.character = character;
	}
	
	/**
	 * 将连续出错的词语组成一个字符串
	 * @return
	 */
	public String build(){
		StringBuilder build = new StringBuilder();
		for(int i=0; i < character.size();i++){
			build.append(character.get(i));
		}
		
		return build.toString();
	}
	
	@Override
	public String toString(){
		return "word="+build()+" type="+type.ordinal();//"start="+start+" end="+end+
	}
		
}
