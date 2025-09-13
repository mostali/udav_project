package zk_pages.zznsi_pages.znsi_eiview.homepage;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UFS;
import mpc.fs.UFS_BASE;
import mpc.types.tks.SO1;
import mpc.types.tks.SO2;
import mpu.X;
import mpu.core.ARR;
import mpu.core.ENUM;
import mpu.str.JOIN;
import mpu.str.STR;
import zk_form.notify.ZKI;
import zk_form.notify.ZKI_Messagebox;
import zk_notes.node.NodeDir;
import zk_pages.zznsi_pages.znsi_eiview.BEAPP;
import zk_pages.zznsi_pages.znsi_eiview.ConturMdm;
import zk_pages.zznsi_pages.znsi_eiview.homepage.sections.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class EiRun {

	final CicdSection section;

	public void doRun() {

		SectionHeader sectionHeader = section.getSectionHeader();

		String choicedConturName = sectionHeader.getChoicedConturName();

		String rcDir = NodeDir.ofCurrentPage(choicedConturName).getPathFormFcParent().toString();

		List rslts = new ArrayList<>();

		MdmUpZRunner mdmUpRunner = new MdmUpZRunner();

		fillRunArgs(mdmUpRunner);

		mdmUpRunner.contur = ConturMdm.valueOf(choicedConturName);
		mdmUpRunner.rcDir = rcDir;


		String cmd = mdmUpRunner.buildArgsAsString();

		ZKI_Messagebox.showMessageBoxBlueYN(X.f("Run %s with this settings?", section.getSectionName().toUpperCase()), cmd, yn -> {
			if (!yn) {
				return;
			}
			Object callRslt = mdmUpRunner.callJar();
			rslts.add(callRslt);

			String del = "\n----------------\n";

			Path build = Paths.get(rcDir).resolve("build");
			UFS_BASE.MKDIR.createDirs(build);
			String ls = UFS.lsAsLinesWithInfo(build);

			ZKI.infoEditorBw("Ok" + del + cmd + del + JOIN.allBy(del, rslts) + del + ls);

			RcStoreSection.rerenderFirst();
		});
	}

	private void fillRunArgs(MdmUpZRunner mdmUpRunner) {
		if (section instanceof ExportSection) {

			fillRunArgs_EXPORT(mdmUpRunner);

		} else if (section instanceof ImportSection) {

			fillRunArgs_IMPORT(mdmUpRunner);

		} else if (section instanceof NifiExportSection) {

			NifiExportSection s = (NifiExportSection) section;
			mdmUpRunner.keys.add(BEAPP.BENIFI.NIFI.toDblKey(true));
			mdmUpRunner.keys.addAll(ARR.as(SO1.wrap(NifiPropsPanel.CK_NIFIR_BUCKET), STR.capitalizeLC(s.propsPanel.tbxNifirBucket.getValue())));
			mdmUpRunner.keys.addAll(ARR.as(SO1.wrap(NifiPropsPanel.CK_NIFIR_FLOW), STR.capitalizeLC(s.propsPanel.tbxNifirFlow.getValue())));
			mdmUpRunner.keys.addAll(ARR.as(SO1.wrap(NifiPropsPanel.CK_NIFIR_VERSION), STR.capitalizeLC(s.propsPanel.tbxNifirVersion.getValue())));

		} else if (section instanceof NifiImportSection) {

			mdmUpRunner.keys.add(BEAPP.BENIFI.NIFI.toDblKey(false));

		} else {
			throw new WhatIsTypeException("What is section?" + section.getClass().getSimpleName());
		}
	}

	private void fillRunArgs_EXPORT(MdmUpZRunner mdmUpRunner) {

		ExportSection exportSection = (ExportSection) section;

		{
			mdmUpRunner.lpt[0] = section.getExportPropsPanel().lpPanel.tbxLogin.getValue();
			mdmUpRunner.lpt[1] = section.getExportPropsPanel().lpPanel.tbxPass.getValue();
			mdmUpRunner.lpt[2] = section.getExportPropsPanel().lpPanel.tbxToken.getValue();
		}

		{

			EiPageSP.BeMdmDataForm beMdmDataForm = exportSection.getSectionHeader().getBeMdmDataForm();
			EiPageSP.BeNifiDataForm beNifiDataForm = exportSection.getSectionHeader().getBeaNifiDataForm();

			//ROLES + MDMDATA
			Arrays.stream(BEAPP.BEMDM.values()).forEach(beMdm -> {
				if (beMdmDataForm.isChecked(beMdm)) {
					mdmUpRunner.keys.add("--export" + ENUM.capitalize(beMdm));
					switch (beMdm) {
						case ROLES:
							mdmUpRunner.allowedRoles = exportSection.getExportPropsPanel().tbxAllowedRoles.getValue();
							break;
						case MODEL:
							mdmUpRunner.allowedDocs = exportSection.getExportPropsPanel().tbxAllowedDocs.getValue();
							break;
					}
				}
			});

			//NIFI
			Arrays.stream(BEAPP.BENIFI.values()).forEach(beMdm -> {
				if (beNifiDataForm.isChecked(beMdm)) {
					mdmUpRunner.keys.add("--export" + ENUM.capitalize(beMdm));
				}
			});

		}
	}

	private void fillRunArgs_IMPORT(MdmUpZRunner mdmUpRunner) {

		ImportSection exportSection = (ImportSection) section;

		{
			mdmUpRunner.lpt[0] = section.getImportPropsPanel().lpPanel.tbxLogin.getValue();
			mdmUpRunner.lpt[1] = section.getImportPropsPanel().lpPanel.tbxPass.getValue();
			mdmUpRunner.lpt[2] = section.getImportPropsPanel().lpPanel.tbxToken.getValue();
		}

		{
			EiPageSP.BeMdmDataForm beMdmDataForm = exportSection.getSectionHeader().getBeMdmDataForm();
			EiPageSP.BeNifiDataForm beNifiDataForm = exportSection.getSectionHeader().getBeaNifiDataForm();

			for (BEAPP.BEMDM value : BEAPP.BEMDM.values()) {
				if (beMdmDataForm.isChecked(value)) {
					mdmUpRunner.keys.add(value.toDblKey(false));
				}
			}

			for (BEAPP.BENIFI value : BEAPP.BENIFI.values()) {
				if (beNifiDataForm.isChecked(value)) {
					mdmUpRunner.keys.add(value.toDblKey(false));
				}
			}
		}

	}
}
