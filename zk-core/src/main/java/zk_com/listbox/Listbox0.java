package zk_com.listbox;


import lombok.Getter;
import lombok.SneakyThrows;
import mpe.db.Db;
import mpe.db.JdbcUrl;
import mp.utl_odb.tree.ctxdb.ICtxDb;
import mpc.arr.S_;
import mpc.types.abstype.AbsType;
import mpe.str.CN;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARRi;
import mpv.sql_morpheus.SQLPlatform;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.*;
import zk_os.AppZosConfig;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

	public static Listbox0 fromSqliteDb(Path treeDb, String... tablename) {
		return fromSqliteDb(treeDb, Integer.MAX_VALUE, tablename);
	}

	@SneakyThrows
	public static Listbox0 fromSqliteDb(Path treeDb, int limit, String... tablename) {
		String tablename0 = ARG.toDefOrNull(tablename);
		Db db = Db.of(treeDb);
		IT.state(db.existDbSqlite());
		JdbcUrl jdbcUrl = (JdbcUrl) db.jdbcUrl;
		List<String> allTableNames = new ArrayList<>(Db.getAllTableNames(jdbcUrl, SQLPlatform.SQLITE));
		IT.NE(allTableNames, "db with empty tables");
		String tablenameTarget = tablename0 == null ? ARRi.first(allTableNames) : allTableNames.stream().filter(tn -> X.equals(tn, tablename0)).findFirst().orElse(null);
		IT.NE(tablenameTarget, "Table %s not found", tablename0);
		List<List<AbsType>> lists = db.selectAll(tablenameTarget, limit);
		Listbox0 modalCom = Listbox0.fromListList(lists, treeDb);
		return modalCom;
	}

	public static Listbox0 fromCtxDb(Path treeDb, String... tablename) {

		String tablename0 = ARG.toDefOrNull(tablename);
		ICtxDb tree = tablename0 != null ? ICtxDb.of(treeDb, tablename0) : ICtxDb.of(treeDb);
		List<List<AbsType>> maps = tree.getModelsAsMapRow(tablename0);
		Listbox0 modalCom = Listbox0.fromListList(maps, treeDb);
		return modalCom;
	}

	public static class SortListcell extends Listcell {

		private final @Getter AbsType itemCell;

		public SortListcell(AbsType itemCell, boolean... editable) {
			super(ARG.isDefEqTrue(editable) ? "" : itemCell.getValueAsString());
			this.itemCell = itemCell;
		}

		public Object getItemVal() {
			return itemCell.getValue();
		}

	}

	public static class SortListitem extends Listitem {

		public <T> T getColVal(String col) {
			List<SortListcell> collect = (List) getChildren().stream().filter(c -> c instanceof SortListcell).collect(Collectors.toList());
			Optional<SortListcell> first = collect.stream().filter(c -> c.getItemCell().name().equals(col)).findFirst();
			return (T) first.get().getItemVal();
		}

		public Integer getColValId() {
			Long colVal = getColVal(CN.ID);
			return colVal.intValue();
		}

//		@Override
//		public int compareTo(@NotNull SortListitem i2) {
//			int i = ((Comparable) getColVal(header().getLabelName())).compareTo(i2.getColVal(header().getLabelName()));
//			return ascending ? -i : i;
//		}
//
//		private SortListHeader header() {
//			return (SortListHeader) getParent();
//		}
	}

	public static class SortListhead extends Listhead {
		public Listbox0 getListbox0() {
			return (Listbox0) getParent();
		}
	}

	public static class SortListHeader extends Listheader {

		public SortListHeader(String name) {
			super(name);
		}

		public List<SortListitem> getSortItems() {
			List<Component> items = getParent().getParent().getChildren();
			List<SortListitem> listItems = (List) items.stream().filter(c -> c instanceof SortListitem).collect(Collectors.toList());
			return listItems;
		}

		public String getLabelName() {
			return getLabel();
		}
	}

	public static Listbox0 fromListList(List<List<AbsType>> rows, Path treeDb) {
		IT.notEmpty(rows, "set data");

		Listbox0 listbox = new Listbox0();

		enablePaging(listbox, rows.size(), AppZosConfig.DEF_TREE_LIMIT);

		SortListhead listhead = new SortListhead();

		listbox.appendChild(listhead);

		List<AbsType> head = rows.get(0);

		head.forEach(vl -> {

			SortListHeader listheader = new SortListHeader(vl.name());

			applySort(listheader);

			listhead.appendChild(listheader);


//			listheader.setSort("auto");
//			listheader.setSortDescending((i1, i2) -> {
//				NI.stop("wth");
//				return -1;
//			});
		});

		for (int i = 0; i < rows.size(); i++) {
			SortListitem listitem = new SortListitem();
			List<AbsType> row = rows.get(i);
			AbsType firstCell = S_.findFirst(row, cell -> "id".equals(cell.name()), null);
			if (firstCell != null) {
				listitem.setAttribute(CN.I, firstCell.val() + "");
			}
			int ri = 0;
			for (AbsType vl : row) {
				SortListcell child = newListCell(vl, treeDb, new Integer[]{i, ri++});
				listitem.appendChild(child);
			}

			listbox.appendChild(listitem);
		}

		return listbox;
	}

	private static void applySort(SortListHeader listbox) {
		listbox.setSort("auto");
		listbox.addEventListener(Events.ON_SORT, new EventListener<SortEvent>() {
			@Override
			public void onEvent(SortEvent event) throws Exception {
				SortListHeader header = (SortListHeader) event.getTarget();
				SortListhead head = (SortListhead) header.getParent();
				boolean ascending = event.isAscending();
				List<SortListitem> sortItems = header.getSortItems();
				sortItems.forEach(c -> c.detach());
				TreeSet<SortListitem> sortListitems = new TreeSet<SortListitem>((i1, i2) -> {
					Object colVal1 = i1.getColVal(header.getLabelName());
					Object colVal2 = i2.getColVal(header.getLabelName());
					if (colVal1 == null || colVal2 == null) {
						int rslt = i1.getColValId().compareTo(i2.getColValId());
						return ascending ? -rslt : rslt;
					}
					int i = ((Comparable) colVal1).compareTo(colVal2);
					if (i == 0) {
						int rslt = i1.getColValId().compareTo(i2.getColValId());
						return ascending ? -rslt : rslt;
					}
					return ascending ? -i : i;
				});
				sortItems.forEach(c -> sortListitems.add(c));
				sortListitems.forEach(c -> head.getListbox0().appendChild(c));
			}
		});
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

	public static SortListcell newListCell(AbsType vl, Path treeDb, Integer[] xyIndex) {
		if (treeDb == null) {
			return new SortListcell(vl);
		}
		SortListcell listCell = new SortListcell(vl, true);
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