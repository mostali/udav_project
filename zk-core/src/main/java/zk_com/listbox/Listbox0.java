package zk_com.listbox;


import lombok.SneakyThrows;
import mp.utl_ndb.Db;
import mp.utl_ndb.JdbcUrl;
import mp.utl_odb.tree.UTree;
import mpc.arr.STREAM;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpv.sql_morpheus.SQLPlatform;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Listbox0 extends Listbox {

	@SneakyThrows
	public static Listbox0 fromSqliteDb(Path treeDb) {
		Db db = Db.of(treeDb);
		IT.state(db.existDb());
		JdbcUrl jdbcUrl = (JdbcUrl) db.jdbcUrl;
		List<String> allTableNames = new ArrayList<>(Db.getAllTableNames(jdbcUrl, SQLPlatform.SQLITE));
		for (int i = 0; i < allTableNames.size(); i++) {
			String tableName = allTableNames.get(i);
			List<List<AbsType>> lists = db.selectAll(tableName);
			Listbox0 modalCom = Listbox0.fromListList(lists, treeDb);
			//return first
			return modalCom;
		}
		List<List<AbsType>> maps = STREAM.mapToList(allTableNames, s -> ARR.as(AbsType.of("table", s)));
		Listbox0 modalCom = Listbox0.fromListList(maps, treeDb);
		return modalCom;
	}

	public static Listbox0 fromTreeDb(Path treeDb) {
		List<List<AbsType>> maps = UTree.tree(treeDb).getModelsAsMapRow();
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
			;
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