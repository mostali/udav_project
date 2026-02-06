package mp.utl_odb.query_core;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class OrderParam {
	public final String columnName;

	private String orderRaw = EOrderParam.ASC.name();

	public enum EOrderParam {
		ASC, DESC, RANDOM, ANY;

		private static EOrderParam orderOfAscOrDesc(boolean isAscending) {
			return isAscending ? ASC : DESC;
		}

		public static EOrderParam valueOfSafe(String s) {
			try {
				return valueOf(s);
			} catch (Exception ex) {
				return null;
			}
		}

	}

	public String toStringRest() {
		return "order:" + orderRaw.toLowerCase() + ":" + columnName;
	}

	public static OrderParam paramAsc(String param) {
		return paramAscOrDesc(param, true);
	}

	public static OrderParam paramDesc(String param) {
		return paramAscOrDesc(param, false);
	}

	public static OrderParam paramAscOrDesc(String param, boolean isAscending) {
		return new OrderParam(param, isAscending);
	}

	private OrderParam(String param, boolean isAscending) {
		this(param, EOrderParam.orderOfAscOrDesc(isAscending));
	}

	private OrderParam(String param, EOrderParam eparam) {
		this.columnName = param;
		this.orderRaw = eparam.name();
	}

	public static OrderParam paramRandom(String param) {
		return new OrderParam(param, EOrderParam.RANDOM);
	}

	public static OrderParam paramAny(String param) {
		return new OrderParam(param, EOrderParam.ANY);
	}

	public QueryBuilder apply(QueryBuilder<?, String> queryBuilder) {
		switch (EOrderParam.valueOf(orderRaw)) {
			case ASC: {
				return queryBuilder.orderBy(columnName, true);
			}
			case DESC: {
				return queryBuilder.orderBy(columnName, false);
			}
			case RANDOM: {
				return queryBuilder.orderByRaw("RANDOM()");
			}
			case ANY: {
				return queryBuilder.orderByRaw(columnName);
			}
			default:
				throw new RuntimeException("WTF apply :" + orderRaw);
		}
	}

	public static QueryBuilder<?, String> apply(QueryBuilder<?, String> queryBuilder, OrderParam[] equalsFields) {
		Where where = null;
		for (OrderParam ep : equalsFields) {
			if (ep != null) {
				switch (EOrderParam.valueOf(ep.orderRaw)) {
					case ASC: {
						queryBuilder = queryBuilder.orderBy(ep.columnName, true);
						break;
					}
					case DESC: {
						queryBuilder = queryBuilder.orderBy(ep.columnName, false);
						break;
					}
					case RANDOM: {
						queryBuilder = queryBuilder.orderByRaw("RANDOM()");
						break;
					}
				}
			}
		}
		return queryBuilder;
	}

}