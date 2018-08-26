package me.vinceh121.foscamclient;

public class CGIException extends Exception {
	private int result;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CGIException(int result) {
		super(getMessageForCode(result));
		this.result = result;
	}

	public static String getMessageForCode(int code) {
		String s = null;
		switch (code) { // Those code have been copied directly from the documentation (yes, including the English mistakes)
		case 0:
			s = "Success";
			break;
		case -1:
			s = "CGI request string format error";
			break;
		case -2:
			s = "Username or password error";
			break;
		case -3:
			s = "Access deny";
			break;
		case -4:
			s = "CGI execute fail";
			break;
		case -5:
			s = "Timeout";
			break;
		case -6:
			s = "Reserve";
			break;
		case -7:
			s = "Unknown error";
			break;
		case -8:
			s = "Reserve";
			break;
		}
		return s;
	}

	public int getResult() {
		return result;
	}
}
