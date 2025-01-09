package zk_com.base_ext;


import mpc.types.abstype.AbsType;
import mpu.IT;
import mpu.X;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.*;

import java.util.List;
import java.util.Map;

public class Listbox0 extends Listbox {

	public static Listbox0 fromListList(List<List<AbsType>> rows) {
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
		head.forEach(vl ->{
			Listheader listheader = new Listheader(vl.name());
			listhead.appendChild(listheader);
			listheader.setSort("auto");
		});

		for (int i = 0; i < rows.size(); i++) {
			Listitem listitem = new Listitem();
			rows.get(i).forEach(vl -> listitem.appendChild(new Listcell(vl.getValueAsString())));
			listbox.appendChild(listitem);
		}

		return listbox;
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