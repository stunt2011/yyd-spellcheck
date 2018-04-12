package com.yyd.spellchecker.detect.mitie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yyd.spellchecker.detect.mitie.EntityTerm;
import com.yyd.spellchecker.detect.mitie.MitieNerModel;
import com.yyd.spellchecker.detect.SpellErrorDetector;
import com.yyd.spellchecker.detect.SpellError;
import com.yyd.spellchecker.detect.SpellError.SpellErrorType;
import com.yyd.spellchecker.segment.WordTerm;

import edu.mit.ll.mitie.TotalWordFeatureExtractor;

public class MitieSpellErrorDetector implements SpellErrorDetector{
	/**
	 * 实体识别模型，场景或技能-->实体模型
	 */
	private Map<String,MitieNerModel> nerModelMap = new HashMap<String,MitieNerModel>();
	
	
	public boolean init(String nerModelPath,TotalWordFeatureExtractor totalWordFeatureExtractor) {
		File file = new File(nerModelPath);
		if(!file.exists()) {
			return false;
		}
		if(!file.isDirectory()) {
			return false;
		}
		
		File[] modelFiles = file.listFiles();
		for(int i=0; i < modelFiles.length;i++) {
			if(!modelFiles[i].isFile()) {
				continue;
			}
			String path = modelFiles[i].getPath();
			MitieNerModel model = new MitieNerModel(path,totalWordFeatureExtractor);
			String scene = modelFiles[i].getName();
			scene = scene.replaceAll(".dat", "");
			scene = scene.replaceAll("ner_", "");
			nerModelMap.put(scene, model);
		}
		
		return true;
	}
	
	@Override
	public List<SpellError> detect(List<WordTerm> sentence,String scene){
		if(null == sentence || sentence.isEmpty()){
			return null;
		}
		
		StringBuilder build = new StringBuilder();
		for(int i=0; i < sentence.size();i++){
			build.append(sentence.get(i).getWord());
		}		
		String text = build.toString();
		
		MitieNerModel model = nerModelMap.get(scene);
		if(null == model) {
			return null;
		}
		List<EntityTerm> termList = model.extractEntities(text);
		if(null == termList || termList.isEmpty()){
			return null;
		}
		 
		List<SpellError> candErrorList = new ArrayList<SpellError>();
		for(int i=0;i < termList.size();i++){
			List<String> list = new ArrayList<String>();
			EntityTerm entity = termList.get(i);
			list.add(entity.getWord());
			//此处错误类型暂时随便填了
			int start = -1;
			int end = -1;
			int len = -1;
			for(int j=0;j < sentence.size();j++){
				int last = len;
				len += sentence.get(j).getWord().length();
				if(start <0 && entity.getStart() > last && entity.getStart() <= len){
					start = j;
				}
				
				if(end <0 && entity.getEnd() > last && entity.getEnd() <= len){
					end = j;
				}
				
				if(start >=0 && end >=0){
					break;
				}
			}
			
			SpellError error = new SpellError(SpellErrorType.NO_WORD,start,end,list);
			error.setNature(entity.getNature());
			candErrorList.add(error);
		}
		 
		 
		return candErrorList;
	}
}
