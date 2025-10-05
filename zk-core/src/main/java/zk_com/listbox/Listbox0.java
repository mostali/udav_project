package zk_com.listbox;


import lombok.SneakyThrows;
import mpe.db.Db;
import mpe.db.JdbcUrl;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.arr.STREAM;
import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpv.sql_morpheus.SQLPlatform;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Listbox0 extends Listbox {
	private String oneCol;

	public Listbox0(String oneCol) {
		this.oneCol = oneCol;
		if (oneCol != null) {
			appendChild(Listbox0.newListHead(oneCol));
		}
	}

	public Listbox0() {
		this(null);
	}

	@SneakyThrows
	public static Listbox0 fromSqliteDb(Path treeDb, String... tablename) {
		String tablename0 = ARG.toDefOrNull(tablename);
		Db db = Db.of(treeDb);
		IT.state(db.existDbSqlite());
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
		enablePaging(listbox, rows.size(), 10);

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
				Listcell child = newListCell(vl, treeDb, new Integer[]{i, ri++});
				listitem.appendChild(child);
			}
			listbox.appendChild(listitem);
		}

		return listbox;
	}

	public static Listhead newListHead(String label) {
		Listhead listhead = new Listhead();
		Listheader listheader = new Listheader(label);
		listhead.appendChild(listheader);
//			listheader.setSort("auto");
		return listhead;
	}

	public void enablePaging(int size, int page) {
		enablePaging(this, size, page);
	}

	public static void enablePaging(Listbox0 listbox, int size, int page) {
		listbox.setPagingPosition("top");
		listbox.setMold("paging");
		listbox.setPageSize(page);
		int pgsz = 10;
		if (size / pgsz < 10) {
//			//listbox.setMold("paging");
//			//listbox.setMold("select");
//			//listbox.setMold("default");
			listbox.getPagingChild().setMold("os");
		}
	}

	public static Listcell newListCell(AbsType vl, Path treeDb, Integer[] xyIndex) {
		if (treeDb == null) {
			return new Listcell(vl.getValueAsString());
		}
		Listcell listCell = new Listcell();
		listCell.appendChild(new CellEditableValue(vl, treeDb, xyIndex));
		return listCell;
	}

	public static Listitem newListItem(Component com) {
		Listitem listitem = new Listitem();
		listitem.appendChild(newListCell(com));
		return listitem;
	}

	public static Listcell newListCell(Component com) {
		Listcell listCell = new Listcell();
		listCell.appendChild(com);
		return listCell;
	}

}