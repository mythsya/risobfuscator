package com.agfa.he.sh.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtil {

	public static final String[] generatePinyins(String source) {
		if (source != null && !source.isEmpty()) {
			HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
			outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
			
			List<String> pinyins = new ArrayList<String>();
			int length = source.length();
			
			try {
				for(int i=0; i<length; i++) {
					String[] pys = PinyinHelper.toHanyuPinyinStringArray(source.charAt(i), outputFormat);
					if (pys != null && pys.length > 0) {
						pinyins.add(pys[0]);
					} else {
						pinyins.add(String.valueOf(source.charAt(i)));
					}
				}
								
				return pinyins.toArray(new String[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static final String generatePinyinStr(String source, boolean spacePadding) {
		String[] pys = generatePinyins(source);
		if (pys != null && pys.length > 0) {
			String delim = "";
			if (spacePadding) {
				delim = " ";
			}
			return StringUtils.arrayToDelimitedString(pys, delim);
		}
		return "";
	}
}
