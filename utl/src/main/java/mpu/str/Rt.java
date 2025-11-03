package mpu.str;

import mpu.core.ARR;
import mpu.core.ARG;
import mpu.X;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//Build pretty (tabbed) report's for Collection & Map
//Report
public class Rt {

	public static final String NULL_NAME = "NULL";

	public static void main(String[] args) {
		X.p(buildReportArray(ARR.of(1, 2)));
		X.p(buildReport(ARR.as(1, 2)));
//			Sys.p(buildReport((Collection) null, "asd", 0));
//			Sys.p("ok");
	}

	public static Sb buildReportArray(Object array, Logger... logger) {
		List<Object> collection = array == null ? null : ARR.toListFromArray(array);
		return buildReport(collection, "Array*" + X.sizeOf(collection), logger);
	}

	public static Sb buildReport(Collection collection, Logger... logger) {
		return buildReport(collection, null, logger);
	}

	public static Sb buildReport(Collection collection, String head, Logger... logger) {
		return buildReport(collection, head == null ? null : new String[]{head}, 0, logger);
	}

	public static Sb buildReport(Collection collection, String head, int tabLevel, Logger... logger) {
		return buildReport(collection, head == null ? null : new String[]{head}, tabLevel, logger);
	}

	public static Sb buildReport(Collection collection, String[] headFoot, int tabLevel, Logger... logger) {
		if (collection == null) {
			return buildReport_NULL(headFoot, tabLevel);
		}
		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb sb = new Sb();
		int size = X.sizeOf(headFoot);
		if (size > 0) {
			if (X.notEmpty(headFoot[0])) {
				sb.append(TAB).NL(headFoot[0]);
			}
		} else {
			sb.append(TAB).append(SFX_TYPE(collection)).NL();
		}
		for (Object o : collection) {
			sb.appendtl(o, tabLevel).NL();
		}
		if (size > 1) {
			sb.append(TAB).NL(headFoot[1]);
		}
		if (ARG.isDef(logger)) {
			ARG.toDef(logger).info(sb.toString());
		}
		sb.deleteEndIf(Sb.NL);
		return sb;
	}

	public static Sb buildReport(Map map, Logger... logger) {
		return buildReport(map, null, logger);
	}

	public static Sb buildReport(Map map, String head, Logger... logger) {
		return buildReport(map, head == null ? null : new String[]{head}, 0, logger);
	}

	public static Sb buildReport(Map map, String head, int tabLevel, Logger... logger) {
		return buildReport(map, head == null ? null : new String[]{head}, tabLevel, logger);
	}

	public static Sb buildReport(Map map, String[] headFoot, int tabLevel, Logger... logger) {
		return buildReport(map, headFoot, tabLevel, true, logger);
	}

	public static Sb buildReport(Map map, String[] headFoot, int tabLevel, boolean explodeValues, Logger... logger) {
		if (map == null) {
			return buildReport_NULL(headFoot, tabLevel);
		}

		int size = X.sizeOf(headFoot);

		String TAB = STR.TAB(tabLevel);
		String TAB2 = STR.TAB(tabLevel + 1);
		String TAB3 = STR.TAB(tabLevel + 2);
		Sb sb = new Sb();

		if (size > 0) {
			if (X.notEmpty(headFoot[0])) {
				sb.append(TAB).append(headFoot[0]).NL();
			}
		} else {
			sb.append(TAB).append(map.getClass().getSimpleName()).append("*").append(X.sizeOf(map)).NL();
		}
		if (X.notEmpty(map)) {
			Map<Object, Object> map0 = map;
			for (Map.Entry<?, ?> entry : map0.entrySet()) {
				sb.append(TAB2);
				{
					Object key = entry.getKey();
					if (key == null) {
						sb.append(key);
					} else if (key.getClass().isArray()) {
						if (key.getClass().getComponentType().isPrimitive()) {
							sb.append(key);
						} else {
							sb.append(ARR.as((Object[]) key));
						}
					} else {
						sb.append(key);
					}
				}
				sb.append("=");
				{
					Object vl = entry.getValue();
					if (vl == null) {
						sb.NL(vl);
					} else if (vl.getClass().isArray()) {
						if (vl.getClass().getComponentType().isPrimitive()) {
							sb.NL(vl);
						} else {
							if (explodeValues) {
								Object[] it = (Object[]) vl;
								sb.NL(vl.getClass().getComponentType().getSimpleName() + "*" + it.length);//
								for (Object o : it) {
									sb.TABNL(tabLevel + 2, o);
								}
							} else {
								sb.NL(ARR.as((Object[]) vl));
							}
						}
					} else {
						if (explodeValues && Iterable.class.isAssignableFrom(vl.getClass())) {
							Collection it = ARR.toList((Iterable) vl);
							sb.NL(vl.getClass().getSimpleName() + "*" + it.size());//
							for (Object o : it) {
								sb.TABNL(tabLevel + 2, o);
							}
						} else {
							sb.appendtl(vl, tabLevel);
						}
					}
				}
				sb.NL();//AFTER RM LAST
			}
			sb.deleteLastChar();//LAST
			if (size > 1) {
				sb.append(TAB).NL(headFoot[1]);
			}
		}
		if (ARG.isDef(logger)) {
			ARG.toDef(logger).info(sb.toString());
		}
		return sb;
	}

	public static Sb buildReport_NULL(String[] headFoot, int tabLevel) {
		String TAB = STR.TAB(tabLevel);
		Sb sb = new Sb();
		int size = X.sizeOf(headFoot);
		if (size > 0) {
			if (X.notEmpty(headFoot[0])) {
				sb.append(TAB).NL(headFoot[0]);
			}
		} else {
			sb.append(TAB).NL(NULL_NAME);
		}
		if (size > 1) {
			sb.append(TAB).NL(headFoot[1]);
		}
		sb.deleteEndIf(Sb.NL);
		return sb;
	}

	public static String toStringLine(Sb buildReport) {
		return STR.clean(buildReport.toString(), true, true, true, STR.COL_DEL);
	}

	//https://stackoverflow.com/questions/18672643/how-to-print-a-table-of-information-in-java
	public static StringBuilder buildTable(List<List<Object>> rows) {
		int[] maxLengths = new int[rows.get(0).size()];
		for (List<Object> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				maxLengths[i] = Math.max(maxLengths[i], String.valueOf(row.get(i)).length());
			}
		}

		StringBuilder formatBuilder = new StringBuilder();
		for (int maxLength : maxLengths) {
			formatBuilder.append("%-").append(maxLength + 2).append("s");
		}
		String format = formatBuilder.toString();

		StringBuilder result = new StringBuilder();
		for (List<Object> row : rows) {
			result.append(String.format(format, row.toArray())).append("\n");
		}
		return result;
	}

	public static String SFX_TYPE(Collection collection, String... name) {
		String string = collection == null ? NULL_NAME : collection.getClass().getSimpleName() + "*" + X.sizeOf(collection);
		return ARG.isDef(name) ? ARG.toDef(name) + "/" + string : string;
	}

	public static String PFX1(String name, int tabLevel) {
		return STR.TAB(tabLevel) + name + STR.ARR_DEL;
	}

	public static String PFX2_NLTB(int tabLevel) {
		return STR.NL + STR.TAB(tabLevel + 1);
	}

	public static String PFX2_SL() {
		return ", ";
	}

	public static String PFXE_SL(String name) {
		return STR.ARR_DEL_TOP + name;
	}

	public static String PFXE_NL(int tabLevel, String name) {
		return STR.NL + STR.TAB(tabLevel + 1) + STR.ARR_DEL_TOP + name;
	}


}
