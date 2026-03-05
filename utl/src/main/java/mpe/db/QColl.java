package mpe.db;

import mpc.exception.WhatIsTypeException;
import mpc.types.abstype.AbsType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QColl<T> {
	final Boolean asList_orMap;

	final List<T> rowAsList;

	final Map<String, T> rowAsMap;

	public QColl(boolean asList_orMap) {
		this.asList_orMap = asList_orMap;
//			List<AbsType> rowAsList = null;
		if (asList_orMap) {
			rowAsList = new ArrayList<>();
			rowAsMap = null;
		} else {
			rowAsMap = new LinkedHashMap<>();
			rowAsList = null;
		}

		this.qMapResult = null;

	}

	final Db.QMapResult qMapResult;

	public QColl(Db.QMapResult qMapResult) {

		asList_orMap = null;
		this.qMapResult = qMapResult;

		switch (qMapResult) {
			case LIST_OBJS:
			case LIST_ABSTYPE:
			case SEQ:
				rowAsList = new ArrayList<>();
				rowAsMap = null;
				break;
			case MAP_OBJS:
			case MAP_ABS_TYPE:
				rowAsMap = new LinkedHashMap<>();
				rowAsList = null;
				break;
			default:
				throw new WhatIsTypeException(qMapResult);

		}

	}

	public void add(AbsType val) {
		if (asList_orMap != null) {
			addOLD(val, asList_orMap);
		} else {
			addNew(val);
		}
	}

	private void addNew(AbsType val) {
		switch (qMapResult) {
			case SEQ:
				rowAsList.add((T) val.val());
				return;
			case MAP_ABS_TYPE:
			case MAP_OBJS:
				addOLD(val, false);
				return;
			case LIST_ABSTYPE:
			case LIST_OBJS:
				addOLD(val, true);
				return;
			default:
				throw new WhatIsTypeException(qMapResult);

		}
	}

	private void addOLD(AbsType val, boolean asList_orMap) {
		if (asList_orMap) {
			rowAsList.add((T) val);
		} else {
			rowAsMap.put(val.name(), (T) val);
		}
	}
}
