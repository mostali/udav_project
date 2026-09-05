package udav_net_exp.uploader_to_phpserver;

import lombok.SneakyThrows;
import mpe.core.P;
import mpu.IT;
import mpc.fs.UF;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

/**
 * OkHttp sending byte[] array not works with vk Using this.
 * <p>
 * Stealed from <a
 * href="https://stackoverflow.com/a/35925747/7519767">there</a>.
 */
public class UploadFile2DomainRequest {

	@SneakyThrows
	public static void main(String[] args) throws IOException {
//		P.exit(UploadFile2DomainRequest.uploadFile("http://q.com:8080/projects/!/gt?ska=go", "i/tmp", "/home/dav/tmp/test.jpg"));
		P.exit(UploadFile2DomainRequest.uploadFile("http://zrs.su/upload.php", "i/tmp", "/home/dav/tmp/test.jpg"));
	}

	private static final Logger L = LoggerFactory.getLogger(UploadFile2DomainRequest.class);

	private HttpURLConnection httpConn;
	private DataOutputStream request;
	private final String boundary = "*****";
	private final String crlf = "\r\n";
	private final String twoHyphens = "--";

	public static void checkIsOkString(String rslt) {
		if ("ok".equalsIgnoreCase(rslt)) {
			return;
		}
		if (L.isErrorEnabled()) {
			L.error("File not upload with no ok message '{}'", rslt);
		}
		throw new IllegalStateException("File not upload");
	}

	@SneakyThrows
	public static String domainhttp(String url) {
		URL uri = new URL(url);
		return uri.getProtocol() + "://" + uri.getHost();
	}

	public static String uploadFile(String serverWithUploadPhp, String serverParentDir, String fileForUpload) {
		IT.isFileExist(fileForUpload, "uploaded file not exist");
		UploadFile2DomainRequest uploadRequest = new UploadFile2DomainRequest(serverWithUploadPhp, serverParentDir);
		File f = new File(fileForUpload);
		uploadRequest.addFilePart("uploaded_file", f);
		String uploadingOfPhotoResponseString = uploadRequest.finish();
		String rslt = STR.trimControlSymbol(uploadingOfPhotoResponseString);
		checkIsOkString(rslt);
		String file = UF.normDir(domainhttp(serverWithUploadPhp)) + UF.normDir(serverParentDir) + f.getName();
		return file;
	}

	/**
	 * This constructor initializes a new HTTP POST request with content type is
	 * set to multipart/form-data
	 */
	// https://gist.github.com/taterbase/2688850
	public UploadFile2DomainRequest(String requestURL, String parent) {

		try {
			URL url = new URL(requestURL);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			httpConn.setRequestMethod("POST");
			if (parent != null) {
				httpConn.setRequestProperty("parent", UF.normFileEnd(parent));
			}

			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Cache-Control", "no-cache");
			httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);

			request = new DataOutputStream(httpConn.getOutputStream());
		} catch (IOException ignored) {
			L.error("Error when trying to connect to the url for uploading file in multipart/form-data, url '" + requestURL + "', ignore error:", ignored);
		}
	}

	/**
	 * Adds a upload file section to the request
	 *
	 * @param fieldName  name of field in body of POST-request
	 * @param uploadFile a File to be uploaded
	 */
	public void addFilePart(String fieldName, File uploadFile) {
		try {
			String fileName = uploadFile.getName();
			request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fileName + "\"" + this.crlf);
			request.writeBytes(this.crlf);

			byte[] bytes = Files.readAllBytes(uploadFile.toPath());
			request.write(bytes);
		} catch (IOException ignored) {
			L.error("Error when adding file as multipart/form-data field. Field name is {} and file path is {}.", fieldName, uploadFile.getAbsolutePath());
		}
	}

	/**
	 * Adds a upload file section to the request
	 *
	 * @param fieldName name of field in body of POST-requestx
	 * @param bytes     an array of bytes to be uploaded
	 */
	public void addBytesPart(String fieldName, String fileName, byte[] bytes) {
		try {
			request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\";filename=\"" + fileName + "\"" + this.crlf);
			request.writeBytes(this.crlf);

			request.write(bytes);
		} catch (IOException ignored) {
			L.error("Error when adding bytes as multipart/form-data field. Field name is {} and file name is {}.", fieldName, fileName);
		}
	}

	/**
	 * Completes the request and receives response from the server.
	 *
	 * @return a list of Strings as response in case the server returned status
	 * OK, otherwise an exception is thrown.
	 */
	public String finish() {

		String response = "error";

		try {
			request.writeBytes(this.crlf);
			request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);

			request.flush();
			request.close();

			int status = httpConn.getResponseCode();

			if (status == HttpURLConnection.HTTP_OK) {
				InputStream responseStream = new BufferedInputStream(httpConn.getInputStream());

				BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

				String line;
				StringBuilder stringBuilder = new StringBuilder();

				while ((line = responseStreamReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}
				responseStreamReader.close();

				response = stringBuilder.toString();
				httpConn.disconnect();
			} else {
				L.error("Some error occured when receiving answer of sending file or bytes in multipart/form-date format: http status is {} and url is {}.", status, httpConn.getURL());
			}
		} catch (IOException ignored) {
			L.error("Some error occured when receiving answer of sending file or bytes in multipart/form-date format: {}", ignored.toString());
		}

		return response;
	}


}

// <?PHP
// if(!empty($_FILES['uploaded_file']))
// {
// $headers=getallheaders();
//
// $path ='.';
//
// if(!empty($headers['parent'])){
// $path = $headers['parent'];
// $path = $path.'/';
// if ( !empty($path) && !file_exists($path) ) {
// mkdir($path, 0777, true);
// }
// }
//
//
// $path = $path . basename( $_FILES['uploaded_file']['name']);
//
// if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $path)) {
// echo 'ok';
// // echo "The file ". basename( $_FILES['uploaded_file']['name']).
// // " has been uploaded";
// } else{
// echo "There was an error uploading the file, please try again!";
// }
// }
// ?>