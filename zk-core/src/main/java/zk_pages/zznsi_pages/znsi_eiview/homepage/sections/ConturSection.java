package zk_pages.zznsi_pages.znsi_eiview.homepage.sections;

import org.zkoss.zul.Window;
import zk_com.base_ctr.Div0;
import zk_com.core.IZWin;
import zk_notes.factory.NFCreate;
import zk_notes.node.NodeDir;
import zk_notes.node.core.NVT;
import zk_notes.node_state.FormState;
import zk_pages.zznsi_pages.znsi_eiview.homepage.EiPageSP;
import zk_pages.zznsi_pages.znsi_eiview.homepage.SectionHeader;

public class ConturSection extends CicdSection {
	@Override
	protected void init() {
		super.init();

		appendChild(new SectionHeader("Contur's") {
			{
				setSingleBt(true);
			}
		});

		Div0 conturProps = new Div0();
		conturProps.flex();
		{
			NodeDir contur1 = NodeDir.ofCurrentPage("contur");
			IZWin form = NFCreate.createForm(contur1, NVT.TEXT);
			FormState formStateProps = form.getFormState_PROPS();
			formStateProps.fields().set_SIZE(3);
//				formStateProps.fields().set_WIDTH(20);
			form.width(300);
			formStateProps.fields().set_HEIGHT(50);
			Window window = form._modal(Window.Mode.EMBEDDED)._showInWindow(conturProps);
//				window.setWidth("500px");
//				appendChild((Component) form);
		}
		{
			NodeDir contur1 = NodeDir.ofCurrentPage("contur2");
			IZWin form = NFCreate.createForm(contur1, NVT.TEXT);
			FormState formStateProps = form.getFormState_PROPS();
			formStateProps.fields().set_SIZE(3);
//				formStateProps.fields().set_WIDTH(20);
			form.width(300);
			formStateProps.fields().set_HEIGHT(50);
			Window window = form._modal(Window.Mode.EMBEDDED)._showInWindow(conturProps);
//				window.setWidth("500px");
//				appendChild((Component) form);
		}

		appendChild(conturProps);
//			appendChild(Xml.HR());


//			PropsPanel propsPanel = new PropsPanel();
//			appendChild(propsPanel);

//		appendChild(ImportSection.BOTTOM_SILVER.get());

		EiPageSP.SECTION_BR.apply(this);

	}
}
