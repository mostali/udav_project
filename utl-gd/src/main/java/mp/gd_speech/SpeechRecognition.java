package mp.gd_speech;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpeechRecognition {
	private SpeechClient speechClient;

	public SpeechRecognition(File keyPath) throws IOException {
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyPath));

		// Создаем клиент для работы с Google Speech Recognition
		SpeechClient speechClient = SpeechClient.create();

		this.speechClient = SpeechClient.create();
	}

//	public void recognizeFromFile(String audioFilePath) throws Exception {
//		// Создаем объект для хранения аудио-данных
//		ByteString audioData = ByteString.readFrom(new FileInputStream(audioFilePath));
//
//		RecognitionAudio audio = RecognitionAudio.newBuilder()
//				.setContent(audioData)
//				.build();
//
//		// Создаем объект для настройки распознавания речи
//		RecognitionConfig config = RecognitionConfig.newBuilder()
//				.setEncoding(AudioEncoding.LINEAR16)
//				.setLanguageCode("ru-RU")
//				.setSampleRateHertz(16000)
//				.build();
//
//		// Распознаем речь
//		SpeechRecognitionResult result = speechClient.recognize(config, audio);
//
//		// Получаем текст, который был распознан
//		for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
//			System.out.println("Распознанный текст: " + alternative.getTranscript());
//		}
//	}

	public void recognizeFromMicrophone() throws Exception {
		// Настройки для записи аудио с микрофона
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();

		// Буфер для хранения аудио-данных
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[line.getBufferSize() / 5];

		// Записываем аудио с микрофона
		while (true) {
			numBytesRead = line.read(data, 0, data.length);
			out.write(data, 0, numBytesRead);
			if (numBytesRead == -1) {
				break;
			}
			// Ожидаем 5 секунд для завершения записи
			Thread.sleep(5000);
			break;
		}

		// Закрываем микрофон
		line.stop();
		line.close();

		// Создаем объект для хранения аудио-данных
		byte[] audioData = out.toByteArray();
		ByteString audioBytes = ByteString.copyFrom(audioData);

		RecognitionAudio audio = RecognitionAudio.newBuilder()
				.setContent(audioBytes)
				.build();

		// Создаем объект для настройки распознавания речи
		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(AudioEncoding.LINEAR16)
				.setLanguageCode("ru-RU")
				.setSampleRateHertz(16000)
				.build();

		// Распознаем речь
//		SpeechRecognitionResult result = speechClient.recognize(config, audio);
		RecognizeResponse result = speechClient.recognize(config, audio);

		// Получаем текст, который был распознан
//		for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
//		System.out.println("Распознанный текст: " + alternative.getTranscript());
		for (SpeechRecognitionResult alternative : result.getResultsList()) {
			System.out.println("Распознанный текст: " + alternative.getAlternatives(0).getTranscript());
		}
	}

	public static void main(String[] args) throws Exception {

		System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "/home/dav/Загрузки/bea-gs-0c911fe2e340.json");

//		Path gdAuth = Paths.get("/opt/appVol/tsm/__GD/gd.key.json");
		Path gdAuth = Paths.get("/home/dav/Загрузки/bea-gs-0c911fe2e340.json");

		SpeechRecognition recognition = new SpeechRecognition(gdAuth.toFile());
		//recognition.recognizeFromFile("path_to_your_audio_file.wav");
		recognition.recognizeFromMicrophone();
	}
}