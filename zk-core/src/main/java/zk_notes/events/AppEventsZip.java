package zk_notes.events;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.exception.NI;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.fs.tmpfile.TmpFolderOperation;
import mpc.log.L;
import mpc.str.sym.SYMJ;
import mpe.rt_exec.UnZipExecEE;
import mpe.rt_exec.ZipExecEE;
import mpu.IT;
import mpu.X;
import mpu.core.ARRi;
import mpu.func.FunctionV;
import mpu.func.FunctionV1;
import mpu.pare.Pare;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.uploader.NativeFileUploaderComposerCustom;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Quest;
import zk_os.coms.AFC;
import zk_os.coms.AFCC;
import zk_os.core.Sdn;
import zk_page.ZKR;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class AppEventsZip extends AppEvents {

	public static Pare<String, SerializableEventListener> applyEvent_ZipPage(Component com, Pare<String, String> sdn, String... event) {
		if (!Sdn.existPage(sdn)) {
			return null;
		}
		return apply(Pare.of(SYMJ.FILE_ARCHIVE_PACK + " Archive Page", (Event e) -> {
			String pagePath = Sdn.of(sdn).toStringPath();
			ZKI_Quest.showMessageBoxBlueYN("Zip page...", X.f("Zip page '%s'?", pagePath), (yes) -> {
				if (yes) {
					Path dstZip = AFC.PAGES.getDirAsZipFile(sdn);
					UFS.MKDIR.mkdirIfNotExist(dstZip.getParent(), 2);
					Path pageDir = AFC.PAGES.getDir(sdn);
					ZipExecEE.zipFd(pageDir, dstZip, ZipExecEE.CreateZipOption.REPLACE);
					IT.isFileExist(dstZip, "Zip File not found '%s'", dstZip);
					ZKI.infoAfterPointer(X.f("Page '%s' was archive (%s) to file '%s'", pagePath, X.sizeOfHu(dstZip), dstZip), ZKI.Level.INFO);
				}
			});
		}), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_DownloadZipPage(Component com, Pare<String, String> sdn, String... event) {
		Path pageDirZip = AFC.PAGES.getDirAsZipFile(sdn);
		if (!UFS.existFile(pageDirZip)) {
			return null;
		}
		return apply(Pare.of(SYMJ.FILE_ARCHIVE_PACK + " Download Archive Page", (Event e) -> ZKR.download(pageDirZip)), com, event);
	}

	public static Pare<String, SerializableEventListener> applyEvent_RecoveryPageFromArchive(Component com, Pare<String, String> sdn, String... event) {
		Path pageDir = AFC.PAGES.getDir(sdn);
		FunctionV rmDir = () -> {
			Path pageDirZip = AFC.PAGES.getDirAsZipFile(sdn);
			if (UFS.existFile(pageDirZip)) {
				UFS.delete(pageDirZip);
				L.info("Deleted previous page archive - " + pageDirZip);
			}
		};
		String label = SYMJ.FILE_ARCHIVE_PACK + " Recovery ZIP to new page '" + pageDir.getFileName() + "'";
		return apply(Pare.of(label, (Event e) -> {
			FunctionV1<List<Path>> afterUpload = (files) -> {
				IT.isLength(files, 1);
				rmDir.apply();
				for (Path path : files) {
					L.info("Uploaded to - " + path);
					new UnzipAndMovePageTo(path, pageDir, true).work();
				}
			};
			NativeFileUploaderComposerCustom.doNativeUploadEvent(pageDir, afterUpload, 1, false);
		}), com, event);
	}

	//
	//
	@RequiredArgsConstructor
	public static class UnzipAndMovePageTo {
		final Path zipFile;
		final Path pageDirectory;
		final boolean cleanDirBeforeUnzip;

		public void work() {
			new TmpFolderOperation() {

				@Override
				public void doOperationImpl(Path tmpFile) throws Exception {
					try {
						doOperationImplImpl(tmpFile);
					} finally {
						UFS.RM.fileQk(zipFile);
					}
				}

				public void doOperationImplImpl(Path workDir) throws Exception {
					UnZipExecEE.unzip(workDir, zipFile, workDir, UnZipExecEE.overwriteToArgs(true));
					String childName = EXT.getFilenameWoExt(zipFile.getFileName().toString());
//					Path pageDir = IT.NN(findPageDir(workDir.resolve(childName)), "Page dir not found");
					Path pageDir = IT.isDirExist(workDir.resolve(childName), "Page dir not found");
//
					IT.isDirOrNotExist(pageDirectory);
					if (cleanDirBeforeUnzip) {
						if (UFS.existDir(pageDirectory)) {
							UFS.RM.deleteDir(pageDirectory);
						}
					} else {
						NI.stop("ni:cleanDirBeforeUnzip");
					}
					//						else {
//							throw new FIllegalStateException("Page '%s' exist. Remove manually or set replace flag=true", targetDir.getFileName());
//						}
//					}
					UFS.MV.move(pageDir, pageDirectory, true);
					ZKI.infoAfterPointer("Page added", ZKI.Level.INFO);
					ZKR.restartPage();
				}

				@SneakyThrows
				private Path findPageDir(Path tmpFile) {
					Collection<Path> paths = UFS.SEARCH.searchFiles(tmpFile, p -> {
						return X.eqObjAny(p.getFileName().toString(), AFCC.DIR_COMS, AFCC.DIR_FORMS, AFCC.FILE_PAGE);
					}, true);
					if (X.empty(paths)) {
						return null;
					}
					return ARRi.first(paths).getParent();
				}

			}.doOperation();

		}

	}

}
