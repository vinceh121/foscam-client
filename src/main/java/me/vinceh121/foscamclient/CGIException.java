package me.vinceh121.foscamclient;

public class CGIException extends Exception {
	private int result;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CGIException(int result) {
		this.result = result;
		
		switch (result) { // TODO: Set message according to result code
		case 0:
			
		}
	}

	public int getResult() {
		return result;
	}
}
