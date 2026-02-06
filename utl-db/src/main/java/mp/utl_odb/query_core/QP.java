package mp.utl_odb.query_core;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.Where;
import mpc.arr.STREAM;
import mpe.str.CN;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARG;
import mpe.core.OPR;
import mpu.IT;
import mpu.core.QDate;
import mpu.core.UTime;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

//https://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/stmt/Where.html
public class QP {

	public static final QP[] EMPTY = new QP[0];

	public final String columnName;
	public final Object columnValue;
	public final Iterable<?> columnValues;

	public String operatorEwhere = null;

	public Boolean isNullOrNotNull = null;

	private QP[] qpsOr = null;
	private QP[] qpsAnd = null;

	public static String likeWrapVal(String keyPart) {
		return "%" + keyPart + "%";
	}

	public static QP[] merge(QP qp, QP... add) {
		return ARR.merge(new QP[]{qp}, add);
	}

	public static QP[] merge(QP[] qps, QP... add) {
		return qps == null || qps.length == 0 ? add : (add == null || add.length == 0 ? qps : ARR.merge(qps, add));
	}

	public static QP or(String colName, Object... args) {
		QP[] qps = new QP[args.length];
		for (int i = 0; i < qps.length; i++) {
			Object arg = args[i];
			qps[i] = QP.param(colName, arg, OPR.EQ);
		}
		return or(qps);
	}

	public static QP or(QP... qps) {
		return new QP(false, qps);
	}

	public static QP and(String colName, Object... args) {
		QP[] qps = new QP[args.length];
		for (int i = 0; i < qps.length; i++) {
			Object arg = args[i];
			qps[i] = QP.param(colName, arg, OPR.EQ);
		}
		return and(qps);
	}

	public static QP and(QP... qps) {
		return new QP(true, qps);
	}

	public static QP[] afterOrBeforeOrEq(Boolean afterBeforeEq, String colname, Object value) {
		IT.NE(colname);
		if (afterBeforeEq == null) {
			return new QP[]{value == null ? QP.pNULL(colname, true) : QP.pEQ(colname, IT.NN(value))};
		} else if (afterBeforeEq) {
			return new QP[]{QP.pGT(colname, IT.NN(value))};
		} else {
			return new QP[]{QP.orderAscOrDescOrRand(colname, false), QP.pLT(colname, IT.NN(value))};
		}
	}

	@Override
	public String toString() {
		return "QP{" + "cn='" + columnName + '\'' + (columnValue == null ? "" : ", cv=" + columnValue) + (columnValues == null ? "" : ", cv's=" + columnValues) + ", where='" + operatorEwhere + '\'' + (qpsOr == null ? "" : ", qpsOr=" + Arrays.toString(qpsOr)) + (qpsAnd == null ? "" : ", qpsAnd=" + Arrays.toString(qpsAnd)) + (isNullOrNotNull == null ? "" : ", N_NN=" + isNullOrNotNull) + (paramOrder == null ? "" : ", ord=" + Arrays.toString(paramOrder)) + (paramLimit == null ? "" : ", lmt=" + paramLimit) + (paramOffset == null ? "" : ", oft=" + paramOffset) + (paramDistinct == null ? "" : ", dst=" + paramDistinct) + '}';
	}

	public static List<QP> ofMap(Map<String, String> eqs) {
		List<QP> l = new ArrayList<>();
		for (Map.Entry<String, String> e : eqs.entrySet()) {
			QP param = QP.param(e.getKey(), e.getValue());
			l.add(param);
		}
		return l;
	}

	private QP(Long limit, Long offset) {
		this.columnName = null;
		this.columnValue = null;
		this.columnValues = null;
		this.paramLimit = limit;
		this.paramOffset = offset;
	}

	private QP(DistinctParam paramDistinct) {
		this.columnName = null;
		this.columnValue = null;
		this.columnValues = null;
		this.paramDistinct = paramDistinct;
	}

	private QP(OrderParam... param) {
		this.columnName = null;
		this.columnValue = null;
		this.columnValues = null;
		this.paramOrder = param;
	}

	private QP(String param) {
		this(param, null);
	}

	protected QP(String colName, Object columnValue) {
		this(colName, columnValue, null);
	}

	private QP(String colName, Object columnValue, Iterable<?> columnValues) {
		this.columnName = colName;
		this.columnValue = columnValue;
		this.columnValues = columnValues;
		this.paramOrder = null;
	}

	private QP(boolean andOr, QP... qps) {
		this.columnName = null;
		this.columnValue = null;
		this.columnValues = null;
		this.paramOrder = null;
		if (andOr) {
			qpsAnd = qps;
		} else {
			qpsOr = qps;
		}
	}

	public static QP p(String colName, Object columnValue) {
		return param(colName, columnValue);
	}

