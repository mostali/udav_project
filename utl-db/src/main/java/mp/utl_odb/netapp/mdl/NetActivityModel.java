package mp.utl_odb.netapp.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.query_core.QP;
import mpu.X;
import mpe.str.CN;
import mp.utl_odb.typedb.TypeDb;
import mpc.str.sym.SYMJ;
import mpu.core.QDate;
import mp.utl_odb.mdl.AModel;
import mp.utl_odb.mdl.TimedIdModel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "activity")
public class NetActivityModel<M extends TimedIdModel> extends TimedIdModel<M> {

	@Setter
	@Getter
	@DatabaseField
	private Long user_uid;

	@Setter
	@Getter
	@DatabaseField
	private String msg, clb, route;

	public static <M extends AModel> TypeDb<M> DB(Path appStoreRoot) {
		return (TypeDb<M>) TypeDb.of(NetActivityModel.class, appStoreRoot.resolve("db$activity.sqlite"), true);
	}

	/**
	 * *************************************************************
	 * ---------------------------- NEW ----------------------------
	 * *************************************************************
	 */

	public static NetActivityModel createNewActivity(TypeDb db, long user_uid, String msg, String clb, String route) {
		NetActivityModel m = new NetActivityModel();
		m.setUser_uid(user_uid);
		m.setMsg(msg);
		m.setClb(clb);
		m.setRoute(route);
		m.setDt(QDate.now().toSqlDate());
		m.saveAsCreate(db);
		return m;
	}

	public static List<NetActivityModel> loadActivitys(TypeDb db, Integer limit, QDate dateLast) {
		List<QP> qps = new ArrayList<>();
		if (limit == null) {
			limit = 10;
		}
		qps.add(QP.limit(limit));
		if (dateLast != null) {
			qps.add(QP.pLE(CN.DTRU, dateLast.toSqlDate()));
		}

		qps.add(QP.orderAscOrDescOrRand(CN.DT, false));

		return db.getModels(qps.toArray(new QP[0]));
	}

	public static List<NetActivityModel> loadActivitys(TypeDb db, QP... qps) {
		return db.getModels(QP.merge(qps, QP.orderAscOrDescOrRand(CN.DT, false)));
	}

	/**
	 * *************************************************************
	 * ---------------------------- TO STRING ----------------------
	 * *************************************************************
	 */
	public static String toMessage(NetActivityModel m) {
		return X.f("User '%s'", m.getUser_uid());
	}

	public static String toLine(NetActivityModel e) {
		String SP = SYMJ.ARROW_RIGHT_SPEC;
		return e.getUser_uid() + SP + e.getRoute() + SP + e.getMsg() + SP + e.getClb() + SP + e.getDtru();
	}
}
