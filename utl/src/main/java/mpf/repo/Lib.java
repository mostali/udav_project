package mpf.repo;

import mpu.IT;
import mpu.core.RW;
import mpc.fs.UF;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Lib {
	private Path absPath;
	public final Repo.GroupId groupId;
	public final Repo.ArtifactId artifactId;
	public final Repo.VersionId versionId;
	public Repo.FileId fileId;

	public Path repoPath;

	@Override
	public String toString() {
		return toStringInfo();
	}

	public String toStringInfo() {
		return "Lib{" +
			   "groupId=" + groupId +
			   ", artifactId=" + artifactId +
			   ", versionId=" + versionId +
			   ", fileId=" + fileId +
			   ", absPath=" + absPath +
			   '}';
	}

	public Path getAbsolutePath() {
		if (absPath == null) {
			if (repoPath == null) {
				throw new IllegalStateException("Set repo");
			} else {
				absPath = repoPath.resolve(getRelativePath());
			}
		}
		return absPath;
	}

	public Path getRelativePath() {
		return Paths.get(groupId.toLocationAsDir(), artifactId.toLocationAsDir(), versionId.toLocationAsDir(), fileId.toLocationAsFile());
	}

	public Lib(String[] lib) {
		this(lib[0], lib[1], lib[2], lib.length > 3 ? lib[3] : null);
	}

	public Lib(String groupId, String artifactId, String versionId, String fileId) {
		this(Repo.GroupId.of(groupId), Repo.ArtifactId.of(artifactId), Repo.VersionId.of(versionId), Repo.FileId.of(fileId, true));
	}

	public Lib(Repo.GroupId groupId, Repo.ArtifactId artifactId, Repo.VersionId versionId, Repo.FileId fileId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.versionId = versionId;
		this.fileId = fileId;
	}

	public static Lib of(Repo.GroupId groupId, Repo.ArtifactId artifactId, Repo.VersionId versionId, Repo.FileId fileId) {
		return new Lib(groupId, artifactId, versionId, fileId);
	}

	public static Lib of(String[] path4) {
		IT.isEq(IT.notEmptyAll(path4).length, 4, "Path of lib must be have length eq 4");
		return of(IT.NE(path4[0]), IT.NE(path4[1]), IT.NE(path4[2]), IT.NE(path4[3]));
	}

	public static Lib of(String groupId, String artifactId, String versionId, String fileId) {
		return new Lib(Repo.GroupId.of(groupId), Repo.ArtifactId.of(artifactId), Repo.VersionId.of(versionId), Repo.FileId.of(fileId, true));
	}


	//
	public boolean exist(String groupId, String artifactId, String versionId, String fileId) {
		return exist();
	}

	public boolean exist() {
		return Files.exists(getAbsolutePath());
	}

	public Lib setRepo(Repo repo) {
		setRepo(repo.getRepoPath());
		return this;
	}

	public Lib setRepo(Path repoPath) {
		this.repoPath = repoPath;
		absPath = null;
		return this;
	}

	public Repo getRepo() {
		return Repo.of(getRepoPath());
	}

	public Path getRepoPath() {
		return this.repoPath;
	}

	public Lib cloneWithFile(String... fileName) {
		String name = fileId.getNameId();
		if (fileName.length > 0) {
			name = fileName[0];
		}
		Lib clone = of(groupId.getNameId(), artifactId.getNameId(), versionId.getNameId(), name);
		clone.setRepo(getRepoPath());
		return clone;
	}

	public void setFileId(String file) {
		this.fileId = Repo.FileId.of(UF.normFile(file));
	}

	public String readFileContent() throws IOException {
		return Repo.readFileContent(Repo.of(getRepoPath()), this);
	}

	public void writeFileContent(CharSequence content) throws IOException {
		Repo.writeFileContent(Repo.of(getRepoPath()), this, content);
	}

	public void saveContentAsString(String content, OpenOption openOption_orNull) throws IOException {
		RW.write_(getAbsolutePath(), content, openOption_orNull);
	}

	public Lib toLibFile(String filename) {
		return of(groupId, artifactId, versionId, Repo.FileId.of(filename, false)).setRepo(getRepoPath());
	}

	public String getFileType() {
		if (fileId == null) {
			throw new NullPointerException("Unpossible get file type. FileId is null.");
		}
		String filename = fileId.nameId;
		return filename.split("\\.", 2)[1];
	}

	public Lib installFileToLib(Repo repo, Path file) throws IOException {
		return repo.installFileToLib(this, file);
	}

	public Lib installLib(Repo repo, Path file) throws IOException {
		return repo.installLib(this, file);
	}

	public void installLib(Repo repo, URL file, URL... urlMD5) throws IOException {
		repo.installLib(this, file, urlMD5);
	}

	public File[] getAllFilesFromParentLib() {
		return Repo.getAllFilesFromParentLib(getRepo(), this);
	}

}
