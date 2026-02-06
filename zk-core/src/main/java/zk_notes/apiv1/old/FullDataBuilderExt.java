package zk_notes.apiv1.old;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpe.core.P;
import mpu.X;
import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Deprecated //old?
public class FullDataBuilderExt {

	public static final int FIELD_MAX_MB = 1;

	public static void main(String[] args) {
//		P.exit(Plane.getAllSd3());
//		Map map = buildMap_ROOT(null);
//		P.exit(map+"");
//		P.p(Rt.buildReport(map));

		SiteDataWalker.fromDir(Paths.get("/home/dav/.data/bea/.planes")).walk();

	}

	@RequiredArgsConstructor
	public static class SiteDataWalker extends DirectoryWalker {

		final File startDirectory;

		public static SiteDataWalker fromDir(Path startDirectory) {
			SiteDataWalker RenamerWalker = new SiteDataWalker(startDirectory.toFile());
			return RenamerWalker;
		}

		@SneakyThrows
		public List walk() {
			List results = new ArrayList();
			walk(startDirectory, results);
			X.p("Found result's");
			X.p(results);
			return results;
		}

		protected boolean handleDirectory(File directory, int depth, Collection results) {
			P.p("Walk dir:" + directory + " |" + depth + "*" + results.size());
			return true;
		}

		protected void handleFile(File file, int depth, Collection results) {
			P.p("Walk file:" + file + " |" + depth + "*" + results.size());
			Map map = FullDataBuilder.buildMap_File(file.toPath(), true);
			results.add(map);

		}
	}
}
