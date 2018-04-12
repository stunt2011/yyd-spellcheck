package com.yyd.spellchecker.correct;

import java.util.List;

/**
 * 中文文本及拼音
 * @author pc
 *
 */
public class PinyinTerm {
	/**
	 * 中文文本
	 */
	private String text; 
	/**
	 * 中文文本对应的字拼音,每个字一个拼音
	 */
	private List<String> pinyin;
	
	public PinyinTerm(String text,List<String> pinyin){
		this.text = text;
		this.pinyin = pinyin;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getPinyin() {
		return pinyin;
	}
	public void setPinyin(List<String> pinyin) {
		this.pinyin = pinyin;
	}
}
