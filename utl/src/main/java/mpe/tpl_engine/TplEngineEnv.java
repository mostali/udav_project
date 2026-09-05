package mpe.tpl_engine;

import lombok.Getter;
import mpc.env.AP;
import mpe.str.IRegex;
import mpe.str.URx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class TplEngineEnv {

	public static final TplEngineEnv _INSTANCE = new TplEngineEnv();

	private @Getter IRegex REGEX;

	private final @Getter List<Path> STORES = new ArrayList<>();

	public static TplEngineEnv get() {

		if (_INSTANCE.REGEX != null) {
			return _INSTANCE;
		}

		//
		// INIT

		{
			_INSTANCE.REGEX = URx.PlaceholderRegex.SIMPLE;
			TplEngine.L.info("Init REGEX:" + _INSTANCE.REGEX);
		}

		{
			Path dir = AP.getAs("app.cmd.support-sql", Path.class, Paths.get("support-sql"));
			boolean hasDirectory = Files.isDirectory(dir);
			_INSTANCE.STORES.add(dir);
			if (hasDirectory) {
				TplEngine.L.info("Init from resources disabled, because exist STORE:" + dir);
			} else {
				ResourceUtils.copyResourceDirectory("/support-sql", dir);
				TplEngine.L.info("Init from resources new STORE:" + dir);
			}
			TplEngine.L.info("Init STORE:" + _INSTANCE.STORES);

		}

		return _INSTANCE;
	}

}