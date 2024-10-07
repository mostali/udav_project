package mpz_deprecated.simple_task;

import mpz_deprecated.EER;
import mpu.core.ENUM;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Deprecated
public class FileTask extends FileSimpleTask<FileTask, Path, IOException> {

	public static FileTask DELETE_FILE_WITH_PARENT() {
		return new FileTask(ERT.DELETE_FILE_WITH_PARENT);
	}

	public static FileTask DELETE_PATH() {
		return new FileTask(ERT.DELETE_PATH);
	}

	public static FileTask DELETE_DIR_CONTENT() {
		return new FileTask(ERT.DELETE_DIR_CONTENT);
	}

	public static enum ERT {
		DELETE_PATH, DELETE_FILE_WITH_PARENT, DELETE_DIR_CONTENT
	}

	public static enum EFT {
		COPY_FILE_TO_FILE, COPY_FILE_TO_DIR, MOVE_FILE_TO_FILE, MOVE_FILE_TO_DIR,//
	}

	public static enum EDT {
		MOVE_DIR, MOVE_DIR_TO_DIR, MOVE_DIR_CONTENT, RESOLVE_DIR
	}

	public static enum TypeTT {
		TRANSFER_FILE, TRANSFER_DIR, DELETE, INFO;

		private static TypeTT getType(Enum type) {
			if (type instanceof EFT) {
				return TRANSFER_FILE;
			} else if (type instanceof EDT) {
				return TRANSFER_DIR;
			} else if (type instanceof ERT) {
				return DELETE;
			}
			throw EER.WRONGLOGIC("Unhandled type ::: " + type);
		}
	}


	private TypeTT getType() {
		return TypeTT.valueOf(getAbsTypeName());
	}

	public FileTask(Enum typeName) {
		super(TypeTT.getType(typeName).name(), typeName.name());
	}


	/**
	 * ======================== FILE
	 */

	public static FileTask FILE_MOVE_TO_FILE() {
		return new FileTask(EFT.MOVE_FILE_TO_FILE);
	}

	public static FileTask FILE_MOVE_TO_DIR() {
		return new FileTask(EFT.MOVE_FILE_TO_DIR);
	}

	public static FileTask FILE_COPY_TO_FILE() {
		return new FileTask(EFT.COPY_FILE_TO_FILE);
	}

	public static FileTask FILE_COPY_TO_DIR() {
		return new FileTask(EFT.COPY_FILE_TO_DIR);
	}


	/**
	 * ======================== DIR
	 */
	public static FileTask DIR_MOVE() {
		return new FileTask(EDT.MOVE_DIR);
	}

	public static FileTask DIR_MOVE_CONTENT() {
		return new FileTask(EDT.MOVE_DIR_CONTENT);
	}

	public static FileTask DIR_MOVE_TO_DIR() {
		return new FileTask(EDT.MOVE_DIR_TO_DIR);
	}


	/**
	 * =======================isCreateDestDir
	 */
	private boolean isCreateDestDir;

	public FileTask OPT__IS_CREATE_DEST_DIR__TRUE() {
		this.isCreateDestDir = true;
		return this;
	}

	public boolean OPT__IS_CREATE_DEST_DIR__IS() {
		return isCreateDestDir;
	}

	/**
	 * ===================setBehaviourIfDestFileIsExist
	 */
	public FileTask OPT__IF_DEST_FILE_EXIST__SKIP() {
		this.ebDestFileExist = EBDestFileExist.SKIP.name();
		return this;
	}

	public FileTask OPT__IF_DEST_FILE_EXIST__IGNORE() {
		this.ebDestFileExist = EBDestFileExist.IGNORE.name();
		return this;
	}

	public FileTask setBehaviourIfDestFileIsExist_DELETE() {
		this.ebDestFileExist = EBDestFileExist.DELETE.name();
		return this;
	}

	private String ebDestFileExist;

	public FileTask setBehaviourIfDestFileIsExist(EBDestFileExist ebDestFileExist) {
		this.ebDestFileExist = ebDestFileExist.name();
		return this;
	}

	public EBDestFileExist getEBDestFileExist() {
		if (ebDestFileExist == null) {
			return EBDestFileExist.THROW;
		}
		return EBDestFileExist.valueOf(ebDestFileExist);
	}

	public boolean applyBehaviourIfDestExist_IS_SKIP(Path file) throws IOException {
		return getEBDestFileExist().applyBehaviourIfDestExist_IS_SKIP(file);
	}

	public boolean applyBehaviourIfDestExist_IS_SKIP(String file) throws IOException {
		return getEBDestFileExist().applyBehaviourIfDestExist_IS_SKIP(file);
	}


	/**
	 * ===================apply
	 */
	public void apply(String... src) throws IOException {
		this.apply(Arrays.stream(src).map(File::new).toArray(File[]::new));
	}

