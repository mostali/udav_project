package udav_net.apis.zznote;

import mpc.env.Env;
import mpc.env.EnvTlp;
import mpc.fs.UFS;
import mpc.fs.ext.EXT;
import mpc.html.EHtml5;
import mpc.log.L;
import mpc.rfl.UReflExt;
import mpf.test.ZNViewAno;
import mpu.core.RW;
import mpu.pare.Pare;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;

public class ZView {

	public static void main(String[] args) {

		Collection<Path> paths = UFS.SEARCH.searchFiles(Env.ROOT_PJM, p -> {
			return EXT.JAVA.has(p);
		}, false);
		for (Path pathJavaClass : paths) {

			String classContent = RW.readString(pathJavaClass);
			boolean contains = classContent.contains("@" + ZNViewAno.class.getSimpleName());

			if (contains) {

				String keyska = EnvTlp.ofHlp("zn", "local").readLine(4);
				NoteApi noteApi = new NoteApi("q.com:8080", keyska);

				Pare<String, String> sdn = Pare.of("udav");
				String itemName = pathJavaClass.getFileName().toString();
				ItemPath itemPath = ItemPath.of(sdn, itemName);

				noteApi.PUT_item(itemPath, EHtml5.wrapPrettyCode(classContent), true);
				try {
					noteApi.PUT_item_state(itemPath, "forms", "view", "PRETTYCODE", false);
				} catch (Exception Ex) {
					L.warn("Update state error", Ex.getMessage());
				}

				noteApi.PUT_item_state(itemPath, "pages", "mode", "tabs", false);

			}
		}

	}

	public static @NotNull Path getSourcePathOfClass(String module, Class mpf) {
		return Env.ROOT_PJM.resolve(module + "/src/main/java").resolve(mpf.getName().replace('.', '/') + ".java");
	}

	public static Class[] scan(String... scan_packages) {
		return UReflExt.getAllPackageClassess_viaDoubleSearch(true, ZNViewAno.class, ZNViewAno.class.getClassLoader(), scan_packages).toArray(new Class[0]);
	}
}
