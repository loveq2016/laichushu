package com.laichushu.book.utils;

import java.util.regex.Pattern;

/**
 * 校验器：利用正则表达式校验邮箱、手机号等
 */
public class Validator
{
	/**
	 * 正则表达式：验证用户名
	 */
	public static final String	REGEX_USERNAME	= "^[a-zA-Z0-9]{4,30}$";

	/**
	 * 正则表达式：验证密码(字母+数字)
	 * 
	 * ^ 匹配一行的开头位置
	 * (?![0-9]+$) 预测该位置后面不全是数字
	 * (?![a-zA-Z]+$) 预测该位置后面不全是字母
	 * [0-9A-Za-z] {8,16} 由8-16位数字或这字母组成
	 * $ 匹配行结尾位置
	 * 注：(?!xxxx) 是正则表达式的负向零宽断言一种形式，标识预该位置后不是xxxx字符。
	 */
	public static final String	REGEX_PASSWORD	= "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$";

	/**
	 * 正则表达式：验证手机号
	 */
	public static final String	REGEX_MOBILE	= "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
	//public static final String	REGEX_MOBILE	= "^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9])|(17[0-9]))\\d{8}$";
	//public static final String	REGEX_MOBILE	= "^(1[0-9][0-9])\\d{8}$";

	/**
	 * 正则表达式：验证邮箱
	 */
	public static final String	REGEX_EMAIL		= "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	/**
	 * 正则表达式：验证汉字
	 */
	public static final String	REGEX_CHINESE	= "^[\u4e00-\u9fa5],{0,}$";

	/**
	 * 正则表达式：验证身份证
	 */
	public static final String	REGEX_ID_CARD	= "(^\\d{18}$)|(^\\d{15}$)";

	/**
	 * 正则表达式：验证URL
	 */
	public static final String	REGEX_URL		= "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

	/**
	 * 正则表达式：验证IP地址
	 */
	public static final String	REGEX_IP_ADDR	= "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	/**
	 * 校验用户名
	 * 
	 * @param username
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUsername(String username)
	{
		return Pattern.matches(REGEX_USERNAME, username);
	}

	/**
	 * 校验密码
	 * 
	 * @param password
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isPassword(String password)
	{
		return Pattern.matches(REGEX_PASSWORD, password);
	}

	/**
	 * 校验手机号
	 * @param mobile
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile)
	{
		//Pattern p = Pattern.compile(MOBILE_PHONE_RE);
		//Matcher m = p.matcher(mobiles);
		//return m.matches();
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	/**
	 * 校验邮箱
	 * 
	 * @param email
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isEmail(String email)
	{
		return Pattern.matches(REGEX_EMAIL, email);
	}

	/**
	 * 校验汉字
	 * 
	 * @param chinese
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isChinese(String chinese)
	{
		return Pattern.matches(REGEX_CHINESE, chinese);
	}

	/**
	 * 校验身份证
	 * 
	 * @param idCard
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isIDCard(String idCard)
	{
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}

	/**
	 * 校验URL
	 * 
	 * @param url
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isUrl(String url)
	{
		return Pattern.matches(REGEX_URL, url);
	}

	/**
	 * 校验IP地址
	 * 
	 * @param ipAddr
	 * @return
	 */
	public static boolean isIPAddr(String ipAddr)
	{
		return Pattern.matches(REGEX_IP_ADDR, ipAddr);
	}

	public static void main(String[] args)
	{
		String username = "fdsdfsdj";
		System.out.println(Validator.isUsername(username));
		System.out.println(Validator.isChinese(username));
	}
}