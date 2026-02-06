package mpf.repo;

import mpu.Sys;
import mpc.fs.*;
import mpu.IT;
import mpu.core.RW;
import mpz_deprecated.EER;
import mpc.env.Env;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ./rootDir/repoName
 * ./file
 * ./group.id/artifact.id/version/filename
 */
public class Repo {

	public static final Logger L = LoggerFactory.getLogger(Repo.class);

	private static final String FN_DATA_SP = "data.sp";
	public static final String FN_DATA_TREE = "data.tree";

	private final String rootDir;
	private final String repoName;
	private final Path repoPath;


	public static void main(String[] args) throws IOException {

		{
			Repo repo = Repo.ofHome(".repo.app").createIfNotExist();
			Lib lib = Lib.of(new String[]{"pages", "usr", "1.0", "@"});
			//repo.writeFileContent(lib, "hello");
			Sys.exit(repo.readFileContent(lib));
			Sys.exit(repo.containsLib(lib));
		}

		Lib lib = lib("groupId", "artifactId", "versionId", "artifact-file-name");
		Repo.of(".", "repo").installLib(lib, Paths.get("artifact-file"));
	}

	public Lib getLib(String... path4) {
		return getLib(this, path4);
	}

	public static Lib getLib(Repo repo, String... path4) {
		return Lib.of(path4).setRepo(repo);
	}

	public static File[] getAllFilesFromParentLib(Repo repo, Lib lib) {
		return lib.setRepo(repo).getAbsolutePath().toFile().getParentFile().listFiles();
	}

	public void saveLib(Lib lib, String contentLib) throws IOException {
		writeFileContent(lib, contentLib);
	}

	public void writeFileContent(Lib lib, String contentLib) throws IOException {
		writeFileContent(this, lib, contentLib);
	}

	public String readFileContent(Lib lib) throws IOException {
		return readFileContent(this, lib);
	}

	public Repo createIfNotExist() {
		UFS.MKDIR.mkdirsIfNotExist(repoPath);
		return this;
	}

	public static Repo defaultRepo() {
		return EnvRepo.repo;
	}

	public static class EnvRepo extends Repo {
		public static final String NAME = ".env.repo";

		public static final EnvRepo repo;

		static {
			Repo tenv = Repo.findUp(NAME, ".", 3);
			String rootDir = tenv == null ? Env.HOME_LOCATION.toString() : tenv.rootDir;
			repo = new EnvRepo(rootDir, NAME);
			tenv = null;
			IT.notNull(repo.getRepoPath());
			INFOf("ENV: Repo initiated as '%s'", repo.getRepoPath());
		}

		private EnvRepo(String rootDir, String repoName) {
			super(rootDir, repoName);
		}

		public static boolean contains(String... path) {
			return repo.containsLib(path);
		}

		/**
		 * *************************************************************
		 * ---------------------------- OTHER --------------------------
		 * *************************************************************
		 */

		public static Path findFileKeyPath_InEnvRepo(String[] credentialsLib) throws IOException {
			Path fileKeyAsLib = null;
			IT.isLength(IT.notNull(credentialsLib), 4);
			if (contains(credentialsLib)) {
				fileKeyAsLib = repo.getLibFullPath(credentialsLib);
			}
			return IT.isFileWithContent(IT.NN(fileKeyAsLib));
		}

	}


	public Repo(String repoPath) {
		this(Paths.get(repoPath));
	}

	public Repo(Path repoPath) {
		this.rootDir = repoPath.getParent().toAbsolutePath().toString();
		this.repoName = repoPath.getFileName().toString();
		this.repoPath = repoPath;
	}

	public Repo(String rootDir, String repoName) {
		this(Paths.get(rootDir, repoName));
	}

	private static Repo findUp(String name, String rootDir, int levels) {
		Path repo = Paths.get(rootDir, name);
		if (Files.isDirectory(repo)) {
			return Repo.of(repo);
		}
		if (levels <= 0) {
			return null;
		}
		Path parent = Paths.get(rootDir).toAbsolutePath();
		do {
			Path parent_ = parent;
			parent = parent.getParent();
			if (parent == null) {
				if (L.isInfoEnabled()) {
					L.info("Parent is null:{}", parent_);
				}
				return null;
			}
			if (!Files.exists(parent)) {
				continue;
			}
			repo = parent.resolve(name);
			if (Files.isDirectory(repo)) {
				return Repo.of(repo);
			}
		} while (--levels >= 0);
		return null;
	}

