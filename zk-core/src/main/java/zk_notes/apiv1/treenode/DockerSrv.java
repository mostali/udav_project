package zk_notes.apiv1.treenode;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.exception.WhatIsTypeException;
import mpu.X;
import mpu.core.ENUM;
import mpu.pare.Pare;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.TKN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DockerSrv {

	public static final Logger L = LoggerFactory.getLogger(DockerSrv.class);

//	private static DockerClient dockerClient;

	public enum Oper {
		ls, lsi, //1
		rm, rmi, //2
		create, start, stop, logs, state, build;

		public static Oper valueOf(String name, Oper... defRq) {
			return ENUM.valueOf(name, Oper.class, defRq);
		}

		public String apply2(String cid_or_imgId) {
			switch (this) {
				case state:
					return DockerSrv.getContainerStatus(cid_or_imgId).ext();
				case create:
					throw new FIllegalStateException("support only container operation");
				case start:
					return DockerSrv.startContainer(cid_or_imgId);
				case stop:
					return DockerSrv.stopContainer(cid_or_imgId);
				case rm:
					return DockerSrv.removeContainer(cid_or_imgId);
				case rmi:
					return DockerSrv.removeImage(cid_or_imgId);
				case logs:
					return JOIN.allByNL(DockerSrv.printContainerLogs(cid_or_imgId));
				default:
					throw new WhatIsTypeException(this);
			}


		}
	}

	public static void main(String[] args) throws IOException {
//		List<Container> allContainers = getAllContainers();
//		X.exit(allContainers);
		// Создание клиента Docker
//		DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
//				.withDockerHost("tcp://localhost:2375") // Убедитесь, что Docker Daemon доступен по этому адресу
//				.build();

//		dockerClient = DockerClientBuilder.getInstance(config).build();
//		DockerClient dockerClient = DockerClientBuilder.getInstance().build();

//		getContainer();
		String containerId = "1797ed697da762d10784591cf6f2cb51fe2caad54442552b0cbd4fa085a63956	";

		String name = "ab1";
		String image = "ab:1";
		String portBinding = "8082:8080";
//		String containerId = createContainer(name, image, portBinding, ARR.as("java", "-jar", "beaapp.jar"));
		String buildImage = buildImage("ab:2", "/home/dav/pjm/glt/dr.Ob.Dockerfile");
		X.exit(buildImage);
//
//		X.exit(containerId);
		if (containerId != null) {
//			startContainer(containerId);//created,running
//			X.p(getContainerStatus(containerId));
//			printContainerLogs(containerId);
//			stopContainer(containerId);
//			removeContainer(containerId);
		}

		// Закрытие клиента
//		dockerClient.close();
	}

	@SneakyThrows
	public static Pare<Integer, String> getVersion() {
		DockerClient dockerClient = dockerClient();
		try {
			return Pare.of(200, dockerClient.versionCmd().exec().getVersion());
		} finally {
			dockerClient.close();
		}

	}

	@SneakyThrows
	public static String createContainer(String name, String image, String portBinding, List<String> cmd) {

		PortBinding portBinding0 = PortBinding.parse(portBinding);

		HostConfig hostConfig = HostConfig.newHostConfig();
		if (X.notEmpty(portBinding)) {
			hostConfig.withPortBindings(portBinding0);
		}

		DockerClient dockerClient = dockerClient();
		CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(image).withName(name).withHostConfig(hostConfig);

		Integer exposePort = TKN.last(portBinding, ":", Integer.class);
		if (exposePort != null && exposePort > 0) {
			ExposedPort tcpPort = ExposedPort.tcp(exposePort);
			createContainerCmd.withExposedPorts(tcpPort);
		}
		if (X.notEmpty(cmd)) {
			createContainerCmd.withCmd(cmd);
		}

		try {

			CreateContainerResponse container = createContainerCmd.exec();
			L.info("Container created with ID: " + container.getId() + ":" + container);

			return container.getId();

		} finally {
			dockerClient.close();
		}

	}

	private static DockerClient dockerClient() {
		DockerClient dockerClient = DockerClientBuilder.getInstance().build();
		return dockerClient;
	}


	@SneakyThrows
	public static String startContainer(String containerId) {
		DockerClient dockerClient = dockerClient();
		try {
			dockerClient.startContainerCmd(containerId).exec();
			L.info("Container started: " + containerId);
			return containerId;

		} finally {
			dockerClient.close();
		}
	}

	@SneakyThrows
	public static String stopContainer(String containerId) {
		DockerClient dockerClient = dockerClient();
		try {
			dockerClient.stopContainerCmd(containerId).exec();
			L.info("Stoped container:" + containerId);
			return containerId;
		} finally {
			dockerClient.close();
		}
	}

	@SneakyThrows
	public static String removeContainer(String containerId) {
		DockerClient dockerClient = dockerClient();
		try {
			dockerClient.removeContainerCmd(containerId).exec();
			L.info("Removed container:" + containerId);
			return containerId;
		} finally {
			dockerClient.close();
		}
	}

	@SneakyThrows
	public static String removeImage(String imageId) {
		DockerClient dockerClient = dockerClient();
		try {
			dockerClient.removeImageCmd(imageId).exec();
			L.info("Removed image:" + imageId);
			return imageId;
		} finally {
			dockerClient.close();
		}
	}

	@SneakyThrows
	public static Pare3<String, String, String> getContainerStatus(String containerId) {
		DockerClient dockerClient = dockerClient();
		try {
			InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
			return Pare3.of(containerInfo.getName(), containerInfo.getId(), containerInfo.getState().getStatus());
		} finally {
			dockerClient.close();
		}
	}

	@SneakyThrows
	public static List<Image> getAllImages() {

		DockerClient dockerClient = dockerClient();
		try {

			// Получение списка всех контейнеров
			ListImagesCmd listContainersCmd = dockerClient.listImagesCmd();
			listContainersCmd.withShowAll(true); // Показывать все контейнеры, включая остановленные
			List<Image> exec = listContainersCmd.exec();
			return exec; // Выполнение команды и получение результата

		} finally {
			dockerClient.close();
		}


	}

	@SneakyThrows
	public static List<Container> getAllContainers() {

		DockerClient dockerClient = dockerClient();
		try {

			// Получение списка всех контейнеров
			ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
			listContainersCmd.withShowAll(true); // Показывать все контейнеры, включая остановленные
			List<Container> exec = listContainersCmd.exec();
			return exec; // Выполнение команды и получение результата

		} finally {
			dockerClient.close();
		}


	}

	@SneakyThrows
	public static List<String> printContainerLogs(String containerId) {

		DockerClient dockerClient = dockerClient();
		try {

			LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId).withStdOut(true).withStdErr(true);
			List<String> logs = new ArrayList<>();
			logContainerCmd.exec(new ResultCallback.Adapter<Frame>() {
				@Override
				public void onNext(Frame object) {
					logs.add(object.toString());
				}
			}).awaitCompletion();
			if (true) {
				X.p("Logs:");
				logs.forEach(System.out::println);
			}
			return logs;

		} finally {
			dockerClient.close();
		}


//
//		try {
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			LogContainerCmd logsCmd = dockerClient.logContainerCmd(containerId)
//					.withStdOut(true)
//					.withStdErr(true)
//					.withFollowStream(true)
//					.withTimestamps(true)
//					.withTailAll();
//
//			logsCmd.exec(outputStream);
//			String logs = outputStream.toString();
//			System.out.println("Logs for container " + containerId + ":");
//			System.out.println(logs);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@SneakyThrows
	public static String buildImage(String imageName, String dockerFile) {

		DockerClient dockerClient = dockerClient();

		String imageId = dockerClient.buildImageCmd()
				.withDockerfile(new File(dockerFile))
				.withPull(true)
				.withNoCache(true)
				.withTag(imageName)
				.exec(new BuildImageResultCallback())
				.awaitImageId();

		return imageId;
//		try {

		// Укажите путь к Dockerfile и контексту сборки
//			File dockerFile = new File("path/to/Dockerfile");
//			File contextDir = new File("path/to/context");

		// Аутентификация в реестре (если требуется)
//			AuthConfig authConfig = new AuthConfig()
//					.withUsername("your-username")
//					.withPassword("your-password")
//					.withEmail("your-email@example.com")
//					.withRegistryAddress(imageRegistry);

//			// Создание образа
//			CreateImageCmd createImageCmd = dockerClient.createImageCmd()
//					.withFromImage(imageName)
//					.withTag("latest") // или другой тег по вашему выбору
//					.withAuthConfig(authConfig)
//					.withDockerfile(dockerFile)
//					.withBuildContext(contextDir);
//
//			// Обработка результата создания образа
//			createImageCmd.exec(new CreateImageResultCallback()).awaitCompletion();

//			File dockerFile = new File("path/to/your/Dockerfile");
//			CreateImageCmd createImageCmd = dockerClient.createImageCmd(null, new FileInputStream(dockerFile));
//
//			try (InputStream dockerFileInputStream = new FileInputStream(dockerFile)) {
////				createImageCmd.withName("your-image-name")
////						.withAuthConfig(AuthConfig.builder().build())
////						.withTarInputStream(dockerFileInputStream)
////						.exec();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			System.out.println("Образ " + imageName + " успешно создан.");
//		} catch (InterruptedException | IOException e) {
//			e.printStackTrace();
//			System.err.println("Ошибка при создании образа: " + e.getMessage());
//		}
	}
}
