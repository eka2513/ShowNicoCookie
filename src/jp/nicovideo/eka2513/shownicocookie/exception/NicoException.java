package jp.nicovideo.eka2513.shownicocookie.exception;


/**
 * てきとうなえくせぷしょん
 * @author eka2513
 *
 */
public class NicoException extends RuntimeException {

	public NicoException(String message) {
		super(message);
	}

	public NicoException(Exception e) {
		super(e);
	}

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8211321414281819954L;
}
