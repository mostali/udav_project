package mp.utl_odb.tree.ctxdb;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mpe.db.JdbcUrl;
import mp.utl_odb.netapp.AppCore;
import mpc.fs.Ns;
import mpu.X;
import mpu.core.ARG;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.List;


public class Ctx10Db extends CtxDb<Ctx10Db.CtxModel10> {


	//
	//


	public static Ctx10Db of(String dbName) {
		return AppCore.of().tree10(dbName);
	}

	public static Ctx10Db of(String ns, String dbName) {
		return of(Ns.of(ns), dbName);
	}

	public static Ctx10Db of(Ns ns, String dbName) {
		return of(ns.path(JdbcUrl.buildDbFileName(dbName)));
	}

	public static Ctx10Db of(Path tree, boolean... isHistory) {
		return ARG.isDefEqTrue(isHistory) ? new Ctx10Db(tree).withHistoryTable() : new Ctx10Db(tree);
	}

	//
	//

	private boolean isHistory = false;

	public Ctx10Db withHistoryTable() {
		this.isHistory = true;
		return this;
	}

	@Override
	public Class getModelClass() {
		return isHistory ? Ctx10Db.CtxModel10h.class : Ctx10Db.CtxModel10.class;
	}

	//
	//


	public Ctx10Db(Class clas, String key) {
		this(clas.getSimpleName(), key);
	}

	public Ctx10Db(String parentDir, String key) {
		super(parentDir, key);
	}

	public Ctx10Db(Path path) {
		super(path);
	}

	/**
	 * *************************************************************
	 * -----------------------------  GET MODEL  --------------------------
	 * *************************************************************
	 */

	@Override
	public CtxModel10 getModelBy(CKey byKey) {
		return UCtxDb.getCtxModelBy(this, byKey);
	}

	@DatabaseTable(tableName = ICtxDb.D10H)
	public static class CtxModel10h extends Ctx10Db.CtxModel10 {

		public static Ctx10Db.CtxModel10h of(Ctx5Db.CtxModel5 ctModel) {
			Ctx10Db.CtxModel10h model10h = new Ctx10Db.CtxModel10h();
			List<String> columnNames = getColumnNames(ctModel.getClass());
			columnNames.forEach(cn -> model10h.setObjectField(cn, ctModel.getObjectFieldAsObject(cn)));
			return model10h;
		}

	}

	@DatabaseTable(tableName = ICtxDb.D10)
	public static class CtxModel10 extends Ctx5Db.CtxModel5 {
		@DatabaseField
		@Setter
		private @Getter String o6, o7, o8, o9, o10;

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	@Override
	public String toString() {
		return UCtxDb.toString(this);
	}

}
