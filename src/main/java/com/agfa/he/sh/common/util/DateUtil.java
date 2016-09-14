package com.agfa.he.sh.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final String DATE_BEGIN = "1900-01-01";

	public static final String DATE_END = "3000-01-01";

	public static final String DAYTIME_BEGIN = " 00:00:00";

	public static final String DAYTIME_END = " 23:59:59";

	public static final String DATE_FORMAT_CN_MM_DD = "MM月dd日";

	public static final String DATE_FORMAT_HH_MM_SS = "HH:mm:ss";

	public static final String DATE_FORMAT_HHMMSS = "HHmmss";

	public static final String DATE_FORMAT_YYYY = "yyyy";

	public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd:HH:mm";

	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String DATE_FORMAT_YYYYMM = "yyyyMM";

	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";

	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	public static String[] Months = new String[] { "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER" };


	public static Timestamp add(Timestamp ts, int field, int n) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(ts);
		ca.add(field, n);
		return new Timestamp(ca.getTime().getTime());
	}

	public static Timestamp addDays(Timestamp ts, int n) {
		return DateUtil.add(ts, Calendar.DATE, n);
	}

	public static Date cloneDate(Date source) throws InstantiationException {
		if (source == null) {
			return source;
		} else {
			String clazzName = source.getClass().getName();

			if ("java.sql.Timestamp".equals(clazzName)) {
				return new java.sql.Timestamp(source.getTime());
			} else if ("java.sql.Date".equals(clazzName)) {
				return new java.sql.Date(source.getTime());
			} else if ("java.util.Date".equals(clazzName)) {
				return new java.util.Date(source.getTime());
			} else {
				throw new InstantiationException("Unsupported class type: " + clazzName);
			}
		}
	}

	public static String format(Date date, String format) {
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			return df.format(date);
		} else {
			return null;
		}
	}

	public static Date getDate(int hour24, int minute, int second) {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.HOUR_OF_DAY, hour24);
		ca.set(Calendar.MINUTE, minute);
		ca.set(Calendar.SECOND, second);
		return ca.getTime();
	}

	/**
	 * Get current date with no time precision, such as '2011-03-20' with the time '00:00:00'
	 *
	 * @return
	 */
	public static Timestamp getDateCurrent() {
		return DateUtil.parse(DateUtil.format(DateUtil.getTimestampCurrent(), DateUtil.DATE_FORMAT_YYYY_MM_DD).concat(" 00:00:00"), DateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
	}

	public static Timestamp getDateWithoutTime(Timestamp ts) {
		if (ts != null) {
			return DateUtil.parse(DateUtil.format(ts, DateUtil.DATE_FORMAT_YYYY_MM_DD).concat(" 00:00:00"), DateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
		}
		return null;
	}

	public static int getDayOfWeek(Date date) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return ca.get(Calendar.DAY_OF_WEEK);
	}

	public static long getDaysBetween(Date from, Date to) {
		return DateUtil.getSecondsBetween(from, to) / 86400;
	}

	public static int getField(Date date, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(field);
	}

	public static long getHoursBetween(Date from, Date to) {
		return DateUtil.getSecondsBetween(from, to) / 3600;
	}

	public static long getMinutesBetween(Date from, Date to) {
		return DateUtil.getSecondsBetween(from, to) / 60;
	}

	public static Timestamp getNextDateWithoutTime(Timestamp ts) {
		Timestamp nextTs = DateUtil.getDateWithoutTime(ts);
		if (nextTs != null) {
			return DateUtil.addDays(nextTs, 1);
		}
		return null;
	}

	public static long getSecondsBetween(Date from, Date to) {
		Calendar f = Calendar.getInstance();
		f.setTime(from);
		Calendar e = Calendar.getInstance();
		e.setTime(to);
		return DateUtil.getSecondsBetween(f, e);
	}

	public static String getStringCurrentDateNoFormat() {
		return DateUtil.format(DateUtil.getTimestampCurrent(), DateUtil.DATE_FORMAT_YYYYMMDD);
	}

	public static String getStringCurrentDateTimeByNoFormat() {
		return DateUtil.format(DateUtil.getTimestampCurrent(), DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
	}

	public static String getStringDate(java.sql.Date d) {
		if (d == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}

	public static String getStringDateNoFormat(Date date) {
		return DateUtil.format(date, DateUtil.DATE_FORMAT_YYYYMMDD);
	}

	public static String getStringDateTimeNoFormat(Date date) {
		return DateUtil.format(date, DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
	}

	public static String getStringTime(Date ts) {
		return DateUtil.format(ts, DateUtil.DATE_FORMAT_HH_MM_SS);
	}

	public static String getStringTimeNoFormat(Date ts) {
		return DateUtil.format(ts, DateUtil.DATE_FORMAT_HHMMSS);
	}

	// Get current sqlTimestamp.
	public static Timestamp getTimestampCurrent() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public static boolean isLeapYear(int year) {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, year);
		int intYear = ca.get(Calendar.YEAR);
		if ((0 == (intYear % 4)) && (((intYear % 100) != 0) || ((intYear % 400) == 0))) {
			return true;
		} else {
			return false;
		}
	}

//	public static void main(String[] args) {
//		String t1 = DateUtil.format(DateUtil.parse("090010", DateUtil.DATE_FORMAT_HHMMSS), "HH:mm");
//		System.out.println(t1);
//
//		Timestamp ts = DateUtil.getTimestampCurrent();
//		System.out.println(ts);
//		ts = DateUtil.getDateWithoutTime(ts);
//		System.out.println(ts);
//		ts = DateUtil.getNextDateWithoutTime(ts);
//		System.out.println(ts);
//	}

	public static Timestamp parse(String strDateTime, String sFormat) {
		Date date = DateUtil.parseStringToUtilDateTimeByFormat(strDateTime, sFormat);
		if (date != null) {
			return new Timestamp(date.getTime());
		}
		return null;
	}

	public static Calendar parseStringToCalendar(String strDate) {
		java.util.Date date = DateUtil.parseStringToUtilDate(strDate);
		if (date == null) {
			return null;
		}
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return ca;
	}

	public static Calendar parseStringToCalendar(String strDate, String strTime) {
		java.util.Date date = DateUtil.parseStringToUtilDate(strDate, strTime);

		if (date == null) {
			return null;
		}
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return ca;
	}

	public static Calendar parseStringToCalendarByFormat(String strDateTime, String sFormat) {
		java.util.Date date = DateUtil.parseStringToUtilDateTimeByFormat(strDateTime, sFormat);

		if (date == null) {
			return null;
		}
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		return ca;
	}

	public static java.util.Date parseStringToUtilDate(String strDate) {

		java.util.Date date = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			date = df.parse(strDate);
		} catch (Exception e) {
		}
		return date;
	}

	public static java.util.Date parseStringToUtilDate(String strDate, String strTime) {

		java.util.Date date = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = df.parse(strDate + " " + strTime);
		} catch (Exception e) {
		}
		return date;
	}

	private static long getSecondsBetween(Calendar from, Calendar to) {
		long f = from.getTimeInMillis();
		long t = to.getTimeInMillis();
		return (t - f) / 1000;
	}

	private static Date parseStringToUtilDateTimeByFormat(String strDateTime, String sFormat) {

		Date date = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(sFormat);
			date = df.parse(strDateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
}
