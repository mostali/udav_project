package mpc.net;

import lombok.SneakyThrows;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.str.STR;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JHttp_MultipartClient_SendXml {

	/**
	 * Отправляет строку как файл в POST-запросе multipart/form-data
	 *
	 * @param url            URL для отправки
	 * @param headers        Дополнительные заголовки (массив строк: { {"Header-Name", "Header-Value"} })
	 * @param formDataString Содержимое файла (строка)
	 * @param filename       Имя файла (например, "enums.export.xml")
	 * @param <T>            Тип возвращаемого значения (например, String)
	 * @return Ответ сервера как строка (можно расширить под другие типы)
	 * @throws Exception При ошибках соединения, ввода-вывода
	 */
	@SneakyThrows
	public static <T> T sendPostWithStringAsFile_XML(String url, String[][] headers, String formDataString, String filename, boolean jsonOrXml) {

		String logMark = "POST" + " >> " + url + " H:" + X.sizeOf0(headers) + " D:";//+ STR.toStrLine(formDataString);
//		if (L.isTraceEnabled()) {
//			L.trace(SYMJ.ARROW_RIGHT2 + logMark + "\n" + formDataString);
//		} else if (L.isInfoEnabled()) {
		CON.L.info(SYMJ.ARROW_RIGHT2 + logMark);
//		}

		String boundary = "------------------------" + System.currentTimeMillis();

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
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

		// Пишем часть multipart: файл
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");
		writer.append("Content-Type: text/xml\r\n");
		writer.append("\r\n");
		writer.flush();

		// Пишем тело файла (строку) как байты
		outputStream.write(formDataString.getBytes(StandardCharsets.UTF_8));
		outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

		// Завершаем multipart
		writer.append("--").append(boundary).append("--").append("\r\n");
		writer.close();

		// Получаем ответ
		int responseCode = conn.getResponseCode();
		InputStream responseStream = responseCode >= 200 && responseCode < 300
				? conn.getInputStream()
				: conn.getErrorStream();

		String responseBody;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			responseBody = sb.toString();
		}

		conn.disconnect();

		// Пример: возвращаем как String, можно кастовать или парсить дальше
		@SuppressWarnings("unchecked")
		T result = (T) responseBody;
		return result;
	}

	@SneakyThrows
	public static <T> T sendPostWithStringAsFile_JSON(String url, String[][] headers, String formDataString, String filename, boolean jsonOrXml) {

		String logMark = "POST" + " >> " + url + " H:" + X.sizeOf0(headers) + " D:" + STR.toStrLine(formDataString);
		if (CON.L.isTraceEnabled()) {
			CON.L.trace(SYMJ.ARROW_RIGHT2 + logMark + "\n" + formDataString);
		} else if (CON.L.isInfoEnabled()) {
			CON.L.info(SYMJ.ARROW_RIGHT2 + logMark);
		}
		String boundary = "------------------------" + System.currentTimeMillis();

		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setDoOutput(true);
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

		// Пишем часть multipart: файл
		writer.append("--").append(boundary).append("\r\n");
		writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filename).append("\"\r\n");
		writer.append("Content-Type: application/json\r\n");
		writer.append("\r\n");
		writer.flush();

		// Пишем тело файла (строку) как байты
		outputStream.write(formDataString.getBytes(StandardCharsets.UTF_8));
		outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

		// Завершаем multipart
		writer.append("--").append(boundary).append("--").append("\r\n");
		writer.close();

		// Получаем ответ
		int responseCode = conn.getResponseCode();
		InputStream responseStream = responseCode >= 200 && responseCode < 300
				? conn.getInputStream()
				: conn.getErrorStream();

		String responseBody;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			responseBody = sb.toString();
		}

		conn.disconnect();

		// Пример: возвращаем как String, можно кастовать или парсить дальше
		@SuppressWarnings("unchecked")
		T result = (T) responseBody;
		return result;
	}
}