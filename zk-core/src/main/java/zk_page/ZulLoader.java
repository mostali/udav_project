package zk_page;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.core.RW;
import mpc.fs.fd.RES;
import org.zkoss.zk.ui.Component;

import java.nio.file.Path;
import java.util.Map;

public class ZulLoader {
	@SneakyThrows
	public static String loadContentRsrc(String path) {
		return RES.readString(path);
	}

	@SneakyThrows
	public static String loadContentFile(Path path) {
		return RW.readString(path);
	}

	@SneakyThrows
	public static Component loadComponentFromRsrc(String path, Component... parent) {
		return loadComponent(loadContentRsrc(path), parent);
	}

	@SneakyThrows
	public static Component loadComponentFromRsrc(String path, Map context, Component... parent) {
		return loadComponent(loadContentRsrc(path), context, parent);
	}

	@SneakyThrows
	public static Component loadComponentFromRsrc(String path, Component parent, Map context) {
		return loadComponent(loadContentRsrc(path), context, parent);
	}

	public static Component buildComponentFromFile(Path fromFile, Component... parent) {
		return loadComponent(loadContentFile(fromFile), parent);
	}

	public static Component loadComponent(String content, Component... parent) {
		return ZKC.loadComponentOrErrComponent(ARG.toDefOr(null, parent), content);
	}

	public static Component loadComponent(String content, Map context, Component... parent) {
		return ZKC.loadComponentOrErrComponent(ARG.toDefOr(null, parent), context, content);
	}

	public static Component loadComponent(Component parent, Map context, String content, Object... args) {
		return ZKC.loadComponentOrErrComponent(ARG.toDefOr(null, parent), context, content, args);
	}
}