	public void apply(File... src) throws IOException {
		this.apply(Arrays.stream(src).map(File::toPath).toArray(Path[]::new));
	}

	@Override
	public FileTask apply(Path... objs) throws IOException {
		super.applyContext(objs);
		TypeTT typeTT = getType();
		ctx.throwIfEmpty();
		if (TypeTT.DELETE == typeTT) {
			return apply_ERT(ENUM.valueOf(getTypeName(), ERT.class), objs);
		}
		if (!ctx.isOne() && applyBehaviourIfDestExist_IS_SKIP(objs[1])) {
			return this;
		}
		switch (typeTT) {
			case TRANSFER_FILE:
				ctx.throwIfOneSrcArgument();
				return apply_EFT(ENUM.valueOf(getTypeName(), EFT.class), objs);
			case TRANSFER_DIR:
				return apply_EDT(ENUM.valueOf(getTypeName(), EDT.class), objs);
		}
		throw EER.IS("Unknown type of FileTask ::: " + typeTT);
	}


	private FileTask apply_EFT(EFT type, Path... paths) throws IOException {
		Path src = paths[0];
		Path dst = paths[1];
		switch (type) {
			case COPY_FILE_TO_FILE:
				FileUtils.copyFile(src.toFile(), dst.toFile());
				break;
			case COPY_FILE_TO_DIR:
				FileUtils.copyFileToDirectory(src.toFile(), dst.toFile());
				break;
			case MOVE_FILE_TO_FILE:
				FileUtils.moveFile(src.toFile(), dst.toFile());
				break;
			case MOVE_FILE_TO_DIR:
				FileUtils.moveFileToDirectory(src.toFile(), dst.toFile(), OPT__IS_CREATE_DEST_DIR__IS());
				break;
			default:
				throw UNKNOWN_TYPE_FILE_TASK();
		}
		return this;
	}

	private IllegalStateException UNKNOWN_TYPE_FILE_TASK() {
		return EER.IS("Unknown type of FileTask ::: " + getAbsTypeName() + " ::: " + getTypeName());
	}

	private FileTask apply_EDT(EDT type, Path... paths) throws IOException {
		Path src = paths[0].toAbsolutePath();
		Path dst = ctx.isOne() ? src.getParent() : paths[1].toAbsolutePath();
		switch (type) {
			case MOVE_DIR_CONTENT:
				File[] content = src.toFile().listFiles();
				apply_OPT__IS_CREATE_DEST_DIR(dst.toFile());
				for (File file : content) {
					FileUtils.moveToDirectory(file, dst.toFile(), false);
				}
				FileTask task = FileTask.DELETE_PATH().cloneOpts(this, OPT__LOG).apply(src);
				break;
			case MOVE_DIR_TO_DIR:
				ctx.throwIfOneSrcArgument();
				FileUtils.moveDirectoryToDirectory(src.toFile(), dst.toFile(), OPT__IS_CREATE_DEST_DIR__IS());
				break;
			case MOVE_DIR:
				ctx.throwIfOneSrcArgument();
				if (Files.exists(dst) && Files.isSameFile(src.getParent(), dst)) {
					throw EER.IS("Src parent equals target parent");
				}
				FileUtils.moveDirectory(src.toFile(), dst.toFile());
				break;
			default:
				throw UNKNOWN_TYPE_FILE_TASK();

		}
		return this;
	}

	@Override
	public String toString() {
		return "FileTask{" +
				"opts=" + opts +
				'}';
	}

	public FileTask cloneOpts(FileTask fileTask, int... opts) {
		for (int opt : opts) {
			boolean need = fileTask.OPT__IS(opt);
			if (need) {
				this.OPT__SET(opt);
			}
		}
		return this;
	}

	private void apply_OPT__IS_CREATE_DEST_DIR(File file) {
		if (OPT__IS_CREATE_DEST_DIR__IS() && !file.exists()) {
			file.mkdirs();
		}
	}

	private FileTask apply_ERT(ERT type, Path... dstFile) throws IOException {
		for (Path file : dstFile) {
			switch (type) {
				case DELETE_FILE_WITH_PARENT:
					FileUtils.deleteDirectory(file.getParent().toFile());
					break;
				case DELETE_PATH:
					FileUtils.deleteQuietly(file.toFile());
					break;
				case DELETE_DIR_CONTENT:
					if (!Files.isDirectory(file)) {
						throw new IllegalStateException("Path must be directory ::: " + file);
					}
					for (File content : file.toFile().listFiles()) {
						FileUtils.deleteQuietly(content);
					}
					break;
				default:
					throw UNKNOWN_TYPE_FILE_TASK();
			}
		}
		return this;
	}

}
