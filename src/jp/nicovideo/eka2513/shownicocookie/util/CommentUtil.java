package jp.nicovideo.eka2513.shownicocookie.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.nicovideo.eka2513.cookiegetter4j.util.StringUtil;
import jp.nicovideo.eka2513.shownicocookie.exception.NicoException;

public class CommentUtil extends RequestUtil {

	public CommentUtil() {
	}

	/**
	 * 運営米を発射します。
	 * @param lv
	 * @return
	 */
	public void send(String lv, String comment) throws NicoException {
		if (lv == null || lv.length() == 0) {
			throw new NicoException("放送ID・放送URLが入力されていません。");
		}
		lv = validateLv(lv);
		if (getCookieString() == null || getCookieString().length() == 0) {
			throw new NicoException("Cookieが取得できませんでした");
		}
		sendOwnerComment(lv, comment, "184");
	}

	private String validateLv(String lv) throws NicoException {
		String result = StringUtil.groupMatchFirst("(lv[0-9]+)", lv);
		if (result == null || result.length() == 0) {
			throw new NicoException("放送ID・放送URLが正しく入力されていません。");
		}
		return result;
	}

	private void sendOwnerComment(String lv, String comment, String mail) {
		try {
			if (lv == null || lv.length() == 0) {
				throw new NicoException("放送ID・放送URLが入力されていません。");
			}
			lv = validateLv(lv);
			if (getCookieString() == null || getCookieString().length() == 0) {
				throw new NicoException("Cookieが取得できませんでした");
			}
			String token = getTokenFromPublishStatus(lv);

			final String uri = String.format("http://watch.live.nicovideo.jp/api/broadcast/%s", lv);
			final String param = String.format("body=%s&mail=%s&token=%s",
				URLEncoder.encode(comment, "UTF-8"),
				URLEncoder.encode(mail, "UTF-8"),
				URLEncoder.encode(token, "UTF-8")
			);
			get(uri + "?" + param);
		} catch (UnsupportedEncodingException ignore) {
			
		}
	}
	public String getTokenFromPublishStatus(String lv) throws NicoException {
		final String uri = String.format("http://live.nicovideo.jp/api/getpublishstatus?v=%s", lv);
		String result = get(uri);
		String token  = StringUtil.groupMatchFirst("<token>([^<]*)", result);
		if (token == null || token.length() == 0) {
			throw new NicoException("延長に必要なパラメータの取得に失敗しました。ログインできていないかもしれません。");
		}
		return token;
	}
	public String getName() {
		return null;
	}
}
