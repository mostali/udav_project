package api_kafka37;

import java.net.UnknownHostException;

public class ApiKafkaSend {

	public static void main(String[] args) throws UnknownHostException {

		String url = "localhost:9092";
		String topic = "topic";

		for (int i = 0; i < 10; i++) {
			ApiKafka.produceMsg(url, topic, "keeeey", "vlllll ываыва ()()() 18 - " + i, null);
		}

	}

}