package mpe.docker;

import mpc.fs.UFS;
import mpu.X;
import mpu.core.RW;
import mpc.fs.UF;
import mpe.rt.core.ExecRq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class DockerBuilderImage {

	public static final Logger L = LoggerFactory.getLogger(DockerBuilderImage.class);

	public static final String CMD_BUILD_IMG = "docker build -t %s -f %s %s";

	public static void buildDockerImage(String imgname_with_version, String dockerfile_content) throws ExecRq {
		Path dockerFile = Paths.get("tmp", UUID.randomUUID().toString(), UF.clearStringCyrRemoveSlash(imgname_with_version) + ".Dockerfile");
		if (L.isInfoEnabled()) {
			L.info("Begin build docker image '{}' on file '{}', with content \n{}", imgname_with_version, dockerFile, dockerfile_content);
		}
		try {
			RW.write(dockerFile, dockerfile_content, true, StandardOpenOption.CREATE_NEW);
			String buildImageDockerCmd = X.f(CMD_BUILD_IMG, imgname_with_version, dockerFile.toAbsolutePath(), ".");
			ExecRq exe = ExecRq.exec(false, buildImageDockerCmd);
			if (L.isInfoEnabled()) {
				L.info("Docker Image '{}' build SUCCESS\n{}", imgname_with_version, exe.getMessageReport(0));
			}
		} finally {
			UFS.RM.deleteDir(dockerFile.getParent());
		}
	}
}
