package com.yyd.spellchecker.segment;

public class WordTerm {
	private String word;
	private String nature;
	
	public WordTerm(String word,String nature){
		this.word = word;
		this.nature = nature;
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
	
	@Override
	public String toString(){
		return this.word+"/"+this.nature;
	}
}
