package com.yyd.spellchecker.service;

public interface SpellCheckerService {
	/**
	 * 初始化
	 * @param SpellCheckerParam：配置参数
	 * @return
	 */
	boolean init(SpellCheckerParam param)throws Exception;
	
	/**
	 * 暂时不需要调用
	 * @throws Exception
	 */
	void close()throws Exception;
	
	/**
	 * 纠错
	 * @param text：待纠错文本
	 * @param scene：场景或技能
	 * @return
	 */
	String correct(String text,String scene)throws Exception;
}
