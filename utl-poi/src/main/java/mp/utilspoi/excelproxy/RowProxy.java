package mp.utilspoi.excelproxy;

import mpu.IT;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RowProxy implements Row {

	private final Row row;

	public RowProxy(Row row) {
		IT.stateNot(row instanceof RowProxy, "unwrap");
		this.row = row;
	}

	@Override
	public CellProxy createCell(int i) {
		return new CellProxy(row.createCell(i));
	}

	@Override
	public CellProxy createCell(int i, CellType cellType) {
		return new CellProxy(row.createCell(i, cellType));
	}

	@Override
	public void removeCell(Cell cell) {
		row.removeCell(cell);
	}

	@Override
	public void setRowNum(int i) {
		row.setRowNum(i);
	}

	@Override
	public int getRowNum() {
		return row.getRowNum();
	}

	@Override
	public Cell getCell(int i) {
		return row.getCell(i);
	}

	@Override
	public Cell getCell(int i, MissingCellPolicy missingCellPolicy) {
		return row.getCell(i, missingCellPolicy);
	}

	@Override
	public short getFirstCellNum() {
		return row.getFirstCellNum();
	}

	@Override
	public short getLastCellNum() {
		return row.getLastCellNum();
	}

	@Override
	public int getPhysicalNumberOfCells() {
		return row.getPhysicalNumberOfCells();
	}

	@Override
	public void setHeight(short i) {
		row.setHeight(i);
	}

	@Override
	public void setZeroHeight(boolean b) {
		row.setZeroHeight(b);
	}

	@Override
	public boolean getZeroHeight() {
		return row.getZeroHeight();
	}

	@Override
	public void setHeightInPoints(float v) {
		row.setHeightInPoints(v);
	}

	@Override
	public short getHeight() {
		return row.getHeight();
	}

	@Override
	public float getHeightInPoints() {
		return row.getHeightInPoints();
	}

	@Override
	public boolean isFormatted() {
		return row.isFormatted();
	}

	@Override
	public CellStyle getRowStyle() {
		return row.getRowStyle();
	}

	@Override
	public void setRowStyle(CellStyle cellStyle) {
		row.setRowStyle(cellStyle);
	}

	@Override
	public Iterator<Cell> cellIterator() {
		return row.cellIterator();
	}

	@Override
	public Sheet getSheet() {
		return row.getSheet();
	}

	@Override
	public int getOutlineLevel() {
		return row.getOutlineLevel();
	}

	@Override
	public void shiftCellsRight(int i, int i1, int i2) {
		row.shiftCellsRight(i, i1, i2);
	}

	@Override
	public void shiftCellsLeft(int i, int i1, int i2) {
		row.shiftCellsLeft(i, i1, i2);
	}

	@NotNull
	@Override
	public Iterator<Cell> iterator() {
		return row.iterator();
	}
}
