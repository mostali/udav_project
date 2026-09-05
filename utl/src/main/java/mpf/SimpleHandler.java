package mpf;

public interface SimpleHandler<IN, OUT> {
	OUT handle(IN input) throws Exception;
}
