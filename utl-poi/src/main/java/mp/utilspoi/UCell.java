package mp.utilspoi;

import mpu.X;
import org.apache.poi.ss.usermodel.*;

/**
 * @author dav 27.01.2021
 */
public class UCell {

	public static boolean isEmpty(Cell cell) {
		return cell == null || X.empty(cell.getStringCellValue());
	}

	public static boolean isNotEmpty(Cell cell) {
		return !isEmpty(cell);
	}
}
