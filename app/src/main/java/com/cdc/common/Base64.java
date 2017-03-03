package com.cdc.common;

import java.io.*;

import android.util.Log;

/**
 * 
 * 类名: Base64</br> 
 * 包名：com.cndatacom.gdcn.util </br> 
 * 描述: base64的加密解析方法</br>
 * 发布版本号：</br>
 * 开发人员： huangzy</br>
 * 创建时间： 2013-3-1
 */
public class Base64 {

	static final char[] charTab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	public static String encode(byte[] data) {
		return encode(data, 0, data.length, null).toString();
	}

	public static StringBuffer encode(byte[] data, int start, int len,
			StringBuffer buf) {

		if (buf == null)
			buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append(charTab[(d >> 6) & 63]);
			buf.append(charTab[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append("\r\n");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append(charTab[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append("==");
		}

		// Log.d("DataHelper", "buf : " + buf );
		
		return buf;
	}

	static int decode(char c){
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				return 0;
//				throw new RuntimeException("unexpected code: " + c);
//				throw new Exception();
			}
	}

	public static byte[] decode(String s) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (Exception e) {
			// throw new RuntimeException();
		}finally{
			try {
				bos.close();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}

	public static void decode(String s, OutputStream os) throws Exception {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}
}
