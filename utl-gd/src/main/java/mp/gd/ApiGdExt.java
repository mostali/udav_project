package mp.gd;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Setter;
import lombok.SneakyThrows;
import mpe.db.datasrc.DbType;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.IT;
import mpc.env.AP;
import mpc.exception.FIllegalStateException;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.Sys;
import mpu.X;
import mpu.func.Function3T;
import mpu.func.FunctionV4T;
import udav_net_exp.m2_repo.M2Repo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

//FRESH GUIDE
//https://developers.google.com/sheets/api/quickstart/java
//
//https://developers.google.com/sheets/api/guides/values
//https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption
//other libs
//https://github.com/softcom-lab/ibox/tree/master/src/main/java/edu/csupomona/cs585/ibox/sync
public class ApiGdExt extends ApiGd {

	public static final String APK_GS_KEY_PATH = "gs.key.path";

	public static final Function3T<Path, String, String, List<List<Object>>, IOException> gsLoader = (pathKey, sheetid, sheetrange) -> (List<List<Object>>) ApiGdExt.loadValues_AuthFileKey(pathKey, sheetid, sheetrange, Collection.class);
	public static final FunctionV4T<Path, String, String, List<List<Object>>, IOException> gsWriter = (pathKey, sheetid, sheetrange, rows) -> ApiGdExt.writeValues_AuthFileKey(pathKey, sheetid, sheetrange, rows);

//	static {
//		initLoaders();
//	}

	public static void initLoaders() {
		if (DbType.LOADER_GS.get() == null) {
			DbType.LOADER_GS.set(gsLoader);
		}
		if (DbType.WRITER_GS.get() == null) {
			DbType.WRITER_GS.set(gsWriter);
		}
	}

	private static class CredentialsByUserAlias {
		private String[] credentialsPathLib;
		private static final String[] DEFAULT_BLANK_CREDENTIALS_LIB = new String[]{"acc.gd", null, "1.0", "gd.key.json"};

		private final String userAlias;

		public CredentialsByUserAlias(String userAlias) {
			this.userAlias = userAlias;
		}

		public static Path toFileKey_FromEnvRepo(String userAlias) throws IOException {
			String[] cred = getUserCredentialsByAliasName(userAlias);
			Path fileKeyPath = M2Repo.EnvRepo.findFileKeyPath_InEnvRepo(cred);
			return fileKeyPath;
		}

		public String[] getUserCredentialsByAliasName() {
			if (credentialsPathLib == null) {
				credentialsPathLib = getUserCredentialsByAliasName(userAlias);
			}
			return credentialsPathLib;
		}

		public static String[] getUserCredentialsByAliasName(String name) {
			String[] credentials = Arrays.copyOf(DEFAULT_BLANK_CREDENTIALS_LIB, DEFAULT_BLANK_CREDENTIALS_LIB.length);
			credentials[1] = name;
			return credentials;
		}
	}

	public static class SheetInfoWithDataRange {
		public final String sheetId;
		public final String listWithRange;
		//
		private CredentialsByUserAlias credentialsByUserAlias;

		public static SheetInfoWithDataRange of(String userAliasName, String sheetId, String listWithRange) {
			listWithRange = ApiGdExt.GoogleSheetData.ListRange.toPattern(listWithRange);//, "A", "Z"
			SheetInfoWithDataRange sheetInfo = new SheetInfoWithDataRange(sheetId, listWithRange);
			sheetInfo.setUserCredentialsAliasName(userAliasName);
			return sheetInfo;
		}

		public static SheetInfoWithDataRange of(String[] sheetInfo) {
			return new SheetInfoWithDataRange(sheetInfo[1], sheetInfo[2]).setUserCredentialsAliasName(sheetInfo[0]);
		}

		@Override
		public String toString() {
			return "SheetInfo{" + "sheetId='" + sheetId + '\'' + ", listWithRange='" + listWithRange + '\'' + '}';
		}

		public SheetInfoWithDataRange setUserCredentialsAliasName(String userAlias) {
			this.credentialsByUserAlias = new CredentialsByUserAlias(userAlias);
			return this;
		}

		public String getUserCredentialsAliasName() {
			return getCredentialsPathLib()[1];
		}

		public SheetInfoWithDataRange(String sheetId, String listWithRange) {
			this.sheetId = sheetId;
			this.listWithRange = listWithRange;
		}

		public String[] getCredentialsPathLib() {
			return this.credentialsByUserAlias.getUserCredentialsByAliasName();
		}

