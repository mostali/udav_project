package mp.gd;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
//import com.google.api.services.translate.Translate;
import mpu.Sys;
import mpc.exception.RecallRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpe.rt.ValueOutStream;
import mpc.str.sym.SYMJ;
import mpu.core.ARG;
import mpu.IT;
import mpu.X;
import udav_net.wrappercall.WrapperCallAnyTc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpc.env.Env;
import mpc.exception.FIllegalArgumentException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ApiGd {

	public static final Logger L = LoggerFactory.getLogger(ApiGd.class);

	private static class Holder {
		static final ApiGd INSTANCE = new ApiGd();
	}

	public static ApiGd get() {
		return Holder.INSTANCE;
	}

	private static final String APPLICATION_NAME = "Google Sheets Service";

	public static class GoogleCredentialsFinder {

		public static Path findKeyPathRq(Path findInDir, String fileKeyName, boolean checkRunLocation, boolean checkHomeLocation) {
			Path filePath = findInDir.resolve(IT.NE(fileKeyName));
			if (Files.exists(filePath)) {
				return filePath;
			}
			//CHECK PROJECT LOCATION
			filePath = Env.RUN_LOCATION.resolve(fileKeyName);
			if (Files.exists(filePath)) {
				return filePath;
			}
			//CHECK HOME LOCATION
			filePath = Env.HOME_LOCATION.resolve(fileKeyName);
			if (Files.exists(filePath)) {
				return filePath;
			}
			throw new FIllegalArgumentException("Key [%s] not found in path [%s], checkRunLocation[%s],checkHomeLocation[%s]", fileKeyName, findInDir, checkRunLocation, checkHomeLocation);
		}

	}


	/**
	 * Global instance of the {@link FileDataStoreFactory}.
	 */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/**
	 * Global instance of the JSON factory.
	 */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/**
	 * Global instance of the HTTP transport.
	 */
	private static HttpTransport HTTP_TRANSPORT;


	/**
	 * Global instance of the scopes required by this quickstart.
	 * <p>
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	// private static final List<String> SCOPES =
	// Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	private static void initServiceVars(Path keyPath) {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(keyPath.getParent().toFile());
			if (L.isDebugEnabled()) {
				L.debug("GD: Service Credentials was initiated in dir ::: " + keyPath.getParent());
			}
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	public static Credential authorize(Path keyPath) throws IOException {

		initServiceVars(keyPath);

		InputStream keyPathIS = new FileInputStream(keyPath.toFile());

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(keyPathIS));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();

		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

		return credential;
	}

	public static Sheets getSheetsServiceCustom(Path findInDir, String fileKeyName, boolean checkRunLocation, boolean checkHomeLocation) throws IOException {
		Path fileKeyPath = GoogleCredentialsFinder.findKeyPathRq(findInDir, fileKeyName, checkRunLocation, checkHomeLocation);
		return getSheetsService(IT.isFileWithContent(fileKeyPath));
	}

	public static Sheets getSheetsService(Path fileKeyPath) throws IOException {
		Credential credential = authorize(fileKeyPath);
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
				.build();
	}

	public static void printValueRange(ValueRange response) {
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			System.out.println("No data found. Result is empty");
		} else {

			for (int i = 0; i < values.size(); i++) {
				System.out.println("Row________________:" + i);

				List<Object> row = values.get(i);
				for (Object object : row) {
					String value = object.toString();
					Sys.p(value + ", ");
				}
				Sys.p("");

			}
		}
	}

	public static String range(String listName, String startCellName, String endCellName, int rowIndex) {
		return listName + "!" + startCellName + rowIndex + ":" + endCellName;
	}

	/**
	 * *************************************************************
	 * ----------------------------- LOAD --------------------------
	 * *************************************************************
	 */

	public static ValueRange loadValues_With_FileKey(Path fileKeyPath, String sheetId, String listWithRange) throws IOException {
		return loadValues_AuthFileKey(fileKeyPath, sheetId, listWithRange, ValueRange.class);
	}

	public static <T> T loadValues_AuthFileKey(Path fileKeyPath, String sheetId, String listWithRange, Class<T> returnAsType) throws IOException {
		if (L.isInfoEnabled()) {
			if (L.isDebugEnabled()) {
				L.debug(SYMJ.JET + "loadValues_With_FileKey from spreadsheet [{}/{}], gd-key=file://{}", sheetId, listWithRange, fileKeyPath);
			} else {
				L.info(SYMJ.JET + "loadValues_With_FileKey from spreadsheet [{}/{}]", sheetId, listWithRange);
			}
		}
		Sheets service3 = getSheetsService(fileKeyPath);
		Sheets.Spreadsheets.Values.Get get = service3.spreadsheets().values().get(sheetId, listWithRange);

		return new WrapperCallAnyTc<T, Exception>() {
			@Override
			public T callImpl() throws Exception {
				try {
					HttpResponse httpResponse = get.executeUnparsed();
					if (returnAsType == null || HttpResponse.class.isAssignableFrom(returnAsType)) {
						return (T) httpResponse;
					} else if (returnAsType == ValueRange.class) {
						return (T) httpResponse.parseAs(ValueRange.class);
					} else if (Collection.class.isAssignableFrom(returnAsType)) {
						return (T) httpResponse.parseAs(ValueRange.class).getValues();
					} else if (CharSequence.class.isAssignableFrom(returnAsType)) {
						return (T) ValueOutStream.of(httpResponse.getContent()).getValue();
					} else {
						throw new WhatIsTypeException(returnAsType);
					}
				} catch (WhatIsTypeException ex) {
					throw ex;
				} catch (Exception ex) {
					L.error("Happens error executing request to get GData, try recall (tc=" + getTotalTc() + ")", ex);
					throw new RecallRuntimeException(ex);
				}

			}
		}.setLogger(L).call();
	}

	public static Spreadsheet loadSpreadsheet(Path fileKeyPath, String sheetId, List<String> ranges, boolean... isIncludeData) throws IOException {
		boolean _isIncludeData = ARG.isDefEqTrue(isIncludeData);
		if (L.isInfoEnabled()) {
			if (L.isDebugEnabled()) {
				L.debug(SYMJ.JET + "loadValues_With_FileKey from spreadsheet [{}/{}], include-data:{}, gd-key [{}]", sheetId, ranges, _isIncludeData, fileKeyPath);
			} else {
				L.info(SYMJ.JET + "loadValues_With_FileKey from spreadsheet [{}/{}], include-data:{}", sheetId, _isIncludeData, ranges);
			}
		}
		Sheets service3 = getSheetsService(fileKeyPath);
//			Sheets.Spreadsheets.Get get = service3.spreadsheets().get(sheetId).setIncludeGridData(true).setRanges(Arrays.asList("@!G6")).setFields("sheets.data.rowData.values.userEnteredValue");
		Sheets.Spreadsheets.Get get = service3.spreadsheets().get(sheetId);
		if (_isIncludeData) {
			get.setIncludeGridData(true);
		}
		if (X.notEmpty(ranges)) {
			get.setRanges(ranges);
		}
//			Sheets.Spreadsheets.Values.Get get = service3.spreadsheets().values()
//					.get(sheetId, listWithRange);
//			get.getUnknownKeys().put("includeGridData", "true");
//			ValueRange response3 = get.execute();
		Spreadsheet response3 = get.execute();
		return response3;
	}


	/**
	 * *************************************************************
	 * ---------------------------- WRITE --------------------------
	 * *************************************************************
	 */

	public static void uploadValues(Path fileKeyPath, String sheetId, String commonRange, List<List<Object>> values) throws IOException {
		if (L.isInfoEnabled()) {
			L.info(SYMJ.JET + "GD: UPLOAD: Writing sheet:{} values:{} range:{}", sheetId, values.size(), commonRange);
		}
		ValueRange body = new ValueRange().setValues(values);
		Sheets service = getSheetsService(fileKeyPath);
		UpdateValuesResponse result = service.spreadsheets().values().update(sheetId, commonRange, body).setValueInputOption("USER_ENTERED").execute();// "RAW"
	}

	/**
	 * *************************************************************
	 * ---------------------------- CLEAR --------------------------
	 * *************************************************************
	 */
	public static void clearValues(Path fileKeyPath, String sheetId, String commonRange) throws IOException {
		if (L.isInfoEnabled()) {
			L.info(SYMJ.JET + "GD: CLEAR: Writing sheet:{} range:{}", sheetId, commonRange);
		}
		Sheets service = getSheetsService(fileKeyPath);
		service.spreadsheets().values().clear(sheetId, commonRange, new ClearValuesRequest()).execute();
	}
}
