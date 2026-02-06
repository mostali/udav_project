package zk_form.control.breadcrumbs;

import mpc.exception.WhatIsTypeException;
import mpc.str.sym.SYMJ;
import mpu.core.ARR;
import mpu.pare.Pare;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Menupopup0;
import zk_notes.ANI;
import zk_notes.events.ANMD;
import zk_notes.events.ANMP;
import zk_os.core.Sdn;
import zk_os.sec.SecMan;
import zk_page.ZKC;
import zk_page.ZKCFinderExt;
import zk_page.events.ECtrl;
import zk_page.index.PageDdChoicer;
import zk_page.index.PlaneDdChoicer;
import zk_page.index.RSPath;

public class BreadDiv extends Div0 {

	public static BreadDiv findFirst(BreadDiv... defRq) {
		return ZKCFinderExt.findFirst_inPage0(BreadDiv.class, true, defRq);
	}

	static Double[] TOP_LEFT = ARR.of(9.0, 13.0);

	public BreadDiv(Pare sdn) {
		super();

		Sdn sdn0 = Sdn.of(sdn);
		RSPath pathType = sdn0.getPathType();

		BreadLn rootLn = null, planeLn = null, pageLn = null;

		switch (pathType) {

			case PAGE:

//				pageLn = new BreadLn(SYMJ.ARROW_REPEAT_TRIANGLE_BW + " " + sdn0.page(), e -> RSPath.redirectToPage(sdn0));
				pageLn = new BreadLn(ANI.PAGE_ENT + " " + sdn0.page());
				pageLn.onCLICK(e -> {
					if (ECtrl.ofAsCtrl(e) == ECtrl.CTRL) {
						PageDdChoicer child = new PageDdChoicer(sdn0.plane()) {

							@Override
							public void onChoicePage(String pagename) {
								RSPath.toPage_Redirect_CheckNoIndex(sdn0.plane(), pagename);
							}
						};
						child.openDefaultModalWindow("Choice page..");
					} else {
						sdn0.redirectTo();
					}
				});
//				pageLn = new BreadLn(SYMJ.ARROW_RIGHT_SPEC + " " + sdn0.page(), e -> RSPath.redirectToPage(sdn0));

				if (SecMan.isAllowedEditPlane(sdn0.plane())) {
					Menupopup0 pageMenu = pageLn.getOrCreateMenupopup(ZKC.getFirstWindow());
					ANMP.applyPageLink(pageMenu, sdn0);
				}


			case PLANE: {


				if (!sdn0.isEmptyOrIndexPlane()) { //not show with index plane

					planeLn = new BreadLn(sdn0.plane());

					planeLn.onCLICK(e -> {
						if (ECtrl.ofAsCtrl(e) == ECtrl.CTRL) {
							PlaneDdChoicer child = new PlaneDdChoicer() {
								@Override
								public void onChoiceSd3(String sd3) {
									RSPath.toPlane_Redirect_CheckNoIndex(sd3);
								}
							};
							child.openDefaultModalWindow("Choice plane..");
						} else {
							sdn0.toSdnPlane().redirectTo();
						}
					});

					if (SecMan.isAllowedEditPlane(sdn0.plane())) {
						Menupopup0 planeMenu = planeLn.getOrCreateMenupopup(ZKC.getFirstWindow());
						ANMD.applyPlaneLink(planeMenu, sdn0.plane());
					}
				}


			}

			case ROOT:
//				rootLn = new BreadLn(SYMJ.HOME, e -> RSPath.redirectToPage(Sdn.ofRootPlane()));
				rootLn = new BreadLn(SYMJ.GLOB_GRID, e -> Sdn.ofRootPlane().redirectTo());
				break;


			default:
				throw new WhatIsTypeException(pathType);
		}

		appendChild(rootLn);

		if (planeLn != null) {
			appendChild(planeLn);
		}

		if (pageLn != null) {
			appendChild(pageLn);
		}

		fixed();

		top_left(TOP_LEFT[0], TOP_LEFT[1]);
	}


}
