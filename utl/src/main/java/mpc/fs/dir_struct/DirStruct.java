package mpc.fs.dir_struct;

import lombok.SneakyThrows;
import mpc.fs.RW;
import mpc.types.ruprops.RuProps;
import mpc.args.ARG;
import mpc.fs.UFS_BASE;
import mpc.json.GsonMap;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirStruct {
	public final String fd_name;

	public static final DirStruct SELF = new DirStruct(".");

	public DirStruct(String fd_name) {
		this.fd_name = fd_name;
	}

	public Path path(Path struct) {
		return getPath(struct);
	}

	public String name(Path entity) {
		return path(entity).getFileName().toString();
	}

	public Path getPath(Path entity) {
		return ".".equals(fd_name) ? entity : entity.resolve(fd_name);
	}

	public Path getPathWith(Path from, String with) {
		return getPath(from).resolve(with);
	}

	public RuProps getRuProps(Path from, String file, boolean... syncWrite) {
		return RuProps.of(path(from).resolve(file)).syncWrite(ARG.isDefEqTrue(syncWrite));
	}

	public GsonMap getGsonMap(Path from, String file) {
		return GsonMap.read(path(from).resolve(file));
	}

	public <T> T getAs(Path from, String file, Class<T> asType, T... defRq) {
		return RW.readAs(path(from).resolve(file), asType, defRq);
	}

	@SneakyThrows
	public Path moveToMe(Path struct, Path moved) {
		return UFS_BASE.MV.moveIn_(moved, path(struct));
	}

	@SneakyThrows
	public Path moveMeTo(Path struct, Path dst) {
		return UFS_BASE.MV.moveIn_(path(struct), dst);
	}

	public Path moveMe(Path struct, Path dst, Boolean mkdirs_mkdir_ornot, CopyOption... copyOptions) {
		return UFS_BASE.MV.move(path(struct), dst, mkdirs_mkdir_ornot, copyOptions);
	}

	public Path rename(Path struct, String oldChild, String newChild, CopyOption... copyOptions) {
		Path path = path(struct);
		return UFS_BASE.MV.move(path.resolve(oldChild), path.resolve(newChild), null, copyOptions);
	}

	public Path renameMe(Path entity, String newName, CopyOption... copyOptions) {
		Path path = path(entity);
		return UFS_BASE.MV.rename(path, newName, null, copyOptions);
	}

	@SneakyThrows
	public Path writeToFile(Path entity, String file, String content, Boolean mkDirs_mkdir_orNot) {
		return writeToFile_(entity, file, content, mkDirs_mkdir_orNot);
	}

	public Path writeToFile_(Path entity, String file, String content, Boolean mkDirs_mkdir_orNot) throws IOException {
		return DirStructRW.writeToChild_(path(entity), file, content, mkDirs_mkdir_orNot);
	}

	@SneakyThrows
	public Path writeToDir(Path entity, String child_dir, String file, String content, Boolean mkDirs_mkdir_orNot) {
		return writeToDir_(entity, child_dir, file, content, mkDirs_mkdir_orNot);
	}

	public Path writeToDir_(Path entity, String child_dir, String file, String content, Boolean mkDirs_mkdir_orNot) throws IOException {
		return DirStructRW.writeToChild2_(path(entity), child_dir, file, content, mkDirs_mkdir_orNot);
	}


	public Path mkDir_(Path entity, String child) throws IOException {
		return Files.createDirectory(path(entity).resolve(child));
	}

	public Path mkDirs_(Path entity, String child) throws IOException {
		return Files.createDirectories(path(entity).resolve(child));
	}

	public void setPropsProperty(Path entity, String filename, String key, String value) {
		getRuProps(path(entity), filename, true).setString(key, value);
	}

	public boolean existsDir(Path struct) {
		return Files.isDirectory(path(struct));
	}

	public boolean existsFile(Path struct, String filename) {
		return Files.isDirectory(getPathWith(struct, filename));
	}
}
