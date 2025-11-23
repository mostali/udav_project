package mp.gd;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import mp.gd.GdException.GdErrors;

public class ApiGdStuff extends ApiGdExt {

	public static GoogleScheetLoader sheet(String sheetId) {
		return new GoogleScheetLoader(sheetId);
	}

	public static void writeValues(Path fileKeyPath, String sheedId, String commonRange, List<List<Object>> values) throws IOException {
		GoogleScheetLoader sheet = sheet(sheedId);
		sheet.saveValueRange(fileKeyPath, commonRange, values);

		uploadValues(fileKeyPath, sheedId, commonRange, values);
	}

	public static class GoogleScheetLoader {
		private final String sheetId;

		public GoogleScheetLoader(String sheetId) {
			this.sheetId = sheetId;
		}

		ValueRange loadValueRange(String listName, String startCellName, String endCellName, int rowIndex)
				throws IOException {
			return loadValueRange(range(listName, startCellName, endCellName, rowIndex));
		}

		private ValueRange loadValueRange(String commonRange) throws IOException {

			Sheets service = getSheetsService(getFileKeyPath_NI());
			ValueRange response = service.spreadsheets().values().get(sheetId, commonRange).execute();

			return response;
		}

		private Path getFileKeyPath_NI() {
			throw new UnsupportedOperationException("need key path");
		}


		public void createSheet(String nameSheet) throws IOException, GdException {
			Sheets service = getSheetsService(getFileKeyPath_NI());

			List<Request> requests = new ArrayList<>();

			SheetProperties properties = new SheetProperties();
			properties.setTitle(nameSheet);

			AddSheetRequest addSheet = new AddSheetRequest();
			addSheet.setProperties(properties);

			Request r = new Request().setAddSheet(addSheet);
			requests.add(r);

			BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			try {
				BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(sheetId, body).execute();
			} catch (GoogleJsonResponseException ex) {

				if (GdErrors.SHEET_EXISTS.isHappens(ex)) {
					GdException.GdErrors.SHEET_EXISTS.ON();
				} else {
					ex.printStackTrace();
				}

			}
		}

		public void clearSheet(String nameSheet) throws IOException, GdException {
			Sheets service = getSheetsService(getFileKeyPath_NI());

			// The A1 notation of the values to clear.
			String range = range(nameSheet, "A", "Z", 1);

			ClearValuesRequest requestBody = new ClearValuesRequest();

			Sheets.Spreadsheets.Values.Clear request = service.spreadsheets().values().clear(sheetId, range,
					requestBody);

			try {
				ClearValuesResponse response = request.execute();
				mpc.log.L.info("Clear ok");
			} catch (GoogleJsonResponseException ex) {
				if (GdErrors.SHEET_EXISTS.isHappens(ex)) {
					GdErrors.SHEET_EXISTS.ON();
				} else {
					ex.printStackTrace();
				}

			}
		}

		public void create(Sheets service) throws IOException {
			List<Request> requests = new ArrayList<>();
			// Change the spreadsheet's title.

			// requests.add(new Request().setUpdateSpreadsheetProperties(new
			// UpdateSpreadsheetPropertiesRequest()
			// .setProperties(new
			// SpreadsheetProperties().setTitle("aaaaaaa")).setFields("title")));

			// Find and replace text.
			// requests.add(new Request().setFindReplace(
			// new
			// FindReplaceRequest().setFind(find).setReplacement(replacement).setAllSheets(true)));
			// Add additional requests (operations) ...

			SheetProperties properties = new SheetProperties();
			properties.setTitle("asdasd");

			AddSheetRequest addSheet = new AddSheetRequest();
			addSheet.setProperties(properties);

			Request r = new Request().setAddSheet(addSheet);
			requests.add(r);

			BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(sheetId, body).execute();
			// FindReplaceResponse findReplaceResponse =
			// response.getReplies().get(1).getFindReplace();
			// System.out.printf("%d replacements made.",
			// findReplaceResponse.getOccurrencesChanged());
		}

		public void saveValueRange(Path fileKeyPath, String commonRange, List<List<Object>> values) throws IOException {
			ApiGd.uploadValues(fileKeyPath, this.sheetId, commonRange, values);
		}
	}
}
