package mp.utilspoi;

import mpe.core.P;
import mpu.Sys;
import mpu.X;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * TODO: comment
 *
 * @author dav 15.09.2021   15:32
 */
public class UDocx {

	//	public static final String MONEY_FORMAT_PATTERN = "#,##0.00";
	public static final String MONEY_FORMAT_PATTERN = "#,##0.0";
	public static final String CURRENCY_FORMAT_PATTERN = "#,##0.0000";


	private static File toFile(Workbook wb, File file) {
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

	private static byte[] toBytes(Workbook wb) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			wb.write(bos);
			return bos.toByteArray();
		} catch (Exception e) {
//			L.error(e.getMessage(), e);
			return null;
		}
	}

	public static void main(String[] args) throws IOException {

//153 412 345 678 123.12
//153 200 300 400 500
//153 200 300 400 500 600


		double d = 153200300400500d;
		double d2 = 153200300400500.12d;
		double d3 = 153200300400500.12d;

		BigDecimal bd = new BigDecimal("111_222_333_444_555");
//		BigDecimal bd=BigDecimal.valueOf(153_400_000_0);
		P.exit(bd);
		File file = new File("/home/dav/Рабочий стол/ОтчетW.xls");

//		FileInputStream fis = new FileInputStream(file);
		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		Cell cell2 = row.createCell(1);
		cell.setCellValue(1.00);
		cell2.setCellValue(2.00);

		CellStyle cellStyle = cell.getCellStyle();

		final DataFormat dataFormat = wb.createDataFormat();
		short format = dataFormat.getFormat(MONEY_FORMAT_PATTERN);
		cellStyle.setDataFormat(format);
//		cell.setCellStyle(cellStyle);

		toFile(wb, file);
		X.p("file://" + file);
//		readFile();

//		readDocFile();
//		readDocxFile();


	}

	private static void readFile() throws IOException {
		File file = new File("/home/dav/Рабочий стол/Отчет.xls");
		FileInputStream fis = new FileInputStream(file);
		Workbook wb = new HSSFWorkbook(fis);

		for (Sheet sheet : wb) {
			X.p("Sheet");
			for (Row row : sheet) {
				X.p("Row");
				for (Cell cell : row) {
					X.p(cell);
				}
			}
		}
	}

	public static void readDocFile() {
		try {
			File file = new File("/home/dav/pj***********-WORK-task/309/t5/--DBD_BudgInvent_LastEdit/4 Приложение 10_2 стр. 25-418_389_ФЗ от 02.12.19.doc");
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());

			HWPFDocument document = new HWPFDocument(fis);
			Range docRange = document.getRange();

			for (int i = 0; i < docRange.numParagraphs(); i++) {
				Paragraph paragraph = docRange.getParagraph(i);

				Table table;

				try {
					table = docRange.getTable(paragraph);
				} catch (java.lang.IllegalArgumentException e) {
					Sys.p(i + "::no table::" + e.getMessage());
					continue;
				}

				int numRows = table.numRows();
				for (int j = 0; j < numRows; j++) {
					TableRow grafRow = table.getRow(0);
					if (j == 393) {
						Sys.p("go");
					}
					TableRow tableRow = table.getRow(j);
					for (int k = 0; k < tableRow.numCells(); k++) {
						TableCell cell = tableRow.getCell(k);
						TableCell grafCell = grafRow.getCell(k);
//						U.p(j + "/" + k + "::" + getTextFromCell(grafCell) + "=" + getTextFromCell(cell));
						Sys.p(j + "/" + k + "::" + grafCell.text() + "=" + cell.text());

					}
					if (j == 396) {
						Sys.exit("end");
					}
				}
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readDocxFile(File file) {
		try {
//			File file = new File("*****************_25-05-2022.docx");
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());

			XWPFDocument document = new XWPFDocument(fis);

			List<XWPFParagraph> paragraphs = document.getParagraphs();

			for (int i = 0; i < paragraphs.size(); i++) {
				XWPFParagraph paragraph = paragraphs.get(i);

//				byte[] data = paragraphs.get(239).getRuns().get(2).getEmbeddedPictures().get(0).getPictureData().getData();
//				RW.write_(Paths.get(),data,"png");
				String text = paragraph.getText();
				Sys.p("P:" + paragraph.getNumID() + ":" + paragraph.getStyleID() + ":" + text);
				Sys.p(paragraph.getPictureText());
//				System.out.println(p.getText().replaceAll("\\s++", ""));
//				String line = p.getText();
//				line = line.replaceAll(SPACE_WTF, " ");
//				line = US.removeStartString(US.removeStartString(line, " ", true), "ф. ", true);
//				line = USToken.first(line, " ");
//				if (!n33 && line.equals("33н")) {
//					n33 = true;
//				}
//				if (n33) {
//					l33n.add(line);
//				} else {
//					l191.add(line);
//				}

			}

			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		l191.remove(0);
//		l33n.remove(0);
//		U.p(l191.stream().collect(Collectors.joining(",")));
//		U.p(l33n.stream().collect(Collectors.joining(",")));
	}

	public static String getTextFromCell(TableCell cell) {
		String text = null;
		for (int m = 0; m < cell.numParagraphs(); m++) {
			for (int n = 0; n < cell.getParagraph(m).numCharacterRuns(); n++) {
				if (!cell.getParagraph(m).getCharacterRun(n).isMarkedDeleted()) {
					text = (StringUtils.isEmpty(text)) ? cell.getParagraph(m).getCharacterRun(n).text() : text + cell.getParagraph(m).getCharacterRun(n).text();
				}
			}
		}
		return prepareText(text);
	}

	public static String prepareText(String value) {
		value = StringUtils.isEmpty(value) ? StringUtils.EMPTY : value.replaceAll("[\\x07\\x0B\\r\\n\\t\\f\\s]", " ").replace((char) 160, ' ');
		value = handleHyperLink(value);
		value = replaceIncorrectSymbols(value);
		return value;
	}

	private static final String HYPERLINK = "HYPERLINK";

	public static String handleHyperLink(String value) {
		StringBuilder builder = new StringBuilder();
		if (value.contains(HYPERLINK) || value.contains("http")) {
			value = value.replaceAll(HYPERLINK, "");
			value = value.replace('\u0014', ' ');
			value = value.replace('\u0015', ' ');
			value = value.replace('\u0013', ' ');
			value = value.replace('\u0001', ' ');
			value = value.replaceAll("\\s+", " ");
			String[] strings = value.split(" ");

			for (int i = 0; i < strings.length; i++) {
				if (!strings[i].contains("ref") && !strings[i].contains("http")) { // убираем все ссылки
					builder.append(strings[i]).append(" ");
				}
			}
		} else {
			builder.append(value);
		}

		return builder.toString();
	}

	/*
	оставляем только русские буквы, цифры, знаки "+", "-", ",", "." и " "
	 */
	public static String replaceIncorrectSymbols(String value) {
		value = value.replaceAll("[^\\d+\\-,. ^а-яА-Яa-zA-Z]", "");
		return value;
	}

	/*
	оставляем только цифры, знаки "+", "-", ","
 	*/
	public static String leaveOnlyDigitsAndSomeChars(String value) {
		value = value.replaceAll("[^\\d+\\-,.]", "");
		value = value.replace(",", "."); // заменяем сразу
		return value;
	}

	/*
	оставляем только цифры
 	*/
	public static String leaveOnlyDigits(String value) {
		value = value.replaceAll("[^\\d]", "");
		return value;
	}

}
