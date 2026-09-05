package mp.utl_odb.mdl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import mp.utl_odb.query_core.QP;
import mp.utl_odb.typedb.TypeDb;

import java.util.List;

//Link
@DatabaseTable
public class ALModel<M extends ALModel> extends IdModel<M> {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@DatabaseField(throwIfNull = true)
	private long uid, uidl;

	@Override
	public String toString() {
		return cn() + "{" +
				"uid1=" + uid +
				",uid2=" + uidl +
				'}';
	}

	public ALModel(long id) {
		setId(id);
	}

	public ALModel() {
	}

	public static <M extends ALModel> List<M> parent_links(Class<M> linkClass, long uid) {
		TypeDb<M> db = DB(linkClass);
		return db.getModels(QP.pUID(uid));
	}

	public static <M extends ALModel> List<M> child_links(Class<M> linkClass, long uidl) {
		TypeDb<M> db = DB(linkClass);
		return db.getModels(QP.pUIDL(uidl));
	}

	public static <M extends ALModel> M link(Class<M> linkClass, long uid, long uidl) {
		TypeDb<M> db = DB(linkClass);
		M model = AModel.newModel(linkClass);
		model.setUid(uid);
		model.setUidl(uidl);
		model.saveAsCreate(db);
		return model;
	}

	public static <M extends ALModel> M unlink(Class<M> linkClass, long uid, long uidl) {
		TypeDb<M> db = DB(linkClass);
		M m = db.getModelNN(QP.pUID(uid), QP.pUIDL(uidl));
		m.rm(db);
		return m;
	}
}