		public String[] toContextKey() {
			return new String[]{getUserCredentialsAliasName(), sheetId, listWithRange};
		}
	}

	public static class GoogleSheetData {
		private final SheetInfoWithDataRange sheetInfo;
		private ValueRange responseValueRange;

		public GoogleSheetData(String sheetId, String listWithRange) {
			sheetInfo = new SheetInfoWithDataRange(sheetId, listWithRange);
		}

		public GoogleSheetData(SheetInfoWithDataRange sheetInfo) {
			this.sheetInfo = sheetInfo;
		}

		@Deprecated
		public void setUserCredentialsAliasName(String userAliasName) {
			getSheetInfo().setUserCredentialsAliasName(userAliasName);
		}

//		public String[] getCredentialsPathLib() {
//			return getSheetInfo().getCredentialsPathLib();
//		}

		public SheetInfoWithDataRange getSheetInfo() {
			return sheetInfo;
		}

		public ValueRange getResponseValueRange(boolean... fresh) {
			if (responseValueRange == null || ARG.isDefEqTrue(fresh)) {
				if (L.isInfoEnabled()) {
					L.info("Load Lazy data :" + getSheetInfo());
				}
				responseValueRange = loadValues();
			}
			return responseValueRange;
		}

		private ValueRange loadValues() {
			try {

				return ApiGd.loadValues_With_FileKey(fileKeyPath(), getSheetInfo().sheetId, getSheetInfo().listWithRange);
			} catch (Exception e) {
				return X.throwException(e);
			}
		}

		private @Setter Path fileKeyPath = null;

		@SneakyThrows
		private Path fileKeyPath() {
			if (fileKeyPath != null) {
				return fileKeyPath;
			}
			//DEPRECATED
			fileKeyPath = M2Repo.EnvRepo.findFileKeyPath_InEnvRepo(this.sheetInfo.getCredentialsPathLib());
			return fileKeyPath;
		}

		@Deprecated
		private Spreadsheet loadSpreadsheet(boolean... isIncludeData) {
			try {
				Path fileKeyPath = M2Repo.EnvRepo.findFileKeyPath_InEnvRepo(this.sheetInfo.getCredentialsPathLib());
				Spreadsheet spreadsheet = ApiGdExt.loadSpreadsheet(fileKeyPath, getSheetInfo().sheetId, Arrays.asList(getSheetInfo().listWithRange), isIncludeData);
				return spreadsheet;
			} catch (IOException e) {
				return X.throwException(e);
			}
		}

		public static GoogleSheetData of(SheetInfoWithDataRange sheetInfo) {
			IT.notEmpty(sheetInfo.sheetId, "SheetId argument is empty", sheetInfo);
			IT.notEmpty(sheetInfo.listWithRange, "ListWithRange argument is empty", sheetInfo);
			IT.notNull(sheetInfo.getCredentialsPathLib(), "CredentialsPathLib argument is empty", sheetInfo);
			return new GoogleSheetData(sheetInfo);
		}

		@Deprecated
		public static GoogleSheetData of(String userAliasName, String sheetId, String listWithRange) {
			IT.notEmpty(sheetId, "SheetId argument is empty");
			IT.notEmpty(listWithRange, "ListWithRange argument is empty");
			IT.notEmpty(userAliasName, "UserAliasName argument is empty");
			ListRange.check(listWithRange);
			GoogleSheetData googleSheetData = new GoogleSheetData(sheetId, listWithRange);
			googleSheetData.setUserCredentialsAliasName(userAliasName);
			return googleSheetData;
		}
		public static GoogleSheetData of(Path pathGdKey, String sheetId, String listWithRange) {
			IT.notEmpty(sheetId, "SheetId argument is empty");
			IT.notEmpty(listWithRange, "ListWithRange argument is empty");
			IT.isFileExist(pathGdKey, "PathKey file exist");
			ListRange.check(listWithRange);
			GoogleSheetData googleSheetData = new GoogleSheetData(sheetId, listWithRange);
			googleSheetData.setFileKeyPath(pathGdKey);;
			return googleSheetData;
		}
		public List<List<Object>> get_VALUES(boolean... fresh) {
			return getResponseValueRange(fresh).getValues();
		}

		public List<GridData> get_GridData(boolean includeData, Integer... sheetIndexOr0) {
			List<Sheet> sheets = loadSpreadsheet(includeData).getSheets();
			int _sheetIndex = ARG.isDef(sheetIndexOr0) ? sheetIndexOr0[0] : 0;
			return sheets.get(_sheetIndex).getData();
		}

