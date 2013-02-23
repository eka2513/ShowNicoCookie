package jp.nicovideo.eka2513.shownicocookie.main;

import gnu.getopt.Getopt;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.nicovideo.eka2513.cookiegetter4j.constants.NicoCookieConstants;
import jp.nicovideo.eka2513.cookiegetter4j.cookie.NicoCookieManager;
import jp.nicovideo.eka2513.cookiegetter4j.cookie.NicoCookieManagerFactory;
import jp.nicovideo.eka2513.cookiegetter4j.util.PropertyUtil;
import jp.nicovideo.eka2513.shownicocookie.exception.NicoException;
import jp.nicovideo.eka2513.shownicocookie.util.CommentUtil;

public class Main {

	private static boolean logging = true;
	
	static class Logger {
		public static synchronized void println(String s) {
			if (!logging)
				return;
			System.out.println(String.format("%s: %s", new SimpleDateFormat(
					"yyyy.MM.dd HH:mm:ss").format(new Date()), s));
		}
		public static synchronized void printErrln(String s) {
			System.out.println(s);
		}
	}

	static class FieldReflecter {
		public static String getBrowser(String param) {
			try {
				Field f = NicoCookieConstants.class.getField("BROWSER_" + param);
				return f.get(null).toString();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				throw new NicoException("ブラウザ指定ミス");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Getopt options = new Getopt("nicoownercomment", args, "s:l:c:b:");
			String cookie = null;
			String lv = null;
			String comment = null;
			int c;
			while ((c = options.getopt()) != -1) {
				switch (c) {
				case 's':
					cookie = options.getOptarg();
					break;
				case 'b':
					//ブラウザから取得
					String browser = FieldReflecter.getBrowser(options.getOptarg());
					NicoCookieManager manager = NicoCookieManagerFactory.getInstance(browser);
					cookie = manager.getSessionCookie().toCookieString();
					break;
				case 'l':
					//lv
					lv = options.getOptarg();
					break;
				case 'c':
					//comment
					comment = options.getOptarg();
					break;
				default:
					break;
				}
			}
			if (cookie == null) {
				Logger.printErrln("usage");
				Logger.printErrln("\t-b [browser] -l [lv] -c [comment]");
				if (PropertyUtil.isMac()) {
					//BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_SAFARI, BROWSER_SAFARI_50_UNDER
				Logger.printErrln("\t-b [browser]     : CHROME FIREFOX SAFARI SAFARI_50_UNDER");
				}
				else if (PropertyUtil.isWindows()) {
					//BROWSER_IE, BROWSER_CHROME, BROWSER_FIREFOX, BROWSER_SAFARI, BROWSER_SAFARI_50_UNDER
				Logger.printErrln("\t-b [browser]     : IE CHROME FIREFOX SAFARI SAFARI_50_UNDER");
				}
				Logger.printErrln("-s [cookiestring] (instead of -b)");
				Logger.printErrln("-l -c is optional.");
				Logger.printErrln("\tif not set, will not send owner comment.");
				Logger.printErrln("\tonly print cookie");
				 
				return;
			}
			//commentとlvが入ってたら運営コメを発射します
			if (comment != null && lv != null) {
				CommentUtil u = new CommentUtil();
				u.setCookieString(cookie);
				//運営コメント発射
				u.send(lv, comment);
			} else {
				Logger.println(cookie);
			}
		} catch (Exception e) {
			throw new NicoException(e);
		}
	}
}
