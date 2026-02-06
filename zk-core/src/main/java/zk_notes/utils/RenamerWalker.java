package zk_notes.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpe.core.P;
import mpu.X;
import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class RenamerWalker extends DirectoryWalker {

	final File startDirectory;

	public static void main(String[] args) {
		RenamerWalker.fromDir(Paths.get("/home/dav/.data/bea/.sd3/.index/notes/.coms")).renameAll();
	}

	public static RenamerWalker fromDir(Path startDirectory) {
		RenamerWalker RenamerWalker = new RenamerWalker(startDirectory.toFile());
		return RenamerWalker;
	}

	@SneakyThrows
	public List renameAll() {
		List results = new ArrayList();
		walk(startDirectory, results);
		X.p("Found form's");
		X.p(results);
		return results;
	}

	protected boolean handleDirectory(File directory, int depth, Collection results) {
		P.p("Walk dir:" + directory + ":" + depth + ":" + results.size());
		// delete svn directories and then skip
//			if (".svn".equals(directory.getName())) {
//				return false;
//			} else {
//				return true;
//			}
		return true;
	}

	protected void handleFile(File file, int depth, Collection results) {
		P.p("Walk file:" + file + ":" + depth + ":" + results.size());
		results.add(file);
		if (file.getName().equals("ZkPage.props")) {
//				QuestAnswer.CONTINUE_YN(file.getAbsolutePath());
			UFS.MV.move(file.toPath(), file.getParentFile().toPath().resolve("AppNotes.props"), null);
			P.p("moved:" + file);

		}
	}
}
