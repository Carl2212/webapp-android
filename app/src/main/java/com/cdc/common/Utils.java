package com.cdc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;

public class Utils {

	public static DateFormat SHORT_DATE_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static DateFormat SHORT_DATE_FORMATER2 = new SimpleDateFormat(
			"yyyy年MM月dd日");
	public static DateFormat LONG_DATE_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static DateFormat TIME_FORMATER = new SimpleDateFormat("HH:mm:ss");
	public static DateFormat HOUR_MINUTE_FORMATER = new SimpleDateFormat(
			"hh:mm");

	// size
	public final static long BITMAP_MAX_SIZE = 307200; // 300K
	private static int[] deviceWidthHeight = new int[2];
	private static String[] weekDays = { "", "星期日", "星期一", "星期二", "星期三", "星期四",
			"星期五", "星期六" };

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}
	
	/** 判断字符串是否为空 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str) || "null".equals(str);
	}
	/** 判断字符串不为空 */
	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str);
	}

	/** 判断字符串是否为邮箱 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher mc = pattern.matcher(str);
		return mc.matches();
	}

	/** 判断List是否为空 */
	public static boolean listIsNullOrEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * 格式化成0个小数点
	 */
	public static String double2point(double value) {
		DecimalFormat format = new DecimalFormat("#0");
		return format.format(value);
	}
	public static String float2point(double value) {
		DecimalFormat format = new DecimalFormat("#0.0");
		return format.format(value);
	}

	public static String float2int(double value) {
		DecimalFormat format = new DecimalFormat("#0");
		return format.format(value);
	}
	
	public static String stringNoPoint(String s) {
		if(s.contains(".")) {
			String res = s.substring(0, s.lastIndexOf("."));
			return res;
		} else {
			return s;
		}
	}

	/** 判断map是否为空 */
	public static boolean mapIsNullOrEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty() || map.size() == 0;
	}

	/** 判断object是否为空 */
	public static boolean objectIsNull(Object object) {
		return object == null;
	}

	/** 获取文件后缀 */
	public static String getFileType(String fileUri) {
		File file = new File(fileUri);
		String fileName = file.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		return fileType;
	}

	/** 获取一周前的时间 */
	public static Date getBeforeWeekDate(Calendar cal) {
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 8);

		return cal.getTime();
	}

	/** 获取前一天的时间 */
	public static Date getBeforeDate(Calendar cal) {
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		return cal.getTime();
	}

	static public String mSec2TimeStr(long mSec) {

		long hours = (mSec % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mSec % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mSec % (1000 * 60)) / 1000;
		String h = Long.toString(hours);
		String m = Long.toString(minutes);
		String s = Long.toString(seconds);
		if (h.length() < 2) {
			h = "0" + h;
		}
		if (m.length() < 2) {
			m = "0" + m;
		}
		if (s.length() < 2) {
			s = "0" + s;
		}
		if (hours <= 0) {
			return m + ":" + s + ":";
		} else {
			return h + ":" + m + ":" + s + ":";
		}
	}

	/**
	 * 
	 * @description 计算两个时间的时间差
	 * @param date1
	 * @param date2
	 * @return
	 * @return String
	 * @history created by dengwenguang on Mar 28, 2011
	 */
	public static String diffBetweenTwoTime(Date date1, Date date2) {
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		String str = diffBetweenTwoTime(time1, time2);
		return str;
	}

	/**
	 * 
	 * @description 计算两个时间的时间
	 * @param time1
	 * @param time2
	 * @return
	 * @return String
	 * @history created by dengwenguang on Apr 10, 2011
	 */
	public static String diffBetweenTwoTime(long time1, long time2) {
		long l;
		if (time1 > time2) {
			l = time1 - time2;
		} else {
			l = time2 - time1;
		}
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long second = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String str = "";
		if (day > 0) {
			str = day + "天" + hour + "小时" + min + "分" + second + "秒";
		} else if (hour > 0) {
			str = hour + "小时" + min + "分" + second + "秒";
		} else if (min > 0) {
			str = min + "分" + second + "秒";
		} else if (second >= 0) {
			str = second + "秒";
		}
		return str;
	}

	// 计算天数间隔
	public static boolean twoDateOver(Date date1, Date date2, int d) {
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long l;
		if (time1 > time2) {
			l = time1 - time2;
		} else {
			l = time2 - time1;
		}
		long day = l / (24 * 60 * 60 * 1000);
		if (day > d) {
			return true;
		}
		return false;
	}

	public static String diffBetweenTwoTime2(long time1, long time2) {
		long l;
		if (time1 > time2) {
			l = time1 - time2;
		} else {
			l = time2 - time1;
		}
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long second = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String str = "";
		if (day > 0) {
			str = day + "天";
		} else if (hour > 0) {
			str = hour + "小时";
		} else if (min > 0) {
			str = min + "分";
		} else if (second >= 0) {
			str = second + "秒";
		}
		return str;
	}

	public static String second2Time(long second) {
		return diffBetweenTwoTime(second * 1000, 0);
	}

	/**
	 * 把dip单位转成px单位
	 * 
	 * @param context
	 *            context对象
	 * @param dip
	 *            dip数值
	 * @return
	 */
	public static int formatDipToPx(Context context, int dip) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return (int) Math.ceil(dip * dm.density);
	}

	public static String trimVerName(String verName) {
		byte[] verString = verName.trim().getBytes();
		StringBuffer result = new StringBuffer();

		char chr;
		for (int i = 0; i < verString.length; i++) {
			chr = (char) verString[i];
			if (chr >= 48 && chr <= 57) {
				result.append(chr);
			}
		}
		while (result.length() < 20) {
			result.append('0');
		}
		return result.toString();
	}

	public static String formatNumber(double num, String... style) {
		DecimalFormat df = new DecimalFormat();
		if (style != null && style.length > 0) {
			df.applyPattern(style[0]);
		} else {
			df.applyPattern("0.00");
		}
		return df.format(num);
	}

	public static String formatDate(Date date, String... dateFormat) {
		DateFormat df = null;
		if (dateFormat != null && dateFormat.length > 0)
			df = new SimpleDateFormat(dateFormat[0]);
		else
			df = new SimpleDateFormat("MM月dd日 HH:mm");
		return df.format(date);
	}

	public static String formatDateCurrentTime(long time, String... dateFormat) {
		Date date = new Date(time);
		DateFormat df = null;
		if (dateFormat != null && dateFormat.length > 0)
			df = new SimpleDateFormat(dateFormat[0]);
		else
			df = new SimpleDateFormat("MM月dd日 HH:mm");
		return df.format(date);
	}

	public static String array2String(Object[] objects, String... mark) {
		String defaultMark = ",";
		if (mark != null && mark.length > 0) {
			defaultMark = mark[0];
		}
		if (objects != null && objects.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (Object object : objects) {
				sb.append(object.toString()).append(defaultMark);
			}

			sb.delete(sb.length() - defaultMark.length(), sb.length());
			return sb.toString();
		} else {
			return "";
		}

	}

	public static String sortKey2Num(String sortKey) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sortKey.length(); i++) {
			sb.append(char2Number(sortKey.charAt(i)));
		}
		return sb.toString();
	}

	public static char char2Number(char charStr) {
		charStr = Character.toLowerCase(charStr);
		// TODO Auto-generated method stub
		switch (charStr) {
		case '1':
			return '1';
		case 'a':
		case 'b':
		case 'c':
		case '2':
			return '2';
		case 'd':
		case 'e':
		case 'f':
		case '3':
			return '3';
		case 'g':
		case 'h':
		case 'i':
		case '4':
			return '4';
		case 'j':
		case 'k':
		case 'l':
		case '5':
			return '5';
		case 'm':
		case 'n':
		case 'o':
		case '6':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case '7':
			return '7';
		case 't':
		case 'u':
		case 'v':
		case '8':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case '9':
			return '9';
		case '0':
			return '0';
		case ' ':
			return ' ';
		case '+':
			return '+';
		default:
			return '*';
		}
	}

	public static boolean isNumber(String number) {
		for (int i = 0; i < number.length(); i++) {
			if (!Character.isDigit(number.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	static StringBuffer sb = new StringBuffer();

	public static String words2T9Number(StringBuffer words) {
		sb.setLength(0);
		for (int i = 0; i < words.length(); i++) {
			char number = char2Number(words.charAt(i));
			if (number != '*') {
				sb.append(number);
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @description 根据不同的手机号码返回其对应的手机邮箱
	 * @param mobileNumber
	 * @return
	 * @return String
	 * @history created by dengwenguang on 2011-10-31
	 */
	// public static String getMobileEmailAddress(String mobileNumber) {
	// String emailAddress = null;
	// if (!Utils.isEmpty(mobileNumber)) {
	// int spType = PhoneUtils.getMobileSP(mobileNumber);
	// switch (spType) {
	// case 1:
	// emailAddress = mobileNumber + "@189.cn";
	// break;
	// case 2:
	// emailAddress = mobileNumber + "@139.com";
	// break;
	// case 3:
	// emailAddress = mobileNumber + "@wo.com.cn";
	// break;
	// default:
	// break;
	// }
	// }
	// return emailAddress;
	// }

	// 判断是不是字符串
	public static boolean isStrAndLetter(String str) {
		// StringBuffer sb=new StringBuffer();
		// StringBuffer sb2=new StringBuffer();
		System.out.println(str);
		boolean flag = true;
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.substring(i,i+1).matches("[\\u4e00-\\u9fbf]+"));
			if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fbf]+")
					|| str.substring(i, i + 1).matches("[a-zA-Z]")) {
			} else {
				return false;
			}
		}
		return flag;
	}

	// 获取群发分隔符
	public static String getSmsSeparator() {
		if (Build.MODEL.contains("HTC") || Build.MODEL.contains("C8500")
				|| Build.MODEL.contains("C8600")
				|| Build.MODEL.contains("C8800")
				|| Build.MODEL.contains("C8650")) {
			return ";";
		}
		return ",";
	}

	// public static boolean isSuccess(ResultMsg result) {
	// return result != null
	// && result.ERROR_CODE_NONE.equals(result.getErrorCode());
	// }

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String second2Str(int msec) {
		int sec = msec / 1000;
		int min = sec / 60;
		int second = sec % 60;
		String strMin = "";
		String strSec = "";
		if (min < 10) {
			strMin = "0" + min;
		} else {
			strMin = "" + min;
		}
		if (second < 10) {
			strSec = "0" + second;
		} else {
			strSec = "" + second;
		}
		return strMin + ":" + strSec;
	}

	public final static String FROMPATH = "/mnt/sdcard/zipFile.zip";
	public final static String TOPATH = "/mnt/sdcard/IMusic/theme/";

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFile
	 *            要解压的压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static boolean upZipFile(File zipFile, String folderPath) {

		// zipFile = new File(FROMPATH);
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}

		ZipFile zf;
		InputStream in = null;
		OutputStream out = null;
		try {
			zf = new ZipFile(zipFile);
			for (Enumeration<?> entries = zf.entries(); entries
					.hasMoreElements();) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				in = zf.getInputStream(entry);
				String str = folderPath + File.separator + entry.getName();
				str = new String(str.getBytes("8859_1"), "GB2312");
				File desFile = new File(str);
				if (!desFile.exists()) {
					File fileParentDir = desFile.getParentFile();
					if (!fileParentDir.exists()) {
						fileParentDir.mkdirs();
					}
					desFile.createNewFile();
				}
				out = new FileOutputStream(desFile);
				byte buffer[] = new byte[1024];
				int realLength;
				while ((realLength = in.read(buffer)) > 0) {
					out.write(buffer, 0, realLength);
				}
				in.close();
				out.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			System.out.println("报错:" + sw.toString());
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				System.out.println("报错:" + sw.toString());
			}

		}
		return false;

	}

	public static boolean unzip(InputStream zipFileName, String outputDirectory) {
		try {
			ZipInputStream in = new ZipInputStream(zipFileName);
			// 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
			// 当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
			// 输入流读取完成；
			ZipEntry entry = in.getNextEntry();
			while (entry != null) {
				// 创建以zip包文件名为目录名的根目录
				File file = new File(outputDirectory);
				file.mkdir();
				if (entry.isDirectory()) {
					String name = entry.getName();
					// System.out.println("entry.getName()="+ entry.getName());
					name = name.substring(0, name.length() - 1);
					// System.out.println("outputDirectory + File.separator + name="+outputDirectory
					// + File.separator + name);
					file = new File(outputDirectory + File.separator + name);
					file.mkdir();
				} else {
					file = new File(outputDirectory + File.separator
							+ entry.getName());
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					// System.out.println("outputDirectory + File.separator + entry.getName()="+outputDirectory
					// + File.separator + entry.getName());
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					int b;
					while ((b = in.read()) != -1) {
						out.write(b);
					}
					out.close();
				}
				// 读取下一个ZipEntry
				entry = in.getNextEntry();
			}
			in.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			System.out.println("报错:" + sw.toString());
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			System.out.println("报错:" + sw.toString());
		}
		return false;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			System.out.println("报错:" + sw.toString());
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static boolean delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		if (!file.isDirectory()) {
			return false;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
		return true;
	}

	public static String FilterHtml(String str) {
		str = str.replaceAll("<(?!br|img)[^>]+>", "").trim();
		return str;
	}

	public static String musicUriToFilePath(String musicUri) {
		String fileName = Integer.toString(musicUri.hashCode()); // MD5.crypt(downloadUrl);
		String fileExtName = musicUri.substring(musicUri.lastIndexOf("."));
		// String localFilePath = SPConfig.MUSIC_CACHE_PATH + fileName
		// + fileExtName;
		String localFilePath = Environment
				.getExternalStorageDirectory()
				+ "/"
				+ "Android/data/cn.eshore.imusic/music_cache"
				+ File.separator
				+ fileName + fileExtName;

		return localFilePath;
	}

	public static long deleteToManyMusic() {// 递归求取目录文件个数
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/" + "Android/data/cn.eshore.imusic/music_cache");
		long size = 0;
		File[] flist = f.listFiles();
		size = flist.length;
		System.out.println("歌曲数=" + size);
		if (size > 20) {
			Arrays.sort(flist, new CompratorByLastModified());
			// for (int i = 0; i < flist.length; i++) {
			// System.out.println(flist[i].lastModified());
			// }
			File dFile = flist[0];
			dFile.delete();
			flist = f.listFiles();
			System.out.println("删除后歌曲数=" + flist.length);
		}
		return size;

	}

	static class CompratorByLastModified implements Comparator {
		public int compare(Object o1, Object o2) {
			File file1 = (File) o1;
			File file2 = (File) o2;
			long diff = file1.lastModified() - file2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}

		public boolean equals(Object obj) {
			return true; // 简单做法
		}
	}

	public static String timestamp2String(long timestamp) {
		String timeString = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(timestamp);
			timeString = sdf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timeString;
	}

	public static String timestamp2YearMonthDay(long timestamp) {
		String timeString = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date(timestamp);
			timeString = sdf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timeString;
	}

	/**
	 * 
	 * @param timestamp
	 * @return 时：分
	 */
	public static String timeDivision(long timestamp) {
		String timeString = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			Date date = new Date(timestamp);
			timeString = sdf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timeString;
	}

	public static String timestamp2String(long timestamp, String formatString) {
		String timeString = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(formatString);
			Date date = new Date(timestamp);
			timeString = sdf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timeString;
	}

	/**
	 * 日期转成星期
	 */
	public static String date2Week(long time) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(time));
			System.out.println(weekDays[calendar.get(Calendar.DAY_OF_WEEK)]);
			return weekDays[calendar.get(Calendar.DAY_OF_WEEK)];
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 把年 月 日 转成 月 日hezhoup
	 */
	public static String date2Time(long time, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(new Date(time));
	}

	/**
	 * 把时间拼成 月 日 星期 的格式 hezhoup
	 */
	public static String obtainDate(long time, String format) {
		return date2Time(time, format) + " " + date2Week(time);
	}

	/*
	 * 日期转星期 liyoro
	 */
	public static final int WEEKDAYS = 7;
	public static String[] WEEK = { "星期六", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };

	public static String DateToWeek(long timestamp) {
		try {
			Date date = new Date(timestamp);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
			if (dayIndex < 1 || dayIndex > WEEKDAYS) {
				return null;
			}

			return WEEK[dayIndex - 1];

		} catch (Exception e) {
		}
		return null;
	}

	private String databasepath = "data/data/com.android.egozixun/databases/";
	private String databasefn = "labelname.db";

	// 把数据库复制到指定目录
	private void copyDatabase() {
		try {
			// 获得li.db 绝对路径
			String DATABASEFN = databasepath + databasefn;
			File dir = new File(databasepath);
			// 如果/sdcard/li目录中存在，创建这个目录
			if (!dir.exists()) {
				System.out.print("1");
			}
			dir.mkdir();

			// 如果在目录中不存在
			// 文件，则从res\raw目录中复制这个文件到
			if (!(new File(DATABASEFN).exists())) {
				// 获得封装 文件的InputStream对象
				InputStream is = null;
				// InputStream is =
				// AppApplication.getInstance().getResources().openRawResource(R.raw.labelname);
				FileOutputStream fos = new FileOutputStream(DATABASEFN);
				// 下面这一段代码不是很理解，有待研究

				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片圆角
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
		} catch (Error e) {
			e.printStackTrace();
			output.recycle();
			System.gc();
			return bitmap;
		}
		return output;
	}

	public static Bitmap decodeImage(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();

		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
		return bmp;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/***
	 * @description 只取四张图片
	 * @param list
	 * @return
	 */
	public static String[] getPreImageLst(List<String> list) {
		String[] urlList = new String[4];
		if (list != null && list.size() > 0) {
			int k = 0;
			int l = 0;
			// if ((list.size() - 4) > 0) {
			// l = 4;
			// } else {
			// l = list.size();
			// }
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null) {
					if (list.get(i)
							.substring(list.get(i).length() - 8,
									list.get(i).length() - 7).equals("p")) {
						urlList[k] = list.get(i);
						k++;
					}
				}
			}
		}
		return urlList;
	}

	public static List<String> getImagetLst(List<String> list) {
		List<String> retVal = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (int k = 0; k < list.size(); k++) {
				if (!list
						.get(k)
						.substring(list.get(k).length() - 8,
								list.get(k).length() - 7).equals("p")) {
					retVal.add(list.get(k));
				}
			}
		}
		return retVal;
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static String string2low(String tem, int length) {
		StringBuilder string2 = new StringBuilder();
		for (int i = 0; i < tem.length(); i++) {

			char tmp = tem.charAt(i);
			if (i >= length) {
				string2.append("...");
				break;
			} else {
				string2.append(tmp);
			}
		}
		return string2.toString();

	}

	/***
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String timestamp2DateHourMin(long timestamp) {
		String timeString = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = new Date(timestamp);
			timeString = sdf.format(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timeString;
	}

	/**
	 * 检查存储卡是否插入
	 * 
	 * @return
	 */
	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static int[] getDeviceInfo(Context context) {
		if ((deviceWidthHeight[0] == 0) && (deviceWidthHeight[1] == 0)) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			deviceWidthHeight[0] = metrics.widthPixels;
			deviceWidthHeight[1] = metrics.heightPixels;
		}
		return deviceWidthHeight;
	}

	/**
	 * 判断是否是三星手机
	 * 
	 * @return
	 */
	public static boolean isSumsungPhone() {
		boolean isSumsungPhone = false;
		if (Build.MODEL.contains("SCH")) {
			isSumsungPhone = true;
		}
		return isSumsungPhone;
	}

	/**
	 * 复制文件
	 * 
	 * @return 返回复制后的文件的绝对路径
	 */
	public static String copyFile(String oldPath, String newPath) {
		if (oldPath == null || newPath.equals(oldPath)) {
			return oldPath;
		}
		File oldFile = new File(oldPath);
		if (!oldFile.exists() || !oldFile.canRead()) {
			return oldPath;
		}
		File newFile = new File(newPath);
		if (!newFile.exists()) {
			File dir = newFile.getParentFile();
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return oldPath;
				}
			}
		}
		try {
			InputStream inStream = new FileInputStream(oldFile); // 读入原文件的字节流
			FileOutputStream outStream = new FileOutputStream(newFile);// 写入新文件的字节流
			int length = inStream.available();
			byte[] buffer = new byte[length];
			int tmp;
			while ((tmp = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, tmp);
			}
			inStream.close();
			outStream.close();
			return newPath;
		} catch (Exception e) {
			e.printStackTrace();
			return oldPath;
		}
	}

	/**
	 * 复制文件
	 * 
	 * @return 返回复制后的文件的绝对路径
	 */
	public static String copyFile(File oldFile, String newPath) {
		if (oldFile == null || !oldFile.exists() || !oldFile.canRead()) {
			return null;
		}
		String oldPath = oldFile.getAbsolutePath();
		if (oldPath.equals(newPath)) {
			return oldPath;
		}
		File newFile = new File(newPath);
		if (!newFile.exists()) {
			File dir = newFile.getParentFile();
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return oldPath;
				}
			}
		}
		try {
			InputStream inStream = new FileInputStream(oldFile); // 读入原文件的字节流
			FileOutputStream outStream = new FileOutputStream(newFile);// 写入新文件的字节流
			int length = inStream.available();
			byte[] buffer = new byte[length];
			int tmp;
			while ((tmp = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, tmp);
			}
			inStream.close();
			outStream.close();
			return newPath;
		} catch (Exception e) {
			e.printStackTrace();
			return oldPath;
		}
	}

	/**
	 * 复制文件
	 * 
	 * @return 返回复制后的文件的绝对路径
	 */
	public static String copyFile(File oldFile, File newFile) {
		if (oldFile == null || !oldFile.exists() || !oldFile.canRead()) {
			return null;
		}
		String oldPath = oldFile.getAbsolutePath();
		String newPath = newFile.getAbsolutePath();
		if (oldPath.equals(newPath)) {
			return oldPath;
		}
		if (!newFile.exists()) {
			File dir = newFile.getParentFile();
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					return oldPath;
				}
			}
		}
		try {
			InputStream inStream = new FileInputStream(oldFile); // 读入原文件的字节流
			FileOutputStream outStream = new FileOutputStream(newFile);// 写入新文件的字节流
			int length = inStream.available();
			byte[] buffer = new byte[length];
			int tmp;
			while ((tmp = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, tmp);
			}
			inStream.close();
			outStream.close();
			return newPath;
		} catch (Exception e) {
			e.printStackTrace();
			return oldPath;
		}
	}
	
	/**
	 * 随机生成的设备的序列号
	 * 
	 * @param ctx
	 *            An activity or other context
	 * @return 序列号
	 */
	public static String getAndroidID(Context ctx) {
		String androidID = null;
		try {
			androidID = Settings.Secure.getString(ctx.getContentResolver(),
					Settings.Secure.ANDROID_ID);

		} catch (Exception e) {
//			MyLog.i("获取设备ID错误");
		}
		return androidID;
	}
}
