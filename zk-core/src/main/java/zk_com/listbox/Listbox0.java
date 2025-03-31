package zk_com.listbox;


import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mp.utl_ndb.JdbcUrl;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.arr.STREAM;
import mpc.exception.FIllegalStateException;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpv.sql_morpheus.SQLPlatform;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Listbox0 extends Listbox {

	@SneakyThrows
	public static Listbox0 fromSqliteDb(Path treeDb, String... tablename) {
		String tablename0 = ARG.toDefOrNull(tablename);
		Db db = Db.of(treeDb);
		IT.state(db.existDb());
		JdbcUrl jdbcUrl = (JdbcUrl) db.jdbcUrl;
		List<String> allTableNames = new ArrayList<>(Db.getAllTableNames(jdbcUrl, SQLPlatform.SQLITE));
		IT.NE(allTableNames, "db with empty tables");
		String tablenameTarget = tablename0 == null ? ARRi.first(allTableNames) : allTableNames.stream().filter(tn -> X.equals(tn, tablename0)).findFirst().orElse(null);
		IT.NE(tablenameTarget, "Table %s not found", tablename0);
		List<List<AbsType>> lists = db.selectAll(tablenameTarget);
		Listbox0 modalCom = Listbox0.fromListList(lists, treeDb);
		return modalCom;
//		List<List<AbsType>> maps = STREAM.mapToList(allTableNames, s -> ARR.as(AbsType.of("table", s)));
//		Listbox0 modalCom = Listbox0.fromListList(maps, treeDb);
//		return modalCom;
	}

	public static Listbox0 fromCtxDb(Path treeDb, String... tablename) {
		String tablename0 = ARG.toDefOrNull(tablename);
		ICtxDb tree = tablename0 != null ? ICtxDb.of(treeDb, tablename0) : ICtxDb.of(treeDb);
		List<List<AbsType>> maps = tree.getModelsAsMapRow(tablename0);
		Listbox0 modalCom = Listbox0.fromListList(maps, treeDb);
		return modalCom;
	}

	public static Listbox0 fromListList(List<List<AbsType>> rows, Path treeDb) {
		IT.notEmpty(rows, "set data");

		Listbox0 listbox = new Listbox0();
		listbox.setMold("paging");
		int size = rows.size();
		int pgsz = 10;

		if (size > pgsz) {
			listbox.setPageSize(pgsz);
			listbox.setPagingPosition("top");
			if (size / pgsz < 10) {
				//listbox.setMold("paging");
				//listbox.setMold("select");
				//listbox.setMold("default");
				listbox.getPagingChild().setMold("os");
			}
		}

		Listhead listhead = new Listhead();

		listbox.appendChild(listhead);

		List<AbsType> head = rows.get(0);
		head.forEach(vl -> {
			Listheader listheader = new Listheader(vl.name());
			listhead.appendChild(listheader);
			listheader.setSort("auto");
		});

		for (int i = 0; i < rows.size(); i++) {
			Listitem listitem = new Listitem();
			List<AbsType> row = rows.get(i);
			AbsType firstCell = STREAM.findFirst(row, cell -> "id".equals(cell.name()), null);
			if (firstCell != null) {
				listitem.setAttribute("i", firstCell.val() + "");
			}
			int ri = 0;
			for (AbsType vl : row) {
				Component child = newListCell(vl, treeDb, new Integer[]{i, ri++});
				listitem.appendChild(child);
			}
			listbox.appendChild(listitem);
		}

		return listbox;
	}

	private static Component newListCell(AbsType vl, Path treeDb, Integer[] xyIndex) {
		if (treeDb == null) {
			return new Listcell(vl.getValueAsString());
		}
		Listcell listCell = new Listcell();
		listCell.appendChild(new CellEditableValue(vl, treeDb, xyIndex));
		return listCell;
	}


//	public static Table createTableFromMap(Map<String, Object> row) {
//		// Создаем новую таблицу
//		Table table = new Table();
//
//		// Добавляем заголовки столбцов
//		for (String key : row.keySet()) {
//			table.add
//			TableColumn column = new TableColumn(key);
//			table.appendChild(column);
//		}
//
//		// Создаем строку данных
//		TableRow dataRow = new TableRow();
//		for (Object value : row.values()) {
//			dataRow.appendChild(new TableCell(value.toString()));
//		}
//
//		// Добавляем строку данных в таблицу
//		table.appendChild(dataRow);
//
//		return table;
//	}
}