package mpf.ns.space.oper.def;

import mpc.args.ARG;
import mpc.ERR;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS_BASE;
import mpf.ns.space.ST;
import mpf.ns.space.Src;
import mpf.ns.space.Topic;
import mpf.ns.space.core.ISs;
import mpf.ns.space.oper.BaseDstNsOp;
import mpc.time.QDate;

import java.nio.file.Path;

public class CopyNsOp extends BaseDstNsOp<Path> {

	public CopyNsOp(ISs iSpaceSrc) {
		super(iSpaceSrc);
	}


	public static void testTopic() {
		Topic topic0 = (Topic) Topic.of("/tmp/gx0/space0/topic0").mk();
		Path rslt = (Path) new CopyNsOp(topic0).mkdirs_mkdir_not().doOp();
		ERR.isDirExist(rslt);
		UFS_BASE.RM.deleteDir(rslt);
	}

	public static void testSrc() {
		Src topic0 = (Src) Src.of("/tmp/gx0/space0/src0").mk();
		Path rslt = (Path) new CopyNsOp(topic0).mkdirs_mkdir_not().doOp();
		ERR.isFileExist(rslt);
		UFS_BASE.RM.deleteDir(rslt);
	}

	@Override
	public void doOpImpl() {
		ISs iSpaceSrc = ss();
		Path ssPath = iSpaceSrc.path();
		ST sst = iSpaceSrc.srcType();
		if (L.isDebugEnabled()) {
			L.debug("Copy Ns Operation init, ss '{}'", iSpaceSrc);
		}
		String cnt = QDate.now().f(QDate.F.MONO15_FILE_SEC);
		Path newPath = ssDst();
		if (newPath == null) {
			newPath = ssPath.getParent().resolve(ssPath.getFileName() + "_copy" + cnt);
		}

		switch (sst) {
			case SRC:
				UFS_BASE.COPY.copyFileAs(ssPath, newPath, ARG.toDefBoolean(mkdirs_mkdir_not));
				break;
			case GX:
			case SPACE:
			case TOPIC:
				UFS_BASE.COPY.copyDirContentWithReplace(ssPath, newPath, ARG.toDefBoolean(mkdirs_mkdir_not));
				break;
			default:
				throw new WhatIsTypeException(sst);
		}
		ERR.state(newPath != null, "set newPath");
		this.result = newPath;
		if (L.isInfoEnabled()) {
			L.info("Copy Ns Operation success, ss '{}' copied '{}'", iSpaceSrc, newPath);
		}
	}


}
