package mp.utl_odb.query_core;

import com.j256.ormlite.stmt.Where;
import lombok.RequiredArgsConstructor;
import mpe.core.OPR;
import mpu.IT;
import mpu.X;
import mpc.exception.WhatIsTypeException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class ApplyWhere {

	public static Where applyAnyQP(Where where, QP qp) throws SQLException {
		if (qp.isSimple()) {
			return applySimpleValue(where, qp);
		} else {
			return applyAndOrQP(where, qp);
		}
	}

	public static Where applyAndOrQP(Where where, QP qp) throws SQLException {
		QP[] qps = qp.getAndOrQps();
		Where whereQP = null;
		switch (IT.notEmpty(qps).length) {
			case 1:
				whereQP = applyAnyQP(where, qps[0]);
				break;
			default:
				List<Where> whereInners = new ArrayList<>();
				for (int i = 2; i < qps.length; i++) {
					Where whereInner = applySimpleValue(where, qps[i]);
					whereInners.add(whereInner);
				}
				Where whereLeft = applySimpleValue(where, qps[0]);
				Where whereRight = applySimpleValue(where, qps[1]);
				whereQP = applyLeftRight(where, whereLeft, whereRight, whereInners, qp.isAnd());
				break;
		}
		return whereQP;
	}

	public static Where applyLeftRight(Where where, QP left, QP right, List<QP> others, boolean andOr) throws SQLException {
		List<Where> wheres = new ArrayList<>();
		if (others != null) {
			for (QP qp : others) {
				wheres.add(applyAnyQP(where, qp));
			}
		}
		return applyLeftRight(where, applyAnyQP(where, left), applyAnyQP(where, right), wheres, andOr);
	}

	public static Where applyLeftRight(Where where, Where whereLeft, Where whereRight, List<Where> others, boolean andOr) {
		Where inner = null;
		if (andOr) {
			if (X.empty(others)) {
				inner = where.and(whereLeft, whereRight);
			} else {
				inner = where.and(whereLeft, whereRight, others.toArray(new Where[0]));
			}
		} else {
			if (X.empty(others)) {
				inner = where.or(whereLeft, whereRight);
			} else {
				inner = where.or(whereLeft, whereRight, others.toArray(new Where[0]));
			}
		}
		return inner;
	}

	public static Where applySimpleValue(Where where, QP qp) throws SQLException {
		IT.isTrue(qp.isSimple());
		OPR whereType = OPR.valueOf(qp.operatorEwhere);
		Where whereQP;
		switch (whereType) {
			case IN: {
				whereQP = where.in(qp.columnName, qp.columnValues);
				break;
			}
			case NOTIN: {
				whereQP = where.notIn(qp.columnName, qp.columnValues);
				break;
			}
			case BETWEEN: {
				Iterator<?> it = qp.columnValues.iterator();
				Object low = it.next();
				Object high = it.next();
				whereQP = where.between(qp.columnName, low, high);
				break;
			}
			case EQ: {
				whereQP = where.eq(qp.columnName, qp.columnValue);
				break;
			}
			case NEQ: {
				whereQP = where.ne(qp.columnName, qp.columnValue);
				break;
			}
			case GT: {
				whereQP = where.gt(qp.columnName, qp.columnValue);
				break;
			}
			case GE: {
				whereQP = where.ge(qp.columnName, qp.columnValue);
				break;
			}
			case LT: {
				whereQP = where.lt(qp.columnName, qp.columnValue);
				break;
			}
			case LE: {
				whereQP = where.le(qp.columnName, qp.columnValue);
				break;
			}
			case LIKE: {
				whereQP = where.like(qp.columnName, qp.columnValue);
				break;
			}
			case ISNULL: {
				whereQP = where.isNull(qp.columnName);
				break;
			}
			case ISNOTNULL: {
				whereQP = where.isNotNull(qp.columnName);
				break;
			}
			default:
				throw new WhatIsTypeException(whereType);
		}
		return whereQP;
	}

}
