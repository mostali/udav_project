package mpf.ns.space.oper.def;

import mpu.IT;
import mpc.exception.WhatIsTypeException;
import mpu.core.RW;
import mpc.fs.UFS_BASE;
import mpf.ns.space.ST;
import mpf.ns.space.Space;
import mpf.ns.space.Topic;
import mpf.ns.space.core.ISs;
import mpf.ns.space.oper.BaseNsOp;
import mpu.core.QDate;

import java.nio.file.Path;

public class AddNsOp extends BaseNsOp<Path> {

	public AddNsOp(ISs iSpaceSrc) {
		super(iSpaceSrc);
	}

	public static void testSpace() {
		Space topic0 = Space.of("/tmp/gx0/space0/");
		Path rslt = (Path) new AddNsOp(topic0).mkdirs_mkdir_not().doOp();
		IT.isDirExist(rslt);
		UFS_BASE.RM.deleteDir(rslt);
	}

	public static void testTopic() {
		Topic topic0 = Topic.of("/tmp/gx0/space0/topic0");
		Path rslt = (Path) new AddNsOp(topic0).mkdirs_mkdir_not().doOp();
		IT.isFileExist(rslt);
		UFS_BASE.RM.deleteDir(rslt);
	}

	@Override
	public void doOpImpl() {
		ISs iSpaceSrc = ss();
		Path ssPath = iSpaceSrc.fPath();
		ST sst = iSpaceSrc.srcType();
		if (L.isDebugEnabled()) {
			L.debug("Add Ns Operation init, ss '{}'", iSpaceSrc);
		}
		Path newPath = null;
		switch (sst) {
			case GX: {
				String cnt = QDate.now().f(QDate.F.MONO15_FILE_SEC);
				newPath = ssPath.resolve("newSpace_" + cnt);
				UFS_BASE.MKDIR.createDirsOrSingleDirOrCheckExist(newPath, super.mkdirs_mkdir_not);
				break;
			}
			case SPACE: {
				String cnt = QDate.now().f(QDate.F.MONO15_FILE_SEC);
				newPath = ssPath.resolve("newTopic_" + cnt);
				UFS_BASE.MKDIR.createDirsOrSingleDirOrCheckExist(newPath, super.mkdirs_mkdir_not);
				break;
			}
			case TOPIC: {
				String cnt = QDate.now().f(QDate.F.MONO15_FILE_SEC);
				newPath = ssPath.resolve("newSrc_" + cnt);
				RW.write(newPath, cnt, super.mkdirs_mkdir_not);
				break;
			}
			default:
				throw new WhatIsTypeException(sst);
		}
		IT.state(newPath != null, "set newPath");
		this.result = newPath;
		if (L.isInfoEnabled()) {
			L.info("Add Ns Operation success, ss '{}' added '{}'", iSpaceSrc, newPath);
		}
	}


}