	public static QP param(String colName, Object columnValue) {
		return param(colName, columnValue, OPR.EQ);
	}

	//
	//
	public static QP pID(int id) {
		return param(CN.ID, id, OPR.EQ);
	}

	public static QP pID(long id) {
		return param(CN.ID, id, OPR.EQ);
	}

	public static QP pKEY(String columnValue) {
		return param(CN.KEY, columnValue, OPR.EQ);
	}

	public static QP pTYPE(String columnValue) {
		return param(CN.TYPE, columnValue, OPR.EQ);
	}

	public static QP pSTATE(Object state) {
		return param(CN.STATE, state, OPR.EQ);
	}

	public static QP pSTATUS(Long columnValue) {
		return param(CN.STATUS, columnValue, OPR.EQ);
	}

	public static QP pNAME(Object columnValue) {
		return param(CN.NAME, columnValue, OPR.EQ);
	}

	public static QP pVALUE(Object columnValue) {
		return param(CN.VALUE, columnValue, OPR.EQ);
	}

	public static QP pHC(Object columnValue) {
		return param(CN.HC, columnValue, OPR.EQ);
	}

	public static QP pUID(long uid) {
		return param(CN.UID, uid, OPR.EQ);
	}

	public static QP pUIDL(long uidl) {
		return param(CN.UIDL, uidl, OPR.EQ);
	}

	public static QP pGUID(String guid) {
		return param(CN.GUID, guid, OPR.EQ);
	}

	public static QP pDATE(java.sql.Date columnValue) {
		return param(CN.DATE, columnValue, OPR.EQ);
	}

	public static QP pDT(java.sql.Date columnValue) {
		return param(CN.DT, columnValue, OPR.EQ);
	}

	public static QP pDATA(String columnValue) {
		return param(CN.DATA, columnValue, OPR.EQ);
	}

	//
	//
	public static QP paramNot(String colName, Object columnValue) {
		columnValue = columnValue == null ? "" : columnValue;
		return param(colName, columnValue, OPR.NEQ);
	}

	public static QP pNULL(String colName, boolean... isNullOrNotNull) {
		return new QP(colName).setWhere(ARG.isDefEqTrue(isNullOrNotNull) ? OPR.ISNULL : OPR.ISNOTNULL);
	}

	public static @NotNull QP likeSEcomma(String colName, String... values) {
		return likeSE(colName, ", ", ", ", values);
	}

	public static @NotNull QP likeSE(String colName, String pfx, String sfx, String... values) {
		Set<String> setVl = IT.NE(Arrays.stream(IT.NE(values)).filter(X::NE).collect(Collectors.toSet()));
		List<String> allLikes = setVl.stream().map(vl -> ARR.as(vl + sfx + "%", "%" + pfx + vl + sfx + "%", "%" + pfx + vl)).flatMap(List::stream).collect(Collectors.toList());
		QP[] allLikesArr = STREAM.mapToList(allLikes, lVl -> QP.like(colName, lVl)).toArray(new QP[allLikes.size()]);
		QP[] eq = setVl.stream().map(vl -> QP.pEQ(colName, vl)).toArray(QP[]::new);
		return QP.or(ARR.addElements(allLikesArr, eq));
	}

	public static QP _like_(String colName, Object columnValue) {
		return param(colName, "%" + columnValue + "%", OPR.LIKE);
	}

	public static QP like(String colName, Object columnValue) {
		return param(colName, columnValue, OPR.LIKE);
	}

	public static QP param(String colName, Object columnValue, OPR where) {
		return new QP(colName, columnValue).setWhere(where);
	}

	public static QP paramNotIn(String colName, Iterable<?> columnValues) {
		return paramNotIn(colName, columnValues, OPR.NOTIN);
	}

	public static QP paramNotIn(String colName, Iterable<?> columnValues, OPR where) {
		return new QP(colName, null, columnValues).setWhere(where);
	}

	public static QP paramIn(String colName, Iterable<?> columnValues) {
		return paramIn(colName, columnValues, OPR.IN);
	}

	public static QP paramIn(String colName, Iterable<?> columnValues, OPR where) {
		return new QP(colName, null, columnValues).setWhere(where);
	}

	public static QP paramBetweenSingleDay(String colName, QDate day, Class useDateType) {
		Object[] period = UTime.rangeBetween(day, Calendar.DAY_OF_MONTH, useDateType);
		return paramBetween(colName, period[0], period[1]);
	}

	public static QP paramBetweenDate(String colName, java.sql.Date d1, java.sql.Date d2) {
		return paramBetween(colName, d1, d2);
	}

	public static QP paramBetween(String colName, Object low, Object high) {
		return paramBetween(colName, low, high, OPR.BETWEEN);
	}

	public static QP paramDateM14(String colName, String dateM14, OPR where) {
		return paramDate(colName, QDate.ofMono14(dateM14).toDate(), where);
	}

