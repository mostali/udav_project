package zk_old_core.admin.sys;

import zk_com.editable.TextboxFile;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author dav 10.01.2022   01:27
 */
public class FileMapDiv extends MapDiv {
	private final Path path;

	public FileMapDiv(Path path, String name, Map<Object, Object> map) {
		super(name, map);
		this.path = path;

	}

	@Override
	protected void onBuildComponent() {

//		appendChild(new EditableValueFile(path));
		appendChild(TextboxFile.of(path));

		super.onBuildComponent();
	}
}
