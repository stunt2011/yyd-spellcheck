package com.yyd.spellchecker.correct;

import java.util.List;

import com.yyd.spellchecker.detect.SpellError;
import com.yyd.spellchecker.segment.WordTerm;

public interface SpellErrorCorrector {
	List<WordTerm> correct(List<SpellError> errorList,List<WordTerm> sentence,String scene);
	
	void close();
}
