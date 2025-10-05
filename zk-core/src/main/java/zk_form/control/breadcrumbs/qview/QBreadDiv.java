package zk_form.control.breadcrumbs.qview;

import mpu.pare.Pare;
import zk_com.base_ctr.Div0;
import zk_os.core.Sdn;
import zk_page.index.RSPath;

public class QBreadDiv extends Div0 {

//	final BreadPos level;

//	public BreadDiv() {
//		super();//root
//		appendChild(new BreadLb(AxnTheme.getIcon(), level = BreadPos.ROOT0));
//	}
//
//
//	public BreadDiv(String planeName, boolean... withPlaneLabel) {
//		super();//plane
//		appendChild(new BreadMasterLn(level = BreadPos.PLANE10, RSPath.ROOT.icon()).decoration_none());
//		appendChild(new BreadLb(planeName, BreadPos.PLANE11, withPlaneLabel));
//	}

	public QBreadDiv(Pare sdn) {
		super();//root , plane, page
//		breadMasterLn =
//		breadMasterLn = (BreadLn) new BreadMasterLn(level = 100, SYMJ.ARROW_REPEAT_TRIANGLE_GREEN, true).decoration_none();
//				breadLb =;

		RSPath pathType = Sdn.of(sdn).getPathType();

//		pathType.toPlanPage()
//
//		switch (pathType) {
//			case ROOT:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PAGE100, SYMJ.HOME, true).decoration_none();
//				breadLb = new BreadLb(AxnTheme.getIcon(), BreadPos.ROOT0);
//				break;
//			case PLANE:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PLANE10, RSPath.ROOT.icon()).decoration_none();
//				breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), BreadPos.PAGE101);
//				break;
//			case PAGE:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PAGE100, SYMJ.ARROW_REPEAT_TRIANGLE_BW, true).decoration_none();
//				breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), BreadPos.PAGE101);
//				break;
//
//			default:
//				throw new WhatIsTypeException(pathType);
//		}

//		BreadLn breadMasterLn;
//		BreadLb breadLb;
//		switch (pathType) {
//			case ROOT:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PAGE100, SYMJ.HOME, true).decoration_none();
//				breadLb = new BreadLb(AxnTheme.getIcon(), BreadPos.ROOT0);
//				break;
//			case PLANE:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PLANE10, RSPath.ROOT.icon()).decoration_none();
//				breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), BreadPos.PAGE101);
//				break;
//			case PAGE:
//				breadMasterLn = (BreadLn) new BreadMasterLn(BreadPos.PAGE100, SYMJ.ARROW_REPEAT_TRIANGLE_BW, true).decoration_none();
//				breadLb = new BreadLb(sdn.keyStr(), sdn.valStr(), BreadPos.PAGE101);
//				break;
//
//			default:
//				throw new WhatIsTypeException(pathType);
//		}

//		if (breadMasterLn != null) {
//			appendChild(breadMasterLn);
//		}
//		if (breadLb != null) {
//			appendChild(breadLb);
//		}

//		}

	}

//	@Override
//	protected void init() {
//		super.init();
//	}

//	@Getter
//	@NonNull
//	private BreadLn breadMasterLn, breadLn;
//	private BreadLb breadLb;

//	boolean withPlaneLabel = true;
//
//	public BreadDiv withPlaneLabel() {
//		this.withPlaneLabel = withPlaneLabel;
//		return this;
//	}
}
