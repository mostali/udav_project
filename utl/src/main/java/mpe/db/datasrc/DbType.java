package mpe.db.datasrc;

import mpe.db.Db;
import mpu.IT;
import mpu.func.Function2T;
import mpu.func.Function3T;
import mpu.func.FunctionV3T;
import mpu.func.FunctionV4T;
import mpu.str.SPLIT;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public enum DbType {
	PG, SL, GS;

	public List<List<Object>> loadAllRows(IDataSrc.IModelDesc modelType) {
		return modelType.dataSrc(this).get();
	}

	public void writeAllRows(IDataSrc.IModelDesc modelType, List<List<Object>> rows) {
		modelType.dataSrc(this).apply(rows);
	}

	//Postgres
	public static final AtomicReference<Function2T<List<String>, String, List<List<Object>>, IOException>> LOADER_PG = new AtomicReference<>(Db.pgLoader);
	public static final AtomicReference<FunctionV3T<List<String>, String, List<List<Object>>, SQLException>> WRITER_PG = new AtomicReference<>(Db.pgWriter);

	//Sqlite
	public static final AtomicReference<Function2T<Path, String, List<List<Object>>, IOException>> LOADER_SL = new AtomicReference<>(Db.slLoader);
	public static final AtomicReference<FunctionV3T<Path, String, List<List<Object>>, SQLException>> WRITER_SL = new AtomicReference<>(Db.slWriter);

	//GoogleSheet
	public static final AtomicReference<Function3T<Path, String, String, List<List<Object>>, IOException>> LOADER_GS = new AtomicReference<>();
	public static final AtomicReference<FunctionV4T<Path, String, String, List<List<Object>>, IOException>> WRITER_GS = new AtomicReference<>();

	public Function3T<Path, String, String, List<List<Object>>, IOException> getLoaderGs() {
		IT.state(GS == this, "this loader only for %s", this);
		return LOADER_GS.get();
	}

	public FunctionV4T<Path, String, String, List<List<Object>>, IOException> getWriterGs() {
		IT.state(GS == this, "this writer only for %s", this);
		return WRITER_GS.get();
	}

	//
	//

	public Function2T<Path, String, List<List<Object>>, IOException> getLoaderSl() {
		IT.state(SL == this, "this loader only for %s", this);
		return LOADER_SL.get();
	}

	public FunctionV3T<Path, String, List<List<Object>>, SQLException> getWriterSl() {
		IT.state(SL == this, "this writer only for %s", this);
		return WRITER_SL.get();
	}

	//
	//

	public Function2T<List<String>, String, List<List<Object>>, IOException> getLoaderPg() {
		IT.state(PG == this, "this loader only for %s", this);
		return LOADER_PG.get();
	}

	public FunctionV3T<List<String>, String, List<List<Object>>, SQLException> getWriterPg() {
		IT.state(PG == this, "this writer only for %s", this);
		return WRITER_PG.get();
	}

}
