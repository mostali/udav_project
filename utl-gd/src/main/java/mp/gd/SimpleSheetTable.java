package mp.gd;

import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpu.core.ARR;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class SimpleSheetTable {

	public static void main(String[] args) {
//		new SimpleSheetTable();
	}

	private final String sheetId, sheetName;
	private List<List> rows;

	@SneakyThrows
	public List<List> getRows() {
		if (rows != null) {
			return rows;
		}
		this.rows = getTableAs_ROWS(sheetId, sheetName);
		if (this.rows == null) {
			this.rows = ARR.asAL();
		}
		return rows;
	}

	public void write() throws IOException {
		ApiGdExt.writeValues_AuthAuto(sheetId, sheetName + "!A1:Z", rows);
	}

	private static List<List> getTableAs_ROWS(String sheetId, String tablename) throws IOException {
		ValueRange values = ApiGdExt.loadValues_AuthAuto(sheetId, tablename + "!A1:Z");
		List<List> rows = (List) values.getValues();
		return rows;
	}
}
