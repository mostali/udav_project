package mpf.ns.space.oper;

import mpf.ns.space.core.ISs;

import java.nio.file.Path;

public abstract class BaseDstNsOp<R> extends BaseNsOp<R> {

	private Path iSsDstPath;

	public Path ssDst() {
		return iSsDstPath;
	}

	public BaseDstNsOp<R> ssDst(Path iSsDst) {
		this.iSsDstPath = iSsDst;
		return this;
	}

	public BaseDstNsOp(ISs iSpaceSrc) {
		super(iSpaceSrc);
	}

	public BaseDstNsOp(ISs iSpaceSrc, Path iSsDstPath) {
		super(iSpaceSrc);
		this.iSsDstPath = iSsDstPath;
	}

}
