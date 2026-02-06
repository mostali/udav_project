package mpe.app_model;

import mpu.X;

public interface IMem {
	default String fid() {
		return null;
	}

	String msg();

	MEM type();

	default boolean empty() {
		return X.notEmpty(msg());
	}

	enum MEM {
		MSG, PHOTO, VIDEO, AUDIO, POOL;
	}

	static IMem simple(String msg, Object... args) {
		return new IMem.Simple(msg, args);
	}

	class Simple implements IMem {
		public final String msg;
		public final MEM type;

		public Simple(String msg, Object... args) {
			this(MEM.MSG, X.f(msg, args));
		}

		public Simple(MEM type, String msg) {
			this.msg = msg;
			this.type = type;
		}

		@Override
		public String msg() {
			return msg;
		}

		@Override
		public MEM type() {
			return type;
		}
	}
}
