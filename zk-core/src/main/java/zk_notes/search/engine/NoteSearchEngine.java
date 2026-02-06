package zk_notes.search.engine;

import lombok.Getter;
import lombok.Setter;
import mpc.arr.STREAM;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.map.BootContext;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import zk_notes.node.NodeDir;
import zk_os.core.Sdn;
import zk_os.walkers.TotalFormsWalker;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NoteSearchEngine<T> extends SearchEngine<T> {

	private final SearchNoteMode searchMode;
	@Setter
	private @Getter Path searchPath;

	private Path getSearchPath() {
		return searchPath == null ? APP.LOCATION.getAppDataDir(true) : searchPath;
	}

	public static SearchEngine of(SearchNoteMode type) {
		return new NoteSearchEngine(type);
	}

	@Override
	public SearchNoteMode getSearchMode() {
		return searchMode;
	}

	public enum SearchNoteMode {
		NOTE, NOTE_VAL, PAGE, FILE, WILDCARD, AP, AP_VAL
	}


	public NoteSearchEngine(SearchNoteMode type) {
		super();
		this.searchMode = type;
	}


	public Collection<T> searchImpl(String searchPart, int count) {
		Collection founded;
		switch (searchMode) {

			case NOTE: {
				Predicate<NodeDir> nodeDirPredicate = needle -> X.empty(searchPart) ? true : needle.nodeName().contains(searchPart);
				founded = TotalFormsWalker.findAllNotes(nodeDirPredicate, count);
				break;
			}

			case NOTE_VAL:
				Predicate<NodeDir> nodeDataPredicate = needle -> {
					if (X.empty(searchPart)) {
						return true;
					} else if (needle.nodeDataStrCached() == null) {
						return false;
					}
					return needle.nodeDataStrCached().contains(searchPart);
				};
				founded = TotalFormsWalker.findAllNotes(nodeDataPredicate, count);
				break;

			case PAGE: {
				Predicate<Sdn> findPagePredicate = needle -> X.empty(searchPart) ? true : needle.val().contains(searchPart);
				founded = TotalFormsWalker.findAllPages(findPagePredicate, count);
				break;
			}

			case AP_VAL:
			case AP: {

				BootContext bootContext = BootContext.get();
				Predicate<String> searchPredicate = k -> searchPart == null ? true : k.contains(searchPart);
				Map<Pare<BootContext.ApType, String>, Pare<String, List<String>>> mapKeys;
				if (searchMode == SearchNoteMode.AP_VAL) {
					mapKeys = bootContext.findValuesMapByVal(searchPredicate, false);
				} else {
					mapKeys = bootContext.findValuesMapByKey(searchPredicate, false);
				}
				if (X.empty(mapKeys)) {
					founded = ARR.as();
				} else {
					List<Pare3<BootContext.ApType, String, String>> collect = mapKeys.entrySet().stream().map(kv -> {
						Pare<BootContext.ApType, String> key = kv.getKey();
						Pare<String, List<String>> value = kv.getValue();
						List<Pare3<BootContext.ApType, String, String>> pares = STREAM.mapToList(value.val(), v -> Pare3.of(key.key(), value.key(), v));
						return pares;
					}).flatMap(List::stream).collect(Collectors.toList());

//					Collection<String> rslt = mapKeys.values().stream().filter(X::NN).flatMap(List::stream).filter(X::NN).collect(Collectors.toList());
					founded = collect;
//				}
				}
				break;
			}

			case FILE:
			case WILDCARD: {

				Path searchPath = getSearchPath();
				PathSearchEngine searchEngine = new PathSearchEngine(searchPath);
				if (searchMode == SearchNoteMode.WILDCARD) {
					searchEngine.setSearchMode(PathSearchEngine.SearchFileMode.WILDCARD);
					founded = searchEngine.search(searchPart, count);
				} else {
					searchEngine.setSearchMode(PathSearchEngine.SearchFileMode.FILE);
					PathSearchEngine.RelDirPredicate relDirPredicate = new PathSearchEngine.RelDirPredicate(searchPath, searchPart);
					founded = searchEngine.search(relDirPredicate, count);
				}

				break;
			}

			default:
				throw new WhatIsTypeException(searchMode);
		}

		return founded;
	}

	@Override
	protected Collection<T> searchImpl(Predicate<T> predicateItem, int count) {
		Collection founded;
		switch (searchMode) {

			case NOTE: {
				founded = TotalFormsWalker.findAllNotes((Predicate) predicateItem, count);
				break;
			}

			case PAGE: {
				founded = TotalFormsWalker.findAllPages((Predicate) predicateItem, count);
				break;
			}

			case FILE: {
				Path appDataDir = APP.LOCATION.getAppDataDir(true);
				PathSearchEngine searchEngine = new PathSearchEngine(appDataDir);
				founded = searchEngine.search((Predicate) predicateItem, count);

				break;
			}

			case AP:
			case WILDCARD:
				throw new UnsupportedOperationException("use wildcard");

			default:
				throw new WhatIsTypeException(searchMode);
		}

		return founded;
	}


}
