package udav_net_exp.m2_repo;

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

public class M2Lib {
	private Path absPath;
	public final M2Repo.GroupId groupId;
	public final M2Repo.ArtifactId artifactId;
	public final M2Repo.VersionId versionId;
	public M2Repo.FileId fileId;

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

	public M2Lib(String[] lib) {
		this(lib[0], lib[1], lib[2], lib.length > 3 ? lib[3] : null);
	}

	public M2Lib(String groupId, String artifactId, String versionId, String fileId) {
		this(M2Repo.GroupId.of(groupId), M2Repo.ArtifactId.of(artifactId), M2Repo.VersionId.of(versionId), M2Repo.FileId.of(fileId, true));
	}

	public M2Lib(M2Repo.GroupId groupId, M2Repo.ArtifactId artifactId, M2Repo.VersionId versionId, M2Repo.FileId fileId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.versionId = versionId;
		this.fileId = fileId;
	}

	public static M2Lib of(M2Repo.GroupId groupId, M2Repo.ArtifactId artifactId, M2Repo.VersionId versionId, M2Repo.FileId fileId) {
		return new M2Lib(groupId, artifactId, versionId, fileId);
	}

	public static M2Lib of(String[] path4) {
		IT.isEq(IT.notEmptyAll(path4).length, 4, "Path of lib must be have length eq 4");
		return of(IT.NE(path4[0]), IT.NE(path4[1]), IT.NE(path4[2]), IT.NE(path4[3]));
	}

	public static M2Lib of(String groupId, String artifactId, String versionId, String fileId) {
		return new M2Lib(M2Repo.GroupId.of(groupId), M2Repo.ArtifactId.of(artifactId), M2Repo.VersionId.of(versionId), M2Repo.FileId.of(fileId, true));
	}

	//
	public boolean exist(String groupId, String artifactId, String versionId, String fileId) {
		return exist();
	}

	public boolean exist() {
		return Files.exists(getAbsolutePath());
	}

	public M2Lib setRepo(M2Repo repo) {
		setRepo(repo.getRepoPath());
		return this;
	}

	public M2Lib setRepo(Path repoPath) {
		this.repoPath = repoPath;
		absPath = null;//set because it lazy initiated
		return this;
	}

	public M2Repo getRepo() {
		return M2Repo.of(getRepoPath());
	}

	public Path getRepoPath() {
		return this.repoPath;
	}

	public M2Lib cloneWithFile(String... fileName) {
		String name = fileId.getNameId();
		if (fileName.length > 0) {
			name = fileName[0];
		}
		M2Lib clone = of(groupId.getNameId(), artifactId.getNameId(), versionId.getNameId(), name);
		clone.setRepo(getRepoPath());
		return clone;
	}

	public void setFileId(String file) {
		this.fileId = M2Repo.FileId.of(UF.normFile(file));
	}

	public String readFileContent() throws IOException {
		return M2Repo.readFileContent(M2Repo.of(getRepoPath()), this);
	}

	public void writeFileContent(CharSequence content) throws IOException {
		M2Repo.writeFileContent(M2Repo.of(getRepoPath()), this, content);
	}

	public void saveContentAsString(String content, OpenOption openOption_orNull) throws IOException {
		RW.write_(getAbsolutePath(), content, openOption_orNull);
	}

	public M2Lib toLibFile(String filename) {
		return of(groupId, artifactId, versionId, M2Repo.FileId.of(filename, false)).setRepo(getRepoPath());
	}

	public String getFileType() {
		if (fileId == null) {
			throw new NullPointerException("Unpossible get file type. FileId is null.");
		}
		String filename = fileId.nameId;
		return filename.split("\\.", 2)[1];
	}

	public M2Lib installFileToLib(M2Repo repo, Path file) throws IOException {
		return repo.installFileToLib(this, file);
	}

	public M2Lib installLib(M2Repo repo, Path file) throws IOException {
		return repo.installLib(this, file);
	}

	public void installLib(M2Repo repo, URL file, URL... urlMD5) throws IOException {
		repo.installLib(this, file, urlMD5);
	}

	public File[] getAllFilesFromParentLib() {
		return M2Repo.getAllFilesFromParentLib(getRepo(), this);
	}

}
