
# Google Sheet Java Api
Библиотека для работы с Google Sheet Java Api

## 📚 Описание
- Создайте Google-пользователя. Получите для него ФАЙЛ.json c ключом аутентфкации ( см. [документацию](https://developers.google.com/sheets/api/quickstart/java?hl=ru) )
- Установить свойство 'gs.key.path' до ключа аутентификации или 'gs.key.alias' (устаревшее)
- Создайте новый Google Sheet документ. Выдайте права пользователю

## ✅ Quick Start

```javascript

public class UdavGoogleSheetApiExample {

	static final Logger L = LoggerFactory.getLogger(UdavGoogleSheetApiExample.class);

	public static final String SHEET_ID = "<set sheetId>";
	public static final String SHEET_TABLENAME = "<set tablename>";

	public static void main(String[] args) throws Exception {
		SheetTable singleTable = new SheetTable(SHEET_ID, SHEET_TABLENAME);
		List<List<Object>> rows = singleTable.getRows();
		if (X.empty(rows)) {
			writeTable(singleTable);
		} else {
			P.p(rows);
		}
	}

	private static void writeTable(SheetTable singleTable) throws IOException {

		List head = AR.as("key", "type", "name");

		singleTable.getRows().add(head);

		List row = new LinkedList();
		row.add("k1");
		row.add("t1");
		row.add("n1");

		singleTable.getRows().add(row);

		singleTable.write();
	}

	@RequiredArgsConstructor
	static class SheetTable {

		final String sheetId, sheetName;
		List<List<Object>> rows;

		@SneakyThrows
		List<List<Object>> getRows() {
			if (rows != null) {
				return rows;
			}
			this.rows = getTableAs_ROWS(sheetId, sheetName);
			if (this.rows == null) {
				this.rows = AR.ar();
			}
			return rows;
		}

		public void write() throws IOException {
			ApiGdExt.writeValues_With_AutoAuth(sheetId, rows, sheetName + "!A1:Z");
		}
	}

    private static List<List<Object>> getTableAs_ROWS(String sheetId, String tablename) throws IOException {
		ValueRange values = ApiGdExt.loadValues_With_AutoAuth(sheetId, tablename + "!A1:Z");
		List<List<Object>> rows = values.getValues();
		return rows;
	}

}
```

