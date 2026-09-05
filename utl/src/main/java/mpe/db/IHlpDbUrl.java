package mpe.db;

public interface IHlpDbUrl {

	default String[] toHLP() {
		return new String[]{};
	}

	default String getHLP_DbName() {
		return toHLP()[4];
	}

	default String getHLP_Host() {
		return toHLP()[3];
	}

	default String getHLP_port() {
		return toHLP()[2];
	}

}
