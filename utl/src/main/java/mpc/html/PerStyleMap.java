//package mpc.html;
//
//import mpc.json.PerGsonMap;
//
//import java.nio.file.Path;
//import java.util.Map;
//
//public class PerStyleMap extends StyleMap {
//	private final Path path;
//
//	public PerStyleMap(String style, Path path) {
//		this(STYLE.toMap(style), path);
//	}
//
//	public PerStyleMap(Map<String, String> style, Path path) {
//		super(style);
//		this.path = path;
//
//	}
//
//	public void write() {
//		if (super.map() instanceof PerGsonMap) {
//			PerGsonMap pgm = (PerGsonMap) super.map();
//			pgm.writeAndOffWrite();
//		} else {
//			if (L.isWarnEnabled()) {
//				L.warn("SKIP WRITE:" + getClass().getSimpleName());
//			}
//		}
//	}
//
//	@Override
//	public StyleMap add(String style) {
//		super.add(style);
//		write();
//		return this;
//	}
//
//}
