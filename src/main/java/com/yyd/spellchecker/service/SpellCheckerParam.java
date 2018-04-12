package com.yyd.spellchecker.service;

import java.util.List;
import java.util.Map;

import edu.mit.ll.mitie.TotalWordFeatureExtractor;

public class SpellCheckerParam {
	/**
	 * 拼写检查配置文件根路径
	 */
	private String configPath;
	/**
	 * MITIE语言模型
	 */
	private TotalWordFeatureExtractor totalWordFeatureExtractor;
	/**
	 * 实体词库，(场景或技能-->实体类型)-->实体词库
	 */
	private Map<String,Map<String,List<String>>> entitys;
	
	public String getConfigPath() {
		return configPath;
	}
	
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	public TotalWordFeatureExtractor getTotalWordFeatureExtractor() {
		return totalWordFeatureExtractor;
	}
	
	public void setTotalWordFeatureExtractor(TotalWordFeatureExtractor totalWordFeatureExtractor) {
		this.totalWordFeatureExtractor = totalWordFeatureExtractor;
	}
	
	public Map<String,Map<String,List<String>>> getEntitys() {
		return entitys;
	}
	
	public void setEntitys(Map<String,Map<String,List<String>>> entitys) {
		this.entitys = entitys;
	}	
}
