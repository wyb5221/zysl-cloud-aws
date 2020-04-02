package com.zysl.cloud.aws.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class DateUtils {

	private static SimpleDateFormat format_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat format_2 = new SimpleDateFormat("yyyy-MM-dd");

	public static Date from(Instant instant) {
		if(instant == null){
			return null;
		}
		try {
			return new Date(instant.toEpochMilli());
		} catch (ArithmeticException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	/**
	 * 给时间加上几个小时
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date addDateHour(Date date, Integer hour){
		if (null == date)
			return null;
		if(StringUtils.isEmpty(hour)){
			return date;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hour);// 24小时制

		return cal.getTime();
	}

	/**
	 * Date转String，返回yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String getDateToString(Date date) {
		if (date == null)
			return "";
		String dateString = format_1.format(date);
		return dateString;
	}

	/**
	 * String转Date， 返回时间格式yyyy-MM-dd HH:mm:ss
	 * @param strDate
	 * @return
	 */
	public static Date getStringToDate(String strDate) {
		if(StringUtils.isEmpty(strDate)){
			return null;
		}
		Date strtodate = null;
		try {
			strtodate = format_1.parse(strDate);
		} catch (ParseException e) {
			log.info("--getStringToDate--", e);
		}
		return strtodate;
	}

	/**
	 * 日期比较,小于0，date1小于date2；大于0，date1大于date2；等于0，这两个时间相等
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int doCompareDate(Date date1, Date date2) {
		int num = date1.compareTo(date2);
		System.out.println(num);
		return num;
	}

	/**
	 * 创建日期
	 * @param str
	 * @return
	 */
	public static Date createDate(String str){
		if(StringUtils.isEmpty(str)){
			return null;
		}
		try {
			return format_1.parse(str);
		} catch (ParseException e) {
			log.info("--getStringToDate--", e);
			return null;
		}
	}

	public static void main(String[] args) throws ParseException {
//        Date date2 = new Date(1920, 2,21, 20, 00, 00);
		System.out.println(createDate("2020-03-21 21:00:00"));
	}
}
