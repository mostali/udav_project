package app_poi;

import lombok.SneakyThrows;
import mpc.exception.FIllegalArgumentException;
import mpc.fs.UF;
import mpc.log.L;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

public class Wb0 extends MatrixWb {

	public static void main(String[] args) {
		List<List> lists = (List) ARR.as2("asd", "asd");
		writeSheetToFile(Paths.get("/tmp/ttt.xlsx"), lists, "0");
	}

	public static void writeSheetToFile(Path toFileXlsx, List<List> ll, String sheetName) {
		Writer.writeSheetToFile(toFileXlsx, ll, sheetName);
		if (L.isDebugEnabled()) {
			L.info("Write poi {} sheet {}, values\n", UF.ln(toFileXlsx), sheetName, ll);
		} else if (L.isInfoEnabled()) {
			L.info("Write poi {} sheet {} *{}", UF.ln(toFileXlsx), sheetName, X.sizeOf(ll));
		}
	}

	@SneakyThrows
	public static Workbook of(Path path) {
		String fileName = path.getFileName().toString();
		InputStream is = Files.newInputStream(path);
//		try {
		if (fileName.endsWith(".xlsx")) {
			return new XSSFWorkbook(is);
		} else if (fileName.endsWith(".xls")) {
			return new HSSFWorkbook(is);
		} else {
			throw new IllegalArgumentException("Неподдерживаемый формат: " + fileName);
		}
//		} finally {
		// Не закрываем InputStream здесь, так как Workbook может его использовать
		// Workbook закроет поток при своем закрытии
//		}
	}

//	@SneakyThrows
//	public static Workbook readWorkBook(Path file) {
//		FileInputStream fis = new FileInputStream(IT.isFileExist(file).toFile());
//		Workbook workbook = null;
//		String filename = file.getFileName().toString().toLowerCase();
//		if (filename.endsWith("xlsx")) {
//			workbook = new XSSFWorkbook(fis);
//		} else if (filename.endsWith("xls")) {
//			workbook = new HSSFWorkbook(fis);
//		} else {
//			throw new FIllegalArgumentException("What is Excel file '%s'", file);
//		}
//		return workbook;
//	}

	public static class Writer {
		@SneakyThrows
		public static void writeSheetToFile(Path toFileXlsx, List<List> ll, String sheetName) {
			if (ll == null || ll.isEmpty()) {
				throw new IllegalArgumentException("Matrix is null or empty");
			}

			Workbook workbook;
			if (Files.exists(toFileXlsx)) {
				try (FileInputStream fis = new FileInputStream(toFileXlsx.toFile())) {
					workbook = WorkbookFactory.create(fis);
				}
			} else {
				workbook = new XSSFWorkbook();
			}

			// Удаляем существующий лист если есть
			Sheet existingSheet = workbook.getSheet(sheetName);
			if (existingSheet != null) {
				workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
			}

			Sheet sheet = workbook.createSheet(sheetName);

			for (int y = 0; y < ll.size(); y++) {
				Row row = sheet.createRow(y);
				List rowData = ll.get(y);
				if (rowData != null) {
					for (int x = 0; x < rowData.size(); x++) {
						Cell cell = row.createCell(x);
						Object value = rowData.get(x);
						if (value == null) {
							cell.setCellValue("");
						} else {
							cell.setCellValue(value.toString());
						}
					}
				}
			}

			try (FileOutputStream outputStream = new FileOutputStream(toFileXlsx.toFile())) {
				workbook.write(outputStream);
			}
			workbook.close();
		}
	}

	final Workbook workbook;

	public Wb0(Workbook workbook) {
		this.workbook = workbook;
	}

	public static File toFile(Workbook wb, File file) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddmm");
//		String filename = "target/workbookReport" + simpleDateFormat.format(new Date()) + ".xls";
		try {
			OutputStream fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] toBytes(Workbook wb) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			wb.write(bos);
			return bos.toByteArray();
		} catch (Exception e) {
//			L.error(e.getMessage(), e);
			return null;
		}
	}
}
