//package mpc.html;
//
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.Serializable;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class StyleMap implements Serializable {
//
//	public static final Logger L = LoggerFactory.getLogger(StyleMap.class);
//
//	private final Map<String, String> map;
//
//	public StyleMap() {
//		this(new LinkedHashMap());
//	}
//
//	public StyleMap(Map map) {
//		this.map = map;
//	}
//
//	public static StyleMap of(String style) {
//		return of(newStyleMap(style));
//	}
//
//	public static StyleMap of(Map styleMap) {
//		return new StyleMap(styleMap);
//	}
//
//	public Map<String, String> map() {
//		return map;
//	}
//
//	public StyleMap add(String style) {
//		map.putAll(newStyleMap(style));
//		return this;
//	}
//
//	@NotNull
//	protected static Map<String, String> newStyleMap(String style) {
//		return STYLE.toMap(style);
//	}
//
//	public String toStringStyle() {
//		return STYLE.toString(map());
//	}
//
//	protected boolean isEmpty() {
//		return map().isEmpty();
//	}
//}
