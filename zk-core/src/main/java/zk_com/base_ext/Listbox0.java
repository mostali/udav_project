package zk_com.base_ext;


import mp.utl_odb.query_core.QP;
import mp.utl_odb.tree.CtxtDb;
import mp.utl_odb.tree.UTree;
import mpc.exception.WhatIsTypeException;
import mpc.types.abstype.AbsType;
import mpe.core.U;
import mpe.ftypes.core.FDate;
import mpu.IT;
import mpu.core.ARRi;
import mpu.core.QDate;
import mpu.str.UST;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;
import zk_com.editable.EditableValue;
import zk_form.notify.ZKI;

import java.nio.file.Path;
import java.util.List;

public class Listbox0 extends Listbox {

	public static Listbox0 fromDb(Path treeDb, boolean persistence) {
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
			AbsType firstCol = row.get(0);
			if ("id".equals(firstCol.name())) {
				listitem.setAttribute("i", firstCol.val() + "");
			}
			row.forEach(vl -> {
				Component child = newListCell(vl, treeDb);
				if (child != null) {
					listitem.appendChild(child);
				}
			});
			listbox.appendChild(listitem);
		}

		return listbox;
	}

	private static Component newListCell(AbsType vl, Path treeDb) {
		if (treeDb == null) {
			return new Listcell(vl.getValueAsString());
		}
//		if("id".equals(vl.name())){
//			return null;
//		}
		Listcell listCell = new Listcell();
		listCell.appendChild(new EditableValue(vl.getValueAsString()) {

			private Long getModelId() {
				return UST.LONG((String) getParent().getParent().getAttribute("i"));
			}

			CtxtDb.CtxTimeModel getModel() {
				List<CtxtDb.CtxTimeModel> models = treeDb().getModels(QP.pID(getModelId()));
				CtxtDb.CtxTimeModel first = ARRi.first(models);
				return first;
			}

			private UTree treeDb() {
				return UTree.tree(treeDb);
			}


			@Override
			protected void onUpdatePrimaryText(String newValue) {

				CtxtDb.CtxTimeModel model = getModel();

				boolean isNull = U.is__NULL__(newValue);
				if (isNull) {
					newValue = null;
				}
				switch (vl.name()) {
					case "id": {
//						model().setId(ind);
						Long ind = getModelId();
						ZKI.alert("primary key " + ind);
						return;
//						break;
					}
					case "key": {
						model.setKey(newValue);
						break;
					}
					case "val": {
						model.setValue(newValue);
						break;
					}
					case "ext": {
						model.setExt(newValue);
						break;
					}
					case "time": {
						if (newValue == null) {
							ZKI.alert("Illegal state with NULL and time field");
							return;
						} else {
							QDate ms = QDate.of(newValue, FDate.UTC_MS);
							model.setTime(ms.getTime());
						}
						break;
					}
					default:
						throw new WhatIsTypeException(vl.name());
				}

				treeDb().saveModelAsUpdate(model);

				ZKI.infoAfterPointer("Update row #" + model.getId(), ZKI.Level.INFO);

				super.onUpdatePrimaryText(isNull ? "null" : newValue);
			}


		});
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