	public Path getRepoPath() {
		return repoPath;
	}

	public String getRootDir() {
		return rootDir;
	}

	public String getRepoName() {
		return repoName;
	}

	public Lib installLib(Lib lib, Path file) throws IOException {
		return installLib(this, lib, file);
	}

	public static Lib installLib(Repo repo, Lib lib, Path file) throws IOException {
		if (!Files.exists(file)) {
			throw new FileNotFoundException(file.toAbsolutePath().toString());
		}
		lib.setRepo(repo);
		FileUtils.copyFile(file.toAbsolutePath().toFile(), lib.getAbsolutePath().toFile());
		INFOf("Repo Lib was installed [%s] as [%s]", lib.toStringInfo(), file.toAbsolutePath());
		return lib;
	}

	public Lib installFileToLib(Lib lib, Path file) throws IOException {
		return installFileToLib(this, lib, file);
	}

	public static Lib installFileToLib(Repo repo, Lib lib, Path file) throws IOException {
		checkExistFile(file);
		lib.setRepo(repo);
		Path libPath = lib.getAbsolutePath();
		Path filePath = file.toAbsolutePath();
		Path targetPath = libPath.getParent().resolve(filePath.getFileName());
		FileUtils.copyFile(filePath.toFile(), targetPath.toFile());
		INFOf("Repo Lib was installed [%s] as [%s]", lib.toStringInfo(), targetPath.toAbsolutePath());
		lib = lib.cloneWithFile(targetPath.getFileName().toString());
		return lib;
	}

	public void installLib(Lib lib, URL libUrl, URL... urlMD5) throws IOException {
		installLib(this, lib, libUrl, urlMD5);
	}

	public static void installLib(Repo repo, Lib lib, URL libUrl, URL... urlMD5) throws IOException {

		lib.setRepo(repo);

		//init lib
		if (!lib.fileId.isSet()) {
			String fn = UF.fnFromUrl(libUrl);
			if (StringUtils.isBlank(fn)) {
				throw new IllegalArgumentException(String.format("This url [%s] must be with a file", libUrl));
			}
			lib.setFileId(fn);
		}

		//init lib MD5
		Lib libMD5 = null;
		if (urlMD5.length > 0) {
			String fn = UF.fnFromUrl(urlMD5[0]);
			if (StringUtils.isBlank(fn)) {
				throw new IllegalArgumentException(String.format("This url [%s] must be with a file", urlMD5[0]));
			}
			libMD5 = lib.cloneWithFile(fn);
		}

		//init downloader
		DownloaderFile downloader = null;
		if (urlMD5.length > 0) {
			downloader = DownloaderFileWithCheck.of(libUrl.toString(), null, urlMD5[0].toString(), null);
		} else {
			downloader = DownloaderFile.of(libUrl.toString());
		}

		//do download & check
		try {
			downloader.download();
			downloader.moveFileToTargetFile(lib.getAbsolutePath().toString());
			INFOf("Installed Repo Lib [%s] as [%s]", lib.toStringInfo(), lib.getAbsolutePath());
			if (downloader instanceof DownloaderFileWithCheck) {
				((DownloaderFileWithCheck) downloader).moveFileMD5ToTargetFile(libMD5.getAbsolutePath().toString());
				INFOf("Installed Repo Lib MD5 [%s] as [%s]", libMD5.toStringInfo(), libMD5.getAbsolutePath());
			}
		} catch (Exception ex) {
			downloader.deleteAll();
			throw EER.IS(ex);
		}

	}

	public static String readFileContent(Repo repo, Lib lib) throws IOException {
		return RW.readContent_(lib.setRepo(repo).getAbsolutePath().toFile().toString());
	}

	public static void writeFileContent(Repo repo, Lib lib, CharSequence content) throws IOException {
		String path = lib.setRepo(repo).getAbsolutePath().toFile().toString();
		if (L.isInfoEnabled()) {
			L.info("Repo / writeFileContent / file=[{}] / Lib=[{}] to Repo=[{}] / content=[{}]", path, lib, repo, content.length());
		}
		RW.write_(Paths.get(path), content, true, StandardOpenOption.CREATE);
	}

	public Path getLibFullPath(String[] path) {
		return getLibFullPath(Lib.of(path));
	}

	public Path getLibFullPath(Lib lib) {
		return getRepoPath().resolve(lib.getRelativePath());
	}

	public static Path getLibFullPath(Repo repo, Lib lib) {
		return repo.getRepoPath().resolve(lib.getRelativePath());
	}

