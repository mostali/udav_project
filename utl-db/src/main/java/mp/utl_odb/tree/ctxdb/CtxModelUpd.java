package mp.utl_odb.tree.ctxdb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mpu.X;

@RequiredArgsConstructor
public class CtxModelUpd<M extends ICtxDb.CtxModel> {

	public final @Getter ICtxDb<M> db;
	public final @Getter M model;

	public static <M extends ICtxDb.CtxModel> CtxModelUpd<M> of(ICtxDb<M> db, M model) {
		return new CtxModelUpd(db, model);
	}

	@Override
	public String toString() {
		return X.toStringRfl(this);
	}

	public M putAppend(String cKey, String value, boolean append) {
		return db.put(cKey, value, append);
	}

	public M put(String cKey, CKey... values) {
		return db.put(cKey, values);
	}
}
