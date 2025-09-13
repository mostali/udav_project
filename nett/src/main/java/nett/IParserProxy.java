package nett;

public interface IParserProxy {
	String getFreeProxy() throws TgException;

	void removeProxy(String freeProxy);
}
