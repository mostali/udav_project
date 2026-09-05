package mpe.cmsg.core;

import mpc.env.Env;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.json.GsonMap;
import mpc.log.L;
import mpc.map.MAP;
import mpe.cmsg.ns.INodeID;
import mpe.cmsg.ns.NodeID;
import mpe.str.CN;
import mpu.core.RW;
import mpu.pare.Pare;
import mpu.str.RANDOM;
import mpu.str.Sb;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class NewNode implements INodeID {

	public static final String FN_NODE_DATA = "AppNotes.props";
	public static final String FN_NODE_PROPS = "AppNotes.props..";
	public static final String DN_COMS = ".coms";
	public static final String DN_FORMS = ".forms";
	public static final String FN_NODE_COM = "AppNotes.json";

	public final Pare<String, String> sdn;

	public final String nodeName;

	public String nodeName() {
		return nodeName;
	}

	public NewNode(Pare<String, String> sdn, String nodeName) {
		this.sdn = NodeID.wrapSdn(sdn);
		this.nodeName = nodeName;
	}

	@Override
	public String toObjId() {
		return NodeID.of(sdn, nodeName).toObjId();
	}

	public boolean isNewProps() {
		return !UFS.existFile(getTargetNodePropsFile());
	}

	public boolean isNewData() {
		return !UFS.existFile(getTargetNodeDataFile());
	}

	public NewNode writeForm(Optional<String> data, Optional<String> dataProps, Optional<String> linkProps) {
		if (data != null) {
			writeFormData(data);
		}
		if (dataProps != null) {
			writeFormProps(dataProps);
		}
		if (linkProps != null) {
			writeFormLinkProps(linkProps);
		}
		return this;
	}

	public void writeFormData(Optional<String> data) {
		Path targetNodeDataFile = getTargetNodeDataFile();
		if (data.isEmpty()) {
			UFS.RM.fileQk(targetNodeDataFile);
			L.info("WriteRemove newNote in {} with data:\n{}", UF.ln(targetNodeDataFile), data);
			return;
		}
		RW.write(targetNodeDataFile, data.get(), true);
		L.info("Write newNote in {} with data:\n{}", UF.ln(targetNodeDataFile), data);
	}

	public void writeFormProps(Optional<String> dataProps) {
		Path targetNodeDataFile = getTargetNodePropsFile();
		if (dataProps.isEmpty()) {
			UFS.RM.fileQk(targetNodeDataFile);
			L.info("WriteRemove newNote in {} with props:\n{}", UF.ln(targetNodeDataFile), dataProps);
			return;
		}
		RW.write(targetNodeDataFile, dataProps.get(), true);
		L.info("Write newNote in {} with props:\n{}", UF.ln(targetNodeDataFile), dataProps);
	}

	public void writeFormLinkProps(Optional<String> dataProps) {
		Path targetNodeDataFile = getTargetNodeLinkPropsFile();
		if (dataProps.isEmpty()) {
			UFS.RM.fileQk(targetNodeDataFile);
			L.info("WriteRemove newNote in {} with link-props:\n{}", UF.ln(targetNodeDataFile), dataProps);
			return;
		}
		RW.write(targetNodeDataFile, dataProps.get(), true);
		L.info("Write newNote in {} with link-props:\n{}", UF.ln(targetNodeDataFile), dataProps);
	}

	public Path getTargetNodeDataFile() {
		return getTargetNodeDir(true).resolve(NewNode.FN_NODE_DATA);
	}

	public Path getTargetNodePropsFile() {
		return getTargetNodeDir(true).resolve(NewNode.FN_NODE_PROPS);
	}

	public Path getTargetNodeLinkPropsFile() {
		Path targetNodeDir = getTargetNodeDir(false);
		return targetNodeDir.resolve(FN_NODE_COM);
	}

	public Path getTargetNodeDir(boolean formOrCom) {
		return Env.RPA.resolve(NodeID.PLANES_DIR).resolve(NodeID.wrapPlane(sdn.key())).resolve(NodeID.wrapPlane(sdn.val())).resolve(formOrCom ? NodeID.FORMS_DIR : NodeID.COMS_DIR).resolve(nodeName);
	}

	public Optional<String> buildNodeData() {

		Sb sb = new Sb();
		sb.NL("NewNode: " + nodeName);

		return Optional.of(sb.toString());
	}

	public NewNode buildNode() {

		writeForm(buildNodeData(), buildNodeProps(), buildNodeLinkProps());

		return this;
	}

	public Optional<String> buildNodeProps() {

		Map map = MAP.of(CN.STATE, "TEXT");

		map.put(CN.WIDTH, "460px");
		map.put(CN.HEIGHT, "160px");
		map.put("pos", "REL");

		map.put(CN.TOP, RANDOM.range(220, 800) + "px");
		map.put(CN.LEFT, RANDOM.range(220, 800) + "px");

		String dataProps = GsonMap.of(map).toStringPrettyJson();

		return Optional.of(dataProps);
	}

	public Optional<String> buildNodeLinkProps() {

		Map map = MAP.of(CN.STATE, "TEXT");

		map.put("link.visible", false);

		String dataProps = GsonMap.of(map).toStringPrettyJson();

		return Optional.of(dataProps);
	}
}
