package api_kafka37;

import mpu.X;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Function;

public interface KafkaWalkerFunc extends Function<ConsumerRecord<String, String>, Boolean> {

	default Boolean reciveMessage_NextMsg_NoMsg_Exit(ConsumerRecord<String, String> record) {
		X.pf("%s:%s*%s:%s=%s",
				record.topic(), record.partition(), record.offset(), record.key(), record.value());
		return true;
	}

	@Override
	default Boolean apply(ConsumerRecord<String, String> record) {
//		X.pf("%s:%s*%s:%s=%s",
//				record.topic(), record.partition(), record.offset(), record.key(), record.value());
		return reciveMessage_NextMsg_NoMsg_Exit(record);
	}

	static void consumeMessages(String url, String topic, String groupId, Function<ConsumerRecord<String, String>, Boolean> handler) {
		// Настройки для потребителя
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, url); // Адреса брокеров Kafka
		groupId = groupId == null ? groupId : "def";
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // Уникальный идентификатор группы потребителей
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Чтение с самого начала

		// Создание потребителя
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

		// Подписка на топик
		consumer.subscribe(Collections.singletonList(topic));

		try {
			closeConsumer:
			while (true) {
				// Получение сообщений
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); // Пауза между запросами
//				System.out.println("-->>>>>>>>>>>>>>>>>>>>:");
				nextConsumer:
				for (ConsumerRecord<String, String> record : records) {
					Boolean rslt = handler.apply(record);
					if (rslt == null) {
						break closeConsumer;
					} else if (!rslt) {
						break nextConsumer;
					}
					//					X.pf("%s:%s*%s:%s=%s",
					//							record.topic(), record.partition(), record.offset(), record.key(), record.value());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			consumer.close(); // Закрытие потребителя
		}
	}


}
