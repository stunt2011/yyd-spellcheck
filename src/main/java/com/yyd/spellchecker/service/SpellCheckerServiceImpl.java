package com.yyd.spellchecker.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yyd.spellchecker.correct.PinyinSpellErrorCorrector;
import com.yyd.spellchecker.correct.SpellErrorCorrector;
import com.yyd.spellchecker.detect.SpellError;
import com.yyd.spellchecker.detect.SpellErrorDetector;
import com.yyd.spellchecker.detect.mitie.MitieSpellErrorDetector;
import com.yyd.spellchecker.lucene.LuceneRAMSearcher;
import com.yyd.spellchecker.lucene.LuceneSearcher;
import com.yyd.spellchecker.segment.WordTerm;
import com.yyd.spellchecker.util.PinyinUtil;

public class SpellCheckerServiceImpl implements SpellCheckerService{
	/**
	 * 用于查找词库
	 */
	private LuceneSearcher luceneSearcher;
	
	/**
	 * 拼写检查查错模块
	 */
	private SpellErrorDetector detector;
	
	/**
	 * 拼写检查纠错模块
	 */
	private SpellErrorCorrector corrector;
	

	private boolean init =false;
	
	/**
	 * 拼官检查查错方法
	 * @author pc
	 *
	 */
	public enum DetectorType{
		/**
		 * 基于字N元模型
		 */
		NGRAM,
		
		/**
		 * 基于字拼音的N元模型
		 */
		NGRAM_PY,
		
		/**
		 * 基于实体抽取
		 */
		MITIE		
	}
	
	/**
	 * 检验文件路径
	 * @param path
	 * @return
	 */
	private boolean verifyFilePath(String path) {
		if(null == path || path.isEmpty()) {
			return false;
		}
		File file = new File(path);
		if(!file.exists()) {
			return false;
		}
		
		if(!file.isDirectory()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean init(SpellCheckerParam param)throws Exception{	
		if(init) {
			return false;
		}
		
		String configPath = param.getConfigPath();		
		if(!verifyFilePath(configPath)){
			return false;
		}
		if(!configPath.endsWith("/")) {
			configPath +="/";
		}
		
		if(param.getTotalWordFeatureExtractor() == null) {
			return false;
		}
		if(null == param.getEntitys()) {
			return false;
		}
		
		//1.初始化拼音库
		String pinyinDictPath = configPath+"pinyinDict.txt";
		PinyinUtil.init(pinyinDictPath);
		 
		//2.加载正确的词库		
		LuceneRAMSearcher searcher = new LuceneRAMSearcher();
		if(!searcher.indexDictionary(param.getEntitys())) {
			return false;
		}
		luceneSearcher = searcher;		
		 
		//3.加载纠错模块
		corrector = new PinyinSpellErrorCorrector(luceneSearcher);
		
		//4.加载查错模块		
		String modelPath =  configPath+"model/";				
		MitieSpellErrorDetector mitieDetector = new MitieSpellErrorDetector();	
		if(!mitieDetector.init(modelPath, param.getTotalWordFeatureExtractor())) {
			return false;
		}
		detector = mitieDetector;
		
		
		init = true;	
		//5.提前执行一条语句,因为第一次很行时会很慢
		String text ="唱首任贤奇的歌";
		correct(text, "music");
		
		return true;
	}
	
	@Override
	public void close()throws Exception{
		//luceneSearcher.close();
	}
	
	/**
	 * 纠错，句子级别纠错
	 * @param text
	 * @param scene 场景 
	 * @return
	 */
	@Override
	public String correct(String text,String scene)throws Exception{
		if(!init) {
			return text;
		}
		
		if(null == text || text.isEmpty()){
			return text;
		}
		
		if(text.length() < 2){
			return text;
		}
		
		// 1.使用MITIE查错，不用分词
		List<WordTerm> wordList = new ArrayList<WordTerm>();
		char[] array = text.toCharArray();
		for(int i=0;i < array.length;i++) {
			// 词性随便填
			WordTerm term = new WordTerm(String.valueOf(array[i]),"n");
			wordList.add(term);
		}		
		
		// 2.查错
		List<SpellError> errorList = detector.detect(wordList,scene);
		if(errorList ==null || errorList.isEmpty()){
			return text;
		}
				
		// 3.纠错
		List<WordTerm> correctTerm = corrector.correct(errorList, wordList,scene);			
		
		// 4.合成句子
		StringBuilder build = new StringBuilder();
		for(int i=0; i < correctTerm.size();i++){
			build.append(correctTerm.get(i).getWord());
		}
		String suggestionText = build.toString();
		
		return suggestionText;
	}
	
}
