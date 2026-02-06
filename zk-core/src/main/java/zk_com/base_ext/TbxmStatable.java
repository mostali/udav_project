package zk_com.base_ext;


import mpc.env.Env;
import mpc.fs.Ns;
import mpc.fs.UFS;
import mpu.core.RW;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SerializableEventListener;
import zk_com.base.Tbxm;

import java.nio.file.Path;

/**
 * @author dav 12.01.2022   18:50
 */
public class TbxmStatable extends Tbxm {

	Ns ns;

	public TbxmStatable(Path rsPath) {
		super();
		ns = Ns.ofUnsafe(rsPath);
	}

	@Override
	protected void init() {
		super.init();

		String cat = readValue();
		setValue(cat);

		onCHANGING((SerializableEventListener<Event>) event -> onChangingImpl(event));

	}

	private String readValue() {
		return ns.fCat(null);
	}

	@Override
	public void save() {
		super.save();
		ns.fWriteIn(getValue());

		UFS.RM.fdQk(getHomeTmpFile());
	}

	public boolean hasTmpContent() {
		return UFS.isFileWithContent(getHomeTmpFile());
	}

	Path getHomeTmpFile() {
//		return ns.space().homeChildPath(ns.path(), "tmp.value");
		return Env.TMP.resolve("last.tbxm." + ns.fName());
	}

	public void onChangingImpl(Event event) {
		Path org = ns.toPath();
		String orgCnt = RW.readString(org);
		String newCnt = getValue();
		if (orgCnt.equals(newCnt)) {
			return;
		}
		RW.write(getHomeTmpFile(), newCnt);
	}


	//	@Override
//	protected void onSubmitTextValue(Event e) {
//		super.onSubmitTextValue(e);
//	}

//	public TbxmStatable(Path filePath) {
//		super(filePath);
//		adpativeDims(true);
//		multiline(true);
//	}

}