	public static Lib lib(String groupId, String artifactId, String versionId, String fileId) {
		return Lib.of(groupId, artifactId, versionId, fileId);
	}

	public boolean containsLib(String groupId, String artifactId, String versionId, String fileId) {
		return containsLib(Lib.of(groupId, artifactId, versionId, fileId));
	}

	public boolean containsLib(String[] path4) {
		IT.isEq(path4.length, 4, "Path of lib must have length eq 4");
		return containsLib(Lib.of(path4[0], path4[1], path4[2], path4[3]));
	}

	public boolean containsLib(Lib lib) {
		lib.setRepo(this);
		return lib.exist();
	}

	public Path copyLibToDir(Lib lib, Path targetDir) throws IOException {
		lib.setRepo(this);
		File src = lib.getAbsolutePath().toFile();
		File dest = targetDir.toAbsolutePath().toFile();
		FileUtils.copyFileToDirectory(src, dest);
		Path pathCore = Paths.get(dest.getAbsolutePath(), src.getName());
		INFOf("Lib [%s] was copied to [%s]", src, dest);
		return pathCore;

	}

	public static void INFOf(String message, Object... args) {
		if (L.isInfoEnabled()) {
			if (args.length > 0) {
				message = String.format(message, args);
			}
			L.info(message);
		}
	}

	public static class FileId {
		public final String nameId;

		public FileId(String path) {
			this.nameId = path;
		}

		public static FileId of(String name, boolean... isMayBeBlank) {
			if (isMayBeBlank.length == 0 || (!isMayBeBlank[0] && StringUtils.isBlank(name))) {
				throw new IllegalArgumentException("FileId is blank");
			}
			return new FileId(name);
		}

		public String toLocationAsFile() {
			return UF.normFile(nameId);
		}

		public String toLocationAsDir() {
			return UF.normDir(nameId);
		}

		@Override
		public String toString() {
			return Arrays.asList(nameId).stream().collect(Collectors.joining("."));
		}

		public boolean isSet() {
			return !StringUtils.isBlank(nameId);
		}


		public String getNameId() {
			return nameId;
		}
	}

	public static class VersionId extends FileId {

		public VersionId(String path) {
			super(path);
		}

		public static VersionId of(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("VersionId is blank");
			}
			return new VersionId(name);
		}
	}

	public static class ArtifactId extends FileId {

		public ArtifactId(String path) {
			super(path);
		}

		public static ArtifactId of(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("ArtifactId is blank");
			}
			return new ArtifactId(name);
		}
	}


	public static class GroupId extends FileId {

		public GroupId(String path) {
			super(path);
		}

		public static GroupId of(String group) {
			if (StringUtils.isBlank(group)) {
				throw new IllegalArgumentException("GroupId is blank");
			}
			return new GroupId(group);
		}

		@Override
		public String toLocationAsDir() {
			return UF.normDir(UF.replacePackageSeparator(getNameId()));
		}
	}


	public static void checkExistFile(String file) throws FileNotFoundException {
		checkExistFile(Paths.get(file));
	}

	public static void checkExistFile(Path file) throws FileNotFoundException {
		if (!Files.exists(file)) {
			throw new FileNotFoundException(file.toAbsolutePath().toString());
		}
	}

	@Override
	public String toString() {
		return "Repo{" + "rootDir='" + rootDir + '\'' + ", repoName='" + repoName + '\'' + ", repoPath=" + getRepoPath() + '}';
	}


	public static Repo of(String path) {
		return of(Paths.get(path));
	}

	public static Repo of(String rootDir, String repoName) {
		return new Repo(Paths.get(rootDir, IT.isFilename(repoName)));
	}

	public static Repo of(Path path) {
		return new Repo(path);
	}

	public static Repo ofHome(String parent, String repoName) {
		return of(getHomeRepoPath(parent, IT.isFilename(repoName)));
	}

	public static Repo ofHome(String repoName) {
		return of(getHomeRepoPath(repoName));
	}

	public static Path getHomeRepoPath(String repoName) {
		return Env.getUserHome(IT.isFilename(repoName));
	}

	public static Path getHomeRepoPath(String parent, String repoName) {
		return Env.getUserHome(parent, IT.isFilename(repoName));
	}

	public static Path getHomeRepoPath(String parent, String child, String repoName) {
		return Env.getUserHome(parent, child, IT.isFilename(repoName));
	}

}
