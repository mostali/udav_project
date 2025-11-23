package mpc.json;

import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpu.X;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;
import mpc.fs.path.IPath;
import mpc.fs.fd.FILE;
import mpc.fs.fd.RES;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

@lombok.ToString
public class PerGsonMap<V> extends GsonMap<V> implements IPath {

	private final String path;
	private transient Path path0;

	private boolean writable = false;
	private boolean pretty = false;

	public PerGsonMap<V> writable(boolean... writable) {
		this.writable = ARG.isDefNotEqFalse(writable);
		return this;
	}

	public PerGsonMap<V> pretty(boolean... pretty) {
		this.pretty = ARG.isDefNotEqFalse(pretty);
		return this;
	}

	public boolean equalsAsJson(GsonMap json) {
		if (this == json) {
			return true;
		}
		return json == null ? false : toStringJson().equals(json.toStringJson());
	}

	//	@Override
	//	public boolean equals(Object o) {
	//		if (this == o) {
	//			return true;
	//		}
	//		if (!(o instanceof PerGsonMap)) {
	//			return false;
	//		}
	//		PerGsonMap<?> that = (PerGsonMap<?>) o;
	//		return Objects.equals(path, that.path);
	//	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@SneakyThrows
	public static PerGsonMap ofOrCreate(Path path) {
		UFS.MKFILE.createEmptyFileIfNotExist(path, true);
		return of(path);
	}

	public static PerGsonMap of(Class rsrsClass, String rsrcPath) {
		RES rsrc = RES.of(rsrsClass, rsrcPath, FILE.class);
		String json = rsrc.cat();
		return new PerGsonMap(UGson.toMapFromString(json), rsrc.toPath());
	}

	public static PerGsonMap of(Path path) {
		return of(UGson.toMapFromString(path), path);
	}

	public static PerGsonMap of(Map map, Path path) {
		return new PerGsonMap(map, path);
	}

	//
	public static PerGsonMap of(String key, Map json, Path path) {
		return new PerGsonMap(key, json, path);
	}

	public PerGsonMap(Path path) {
		this(null, GsonMap.newEmptyMap(), path);
	}

	public PerGsonMap(Map json, Path path) {
		this(null, json, path);
	}

	public PerGsonMap(String key, Map json, Path path) {
		super(key, json);
		this.path = path == null ? null : path.toString();
		this.path0 = path;
	}

	@Override
	public Path toPath() {
		return path0 != null ? path0 : (path0 = Paths.get(path));
	}

	@Override
	public void putAll(Map map) {
		super.putAll(map);
		if (writable) {
			write();
		}
	}

	@Override
	public V put(String key, V value) {
		V put = super.put(key, value);
		if (writable) {
			write();
		}
		return put;
	}

	public void write() {
		write(toPath(), this, pretty, false);
		if (L.isDebugEnabled()) {
			L.debug("Write PerGsonMap 'file://{}'", path);
		}
	}

	@Deprecated //why off
	public void writeAndOffWrite() {
		writable(true).write();
		writable(false);
	}

	@Override
	public V remove(Object key) {
		V removed = super.remove(key);
		if (writable) {
			write();
		}
		return removed;
	}

	public PerGsonMap getAsGsonMapOrCreate(String key) {
		PerGsonMap child = (PerGsonMap) super.getAsGsonMapOrCreate(key);
		writeAndOffWrite();
		return child;
	}

	@Override
	public PerGsonMap getAsGsonMap(String key, GsonMap... defRq) {
		GsonMap childJson = getAs(key, GsonMap.class, null);
		if (childJson != null) {
			ChildPerGsonMap childPerGsonMap = new ChildPerGsonMap(this, key, childJson.map);
			return childPerGsonMap;
		}
		return childJson != null ? newEmpty(true) : (PerGsonMap) ARG.toDefThrow(() -> new RequiredRuntimeException("PerGsonMap Child '%s' not found, from map '%s'", key, toPath()), defRq);
	}


	public PerGsonMap child(String key, PerGsonMap... defRq) {
		return (PerGsonMap) super.child(key, defRq);
	}

	public PerGsonMap childOrCreate(String key) {
		return (PerGsonMap) super.childOrCreate(key);
	}

	protected PerGsonMap newChild() {
		return new ChildPerGsonMap(this, key, map);
	}

	@Override
	protected PerGsonMap newEmpty(boolean... withParent) {
		PerGsonMap parent = this;
		return new PerGsonMap(toPath()) {
			@Override
			protected PerGsonMap parent() {
				return ARG.isDefEqTrue(withParent) ? parent : null;
			}
		};
	}

	@Override
	protected GsonMap parent() {
		return null;
	}

	@Override
	public String toString() {
		return X.toStringRfl(this);
	}

	public PerGsonMap clone() {
		return PerGsonMap.of(toPath());
	}

	@lombok.ToString
	private static class ChildPerGsonMap extends PerGsonMap {
		private final PerGsonMap parent;

		public ChildPerGsonMap(PerGsonMap parent, String key, Map childJson) {
			super(key, childJson, parent.toPath());
			this.parent = parent;
		}

		@Override
		public void write() {
			parent.put(key, this.map);
			parent.write();
		}

		@Override
		protected GsonMap parent() {
			return parent;
		}

		@Override
		public String toString() {
			return scn();
		}
	}
}
