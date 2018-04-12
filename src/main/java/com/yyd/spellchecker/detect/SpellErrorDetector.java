package com.yyd.spellchecker.detect;

import java.util.List;

import com.yyd.spellchecker.segment.WordTerm;

/**
 * 拼写检查查错接口
 * @author pc
 *
 */
public interface SpellErrorDetector {
	List<SpellError> detect(List<WordTerm> sentence,String scene);	
}
