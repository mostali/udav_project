package mp.utl_odb.tree;

import lombok.RequiredArgsConstructor;
import mp.utl_odb.query_core.QP;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class CtxtlDb extends CtxtDb {

	public static final Logger L = LoggerFactory.getLogger(CtxtlDb.class);

	public CtxtlDb(Class clas) {
		super(clas);
	}

	public CtxtlDb(Class clas, String key) {
		super(clas, key);
	}

	public CtxtlDb(String key) {
		super(key);
	}

	public CtxtlDb(String parentDir, String key) {
		super(parentDir, key);
	}

	public CtxtlDb(String rootParentDir, String parentDir, String key, boolean isFileOrName) {
		super(rootParentDir, parentDir, key, isFileOrName);
	}

	public CtxtlDb(Path path) {
		super(path);
	}

	@RequiredArgsConstructor
	public static class Key {
		final String val;

		public static Key of(String value) {
			return new Key(value);
		}

		public CtxFieldType type() {
			return CtxFieldType.typeOf(this);
		}

		public String colName() {
			return type().name();
		}

		public QP pEQ() {
			return QP.pEQ(colName(), val);
		}

		public static class Val extends Key {
			public Val(String val) {
				super(val);
			}

			public static Val of(String value) {
				return new Val(value);
			}
		}

		public static class Time extends Key {
			public Time(String val) {
				super(val);
			}

			public static Time of(String value) {
				return new Time(value);
			}
		}

		public static class Ext extends Key {
			public Ext(String val) {
				super(val);
			}

			public static Ext of(String value) {
				return new Ext(value);
			}
		}
	}

	public CtxTimeModel getModel_WithMaxLife(String key, long maxLifeMs) throws ShortLifeException {
		return getModel_WithMaxLife(Key.of(key), maxLifeMs);
	}

	//
	public CtxTimeModel getModel_WithMaxLife(Key key, long maxLifeMs) throws ShortLifeException {
		return getModel_WithMaxLife(key, maxLifeMs, false);
	}

	public CtxTimeModel getModel_WithMaxLife(Key key, long maxLifeMs, boolean lastModel) throws ShortLifeException {
		CtxTimeModel mod = super.getCtxTimeModel(key, lastModel);
		if (mod == null) {
			return null;
		}
		if (maxLifeMs != -1) {
			QDate dateUdpated = mod.getTimeAsQDate();
			if (System.currentTimeMillis() - dateUdpated.ms() <= maxLifeMs) {
				return mod;
			} else {
				throw new ShortLifeException(mod, maxLifeMs);
			}
		}
		updateTime(mod);
		return mod;
	}

	public static class ShortLifeException extends Exception {

		private static final long serialVersionUID = 1L;
		public final CtxTimeModel timeModel;
		final long cacheLock;

		public long getLastUpdateMs() {
			return timeModel.getTime();
		}

		private ShortLifeException(CtxTimeModel mod, long lifeCache) {
			super("ShortLifeException " + lifeCache);
			this.timeModel = mod;
			this.cacheLock = lifeCache;
		}

	}

}
