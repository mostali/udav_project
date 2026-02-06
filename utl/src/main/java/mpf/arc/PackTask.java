package mpf.arc;

import mpz_deprecated.simple_task.AbsTask;
import mpz_deprecated.EER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PackTask extends AbsTask<PackTask, Path, IOException> {

	public enum TypePT {
		PACK
	}

	public enum TypePack {
		JAR, UNJAR, ZIP, UNZIP
	}

	public static PackTask ZIP() {
		return new PackTask(TypePack.ZIP);
	}

	public static PackTask UNZIP() {
		return new PackTask(TypePack.UNZIP);
	}

	public static PackTask JAR() {
		return new PackTask(TypePack.JAR);
	}

	public static PackTask UNJAR() {
		return new PackTask(TypePack.UNJAR);
	}

	private TypePack getType() {
		return TypePack.valueOf(getAbsTypeName());
	}

	public PackTask(TypePack typeName) {
		super(TypePT.PACK.name(), typeName.name());
	}

	public void apply(String src, String... dest) throws IOException {
		String dst = dest.length > 0 ? dest[0] : Paths.get(src).toAbsolutePath().getParent().toString();
		this.apply(new File(src), new File(dst));
	}

	public void apply(File src, File... dest) throws IOException {
		File dst = dest.length > 0 ? dest[0] : src.getAbsoluteFile().getParentFile();
		this.apply(src.toPath(), dst.toPath());
	}

	public PackTask apply(Path... objs) throws IOException {
		super.applyContext(objs);
		ctx.throwIfEmpty();
		Path src = objs[0].toAbsolutePath();
		Path dst = objs.length > 1 ? objs[1].toAbsolutePath() : src.getParent();
		switch (TypePack.valueOf(getTypeName())) {
			case UNJAR:
				UnpackJar.unpack(src.toString(), dst.toString());
				return this;
			case UNZIP:
				UnpackZip.unpack(src.toString(), dst.toString());
				return this;
			case JAR:
				throw EER.NEEDIMPL("JAR");
			case ZIP:
				throw EER.NEEDIMPL("ZIP");
		}
		throw EER.WRONGLOGIC(getApplyLog());
	}

}
