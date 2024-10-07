package zk_com.base;

import mpu.core.RW;
import mp.utilspoi.UMd2Html;
import org.zkoss.zk.ui.Page;

import java.io.IOException;
import java.nio.file.Path;

public class Md extends Xml {

	public String data_md;

	public Md(String md) {
		super(UMd2Html.buildHtml(md));
		this.data_md = md;
	}

	public Md(Path fileMd) throws IOException {
		this(RW.readContent_(fileMd));
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
	}

	public static Md buildComponentFromFile(Path fileMd) throws IOException {
		return new Md(loadDataFrom(fileMd));
	}

	public static Md ofRsrc(String file) throws IOException {
		return new Md(loadDataFromRsrc(file));
	}

}
