package mpe.app;

import mpc.env.Env;
import mpc.fs.Ns;
import mpe.NT;

import java.nio.file.Path;

public class AppCore0 {

	public final Ns ns;

//	public static AppCore APP_CORE = null;

//	public static AppCore get() {
//		return APP_CORE == null ? (APP_CORE = new AppCore(Env.getAppNameOrDef())) : APP_CORE;
//	}

	public AppCore0(String core_namespace) {
		this(Env.getDefaultDataDir(), core_namespace);
	}

	public AppCore0(Path rootDir, String core_namespace) {
		ns = Ns.of(rootDir, core_namespace);
	}

	public static AppCore0 of() {
		return of(Env.getAppNameOrDef());
	}

	public static AppCore0 of(String app_namespace) {
		return new AppCore0(app_namespace);
	}

	public static AppCore0 of(NT app) {
		return new AppCore0(app.nameLC());
	}

	//
	//

	public Path path() {
		return namespace().toPath();
	}

	public Path path(String child) {
		return namespace().path(child);
	}

	public Ns namespace() {
		return ns;
	}

	public Ns resolve(String ns_child) {
		return ns.getNamespaceOfChild(ns_child);
	}

	public Ns namespace(String ns_child) {
		return resolve(ns_child);
	}


}
