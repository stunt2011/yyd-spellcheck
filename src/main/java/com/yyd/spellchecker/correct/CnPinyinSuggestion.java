package com.yyd.spellchecker.correct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;



import com.yyd.spellchecker.lucene.LuceneFileSearcher;
import com.yyd.spellchecker.lucene.LuceneSearcher;
import com.yyd.spellchecker.util.PinyinUtil;

/**
 * 基于中文词语拼音的相似度匹配
 * @author pc
 *
 */
public class CnPinyinSuggestion implements StringSuggestion{	
	/**
	 * 检索出的候选文本最大数量
	 */
	private final int MAX_SEARCH_COUNT = 10;
	private LuceneSearcher searcher;	
	
	
	public CnPinyinSuggestion(LuceneSearcher searcher){	
		this.searcher = searcher;
	}
	
	/**
	 * 纠错，即查找最相近的词
	 * @param word:输入词语
	 * @param numSug：匹配数量
	 * @param accuracy:最低相似度，0~1
	 * @param scene:场景或词库标识
	 * @return
	 */
	@Override
	public List<MatchTerm> suggestSimilar(String text, int numSug,float accuracy,String scene)throws Exception{
		if(null == text || text.isEmpty()){
			return null;
		}
		
		if(numSug <=0 || accuracy < 0){
			return null;
		}
		
		
		//1.转化为拼音
		List<String> pinyinList = PinyinUtil.getWrodsPinYin(text);
		
		//2.检索
		BooleanQuery.Builder query = new BooleanQuery.Builder();
		for(int i=0; i < pinyinList.size();i++){
			query.add(new BooleanClause(new TermQuery(new Term("pinyinContent", pinyinList.get(i))), BooleanClause.Occur.SHOULD));
		}		
		query.add(new BooleanClause(new TermQuery(new Term("scene", scene)), BooleanClause.Occur.MUST));
		
		Map<String,String> searchResult  = searcher.search(query.build(), MAX_SEARCH_COUNT);		
        if(null == searchResult || searchResult.size() <= 0){
        	return null;
        }
               
        //2.排序并筛选
        List<MatchTerm> resultList = sort(text,pinyinList,searchResult,numSug,accuracy);        
        
       	return resultList;		
	}
	
	public List<MatchTerm> sort(String targetText,List<String> targetPinyinList,Map<String,String> searchResult,int sum,float accuracy){
		List<MatchTerm> correctList = new ArrayList<MatchTerm>();
		
		//1.对拼音按编辑距离进行排序
		for(Map.Entry<String, String> entry:searchResult.entrySet()){
			String text = entry.getKey();
			String value = entry.getValue();
			String[] pinyin = value.split(" ");
			List<String> pinyinList= Arrays.asList(pinyin);
			if(null == pinyinList || pinyinList.isEmpty()){
				continue;
			}
			
			float score = getDistance(targetPinyinList,pinyinList);
			//对两字的情况作特殊优化,因为两字可能是少一个字
			if(targetPinyinList.size() == 2 && pinyinList.size() == 3){
				if(score > 0.66){					
					int count = getSameCharCount(targetText,text);
					if(count >= 2){
						score += 0.25;;
					}
				}
			}
			if(score < accuracy){
				continue;
			}
			
			MatchTerm term = new MatchTerm();
			term.setText(text);
			term.setScore(score);
			correctList.add(term);
		}
		  
		
		//TODO:对于拼音相同的还应该按照字形进行排序		
		List<MatchTerm> resultList = new ArrayList<MatchTerm>();
		int count = Math.min(correctList.size(), sum);
		//System.out.println("count size " +  correctList.size());
		for(int i=0; i < count;i++){
			MatchTerm term = correctList.get(i);
			for(int j=i+1; j < count;j++){
				MatchTerm otherTerm = correctList.get(j);
				if(otherTerm.getScore() > term.getScore()){
					String tmpText = term.getText();
					float tmpScore = term.getScore();
					
					term.setText(otherTerm.getText());
					term.setScore(otherTerm.getScore());
					otherTerm.setText(tmpText);
					otherTerm.setScore(tmpScore);
				}
			}
			
			resultList.add(term);
		}
		
		return resultList;
	}
	
	
	
