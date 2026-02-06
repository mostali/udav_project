package mp.utl_odb.mdl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import mp.utl_odb.DBU;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;
import mp.utl_odb.typedb.TypeDbEE;
import mpc.rfl.RFL;
import mpc.str.ObjTo;
import mpc.str.sym.SYMJ;
import mpu.X;
import mpu.core.RW;
import mpu.str.SPLIT;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

//AbstractModel
public abstract class AModel<M extends AModel> implements Serializable {

	public static final String FN_DB = "DB";

	public static final String SYM_PARENT = SYMJ.ARROW_RIGHT_SPEC;

	public AModel() {
	}

//	public static <M extends AModel> List<String> getColumnNamesDepr(Class<M> classModel) {
//		return URefl_1.getAllFieldsAsNames(classModel, DatabaseField.class);
//	}

	public static <M extends AModel> List<String> getColumnNames(Class<M> classModel) {
		return DBU.getAllFieldNames(classModel);
	}

	public static <M extends AModel> M newModel(Class<M> classModel) {
		return RFL.instEmptyConstructor(classModel);
	}

	public String cn() {
		return getClass().getSimpleName();
	}


	public static <M extends AModel> TypeDb<M> DB(Class<M> mdlClass) {
		return TypeDbEE.getDbEE(mdlClass);
	}

	public static <M extends AModel> List<M> getAllModels(Class<M> mdlClass, Boolean useEeDb, QP... qps) {
		return getAllModels(mdlClass, null, useEeDb);
	}

	public static <M extends AModel> List<M> getAllModels(Class<M> mdlClass, TypeDb from, boolean use_SrvEE_or_FromModel, QP... qps) {
		if (from != null) {
			return from.getModels(qps);
		} else if (use_SrvEE_or_FromModel) {
			return TypeDbEE.getDbEE(mdlClass).getModels(qps);
		} else {
			return TypeDb.getDefaultTypeDb(mdlClass).getModels(qps);
		}
	}

	/**
	 * *************************************************************
	 * ------------------------- removeModel -------------------------
	 * *************************************************************
	 */
	public void rm(TypeDb... db) {
		TypeDb.getTypeDbAsMaster_Or_Default((Class<M>) getClass(), db).removeModel(this);
	}

	public void rmQk(TypeDb... db) {
		TypeDb.getTypeDbAsMaster_Or_Default((Class<M>) getClass(), db).removeModelIfExist(this);
	}

	/**
	 * *************************************************************
	 * ------------------------- saveModel -------------------------
	 * *************************************************************
	 */

	public void saveAsUpdate(TypeDb... db) {
		TypeDb.getTypeDbAsMaster_Or_Default((Class<M>) getClass(), db).saveAsUpdateRq(this);
	}

	public void saveAsCreateOrUpdate(TypeDb... db) {
		TypeDb.getTypeDbAsMaster_Or_Default((Class<M>) getClass(), db).createOrUpdateModelRq((M) this);
	}

	public void saveAsCreate(TypeDb... db) {
		TypeDb.getTypeDbAsMaster_Or_Default((Class<M>) getClass(), db).createModel((M) this);
	}


	public abstract void setId(Long id);

	public abstract Long getId();

	public Object getFieldValueObj(String fieldName) {
		return RFL.fieldValue(this, fieldName, true);
	}

	public <T> T getFieldValueObjType(String fieldName, Class<T> type) {
		return ObjTo.objTo(getFieldValueObj(fieldName), type);
	}

	public List<String> getFieldValueAsList(String fieldName, String del) {
		String val = getFieldValueObjType(fieldName, String.class);
		return getStringAsListBy(val, del);
	}

	public Object getObjectFieldAsObject(String fieldName) {
		return RFL.fieldValue(this, fieldName, true);
	}

//	@Deprecated
//	public String getObjectFieldAsString(String fieldName) {
//		return URefl_1.getFieldObjectAsString(this, fieldName);
//	}

