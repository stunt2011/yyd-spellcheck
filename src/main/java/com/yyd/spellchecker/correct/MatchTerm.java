package com.yyd.spellchecker.correct;

/**
 * 相似度匹配结果
 * @author pc
 *
 */
public class MatchTerm {
	/**
	 * 匹配得到的词
	 */
	private String text;
	/**
	 * 相似度得分
	 */
	private float  score;
	
	
	private Integer start;
	private Integer end;
	
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	
	@Override
	public String toString(){
		return  "text = "+this.text+",score = "+this.score;
	}
}
