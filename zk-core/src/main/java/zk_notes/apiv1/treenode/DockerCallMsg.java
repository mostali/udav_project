package zk_notes.apiv1.treenode;

import mpc.map.MAP;
import mpc.types.opts.SeqOptions;
import mpc.types.ruprops.URuProps;
import mpe.call_msg.CallMsg;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare3;

import java.util.List;
import java.util.Map;

public class DockerCallMsg extends CallMsg {

	public static final String DOCKER_RUN_KEY = "utl_docker ";

	final String appo;
	final String appn;
	final String apph;
	final Integer appz;
	final String appk;

	final SeqOptions seqOpt;

	public static final String TEST_STRING = "APN=${{apn}};APH=${{aph}};APZ=7777;APO=\"img:${{apv}}\";APK=\"-Dapp.name=$APN -Dapp.host=$APH -Drpa=/opt/appVol/$APN\";sudo docker run -i --rm --name $APN -p $APZ:8080 -v /opt/appVol:/opt/appVol -v /var/run/docker.sock:/var/run/docker.sock $APO java $APK -jar beaapp.jar";

	public static void main(String[] args) {
		DockerCallMsg dockerCallMsg = of(TEST_STRING);
		X.exit(dockerCallMsg);
	}

	public DockerCallMsg(String fullMsg) {
		super(fullMsg = fullMsg.trim(), true);

		if (!ARR.contains(line0, true, DOCKER_RUN_KEY)) {
			addError("Except first line with starts %s", DOCKER_RUN_KEY);
		}
		seqOpt = SeqOptions.of(fullMsg);

//		Map ruPropertiesClassic = URuProps.getRuPropertiesClassic(fullMsg);
		Map ruPropertiesClassic = URuProps.getRuProperties(fullMsg, ";", "=");

		String appo = null;
		String appn = null;
		String apph = null;
		Integer appz = null;
		String appk = null;

		try {
			appo = MAP.getAsString(ruPropertiesClassic, "APO");
			appn = MAP.getAsString(ruPropertiesClassic, "APN");
			apph = MAP.getAsString(ruPropertiesClassic, "APH");
			appz = MAP.getAsInt(ruPropertiesClassic, "APZ");
			appk = MAP.getAsString(ruPropertiesClassic, "APK");
		} catch (Exception ex) {
			addError(ex);
		}

		this.appo = appo;
		this.appn = appn;
		this.apph = apph;
		this.appz = appz;
		this.appk = appk;

	}

	public static DockerCallMsg ofQk(String msg) {
		return new DockerCallMsg(msg);
	}

	public static DockerCallMsg of(String msg) {
		return (DockerCallMsg) ofQk(msg).throwIsErr();
	}

	public String start(String cid) {
		return DockerSrv.startContainer(cid);
	}

	public Pare3<String, String, String> status(String cid) {
		return DockerSrv.getContainerStatus(cid);
	}

	public String create(String name, String image, String portBinding, List<String> cmd) {
		return DockerSrv.createContainer(name, image, portBinding, cmd);
	}

	public List<String> logs(String cid) {
		return DockerSrv.printContainerLogs(cid);
	}

	public void stop(String cid) {
		DockerSrv.stopContainer(cid);
	}

	public void removeContainer(String cid) {
		DockerSrv.removeContainer(cid);
	}

	public void removeImage(String imageId) {
		DockerSrv.removeImage(imageId);
	}
}