		public static class ListRange {
			public static String toPattern(String list, String x, String y) {
				return IT.notEmpty(list) + "!" + IT.notEmpty(x) + ":" + IT.notEmpty(y);
			}

			public static String toPattern(String list) {
				return IT.notEmpty(list);
			}

			public static void check(String listWithRange) {
				String[] listWithRange__ = listWithRange.split("!", 2);
				IT.notEmpty(listWithRange__[0], "SheetName is empty");
			}
		}

	}

	public static Credential getCredentials(String userAlias) throws IOException {
		Path fileKeyPath = getCredentialsFile(userAlias);
		Credential credential = authorize(fileKeyPath);
		return credential;
	}

	public static Path getCredentialsFile(String userAlias) throws IOException {
		return CredentialsByUserAlias.toFileKey_FromEnvRepo(userAlias);
	}

	/**
	 * *************************************************************
	 * ----------------------------- LOAD --------------------------
	 * *************************************************************
	 */

	public static Multimap<String, String> toMultimap(List<List<Object>> rows, boolean skipEmptyKey, Integer... index) {

		Integer keyIndex = ARG.toDefOr(0, index);

		List<List<Object>> rows_clone = (List<List<Object>>) ARR.cloneListWithList(rows);

		Multimap mmap = ArrayListMultimap.create();
		if (rows_clone.isEmpty()) {
			return mmap;
		}
		for (int i = 1; i < rows_clone.size(); i++) {//skip head orw
			List<Object> row = rows_clone.get(i);
			if (X.empty(row)) {
				continue;
			} else if (!ARR.isIndex(keyIndex, row)) {
				Sys.e("Is not index '%s' in row:\n" + row, keyIndex);
				continue;
			}
			String key = row.remove((int) keyIndex).toString();
			if (X.empty(key)) {
				if (!skipEmptyKey) {
					throw new RequiredRuntimeException("Except not empty key. Row:" + rows_clone);
				}
				L.trace("Skip:" + key + ":" + rows_clone);
				continue;
			}
			row.forEach(v -> {
				mmap.put(key, v);
			});
		}
		return mmap;
	}

	public static ValueRange loadValues_AuthAuto(String sheetId, String listWithRange) throws IOException {
		return loadValues_AuthAuto(sheetId, listWithRange, ValueRange.class);
	}

	public static <T> T loadValues_AuthAuto(String sheetId, String listWithRange, Class<T> returnAsType) throws IOException {
		String keyPath = AP.get(APK_GS_KEY_PATH, null);
		if (keyPath != null) {
			L.info("Auto configure '{}' '{}'", APK_GS_KEY_PATH, keyPath);
			return loadValues_AuthFileKey(Paths.get(keyPath), sheetId, listWithRange, returnAsType);
		}
		//Deprecated
		String userAlias = AP.get("gs.key.alias", null);
		if (userAlias != null) {
			L.info("Auto configure 'gs.key.alias' '{}'", userAlias);
			Path fileKeyPath = CredentialsByUserAlias.toFileKey_FromEnvRepo(userAlias);
			return loadValues_AuthFileKey(fileKeyPath, sheetId, listWithRange, returnAsType);
		}
		throw new FIllegalStateException("configure key gd. Set path to file key 'gs.key.path' or key alias 'gs.key.alias'");
	}

	public static ArrayList<List<Object>> loadValues_By_AnyAuth(Object userAlias_orPathKey, String sheetId, String range) throws IOException {
		return loadValues_By_AnyAuth(userAlias_orPathKey, sheetId, range, ArrayList.class);
	}

	public static <T> T loadValues_By_AnyAuth(Object userAlias_orPathKey, String sheetId, String range, Class<T> returnAsType) throws IOException {
		IT.NN(userAlias_orPathKey,"set userAlias_orPathKey");
//		List<List<Object>> values;
		T rsp = null;
		if (userAlias_orPathKey instanceof String) {
			rsp = ApiGdExt.loadValues_AuthUserAlias((String) userAlias_orPathKey, sheetId, range, returnAsType);
		} else if (userAlias_orPathKey instanceof Path) {
			rsp = ApiGdExt.loadValues_AuthFileKey((Path) userAlias_orPathKey, sheetId, range, returnAsType);
		} else if (userAlias_orPathKey instanceof Integer) {
			Integer mode = (Integer) userAlias_orPathKey;
			switch (mode) {
				case 0:
					rsp = ApiGdExt.loadValues_AuthAuto(sheetId, range, returnAsType);
					break;
				default:
					throw new WhatIsTypeException(mode);
			}
		} else {
			throw new WhatIsTypeException(userAlias_orPathKey.getClass() + "-->" + userAlias_orPathKey);
		}
		return rsp;
	}

