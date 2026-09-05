package zk_radio.walker;

import lombok.Getter;
import mpc.env.Env;
import mpc.exception.FIllegalArgumentException;
import mpc.exception.IErrorsCollector;
import mpc.fs.UFS;
import mpc.fs.ext.GEXT;
import mpc.log.L;
import mpe.NT;
import mpe.cmsg.ns.NodeID;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.str.TKN;
import zk_notes.node.NodeDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class NodePlProc {

	private final @Getter Path nodePath;

	private boolean first;

	//
	public NodePlProc withOnlyFirst(boolean... onlyFirst) {
		this.first = ARG.isDefNotEqFalse(onlyFirst);
		return this;
	}

	@Getter
	final LinkedHashSet<String> nodesSeq0 = new LinkedHashSet<>();


	public NodePlProc(String nodeID) {
		this(NodeID.of(nodeID));
	}

	public NodePlProc(NodeDir node) {
		this(node.nodeID());
	}

	public NodePlProc(NodeID node) {
		this.nodePath = NodeDir.ofNodeId(node).toPath();

	}

	/**
	 * Пример использования класса
	 */
	public static void main(String[] args) {
		try {
			Env.setAppName(NT.BEA);
			// Путь к директории ноды
//			String playlistNodePath = "/home/dav/Загрузки";

			// Создаем процессор и обрабатываем плейлист
//			PlaylistNodeProcessor processor = new PlaylistNodeProcessor(NodeID.of("//dueshman"));
			NodeID nodeID = NodeID.of("m//");

//			NodeDir nodeDir = No.ofNodeId("m//");
//			if(nodeDir.nodeId().nodeName()==null)
//			Path path = nodeDir.toPath();
			NodePlProc processor = new NodePlProc(nodeID);
			List<Path> files = processor.processPlaylist();

			// Выводим результаты
			System.out.println("Processing node: " + processor.getNodePath());
			System.out.println("Found:\n" + files);
			for (Path file : files) {
				System.out.println("  - " + file);
			}

		} catch (IOException e) {
			System.err.println("Error processing playlist: " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Обрабатывает плейлист в директории ноды и возвращает список всех файлов
	 *
	 * @return список путей к файлам
	 * @throws IOException если ошибка при чтении файлов
	 */
	public Set<String> processPlaylistSet() throws IOException {
		return processPlaylist().stream().map(Path::toString).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public List<Path> processPlaylist() throws IOException {

		if (!Files.isDirectory(nodePath)) {
			if (L.isWarnEnabled()) {
				L.warn("Node is empty '{}'", nodePath);
			}
			return ARR.EMPTY_LIST;
		}

		// Ищем файл плейлиста в директории ноды
		// Плейлист должен иметь то же имя, что и директория, с расширением .pl

//		NodeID nodeId = NodeID.of("");
		String nodeName = nodePath.getFileName().toString();

		int before = getNodesSeq0().size();
		getNodesSeq0().add(nodeName);
		if (before == getNodesSeq0().size()) {
			if (L.isWarnEnabled()) {
				L.warn("Node *.play file already added '{}'", nodePath);
			}
		}

		Path playlistPath = nodePath.resolve(NodePlaylist.FILE_PLAYLIST);

		if (!Files.exists(playlistPath)) {
			if (L.isInfoEnabled()) {
				L.warn("Node *.play file not found. Will be used mode dir");
			}
			return GEXT.AUDIO.ls(nodePath);
		}

		return processPlaylistFile(playlistPath);
	}

	/**
	 * Обрабатывает файл плейлиста и возвращает список всех файлов
	 *
	 * @param playlistPath путь к файлу плейлиста (.pl)
	 * @return список путей к файлам
	 * @throws IOException если ошибка при чтении файлов
	 */
	private List<Path> processPlaylistFile(Path playlistPath) throws IOException {
		List<Path> result = new ArrayList<>();

		// Читаем все строки плейлиста
		List<String> lines = Files.readAllLines(playlistPath);

		for (String line : lines) {
			line = line.trim();

			// Пропускаем пустые строки
			if (line.isEmpty()) {
				continue;
			}

			// Обрабатываем строку в зависимости от формата
			processLine(line, playlistPath.getParent(), result);
		}

		return result;
	}

	/**
	 * Обрабатывает одну строку плейлиста
	 */
	private void processLine(String line, Path currentDir, List<Path> result) throws IOException {

		// Случай 1: обычный файл в текущей директории
		if (line.startsWith("#")) {
			return;

		}
		if (!line.startsWith("@@")) {
			Path filePath = currentDir.resolve(line);
			if (Files.exists(filePath) && UFS.isFile0(filePath)) {
				result.add(filePath);
			}
			if (L.isWarnEnabled()) {
				L.warn("Skip line with profile [FILE NOT FOUND]:{}", filePath);
			}
			return;
		}

		// Случай 2: @@ NODE-OTHER @@ - ссылка на другой плейлист
		if (line.startsWith("@@ ") && line.endsWith(" @@")) {
			String nodeName = TKN.bw(line, "@@ ", " @@", true, false, null);
//			String nodeName = extractNodeName(line);
			if (nodeName != null) {
				// Путь к другой ноде относительно базовой директории
//				Path basePath = nodePath.getParent();
				NodeID nodeID = NodeID.of(nodeName, null);
				if (nodeID != null) {
					if (getNodesSeq0().contains(nodeName)) {
						L.warn("Playlist alreadu use node '{}'", nodeName);
						return;
					}
					getNodesSeq0().add(nodeName);
					NodePlProc par = this;
					result.addAll(new NodePlProc(nodeName) {
						@Override
						public LinkedHashSet<String> getNodesSeq0() {
							return par.getNodesSeq0();
						}
					}.processPlaylist());
				}
			}
			if (L.isWarnEnabled()) {
				L.warn("Skip line with profile [NODE HANDLE ERROR]:{}", nodeName);
			}
			return;
		}

//		// Случай 3: @@ NODE @@ * - все файлы в директории ноды
//		if (line.startsWith("@@") && line.contains("NODE") && line.contains("*")) {
//			String nodeName = extractNodeName(line);
//			if (nodeName != null) {
//				Path targetNodePath = nodePath.getParent().resolve(nodeName);
//				if (Files.exists(targetNodePath) && Files.isDirectory(targetNodePath)) {
//					List<Path> files = Files.list(targetNodePath)
//							.filter(path -> !Files.isDirectory(path))
//							.filter(path -> !path.toString().endsWith(".pl"))
//							.collect(Collectors.toList());
//					result.addAll(files);
//				}
//			}
//			return;
//		}
//
//		// Случай 4: @@ NODE @@ file.mp3 - конкретный файл в директории ноды
//		if (line.startsWith("@@") && line.contains("NODE") && !line.contains("*")) {
//			String nodeName = extractNodeName(line);
//			String fileName = extractFileName(line);
//			if (nodeName != null && fileName != null) {
//				Path filePath = nodePath.getParent().resolve(nodeName).resolve(fileName);
//				if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
//					result.add(filePath);
//				}
//			}
//		}
	}

//	/**
//	 * Извлекает имя ноды из строки формата "@@ NODE @@ ..." или "@@ NODE-OTHER @@"
//	 */
//	private String extractNodeName(String line) {
//		String[] parts = line.split("@@");
//		if (parts.length >= 2) {
//			return parts[1].trim();
//		}
//		return null;
//	}
//
//	/**
//	 * Извлекает имя файла из строки после @@ NODE @@
//	 */
//	private String extractFileName(String line) {
//		String[] parts = line.split("@@");
//		if (parts.length >= 3) {
//			return parts[2].trim();
//		}
//		return null;
//	}

	/**
	 * Возвращает путь к директории ноды
	 */
	public Path getNodePath() {
		return nodePath;
	}

	public static class PlLine implements IErrorsCollector {

		final String profileName;
		final String file;
		final NodeID nodeID;

		public PlLine(String line) {

			if (!NodePlaylist.PfxNode.hasKeyStart(line)) {
				profileName = null;
				file = null;
				nodeID = null;
				getErrors().add(new FIllegalArgumentException("Except [%s node %s] key from pattern [%s]", NodePlaylist.PfxNode.PFX_PL, NodePlaylist.PfxNode.PFX_PL, line));
				return;
			}

			String[] nodeList = NodePlaylist.PfxNode.two(line);

			if (nodeList == null) {
				profileName = null;
				file = null;
				nodeID = null;
				getErrors().add(new FIllegalArgumentException("Invalid profile pattern [%s], except '@@ NODE @@ FILE.mp3'", line));
				return;
			}

			this.profileName = nodeList[0].trim();
			nodeID = NodeID.of(profileName);
			if (nodeID == null) {
				getErrors().add(new FIllegalArgumentException("Invalid node profile pattern [%s]", profileName));
				file = null;
				return;
			}
			String fileFromNode = nodeList[1];
			if (X.empty(fileFromNode)) {

				//ok it full profile

				file = null;
				return;
			}
			boolean hasAudio = GEXT.AUDIO.has(fileFromNode);
			if (!hasAudio) {
				getErrors().add(new FIllegalArgumentException("Profile [%s] audio type not found from [%s]", profileName, fileFromNode));
				file = null;
				return;
			}

			Path link2File = NodeDir.ofNodeId(nodeID).toPath().resolve(fileFromNode);

			if (!UFS.existFile(link2File)) {
				getErrors().add(new FIllegalArgumentException("Profile [%s] audio source not exists [%s]: ", profileName, link2File));
				file = null;
				return;
			}

			file = link2File.toString();

		}

		public List<String> searchListFiles() {
			throwIsErr();
			List list = new ArrayList();

			if (file != null) {
				list.add(file);
				return list;
			}

			Path path = NodeDir.ofNodeId(nodeID).toPath();
			List<Path> ls = GEXT.AUDIO.ls(path, ARR.EMPTY_LIST);
			List<String> collect = ls.stream().map(Path::toString).collect(Collectors.toList());
			return collect;
		}

		private List<Throwable> _errors;

		@Override
		public List<Throwable> getErrors() {
			return _errors != null ? _errors : (_errors = new LinkedList<>());
		}


	}
}
