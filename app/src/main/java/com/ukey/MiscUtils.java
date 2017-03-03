package com.ukey;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MiscUtils {
	public static String bytesToHexStr(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String tempStr = Integer.toHexString(bytes[i] & 0xFF);
			if (tempStr.length() == 1)
				sb.append("0");
			sb.append(tempStr);
		}
		return sb.toString();
	}

	public static byte[] hexStrToBytes(String hexStr) {
		if (hexStr == null) {
			return null;
		}
		if ((hexStr.length() % 2 != 0) || (hexStr.length() == 0)) {
			return null;
		}
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) Integer.parseInt(
					hexStr.substring(i * 2, i * 2 + 2), 16);
			// result[i] = Byte.parseByte(hexStr.substring(i * 2, i * 2 + 2),
			// 16);
		}

		return result;
	}

	public static X509Certificate createCertficate(byte[] certBytes, String tag) {
		if (certBytes == null) {
			Log.e(tag, "certBytes is null");
			return null;
		}
		InputStream certStream = new ByteArrayInputStream(certBytes);
		CertificateFactory certFactory = null;

		try {
			certFactory = CertificateFactory.getInstance("X.509", "BC");
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		if (certFactory == null) {
			Log.e(tag, "can't get CertFactory");
			return null;
		}

		X509Certificate x509Cert = null;
		try {
			x509Cert = (X509Certificate) certFactory
					.generateCertificate(certStream);
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return x509Cert;
	}

	public static String certificationToString(X509Certificate cert) {
		if (cert == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Certificate subject: " + cert.getSubjectDN().getName()
				+ "\n");
		sb.append("Certificate issuer: " + cert.getIssuerDN().getName() + "\n");
		sb.append("Certificate serial number: " + cert.getSerialNumber() + "\n");
		sb.append("not before: " + cert.getNotBefore() + "\n");
		sb.append("not after: " + cert.getNotAfter() + "\n");
		// sb.append("\n");
		return sb.toString();
	}

	/*********************************** MDP请求 *************************************************/

	/**
	 * 
	 * 方法名: getRequestXml</br> 详述: </br> 开发人员：huangzy</br> 创建时间：2013-11-18</br>
	 * 
	 * @param command
	 * @param map
	 * @return
	 */
	/*public static RequestXml getRequestXml(Context context, String command,
			HashMap<String, String> datas) {
		RequestXml reXml = new RequestXml(context);
		reXml.setCommand(command);
		reXml.addParameter(new RequestParameter("map_session_id", Constant
				.getMap_session_id()));
		reXml.addParameter(new RequestParameter("timestamp", ""
				+ System.currentTimeMillis()));
		Set<Map.Entry<String, String>> set = datas.entrySet();
		for (Iterator<Map.Entry<String, String>> it = set.iterator(); it
				.hasNext();) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) it
					.next();
			System.out.println(entry.getKey() + "--->" + entry.getValue());
			reXml.addParameter(new RequestParameter(entry.getKey(), entry
					.getValue()));
		}
		return reXml;
	}*/

	/**
	 * 
	 * 方法名: loadDatas</br> 详述: 请求数据</br> 开发人员：huangzy</br> 创建时间：2013-11-18</br>
	 * 
	 * @param context
	 * @param tipsString
	 * @param handler
	 * @param isTips
	 */
	public static void loadDatas(Context context, Handler handler,
			String command, HashMap<String, String> datas) {
//		String serviceUrl = CommonMethod.getServiceMainUrl(context);
//		DataHttpRequest request = new DataHttpRequest(context, getRequestXml(
//				context, command, datas), serviceUrl, "", handler, false);
//		request.setDialogMessage(null);
//		request.setCallBackHttpData(true);
//		request.execute();
	}

	/**
	 * 获取标签值
	 * 
	 * <pre>
	 * getTagValue(&quot;&lt;a&gt;111&lt;/a&gt;&quot;, &quot;a&quot;) = 111
	 * </pre>
	 * 
	 * @param xmlData
	 *            数据源
	 * @param tagName
	 *            标签名称
	 * @param callback
	 *            返回值
	 * @return
	 */
	public static String getTagValue(String xmlData, String tagName,
			String callback) {
		String result = callback;
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(tagName);
		// sb.append("(.*?)");// .*?
		sb.append(">");
		sb.append("([\\s\\S]*)");// .*?
		sb.append("</");
		sb.append(tagName);
		sb.append(">");// 形如：<message(.*?)>([\\s\\S]*)</message>
		Pattern pattern = Pattern.compile(sb.toString());
		Matcher matcher = pattern.matcher(xmlData);
		if (matcher.find()) {
			result = matcher.group(matcher.groupCount());// 总是返回最后一个group
		}
		return result;
	}

	/**
	 * 
	 * 方法名: getUkeyZhengShuError</br> 详述: 获取证书失败放回code</br> 开发人员：huangzy</br>
	 * 创建时间：2015年3月4日</br>
	 * 
	 * @param code
	 * @return
	 */
	public static String getUkeyZhengShuError(int code) {
		switch (code) {
		case 0:
			return "成功读取证书";
		case 1:
			return "机卡接口异常";
		case 2:
			return "卡上U盾应用未安装";
		case 3:
			return "U盾Pin码已锁定";
		case 4:
			return "未关联证书";
		case 7:
			return "读取U盾状态失败";
		case 8:
			return "U盾未开通";
		case 9:
			return "U盾未设置Pin码";
		case 10:
			return "U盾应用锁定";
		case 11:
			return "数据不同步，U盾应用中不包含服务端查到的证书（索引不存在）";
		case 12:
			return "证书读取错误";
		case -1:
			return "服务端异常（检查调用者相关信息、获取证书索引）";
		case -2:
			return "U盾版本需要更新";
		case -3:
			return "网络不可用";
		case -7:
			return "协议错误（检查调用者相关信息、获取证书索引）";
		case -8:
			return "未知的U盾状态";
		case -15:
			return "非法调用者";
		case -31:
			return "网络不可用";
		case -32:
			return "需要更新证书（更新证书失败导致）";
		default:
			return "获取Ukey证书失败！";
		}

	}

}
