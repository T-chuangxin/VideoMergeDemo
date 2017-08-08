package com.tian.videomergedemo.utils;

import java.util.List;

/**
 * 数据非空判断的工具类
 * @author hly
 *
 */
public class IsNonEmptyUtils {
	
	/**
	 * 判断list集合是否为空
	 * @param list
	 * @return 当返回为true表示集合不为空，为false表示集合为空
	 */
	public static boolean isList(List<?> list){
		if(null!=list){
			if(list.size()>0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * 判断字符串是否为空
	 * @param ss
	 * @return 当返回为true表示字符串不为空，为false表示字符串为空
	 */
	public static boolean isString(String ss){
		if(ss!=null){
			if(ss.trim()!=null){
				if(ss.trim().length()>0){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
