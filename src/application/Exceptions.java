package application;

public class Exceptions {
	public static class InvalidCountException extends Exception { 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvalidCountException(String errorMessage) {
	        super(errorMessage);
	    }
	}
	public static class UndersizeException extends Exception { 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UndersizeException(String errorMessage) {
	        super(errorMessage);
	    }
	}
	public static class InvalidRangeException extends Exception { 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvalidRangeException(String errorMessage) {
	        super(errorMessage);
	    }
	}
	public static class UnbalancedException extends Exception { 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UnbalancedException(String errorMessage) {
	        super(errorMessage);
	    }
	}
}