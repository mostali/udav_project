package mpf.ngrid;

import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;
import mpu.pare.Tuple;

import java.util.Collection;
import java.util.List;

public interface NRowItem<I> {
	I item();

	default List<List> toValuesRows() {
		I item = item();
		if (item == null) {
			return ARR.EMPTY_LIST;
		} else if (item instanceof Collection) {
			return ARR.as(ARR.toList((Collection) item));
		} else if (item instanceof Tuple) {
			return ARR.as(((Tuple) item).toValuesString());
		}
		throw new WhatIsTypeException("Unsupported  item to row -> %s", item);
	}
}
