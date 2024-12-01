package mpf;

import java.util.Optional;

//https://medium.com/@alexeynovikov_89393/stop-using-exceptions-in-java-456f7db46ea4
public class Result<T> {
	private Optional<T> value;
	private Optional<String> error;

	private Result(T value, String error) {
		this.value = Optional.ofNullable(value);
		this.error = Optional.ofNullable(error);
	}

	public static <U> Result<U> ok(U value) {
		return new Result<>(value, null);
	}

	public static <U> Result<U> error(String error) {
		return new Result<>(null, error);
	}

	public boolean isError() {
		return error.isPresent();
	}

	public T getValue() {
		return value.get();
	}

	public String getError() {
		return error.get();
	}
}
