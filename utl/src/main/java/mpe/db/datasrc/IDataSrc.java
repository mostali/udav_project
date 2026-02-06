package mpe.db.datasrc;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.AP;
import mpc.env.APP;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpe.db.Db;
import mpe.db.IDbUrl;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ARRi;
import mpu.func.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

public interface IDataSrc<T> extends Supplier<List<T>>, FunctionV1<List<T>> {

	public static final Logger L = LoggerFactory.getLogger(IDataSrc.class);

	interface IModelDesc {

		String getPgTablename();

		String getSlPathDb();

		String getSlTablename();

		String getGsTablenameAndRange();

		default <T> IDataSrc<List<T>> dataSrc(DbType dbType) {
			switch (dbType) {
				case PG:
					return (IDataSrc) IDataSrc.of(ARR.as(APP.ULP_DEV_LOCAL), getPgTablename());
				case SL:
					return (IDataSrc) IDataSrc.of(Paths.get(getSlPathDb()), getSlTablename());
				case GS:
					return (IDataSrc) IDataSrc.of(APP.getPathGdKey(), AP.get(APP.GD_SHEET_ID), getGsTablenameAndRange());
				default:
					throw new WhatIsTypeException(dbType);
			}
		}

		default List<List<Object>> upTo(DbUpRoute upRoute) {
			return upTo(upRoute.src, upRoute.dst);
		}

		default List<List<Object>> upTo(DbType srcType, DbType dst) {
			return IDataSrc.upTo(dataSrc(srcType), dataSrc(dst));
		}

//		default List<List<Object>> getAllRows(DbType srcType) {
//			return srcType.getAllRows(this);
//		}
	}

	static <T> List<T> upTo(IDataSrc<T> src, IDataSrc<T> dst) {
		List<T> ts = src.get();
		if (L.isInfoEnabled()) {
			L.info("Src get*{} & start up..", X.sizeOf(ts));
		}
		dst.apply(ts);
		return ts;
	}

	DbType type();

	static PgSrc of(List<String> ulp, String tablename) {
		return new PgSrc(ulp, tablename);
	}

	static SqliteSrc of(Path path, String tablename) {
		return new SqliteSrc(path, tablename);
	}

	static GSheetSrc of(Path path, String sheetid, String sheetrange) {
		return new GSheetSrc(path, sheetid, sheetrange);
	}

	@RequiredArgsConstructor
	public static class SqliteSrc implements IDataSrc<List<Object>> {

		final Path pathDb;
		final String tablename;

		@Override
		public DbType type() {
			return DbType.SL;
		}

		@SneakyThrows
		@Override
		public List<List<Object>> get() {
//			return tree().getAllTableRows(tablename, true, null);
			return type().getLoaderSl().apply(pathDb, tablename);
		}

		public Db getDb() {
			return Db.of(pathDb);
		}

		@SneakyThrows
		@Override
		public void apply(List<List<Object>> rows) {
//			tree().writeToDb(tablename, rows);
			type().getWriterSl().apply(pathDb, tablename, rows);
		}


		@Override
		public String toString() {
			return getClass().getSimpleName() + "@" + type() + "@" + tablename + " from file " + UF.ln(pathDb);
		}

	}

	@RequiredArgsConstructor
	public static class GSheetSrc implements IDataSrc<List<Object>> {

		final Path pathKey;
		final String sheetid;
		final String sheetrange;

		@Override
		public DbType type() {
			return DbType.GS;
		}

		@SneakyThrows
		@Override
		public List<List<Object>> get() {
//			return (List<List<Object>>) ApiGdExt.loadValues_AuthFileKey(pathKey, sheetid, sheetrange, Collection.class);
			return IT.NN(DbType.LOADER_GS.get(), "init loaders with ApiGdExt#initLoaders").apply(pathKey, sheetid, sheetrange);
		}

		@SneakyThrows
		@Override
		public void apply(List<List<Object>> rows) {
//			ApiGdExt.writeValues_AuthFileKey(pathKey, sheetid, sheetrange, rows);
			IT.NN(DbType.WRITER_GS.get(), "init loaders with ApiGdExt#initLoaders").apply(pathKey, sheetid, sheetrange, rows);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "@" + type() + "@" + sheetid + "!" + sheetrange + " from key " + UF.ln(pathKey);
		}
	}

	@RequiredArgsConstructor
	public static class PgSrc implements IDataSrc<List<Object>> {

		final List<String> ulp;
		final String tablename;

		@Override
		public DbType type() {
			return DbType.PG;
		}

		@SneakyThrows
		@Override
		public List<List<Object>> get() {
//			return db().getAllTableRows(tablename, true, null);
			return type().getLoaderPg().apply(ulp, tablename);
		}

		public @NotNull Db db() {
			return Db.of(IDbUrl.ofULP(ulp));
		}

		@SneakyThrows
		@Override
		public void apply(List<List<Object>> rows) {
//			db().writeToDb(tablename, rows);
			type().getWriterPg().apply(ulp, tablename, rows);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "@" + type() + "@" + tablename + " from " + ARRi.first(ulp, null);
		}
	}

}