	public void setObjectField(String fieldName, Object value) {
		RFL.fieldValueSet(this, fieldName, value, true);
	}

	//	public void setColValue(String simpleName, String val) {
	//		RFL.invoke(this, "set" + STR.capitalize(simpleName), new Class[]{String.class}, new Object[]{val});
	//	}

//	@Deprecated
//	public void setObjectFieldOld(String fieldName, Object value) {
//		try {
//			URefl_1.setFieldObject(this, fieldName, value);
//		} catch (IllegalArgumentException | IllegalAccessException e) {
//			throw EER.RT.I(e);
//		}
//	}

//	public List<String> getFieldAsList(String fieldName, String del) {
//		String val = getObjectFieldAsString(fieldName);
//		return getStringAsListBy(val, del);
//	}

//	public Map<String, String> getFieldAsMap(String fieldName) {
//		return getFieldAsMap(fieldName, URuProps.SEP_OR);
//	}

//	public <M> M setFieldAsMap(String fieldName, String key, Object value) {
//		Map map = getFieldAsMap(fieldName);
//		map.put(key, value);
//		return setFieldAsMap(fieldName, map);
//	}

//	public <M> M setFieldAsMap(String fieldName, Map map) {
//		String val = URuProps.toRuProperties(map, System.lineSeparator(), URuProps.SEP_OR);
//		setObjectField(fieldName, val);
//		return (M) this;
//	}


//	public Map<String, String> getFieldAsMap(String fieldName, String lineSeparator) {
//		String val = getObjectFieldAsString(fieldName);
//		return URuProps.getRuProperties(val, System.lineSeparator(), lineSeparator, URuProps.SEP_COMMENT);
//	}

//	@Deprecated
//	public SimpleProps getFieldAsSimpleProps(String colName, String delimetrMain, String delimetrProp) {
//		String v = getObjectFieldAsString(colName);
//		return new SimpleProps(v == null ? "" : v, delimetrMain, delimetrProp);
//	}

	public static List<String> getStringAsListBy(String str, String delimetr) {
		return SPLIT.allBy(str, delimetr);
	}

	public static List<String> getStringAsListBySpaceRx(String str) {
		return SPLIT.allBySpace(str);
	}

//	public static Map<String, String> getStringAsRuMap(String str, String separatorKeys, String charComment) {
//		if (X.empty(str)) {
//			return new LinkedHashMap();
//		}
//		return URuProps.getRuProperties(str, separatorKeys, charComment, "#");
//
//	}

//	public static <M> M getByUidQk(SqlDbUrl urlDb, Class<M> clas, int uid) {
//		return DBU.getModelByUidQk(urlDb, clas, uid);
//	}
//
//	public static <M> M getByQk(SqlDbUrl dbUrl, Class<M> clas, String cname, String cvalue) {
//		return DBU.getModelByQk(dbUrl, clas, cname, cvalue);
//	}

//	public static <M> List<M> getAllRq(SqlDbUrl dbUrl, Class<M> clazz, QP... params) {
//		return DBU.getModels(dbUrl, clazz, params);
//	}

	public String toJson() {
		return toJson(this);
	}

	public static String toJson(AModel model) {
		return new Gson().toJson(model);
	}

	public JsonElement toJsonElement() {
		return new Gson().toJsonTree(this);
	}

	public M fromJson(String json) {
		return (M) fromJson(json, getClass());
	}

	public static <T> T fromJson(String json, Class<T> clas) {
		return new Gson().fromJson(json, clas);
	}

	public M setSerializable(String fieldName, Serializable ex) {
		String serializable = null;
		try {
			serializable = RW.Serializable2String.serializable(ex);
		} catch (IOException e) {
			return X.throwException(e);
		}
		setObjectField(fieldName, serializable);
		return (M) this;
	}

	public Serializable getSerializable(String fieldName) {
		Object val = getObjectFieldAsObject(fieldName);
		try {
			return RW.Serializable2String.deserializable((String) val);
		} catch (Exception e) {
			return X.throwException(e);
		}
	}

}
