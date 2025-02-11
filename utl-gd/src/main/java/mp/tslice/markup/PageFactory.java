package mp.tslice.markup;

import mp.gd.ApiGdExt;
import mp.tslice.SliceIterator;
import mp.tslice.USlice;
import org.jsoup.nodes.Element;
import mpu.core.ARR;
import mpu.IT;
import mpc.arr.QUEUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageFactory {

	public static final String PREFIX_COMMENT = "///";
	public static final String TYPE_MARKER_LINK_PATTERN = "^^";
	public static final String TYPE_MARKER_PAGE = "@";

	private final static Map<String, ApiGdExt.GoogleSheetData> _SHEETS = QUEUE.cache_map_FILO(30);
	private final static Map<String[], List> SHEETS_SLICES = QUEUE.cache_map_FILO(30);

	private final static Map<String, UNode.PageBuilderHtml> PAGE_BUILDERS = QUEUE.cache_map_FILO(30);

	public static ApiGdExt.GoogleSheetData getGoogleSheetDataLoader(String[] sheetContext) {
		String key = sheetContext[1] + sheetContext[2];
		ApiGdExt.GoogleSheetData sheetDataLoader = _SHEETS.get(key);
		if (sheetDataLoader != null) {
			return sheetDataLoader;
		}
		sheetDataLoader = ApiGdExt.GoogleSheetData.of(ApiGdExt.SheetInfoWithDataRange.of(sheetContext));

		_SHEETS.put(key, sheetDataLoader);
		return sheetDataLoader;
	}

	public static UNode.PageBuilderHtml getPageBuilderOrCreate(String userAliasName, String sheetId, String listWithRange) {
		ApiGdExt.SheetInfoWithDataRange sheetInfo = ApiGdExt.SheetInfoWithDataRange.of(userAliasName, sheetId, listWithRange);
		return getPageBuilderOrCreate(sheetInfo.toContextKey());
	}

	public static UNode.PageBuilderHtml getPageBuilderOrCreate(String[] sheetInfo) {
		return getPageBuilderOrCreate(sheetInfo, null); // null - первое попавшееся
	}


	public static UNode.PageBuilderHtml getPageBuilderOrCreate(String[] sheetInfo, String pageName) {
		String key = sheetInfo + pageName;
		UNode.PageBuilderHtml builder = PAGE_BUILDERS.get(key);
		if (builder != null) {
			return builder;
		}
		ApiGdExt.GoogleSheetData sheetDataLoader = getGoogleSheetDataLoader(sheetInfo);
		IT.notNull(sheetDataLoader, "Sheet Data Loader is null", sheetInfo);
		IT.notEmpty(sheetDataLoader.get_VALUES(), "Sheet Data is empty", sheetInfo);

		List<List<Object>> slice0 = findPageSlice(sheetInfo, pageName);

		IT.notEmpty(slice0, "Page slice is empty", sheetInfo, pageName);

		builder = new UNode.PageBuilderHtml(slice0) {
			@Override
			public String[] getContext() {
				return sheetInfo;
			}
		};
		PAGE_BUILDERS.put(key, builder);

		return builder;
	}

	public static List<List<Object>> findLinkPattern(String[] sheetInfo, String linkPatternName) {
		return findSlice(sheetInfo, TYPE_MARKER_LINK_PATTERN, linkPatternName);
	}

	public static List<List<Object>> findPageSlice(String[] sheetInfo, String pageName) {
		return findSlice(sheetInfo, TYPE_MARKER_PAGE, pageName);
	}

	private static List<List<Object>> findSlice(String[] sheetInfo, Object... nvx) {
		List<List<List<Object>>> slices = findSheetSlice(sheetInfo);
		for (List<List<Object>> slice : slices) {
			if (USlice.isSliceEquals(slice, 0, nvx)) {
				return slice;
			}
		}
		return null;
	}

	private static List<List<List<Object>>> findSheetSlice(String[] sheetContext) {
		String key = sheetContext[1] + sheetContext[2];
		List<List<List<Object>>> slices = SHEETS_SLICES.get(key);
		if (slices != null) {
			return slices;
		}
		slices = initSlices(sheetContext);
		return IT.notNull(slices, "List with slices is null, after init", sheetContext);
	}

	private static List initSlices(String[] sheetInfo) {
		ApiGdExt.GoogleSheetData sheetDataLoader = getGoogleSheetDataLoader(sheetInfo);
		SliceIterator it = SliceIterator.createIterator(sheetDataLoader.get_VALUES());
		List slices = new ArrayList();
		do {
			List<List<Object>> slice0 = it.next(0);
			if (slice0 == null) {
				break;
			} else if (slice0.isEmpty()) {
				continue;
			} else {
				slices.add(slice0);
			}
		} while (true);

		return slices;
	}

	public static Element buildFirstPage(String userAliasName, String sheetId, String listWithRange, String... pageName) {
		ApiGdExt.SheetInfoWithDataRange sheetInfo = ApiGdExt.SheetInfoWithDataRange.of(userAliasName, sheetId, listWithRange);
		UNode.PageBuilderHtml builder = PageFactory.getPageBuilderOrCreate(sheetInfo.toContextKey(), ARR.defIfNull(null, pageName));
		return builder.onBuild().getPageElement();
	}
}