	/**
	 * 获取两个字符串相同字符数量
	 * @param wordPinyin
	 * @param subsctionPinyins
	 * @return
	 */
	public int getSameCharCount(String targetText,String text){
		int count=0;
		Map<String,Integer> map1 = new HashMap<String,Integer>();
		Map<String,Integer> map2 = new HashMap<String,Integer>();
		
		char[] targetArray = targetText.toCharArray();
		for(int i =0;i < targetArray.length;i++){
			String cur = String.valueOf(targetArray[i]);
			Integer num = map1.get(cur);
			if(null == num){
				map1.put(cur, 1);
			}
			else
			{
				map1.put(cur, num+1);
			}
		}
		
		char[] textArray = text.toCharArray();
		for(int i=0;i<textArray.length;i++){
			String cur = String.valueOf(textArray[i]);
			Integer num = map2.get(cur);
			if(null == num){
				map2.put(cur, 1);
			}
			else
			{
				map2.put(cur, num+1);
			}
		}
	    
	    for(Map.Entry<String, Integer> entry:map1.entrySet()){
	    	Integer num1 = entry.getValue();
	    	Integer num2 = map2.get(entry.getKey());
	    	
	    	if(null == num1|| null == num2){
	    		continue;
	    	}
	    	
	    	if(num2 >= num1){ //TODO:这里不需要相等，因此subsctionPinyins拼音数比较多，在同音情况下，因此相同的音也可能比较多
	    		count += num1;
	    	}
	    }
	  	
		return count;
	}
	
	/**
	 * 编辑距离
	 * @param target
	 * @param other
	 * @return
	 */
	public float getDistance (List<String> target, List<String> other) {
	      String[] sa;
	      int n;
	      int p[]; //'previous' cost array, horizontally
	      int d[]; // cost array, horizontally
	      int _d[]; //placeholder to assist in swapping p and d
	      
	        sa = target.toArray(new String[target.size()]);
	        n = sa.length;
	        p = new int[n+1]; 
	        d = new int[n+1]; 
	      
	        final int m = other.size();
	        if (n == 0 || m == 0) {
	          if (n == m) {
	            return 1;
	          }
	          else {
	            return 0;
	          }
	        } 


	        // indexes into strings s and t
	        int i; // iterates through s
	        int j; // iterates through t
	        String t_j; // jth character of t
	        int cost; // cost

	        for (i = 0; i<=n; i++) {
	            p[i] = i;
	        }

	        for (j = 1; j<=m; j++) {
	            t_j = other.get(j-1);
	            d[0] = j;

	            for (i=1; i<=n; i++) {
	                cost = sa[i-1].equalsIgnoreCase(t_j) ? 0 : 1;
	                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	                d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
	            }

	            // copy current distance counts to 'previous row' distance counts
	            _d = p;
	            p = d;
	            d = _d;
	        }

	        // our last action in the above loop was to switch d and p, so p now
	        // actually has the most recent cost counts
	        return 1.0f - ((float) p[n] / Math.max(other.size(), sa.length));
	    }
	
	
	 public static void main(String[] args) throws Exception{
		 String indexPath = "F:/Data/lucene/pinyin_cn_check";
		 //String dictPath = "correctWordDict.txt";
		 //LuceneRAMSearcher searcher = new LuceneRAMSearcher();
		 //searcher.indexDictionary(dictPath, "music");
		 LuceneFileSearcher searcher = new LuceneFileSearcher(indexPath);
		 CnPinyinSuggestion checker = new CnPinyinSuggestion(searcher);
		 //checker.indexDictionary(dictPath,"music");
		 String text = "贤奇";//勇艺达,白雪公主和八个小矮人
		 float accuracy = 5.0f;//未来对不同长度的字，再从字形上进行约束
		 //还需要对不同长度的字符串调整阈值。
		 if(text.length() == 2){
			 accuracy = 0.95f;
		 }
		 else if(text.length() == 3){
			 accuracy = 0.65f;
		 }
		 else if(text.length() ==4){
			 accuracy = 0.75f;
		 }
		 else
		 {
			 accuracy = 0.6f;
		 }
		 List<MatchTerm> result = checker.suggestSimilar(text, 5, accuracy,"music");		
		 System.out.println(result);		 
	 }
	 
	 @Override
	 public void close() {
		 
	 }
}