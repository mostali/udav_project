package mp.utilspoi;

import lombok.SneakyThrows;
import mpe.core.P;
import mpc.str.condition.StringConditionPattern;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.nio.file.Path;
import java.util.Iterator;

public class WalkerRow {
	final Workbook book;
	final StringConditionPattern tableCondition;

	public WalkerRow(Path file, StringConditionPattern tableCondition) {
		this(UExcel.readWorkBook(file), tableCondition);

	}

	public WalkerRow(Workbook book, StringConditionPattern table) {
		this.book = book;
		this.tableCondition = table;
	}

	public boolean walk(Row row) {
		P.p(row);
		return true;
	}

	@SneakyThrows
	public WalkerRow walk() {
		try {
			walkImpl();
		} finally {
			book.close();
		}
		return this;
	}

	private void walkImpl() {
		Workbook workbook = book;
		int numberOfSheets = workbook.getNumberOfSheets();

		//loop through each of the sheets
		for (int i = 0; i < numberOfSheets; i++) {

			Sheet sheet = workbook.getSheetAt(i);

			String sheetName = sheet.getSheetName();
			if (!tableCondition.matches(sheetName)) {
				continue;
			}

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				boolean walk = walk(row);
				if (!walk) {
					break;
				}
			}
		}
	}


}