	public static QP paramDate(String colName, Date date, OPR where) {
		return QP.param(colName, QDate.SqlDate.toSqlDate(date), where);
	}

	public static QP paramBetween(String colName, Object low, Object high, OPR where) {
		return new QP(colName, null, Arrays.asList(low, high)).setWhere(where);
	}

	private QP setWhere(OPR where) {
		operatorEwhere = where.name();
		return this;
	}

	public static void apply(QueryBuilder<?, String> queryBuilder, QP... qps) throws SQLException {
		List<QP> eqps = new ArrayList();
		for (QP ep : qps) {
			if (ep.isOrderType()) {
				for (OrderParam order : ep.getOrderParam()) {
					if (order != null) {
						order.apply(queryBuilder);
					}
				}
				continue;
			} else if (ep.isLimitType()) {
				queryBuilder.limit(ep.getLimit());
				continue;
			} else if (ep.isOffsetType()) {
				queryBuilder.offset(ep.getOffset());
				continue;
			} else if (ep.isDistinctType()) {
				queryBuilder.distinct().selectColumns(ep.getDistinctParam().columns());
				continue;
			} else {
				eqps.add(ep);
			}
		}
		if (!eqps.isEmpty()) {
			applyStatement(queryBuilder, eqps.toArray(new QP[eqps.size()]));
		}
	}

	public static void applyStatement(StatementBuilder<?, String> queryBuilder, QP... qps) throws SQLException {
		IT.notEmpty(qps);
		Where where = queryBuilder.where();
		QP first = qps[0];
		boolean and = true;
		if (first.isOr()) {
			if (qps.length > 1) {
				throw new SQLException("Arg OR must be single. ");
			}
			qps = first.getAndOrQps();
			and = false;
		}
		try {
			switch (qps.length) {
				case 1: {
					ApplyWhere.applyAnyQP(where, qps[0]);
					break;
				}
				default: {
					List<QP> others = ARR.as(ARR.sublist(qps, 2, null), null);
					ApplyWhere.applyLeftRight(where, qps[0], qps[1], others, and);
					break;
				}
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	public QP[] getAndOrQps() {
		IT.isFalse(isSimple());
		return isAnd() ? qpsAnd : qpsOr;
	}

	public boolean isSimple() {
		return !isAnd() && !isOr();
	}

	public boolean isAnd() {
		return qpsAnd != null;
	}

	public boolean isOr() {
		return qpsOr != null;
	}

	/**
	 * Order Param
	 */
	public static QP orderAscOrDescOrRand(String colName, Boolean isAscOrDescOrRand) {
		if (isAscOrDescOrRand == null) {
			return new QP(OrderParam.paramRandom(colName));
		} else {
			return new QP(OrderParam.paramAscOrDesc(colName, isAscOrDescOrRand));
		}
	}

	private OrderParam[] paramOrder = null;

	public void setOrderParam(OrderParam... orders) {
		this.paramOrder = orders;
	}

	public OrderParam[] getOrderParam() {
		return paramOrder;
	}

	public boolean isOrderType() {
		return paramOrder != null;
	}

	/**
	 * Limit Param
	 */
	public static QP limit(Long limit) {
		return new QP(limit, (Long) null);
	}

	public static QP limit(Integer limit) {
		return new QP(limit.longValue(), (Long) null);
	}

	private Long paramLimit = null;

	public boolean isLimitType() {
		return paramLimit != null;
	}

	public Long getLimit() {
		return paramLimit;
	}

	/**
	 * Offset Param
	 */
	public static QP offset(Long offset) {
		return new QP((Long) null, offset);
	}

	private Long paramOffset = null;

	public boolean isOffsetType() {
		return paramOffset != null;
	}

	public Long getOffset() {
		return paramOffset;
	}

	/**
	 * Distinct Param
	 */
	public static QP distinct(String... columns) {
		return new QP(DistinctParam.param(columns));
	}

	private DistinctParam paramDistinct = null;

	public void setDistinctParam(DistinctParam paramDistinct) {
		this.paramDistinct = paramDistinct;
	}

	public DistinctParam getDistinctParam() {
		return paramDistinct;
	}

	public boolean isDistinctType() {
		return paramDistinct != null;
	}

	public static QP pEQ(String colName, Object colValue) {
		return param(colName, colValue, OPR.EQ);
	}

	public static QP pGT(String colName, Object colValue) {
		return param(colName, colValue, OPR.GT);
	}

	public static QP pGE(String colName, Object colValue) {
		return param(colName, colValue, OPR.GE);
	}

	public static QP pLT(String colName, Object colValue) {
		return param(colName, colValue, OPR.LT);
	}

	public static QP pLE(String colName, Object colValue) {
		return param(colName, colValue, OPR.LE);
	}


}