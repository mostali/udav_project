package zk_old_core.std_core;


import mpu.X;
import mpc.exception.CleanMessageRuntimeException;
import mpc.exception.EException;
import mpc.exception.SimpleMessageRuntimeException;
import mpe.str.URx;
import mpu.str.STR;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;


public class FrmEE extends EException {
	private static final Logger L = LoggerFactory.getLogger(FrmEE.class);

	private static final long serialVersionUID = 1L;

	public static String createBlankForm() {
		return "<h2>New Component:" + QDate.now() + "</h2>";
	}

	public static String incrementNextFormName(Path last) {
		String lastName = last.getFileName().toString();
//		String lastNum = US.two(lastName, "0123456789", US.SplitType.ALLOWED)[0];
		Integer num = URx.cutFirstNum(lastName, 10);
		String numStr = num.toString();
		switch (numStr.length()) {
			case 1:
				return "20";
			case 2:
				int first = STR.charAtInt(numStr, 0);
				int len = numStr.substring(1).length();
				String other = STR.repeat("0", len);
				if (first == 9) {
					return "10" + other;
				} else {
					return ++first + other;
				}
			default:
				return String.valueOf(num + 10);

		}
	}


	/**
	 * *************************************************************
	 * ---------------------------- INIT --------------------------
	 * *************************************************************
	 */
	@Override
	public EE type() {
		return super.type(EE.class);
	}

	public enum EE {
		NOSTATUS,
		;

		public FrmEE I() {
			return new FrmEE(this);
		}

		public FrmEE I(Throwable ex) {
			return new FrmEE(this, ex);
		}

		public FrmEE I(String message) {
			return new FrmEE(this, new SimpleMessageRuntimeException(message));
		}

		public FrmEE I(String message, Object... args) {
			return new FrmEE(this, new SimpleMessageRuntimeException(X.f(message, args)));
		}

		public FrmEE M(String message, Object... args) {
			return new FrmEE(this, new CleanMessageRuntimeException(X.f(message, args)));
		}
	}

	public FrmEE() {
		super(EE.NOSTATUS);
	}

	public FrmEE(EE error) {
		super(error);
	}

	public FrmEE(EE error, Throwable cause) {
		super(error, cause);
	}


}

