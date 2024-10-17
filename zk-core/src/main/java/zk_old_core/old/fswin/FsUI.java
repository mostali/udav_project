package zk_old_core.old.fswin;

import mpe.core.UBool;
import mpu.X;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpu.str.STR;
import mpu.core.QDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Window;
import zk_form.notify.ZKI_Modal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Supplier;

public class FsUI {

	public static final Logger L = LoggerFactory.getLogger(FsUI.class);

	private static void on(String msg, Supplier handler, Supplier clbOk) {
		try {
			handler.get();
			ZKI.infoSingleLine(STR.removeEndString(msg, "?", true));
			if (clbOk != null) {
				clbOk.get();
			}
		} catch (Throwable e) {
			if (L.isErrorEnabled()) {
				L.error(msg, e);
			}
			ZKI_Window.errorIQ(e);
		}
	}

	public static void rmSafe(String path, Supplier clbOk) {
		rmSafe(Paths.get(path), clbOk);
	}

	public static void rmSafe(Path path, Supplier clbOk) {
		String msg = X.f("Delete (safety) '%s' ?", path);
		ZKI_Modal.showMessageBoxBlueYN("Deleting..", msg, (Function<Boolean, Void>) answer -> {
			if (UBool.isTrue(answer)) {
				on(msg, () -> {
					rmInParentRmm(path);
					//UFS.RM.delete(file);
					return null;
				}, clbOk);
			}
			return null;
		});
	}

	private static void rmInParentRmm(Path file) {
//		Path file = Paths.get(path);
		Path parentRmmDated = checkOrMkdirRmmForChild(file);
		Path newPath = UFS_BASE.MV.moveIn(file, parentRmmDated);
		if (L.isInfoEnabled()) {
			L.info(" 'rmInParentRmm {}' success. New dst '{}'", file, newPath);
		}
	}

	private static Path checkOrMkdirRmmForChild(Path file) {
		Path parentRmm = file.getParent().resolve(".rmm");
		UFS_BASE.MKDIR.mkdirIfNotExist(parentRmm);
		Path parentRmmDated = parentRmm.resolve(QDate.now().f(QDate.F.MONO15_FILE_SEC));
		UFS_BASE.MKDIR.mkdirIfNotExist(parentRmmDated);
		return parentRmmDated;
	}

	public static void rmHard(String path, Supplier clbOk) {
		String msg = X.f("Delete  '%s' ?", path);
		ZKI_Modal.showMessageBoxBlueYN("Deleting..", msg, (Function<Boolean, Void>) answer -> {
			if (UBool.isTrue(answer)) {
				on(msg, () -> {
					UFS.RM.deleteDir(Paths.get(path));
					return null;
				}, clbOk);
			}
			return null;
		});
	}

	public static void mv(String pathSrc, String pathDst, Supplier clbOk) {
		String msg = "Move '" + pathSrc + "' to '" + pathDst + "'";
		ZKI_Modal.showMessageBoxBlueYN("Moving..", msg, (Function<Boolean, Void>) answer -> {
			if (UBool.isTrue(answer)) {
				on(msg, () -> UFS_BASE.MV.move(Paths.get(pathSrc), Paths.get(pathDst), true), clbOk);
			}
			return null;
		});
	}

	public static void cp(String pathSrc, String pathDst, Supplier clbOk) {
		String msg = "Copy '" + pathSrc + "' to '" + pathDst + "'";
		ZKI_Modal.showMessageBoxBlueYN("Coping..", msg, (Function<Boolean, Void>) answer -> {
			if (UBool.isTrue(answer)) {
				on(msg, () -> UFS_BASE.COPY.copyDirContentWithReplace(Paths.get(pathSrc), Paths.get(pathDst), true), clbOk);
			}
			return null;
		});
	}

}
