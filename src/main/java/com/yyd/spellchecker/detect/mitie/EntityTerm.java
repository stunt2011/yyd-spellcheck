package com.yyd.spellchecker.detect.mitie;

public class EntityTerm {
	private String word;
	private String nature;
	private Double score;
	private Integer start;
	private Integer end;
	
	public EntityTerm(String word,String nature){
		this.word = word;
		this.nature = nature;
	}

	public Double getScore(){
		return this.score;
	}
	
	public void setScore(Double score){
		this.score = score;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String toString(){
		return this.nature+"="+this.word;
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
}
