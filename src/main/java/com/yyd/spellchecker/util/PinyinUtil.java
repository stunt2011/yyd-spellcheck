package com.yyd.spellchecker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

	/**
	 * 拼音词典，用于处理多音字等容易出错的字词的拼音
	 * 汉语词语-->拼音，每个汉字一个拼音
	 */
	private static Map<String,ArrayList<String>> pinyinDict = new HashMap<String,ArrayList<String>>();

	
	/**
	 * 加载拼音词典
	 */
	public  static void init(String filePath){
			
		if(null != filePath &&  !filePath.isEmpty()){
			List<String> pinyinList = FileUtil.read(filePath);//读hot文件
			for(String str:pinyinList){
				if(str.equals("") || str.startsWith("#") || str.startsWith("//")){
					continue;
				}				
				
				String line = str.trim();
				String[] terms = line.split("=");
				if(terms.length != 2){
					continue;
				}
				
				String word = terms[0];
				word = word.trim();
				
				String[] pinyins = terms[1].split(",");
				ArrayList<String> array = new ArrayList<String>();
				for(int i =0;i < pinyins.length;i++){
					String pinyin = pinyins[i].trim();
					array.add(pinyin);
				}
				
				pinyinDict.put(word, array);
			}
		}
		
		
	}
	
	/**
	 * 获取中文文本对应的拼音
	 * @param inputString 中文文本
	 * @param pinyins     中文文本的拼音
	 */
	public static List<String> getWrodsPinYin(String inputString){
		List<String> pinyins = new ArrayList<String>();
		ArrayList<String> py = pinyinDict.get(inputString);
		if(null != py){
			for(String str:py){
				pinyins.add(str);
			}
			
		}
		else
		{
			for(int i = 0; i < inputString.length(); i++){
				pinyins.add(getPingYin(String.valueOf(inputString.charAt(i))));    		
	    	}
		}
		
		return pinyins;
	}
	
	
    /**
     * 将字符串中的中文转化为拼音,其他字符不变(主要是处理单个字符,如果是多个汉字，则它们的的拼音是连接在一起的)
     * 
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
 
        char[] input = inputString.trim().toCharArray();
        String output = "";
 
        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    if(temp == null || temp.length <= 0){
                    	System.out.println("tmep null or lengt=0,str="+inputString);
                    }
                    else
                    {
                    	 output += temp[0];
                    }
                   
                } else
                    output += java.lang.Character.toString(input[i]);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }
    

    
    /**  
     * 获取汉字串拼音首字母，英文字符不变  
     * @param chinese 汉字串  
     * @return 汉语拼音首字母  
     */  
    public String getFirstSpell(String chinese) {   
            StringBuffer pybf = new StringBuffer();   
            char[] arr = chinese.toCharArray();   
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   
            for (int i = 0; i < arr.length; i++) {   
                    if (arr[i] > 128) {   
                            try {   
                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);   
                                    if (temp != null) {   
                                            pybf.append(temp[0].charAt(0));   
                                    }   
                            } catch (BadHanyuPinyinOutputFormatCombination e) {   
                                    e.printStackTrace();   
                            }   
                    } else {   
                            pybf.append(arr[i]);   
                    }   
            }   
            return pybf.toString().replaceAll("\\W", "").trim();   
    }   
    
    /**  
     * 获取汉字串拼音，英文字符不变  
     * @param chinese 汉字串  
     * @return 汉语拼音  
     */  
    public String getFullSpell(String chinese) {   
            StringBuffer pybf = new StringBuffer();   
            char[] arr = chinese.toCharArray();   
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();   
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);   
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);   
            for (int i = 0; i < arr.length; i++) {   
                    if (arr[i] > 128) {   
                            try {   
                                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);   
                            } catch (BadHanyuPinyinOutputFormatCombination e) {   
                                    e.printStackTrace();   
                            }   
                    } else {   
                            pybf.append(arr[i]);   
                    }   
            }   
            return pybf.toString();   
    }  
    	
	/**
	 * 将汉字转为拼音，如刘德华 --> liu de hua
	 * @param s
	 * @return
	 */
    public String getPingyinWithSpace(String s){
    	StringBuilder sb = new StringBuilder();
    	ArrayList<String> py = pinyinDict.get(s);
    	if(null == py){
    		for(int i = 0; i < s.length(); i++){
        		sb.append(getPingYin(s.charAt(i)+""));
        		sb.append(" ");
        	}
    	}
    	else
    	{
    		for(String str:py){
    			sb.append(str);
        		sb.append(" ");
    		}
    	}
    	
    	return sb.toString().trim();
    }
    
    
    
    public static void main(String[] args) {    
    	List<String> pinyins =PinyinUtil.getWrodsPinYin("红在都");
    	System.out.println(pinyins);    	
    	
//    	String s = "你好";
//    	String ss = s.substring(0, 1);
//    	System.out.println(getPingYin(ss));
	}
}
