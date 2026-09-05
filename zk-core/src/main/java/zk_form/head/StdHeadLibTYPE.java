package zk_form.head;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;

public enum StdHeadLibTYPE {
	CSS, JS, DATA_JS, DATA_CSS, DATA;

	public <R> IHeadRsrc of(R file_rsrc, StdHeadLib... rsrcName) {
		return ARG.isNotDef(rsrcName) ? new Rsrc(this, file_rsrc) : new Rsrc(this, file_rsrc) {
			@Override
			public StdHeadLib getStdHeadLib() {
				return rsrcName[0];
			}
		};
	}

	@RequiredArgsConstructor
	public static class Rsrc<R> implements IHeadRsrc {

		final StdHeadLibTYPE type;
		final R rsrc;

		@Override
		public R rsrc() {
			return rsrc;
		}

		@Override
		public StdHeadLibTYPE type() {
			return type;
		}

		@Override
		public StdHeadLib getStdHeadLib() {
			return IHeadRsrc.super.getStdHeadLib();
		}
	}
}
