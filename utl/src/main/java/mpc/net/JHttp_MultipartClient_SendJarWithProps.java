package mpc.net;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class JHttp_MultipartClient_SendJarWithProps {

	// Пример использования
	public static void main(String[] args) {
		try {
			Path jarFile = Path.of("..");
			String url = "url";

			String[] headers = {"Authorization", "955339da-ea52-47e1-9339-daea5237e13b"};

			HttpResponse<String> response = sendJarFile(
					url, headers,
					jarFile,
					"mg297.jar", "mg297",
					"mg297"
			);

			System.out.println("Status Code: " + response.statusCode());
			System.out.println("Response Body: " + response.body());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SneakyThrows
	public static HttpResponse<String> sendJarFile(
			String url, String[] authHeader,
			Path filePath,
			String filename, String version,
			String description)  {

		// Генерируем уникальную границу для multipart
		String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		String lineEnd = "\r\n";

		// Читаем содержимое файла
		byte[] fileContent = Files.readAllBytes(filePath);

		// Собираем тело запроса вручную (как multipart/form-data)
		StringBuilder bodyBuilder = new StringBuilder();

		// Поле "content" — файл
		bodyBuilder.append("--").append(boundary).append(lineEnd);
		bodyBuilder.append("Content-Disposition: form-data; name=\"content\"; filename=\"")
				.append(filename).append("\"").append(lineEnd);
		bodyBuilder.append("Content-Type: application/x-java-archive").append(lineEnd);
		bodyBuilder.append(lineEnd); // Пустая строка перед телом файла

		String beforeFile = bodyBuilder.toString();
		bodyBuilder.setLength(0); // Очищаем для следующих частей

		// Поле "version"
		bodyBuilder.append("--").append(boundary).append(lineEnd);
		bodyBuilder.append("Content-Disposition: form-data; name=\"version\"").append(lineEnd);
		bodyBuilder.append(lineEnd).append(version).append(lineEnd);

		// Поле "description"
		bodyBuilder.append("--").append(boundary).append(lineEnd);
		bodyBuilder.append("Content-Disposition: form-data; name=\"description\"").append(lineEnd);
		bodyBuilder.append(lineEnd).append(description).append(lineEnd);

		// Поле "filename"
		bodyBuilder.append("--").append(boundary).append(lineEnd);
		bodyBuilder.append("Content-Disposition: form-data; name=\"filename\"").append(lineEnd);
		bodyBuilder.append(lineEnd).append(filename).append(lineEnd);

		// Завершающая граница
		bodyBuilder.append("--").append(boundary).append("--").append(lineEnd);

		String afterFile = bodyBuilder.toString();

		// Собираем всё тело запроса в байты
		byte[] beforeFileBytes = beforeFile.getBytes();
		byte[] afterFileBytes = afterFile.getBytes();

		int totalLength = beforeFileBytes.length + fileContent.length + afterFileBytes.length;
		byte[] requestBody = new byte[totalLength];

		System.arraycopy(beforeFileBytes, 0, requestBody, 0, beforeFileBytes.length);
		System.arraycopy(fileContent, 0, requestBody, beforeFileBytes.length, fileContent.length);
		System.arraycopy(afterFileBytes, 0, requestBody, beforeFileBytes.length + fileContent.length, afterFileBytes.length);

		// Создаём HTTP-клиент и запрос
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(url))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.header(authHeader[0], authHeader[1])
				.POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
				.build();

		// Отправляем запрос
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}


}