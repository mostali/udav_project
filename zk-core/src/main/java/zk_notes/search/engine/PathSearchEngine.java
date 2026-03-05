package zk_notes.search.engine;

import lombok.RequiredArgsConstructor;
import mpc.env.AP;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpu.IT;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class PathSearchEngine extends SearchEngine<Path> {

//	{
//		super.setSearchMode(SearchFileMode.ANY_FILE_DIR);
//	}

	enum SearchFileMode {
		FILE, ANY_FILE_DIR, WILDCARD
	}

	public PathSearchEngine(Path path) {
		super(path.toString());
	}


	public Collection<Path> searchImpl(String wildcard, int count) {
		Collection<Path> paths = searachAllByMode(pathSrcSearch(), wildcard, null, (SearchFileMode) getSearchMode());
		return paths;
	}

	@Override
	protected Collection<Path> searchImpl(Predicate<Path> searchPredicate, int count) {
		Collection<Path> paths = searachAllByMode(pathSrcSearch(), null, searchPredicate, (SearchFileMode) getSearchMode());
		return paths;
	}

	private static @NotNull Collection<Path> searachAllByMode(Path path, String wildcard, Predicate<Path> searchPredicate, SearchFileMode searchMode) {
		switch (searchMode) {
			case ANY_FILE_DIR:
				return UFS.SEARCH.searchAny(path, IT.NN(searchPredicate), false);
			case FILE:
				return UFS.SEARCH.searchFiles(path, IT.NN(searchPredicate), false);
			case WILDCARD:
				return UFS.SEARCH.searchFilesWithWc(path, wildcard);
			default:
				throw new WhatIsTypeException(searchMode);
		}
	}


	@RequiredArgsConstructor
	public static class RelDirPredicate implements Predicate<Path> {

		private final Path fromDIr;
		private final String searchPart;

		@Override
		public boolean test(Path p) {
			if (searchPart == null) {
				return true;
			}
			Path relativize = fromDIr.relativize(p);
//			if (relativize == null) {
//				return false;
//			}
			return relativize.toString().contains(searchPart);
		}
	}
}