	public static <T> T writeValues_AnyAuth(Object userAlias_orPathKey, String sheetId, String range, List rows) throws IOException {
		IT.NN(userAlias_orPathKey);
//		List<List<Object>> values;
		T rsp = null;
		if (userAlias_orPathKey instanceof String) {
			ApiGdExt.writeValues_AuthUserAlias((String) userAlias_orPathKey, sheetId, range, rows);
		} else if (userAlias_orPathKey instanceof Path) {
			ApiGdExt.writeValues_AuthFileKey((Path) userAlias_orPathKey, sheetId, range, rows);
		} else if (userAlias_orPathKey instanceof Integer) {
			Integer mode = (Integer) userAlias_orPathKey;
			switch (mode) {
				case 0:
					ApiGdExt.writeValues_AuthAuto(sheetId, range, rows);
					break;
				default:
					throw new WhatIsTypeException(mode);
			}
		} else {
			throw new WhatIsTypeException(userAlias_orPathKey.getClass() + "-->" + userAlias_orPathKey);
		}
		return rsp;
	}

	public static ValueRange loadValues_AuthUserAlias(String userAlias, String sheetId, String listWithRange) throws IOException {
		return loadValues_AuthUserAlias(userAlias, sheetId, listWithRange, ValueRange.class);
	}

	@Deprecated
	public static <T> T loadValues_AuthUserAlias(String userAlias, String sheetId, String listWithRange, Class<T> returnAsType) throws IOException {
		Path fileKeyPath = CredentialsByUserAlias.toFileKey_FromEnvRepo(userAlias);
		return loadValues_AuthFileKey(fileKeyPath, sheetId, listWithRange, returnAsType);
	}

	/**
	 * *************************************************************
	 * ---------------------------- WRITE --------------------------
	 * *************************************************************
	 */

	public static void writeValues_AuthAuto(String sheetId, String listWithRange, List<List<Object>> rows) throws IOException {
		String keyPath = AP.get(APK_GS_KEY_PATH, null);
		if (keyPath != null) {
			L.info("Auto configure 'gs.key.path' '{}'", keyPath);
			writeValues_AuthFileKey(Paths.get(keyPath), sheetId, listWithRange, rows);
			return;
		}
		String userAlias = AP.get("gs.key.alias", null);
		if (userAlias != null) {
			L.info("Auto configure 'gs.key.alias' '{}'", userAlias);
			writeValues_AuthUserAlias(userAlias, sheetId, listWithRange, rows);
			return;
		}
		throw new FIllegalStateException("configure key gd. Set path to file key 'gs.key.path' or key alias 'gs.key.alias'");
	}

	public static void writeValues_AuthUserAlias(String userAlias, String sheedId, String commonRange, List<List<Object>> values) throws IOException {
		writeValues_AuthFileKey(findFileKeyPath(userAlias), sheedId, commonRange, values);
	}

	public static void writeValues_AuthFileKey(Path fileKeyPath, String sheedId, String commonRange, List<List<Object>> values) throws IOException {
		uploadValues(fileKeyPath, sheedId, commonRange, values);
	}

	/**
	 * *************************************************************
	 * ---------------------------- CLEAR --------------------------
	 * *************************************************************
	 */

	public static void clearValues(String userAlias, String sheedId, String commonRange) throws IOException {
		clearValues(findFileKeyPath(userAlias), sheedId, commonRange);
	}

	public static Path findFileKeyPath(String userAlias) throws IOException {
		String[] credentials = ApiGdExt.CredentialsByUserAlias.getUserCredentialsByAliasName(userAlias);
		Path fileKeyPath = M2Repo.EnvRepo.findFileKeyPath_InEnvRepo(credentials);
		return fileKeyPath;
	}

	public static Sheets getSheetsServiceByLib(String[] credentialsLib) throws IOException {
		Path fileKeyAsLib = null;
		if (M2Repo.EnvRepo.contains(IT.isLength(IT.notNull(credentialsLib), 4))) {
			fileKeyAsLib = M2Repo.EnvRepo.repo.getLibFullPath(credentialsLib);
		}
		return getSheetsService(IT.isFileWithContent(IT.NN(fileKeyAsLib)));
	}


}