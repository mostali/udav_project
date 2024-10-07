package mpc.exception;

public class MultipleMessageException extends Exception {
	private final String[] messages;

	public MultipleMessageException(String... _messages) {
		this.messages = (String[]) _messages.clone();
	}

	public MultipleMessageException(Throwable _cause, String... _messages) {
		super(_cause);
		this.messages = (String[]) _messages.clone();
	}

	public MultipleMessageException(MultipleMessageException _other) {
		this(_other.messages);
	}

	public String[] getMessages() {
		return this.messages;
	}

	public String getMessage() {
		return this.messages.length > 0 ? this.messages[0] : "";
	}

	public String toString() {
		StringBuilder resultMessage = new StringBuilder(super.toString());
		String[] var2 = this.messages;
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			String message = var2[var4];
			resultMessage.append('\n');
			resultMessage.append(message);
		}

		return resultMessage.toString();
	}
}