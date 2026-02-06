//package mpc.types.opts;
//
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@RequiredArgsConstructor
//public class SeqOptionsMap implements Map<String, List<String>> {
//	final SeqOptions opts;
//
//	public static SeqOptionsMap of(String[] args) {
//		return new SeqOptionsMap(SeqOptions.of(args));
//	}
//
//	@Override
//	public int size() {
//		return opts.getCmdOpts().size();
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return  opts.getCmdOpts().isEmpty();
//	}
//
//	@Override
//	public boolean containsKey(Object key) {
//		return opts.conget;
//	}
//
//	@Override
//	public boolean containsValue(Object value) {
//		return false;
//	}
//
//	@Override
//	public List<String> get(Object key) {
//		return List.of();
//	}
//
//	@Override
//	public @Nullable List<String> put(String key, List<String> value) {
//		return List.of();
//	}
//
//	@Override
//	public List<String> remove(Object key) {
//		return List.of();
//	}
//
//	@Override
//	public void putAll(@NotNull Map<? extends String, ? extends List<String>> m) {
//
//	}
//
//	@Override
//	public void clear() {
//
//	}
//
//	@Override
//	public @NotNull Set<String> keySet() {
//		return Set.of();
//	}
//
//	@Override
//	public @NotNull Collection<List<String>> values() {
//		return List.of();
//	}
//
//	@Override
//	public @NotNull Set<Entry<String, List<String>>> entrySet() {
//		return Set.of();
//	}
//}
