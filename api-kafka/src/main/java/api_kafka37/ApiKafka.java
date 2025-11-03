package api_kafka37;

import mpe.call_msg.KafkaCallMsg;
import mpu.X;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;

public class ApiKafka {

	public static final Logger L = LoggerFactory.getLogger(ApiKafka.class);

	public static void main(String[] args) throws UnknownHostException {

		String url = "localhost:9092";
		String topic = "topic";

//		KafkaWalkerFunc.consumeMessages(url, topic, "ovvn", new KafkaWalkerFunc() {
//			@Override
//			public Boolean apply(ConsumerRecord<String, String> record) {
//				return KafkaWalkerFunc.super.apply(record);
//			}
//		});//

//		produceMsg(url, topic, "keeeey", "vlllll2");

	}

	private static void auth_simple(Properties props, String producer) {
		X.p("Auth:" + producer);
		props.setProperty("kafka_username", "admin");
		props.setProperty("kafka_password", "admin-secret");
	}

	public static void producer(String[] args) throws UnknownHostException {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("linger.ms", 1);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		auth_simple(props, "producer");

		Producer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 100; i++) {
			X.p("run:::kafka::message:" + i);
			producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));
		}

		producer.close();

	}

	public static void consumer(String[] args) throws UnknownHostException {
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("group.id", "test");
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "1000");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		auth_simple(props, "consumer");


		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("foo", "bar"));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
			}
			X.p("next-C");
		}
	}
	//
	//
	//
	//
	//
	//

	public static void consumeMsg(KafkaCallMsg kafkaCallMsg, Function<ConsumerRecord<String, String>, Boolean> recordHandler) {
		KafkaWalkerFunc.consumeMessages(kafkaCallMsg.url, kafkaCallMsg.getTopic(), kafkaCallMsg.getGroup("def"), recordHandler);
	}

	public static void produceMsg(KafkaCallMsg kafkaCallMsg, Callback sendCallback) {
		String topic = kafkaCallMsg.getTopic();
		String key = kafkaCallMsg.getKey();
		String body = kafkaCallMsg.getBody_STRING();
		produceMsg(kafkaCallMsg.url, topic, key, body, sendCallback);
	}

	public static void produceMsg(String url, String topic, String key, String value, Callback sendCallback) {
		// Настройки для продюсера
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		// Создание продюсера
		KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

		// Создание сообщения
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

		// Отправка сообщения
		if (sendCallback == null) {
			sendCallback = (metadata, err) -> {
				String msg = String.format("Send msg to topic kafka [%s->%s] %s. With key=%s and value=%s", url, topic, err == null ? "SUCCESS" : "FAIL", key, value);
				if (err != null) {
					L.error(msg, err);
					err.printStackTrace(System.err);
				} else {
					String okMetaMsg = msg + String.format(". Partition:%s, offset:%s", metadata.partition(), metadata.offset());
					L.info(okMetaMsg);
					System.out.println(okMetaMsg);
				}
			};
		}

		producer.send(record, sendCallback);

		// Закрытие продюсера
		producer.close();
	}

//	public static class WalkerKafkaMsg {
//		public static WalkerKafkaMsg of(String url, String topic, String groupId) {
//
//		}
//	}


}