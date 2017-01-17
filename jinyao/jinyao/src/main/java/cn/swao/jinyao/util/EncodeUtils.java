package cn.swao.jinyao.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author ShenJX
 *
 */
public class EncodeUtils {
	/**
	 * unicode解码
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str) {
		Charset set = Charset.forName("UTF-16");
		Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
		Matcher m = p.matcher(str);
		int start = 0;
		int start2 = 0;
		StringBuffer sb = new StringBuffer();
		while (m.find(start)) {
			start2 = m.start();
			if (start2 > start) {
				String seg = str.substring(start, start2);
				sb.append(seg);
			}
			String code = m.group(1);
			int i = Integer.valueOf(code, 16);
			byte[] bb = new byte[4];
			bb[0] = (byte) ((i >> 8) & 0xFF);
			bb[1] = (byte) (i & 0xFF);
			ByteBuffer b = ByteBuffer.wrap(bb);
			sb.append(String.valueOf(set.decode(b)).trim());
			start = m.end();
		}
		start2 = str.length();
		if (start2 > start) {
			String seg = str.substring(start, start2);
			sb.append(seg);
		}
		return sb.toString();
	}

	/**
	 * 获取编码后的url
	 * 
	 * @param url
	 *            需要编码的url
	 * @return 编码后的url
	 * @throws UnsupportedEncodingException
	 */
	public static String getEndcodedUrl(String url) throws UnsupportedEncodingException {
		String[] urls = url.split("?");
		if (urls.length != 2) {
			return null;
		}
		String prefixString = urls[0];
		String argsString = urls[1];
		String[] args = argsString.split("&");
		String[] encodeArgs = new String[args.length];
		for (int i = 0; i < encodeArgs.length; i++) {
			String arg = args[i];
			int index = arg.indexOf("=");
			String result = args[i].substring(index + 1, arg.length());
			String prefix = arg.substring(0, index + 1);
			encodeArgs[i] = prefix + URLEncoder.encode(result, "utf-8");
		}
		String resultArgs = String.join("&", encodeArgs);
		return prefixString + "?" + resultArgs;
	}
}
