package zk_radio.walker;

import lombok.Getter;
import lombok.SneakyThrows;
import mpc.fs.UFS;
import mpe.cmsg.ns.NodeID;
import mpu.IT;
import mpu.X;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.str.STR;
import mpu.str.TKN;
import zk_notes.node.NodeDir;
import zk_os.core.Sdn;

import java.nio.file.Path;
import java.util.List;

public class NodePlaylist {

	public static final String FILE_PLAYLIST = ".play";

	private final @Getter NodeDir node;

	public NodePlaylist(String nodeID) {
		this(NodeDir.ofNodeId(nodeID));
	}

	public NodePlaylist(NodeDir node) {
		this.node = node;
	}

	public NodePlaylist(NodeID node) {
		this.node = NodeDir.ofNodeId(node);
	}

	public static NodePlaylist of(Sdn sdn, Path node, NodePlaylist... defRq) {
		if (UFS.existDir(node)) {
			NodePlaylist plnFile = of(NodeDir.ofDir(sdn, node));
			return plnFile;
		}
		return ARG.throwMsg(() -> X.f("Playlist node [%s] not exist"), defRq);
	}

	public static NodePlaylist of(NodeDir node) {
		return new NodePlaylist(node);
	}


	private boolean containInPlaylist(NodeDir node) {
		return containInPlaylist(node.nodeId());
	}

	public boolean containInPlaylist(String playlistname) {
		Path playlistFile = getPlaylistFile();
		List<String> plLines = RW.readLines(playlistFile, ARR.EMPTY_LIST);
		return X.notEmpty(plLines) && plLines.contains(PfxNode.wrap(playlistname));
	}

	public Path getPlaylistFile() {
		return node.toPath().resolve(FILE_PLAYLIST);
	}

	public Boolean addPlaylistAsNode(NodeDir node) {
		Path playlistSrc = getPlaylistFile();
		if (containInPlaylist(node)) {
			return false;
		}
		RW.writeAppend(playlistSrc, STR.NL + PfxNode.wrap(node.nodeId()));
		return true;
	}

	public String getContentAsString() {
		return RW.readContent(getPlaylistFile());
	}

	public String nodeName() {
		return node.nodeName();
	}

	@SneakyThrows
	public NodePlProc getPlaylistFiles(boolean... first) {
		return new NodePlProc(node).withOnlyFirst(first);
	}


	public static class PfxNode {

		public static final String PFX_PL = "@@";

		public static String wrap(String nodeId) {
			return PFX_PL + " " + nodeId + " " + PFX_PL;
		}

		public static boolean hasKeyStart(String line) {
			return line.startsWith(PfxNode.PFX_PL);
		}

		public static String[] two(String line) {
			IT.state(PfxNode.hasKeyStart(line));
			String[] strings = TKN.twoGreedy(line.substring(PfxNode.PFX_PL.length()), PfxNode.PFX_PL, null);
			strings[0] = strings[0].trim();
			strings[1] = strings[1].trim();
			return strings;
		}

		public static String unwrap(String playlist) {
			return TKN.bw(playlist, "@@ ", " @@", true, false, playlist);
		}
	}
}
