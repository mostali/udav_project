package mpc.net;

import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JHttp_MultipartClient_SendJson_WithRecreate {

	@SneakyThrows
	public static String sendPostWithStringAsFile_JSON_AND_RECREATE(
			String url,
			String[][] headers,
			String fileContent,
			String fileName,
			String recreateJson) {

		String boundary = "------------------------" + System.currentTimeMillis();

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		// Установка дополнительных заголовков
		if (headers != null) {
			for (String[] header : headers) {
				if (header.length >= 2) {
					conn.setRequestProperty(header[0], header[1]);
				}
			}
		}

		OutputStream outputStream = conn.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

		try {
			// === Часть 1: file (как файл с Content-Type text/xml) ===
			writer.append("--").append(boundary).append("\r\n");
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
			writer.append("Content-Type: application/json\r\n");
			writer.append("\r\n");
			writer.flush();

			if (fileContent != null) {
				outputStream.write(fileContent.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

			// === Часть 2: recreate (как строка) ===
			writer.append("--").append(boundary).append("\r\n");
			writer.append("Content-Disposition: form-data; name=\"recreate\"\r\n");
//			writer.append("Content-Type: text/xml\r\n");//????
			writer.append("\r\n");
			writer.flush();

			if (recreateJson != null) {
				outputStream.write(recreateJson.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

			// === Конец multipart ===
			writer.append("--").append(boundary).append("--\r\n");
			writer.flush();

		} finally {
			try {
				writer.close();
			} catch (Exception ignored) {
			}
			try {
				outputStream.close();
			} catch (Exception ignored) {
			}
		}

		// === Чтение ответа ===
		int responseCode = conn.getResponseCode();
		InputStream inputStream = (responseCode >= 200 && responseCode < 300)
				? conn.getInputStream()
				: conn.getErrorStream();

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line).append("\n");
			}
		}

		conn.disconnect();

		return response.toString();
	}

	@SneakyThrows
	public static String sendPostWithStringAsFile_XML_AND_RECREATE(
			String url,
			String[][] headers,
			String fileContent,
			String fileName,
			String recreateJson) {

		String boundary = "------------------------" + System.currentTimeMillis();

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		// Установка дополнительных заголовков
		if (headers != null) {
			for (String[] header : headers) {
				if (header.length >= 2) {
					conn.setRequestProperty(header[0], header[1]);
				}
			}
		}

		OutputStream outputStream = conn.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

		try {
			//
 			//
			// === Часть 1: file (как файл с Content-Type text/xml) ===
			writer.append("--").append(boundary).append("\r\n");
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
			writer.append("Content-Type: text/xml\r\n");
			writer.append("\r\n");
			writer.flush();

			if (fileContent != null) {
				outputStream.write(fileContent.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

			//
 			//
			// === Часть 2: recreate (как строка) ===
			writer.append("--").append(boundary).append("\r\n");
			writer.append("Content-Disposition: form-data; name=\"recreate\"\r\n");
//			writer.append("Content-Type: text/xml\r\n");//????
			writer.append("\r\n");
			writer.flush();

			if (recreateJson != null) {
				outputStream.write(recreateJson.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

			//
 			//
			// === Конец multipart ===
			writer.append("--").append(boundary).append("--\r\n");
			writer.flush();

		} finally {
			try {
				writer.close();
			} catch (Exception ignored) {
			}
			try {
				outputStream.close();
			} catch (Exception ignored) {
			}
		}

		// === Чтение ответа ===
		int responseCode = conn.getResponseCode();
		InputStream inputStream = (responseCode >= 200 && responseCode < 300)
				? conn.getInputStream()
				: conn.getErrorStream();

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line).append("\n");
			}
		}

		conn.disconnect();

		return response.toString();
	}

	@SneakyThrows
	public static String sendPostWithStringAsFile_JSON(String url, String[][] headers, String formDataString, String filename, List<String> sfx) {
		String boundary = "------------------------" + Long.toHexString(System.currentTimeMillis());

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");

		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		// Установка дополнительных заголовков
		if (headers != null) {
			for (String[] header : headers) {
				if (header.length >= 2) {
					conn.setRequestProperty(header[0], header[1]);
				}
			}
		}

		OutputStream outputStream = conn.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

		// Начало multipart части
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");

		writer.append("Content-Type: application/json\r\n");

		writer.append("\r\n");
		writer.flush();

		// Записываем тело файла (JSON-строка)
		if (formDataString != null) {
			outputStream.write(formDataString.getBytes(StandardCharsets.UTF_8));
		}
		outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

		// Завершение multipart
		writer.append("--").append(boundary).append("--").append("\r\n");
		writer.close();

		// Чтение ответа
		int responseCode = conn.getResponseCode();
		InputStream inputStream = (responseCode >= 200 && responseCode < 300)
				? conn.getInputStream()
				: conn.getErrorStream();

		StringBuilder response = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line).append("\n");
			}
		}

		conn.disconnect();

		return response.toString();
	}



//	// === Пример использования ===
//	public static void main(String[] args) {
//		String url = "https://httpbin.org/post"; // тестовый эндпоинт
//
//		String[][] headers = {
//				{"User-Agent", "Java-JSON-File-Client"},
//				{"Accept", "application/json"}
//		};
//
//		String jsonData = "{}";
////		String jsonData = """
////            {
////                "name": "John",
////                "age": 30,
////                "city": "New York"
////            }
////            """;
//
//		try {
//			String response = sendPostWithStringAsFile_JSON___(url, headers, jsonData, "user.json");
//			System.out.println("Response from server:");
//			System.out.println(response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}