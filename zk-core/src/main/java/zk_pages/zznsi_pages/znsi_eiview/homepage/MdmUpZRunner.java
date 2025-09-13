package zk_pages.zznsi_pages.znsi_eiview.homepage;

import lombok.RequiredArgsConstructor;
import mpc.fs.UFS;
import mpe.str.ARGS;
import mpf.zcall.ZJar;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.str.JOIN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_pages.zznsi_pages.znsi_eiview.ConturMdm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
class MdmUpZRunner {

	public static final Logger L = LoggerFactory.getLogger(MdmUpZRunner.class);

	public static void main(String[] args) {
		String choicedConturName = "DEV2";

//			String rcDir = NodeDir.ofCurrentPage(choicedConturName).getPathFormFcParent().toString();
		String rcDir = "/opt/appVol/bea/.planes/.index/eiview/.forms/DEV2/";

		MdmUpZRunner mdmUpRunner = new MdmUpZRunner();

		mdmUpRunner.keys.add("--exportRoles");

//			mdmUpRunner.allowedDocs = propsPanel.tbxAllowedDocs.getValue();
		mdmUpRunner.allowedRoles = "Super_user_MDM,Support_MDM,user_MDM,Admin_MDM";
		mdmUpRunner.contur = ConturMdm.valueOf(choicedConturName);
		mdmUpRunner.rcDir = rcDir;
		mdmUpRunner.lpt[0] = "admin";
		mdmUpRunner.lpt[1] = "Oracle33";

		Object o = mdmUpRunner.callJar();
		X.exit(o);
	}

	final List<String> keys = new ArrayList<>();
	ConturMdm contur;
	String rcDir;
	String[] lpt = new String[3];
	String allowedDocs, allowedRoles;

	public Object callJar() {
		String lclJar = "/opt/appVol/bea/.planes/.index/eiview/.forms/runner/mdmup.jar";
		String pkgJar = "nsi.cicd";
		if (!UFS.existFile(lclJar)) {
			lclJar = "/home/ditts.aleksandr/.data/znsi/.planes/.index/eiview/.forms/runner/mdmup.jar";
		}
		IT.isFileExist(lclJar);
		ZJar zJar = ZJar.of(lclJar, pkgJar);
		String[] args = buildArgs();
		if (L.isInfoEnabled()) {
			L.info("callJar '{}' with args {}", zJar, ARR.as(args));
		}
		Object invokeLines = zJar.invokeWithArgs1("invokeLines", args);
		return invokeLines;

	}

	public String buildArgsAsString() {
		return JOIN.argsBySpace(buildArgs());
	}

	public String[] buildArgs() {
//					MdmUp.main(ARR.of("--exportRoles", "-rcdir", "/home/dav/pjnsi/insi/_cicd/tmp-mdm-roles/" + stand, "-mdm-stand", stand.name(), "-mdm-login", "admin", "-mdm-pass", "Oracle33", "-allowedRoles", allowedRoles));
		List<String> outArgs = new LinkedList<>();
		if (X.notEmpty(keys)) {
			outArgs.addAll(keys);
		}

		outArgs.add("--ui");

		outArgs.add("-rcdir");
		outArgs.add(IT.NE(rcDir));

		outArgs.add("-mdm-stand");
		outArgs.add(IT.NN(contur).toString());

		if (X.notEmpty(lpt[2])) {

			outArgs.add("-mdm-token");
			outArgs.add(IT.NB(ARGS.argsAsStr(lpt, 2), "set not-blank [-mdm-token]"));

		} else {

			String login = ARGS.argsAsStr(lpt, 0, null);

			outArgs.add("-mdm-login");
			outArgs.add(IT.NB(login, "set [-mdm-login]"));

			String pass = ARGS.argsAsStr(lpt, 1, null);
			if (pass != null) {

				outArgs.add("-mdm-pass");
				outArgs.add(IT.NB(pass, "set [-mdm-pass]"));

			}

		}

		if (X.notEmpty(allowedDocs)) {
			outArgs.add("-allowedDocs");
			outArgs.add(allowedDocs);
		}
		if (X.notEmpty(allowedRoles)) {
			outArgs.add("-allowedRoles");
			outArgs.add(allowedRoles);
		}

		return outArgs.toArray(new String[outArgs.size()]);
	}
}
