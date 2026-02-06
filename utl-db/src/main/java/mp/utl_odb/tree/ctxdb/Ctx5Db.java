package mp.utl_odb.tree.ctxdb;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mpc.fs.Ns;

import java.nio.file.Path;
import java.util.List;


public class Ctx5Db extends CtxDb<Ctx5Db.CtxModel5> {

	//
	//

	public static Ctx5Db of(String dbName) {
		return AppCore.of().tree5(dbName);
	}

	public static Ctx5Db of(Ns ns, String dbName) {
		return of(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static Ctx5Db of(Path tree) {
		return new Ctx5Db(tree);
	}

	//
	//

	private @Setter boolean isHistory = false;

	@Override
	public Class getModelClass() {
		return isHistory ? CtxModel5h.class : CtxModel5.class;
	}

	//
	//


	public Ctx5Db(Class clas, String key) {
		this(clas.getSimpleName(), key);
	}

	public Ctx5Db(String parentDir, String key) {
		super(parentDir, key);
	}

	public Ctx5Db(Path path) {
		super(path);
	}

	/**
	 * *************************************************************
	 * -----------------------------  GET MODEL  --------------------------
	 * *************************************************************
	 */

	@Override
	public CtxModel5 getModelBy(CKey byKey) {
		return UCtxDb.getCtxModelBy(this, byKey);
	}

	@DatabaseTable(tableName = ICtxDb.D5H)
	public static class CtxModel5h extends CtxModel5 {

		public static CtxModel5h of(CtxModel5 ctModel) {
			CtxModel5h model5h = new CtxModel5h();
			List<String> columnNames = getColumnNames(ctModel.getClass());
			columnNames.forEach(cn -> model5h.setObjectField(cn, ctModel.getObjectFieldAsObject(cn)));
			return model5h;
		}
	}

	@DatabaseTable(tableName = ICtxDb.D5)
	public static class CtxModel5 extends CtxModel {
		@DatabaseField
		@Setter
		private @Getter String o1, o2, o3, o4, o5;
	}

	@Override
	public String toString() {
		return UCtxDb.toString(this);
	}

}
