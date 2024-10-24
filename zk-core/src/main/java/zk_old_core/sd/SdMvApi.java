package zk_old_core.sd;

import mpu.core.ARR;
import mpu.pare.Pare;
import mpu.X;
import mpc.exception.WhatIsTypeException;
import mpu.str.RANDOM;
import mp.utl_odb.netapp.mdl.NetUsrId;
import mpt.TrmRq;
import mpt.TrmRsp;
import zk_old_core.sd.core.RepoPageDir;
import zk_old_core.sd.core.Sd3ID;
import zk_old_core.sd.core.SdMan;
import zk_old_core.mdl.PageDirModel;

import java.nio.file.Path;

public class SdMvApi {

	public static TrmRsp mv(NetUsrId usr, TrmRq cmd) throws Sd3EE {
		String idExt = cmd.cmd7().ext();
		String idOpt = cmd.cmd7().opt1();

		Sd3ID srcID;
		Sd3ID dstID;

		if (X.empty(idOpt)) {

			// sd mv *****@************

			dstID = Sd3ID.of(idExt, true);

			Sd3ID.Type dstIdType = dstID.type();

			Pare<RepoPageDir, Path> pare = PageDirModel.getCurrentRepoWithPage();
			RepoPageDir srcRepoPage = pare.key();
			String currentSd3 = srcRepoPage.getSubdomain3();

			String srcPageName = PageDirModel.getPageName(pare.val());

			switch (dstIdType) {
				case PAGE: {
					// sd mv @dst-page-name
					CHECK_SAME_PAGES(srcPageName, dstID.page(), currentSd3);
					return mvPageIn_SelfRepo(usr, cmd, srcRepoPage, srcPageName, dstID.page());
				}
				case SD: {
					// sd mv sd@
					CHECK_SAME_SD3(currentSd3, dstID.sd3(), srcPageName);
					RepoPageDir dstRepo = getDstRepoOrCreateOrFail(cmd, dstID.sd3());
					return mvPageIn_OtherRepo(usr, cmd, srcRepoPage, srcPageName, dstRepo, srcPageName);
				}
				case SD_PAGE: {
					// sd mv sd@dst-page-name
					return mvPageIn_Repo(usr, cmd, srcRepoPage, srcPageName, dstID);
				}
				case WORD:
					return FAIL_USE_STRICT_NAMES(dstID);
				default:
					throw new WhatIsTypeException(dstIdType);
			}

		} else {

			// sd mv sd@src-page-name  sd@dst-page-name

			srcID = Sd3ID.of(idExt, true);
			dstID = Sd3ID.of(idOpt, true);

			Sd3ID.Type srcIdType = srcID.type();
			Sd3ID.Type dstIdType = dstID.type();

			switch (srcIdType) {

				case PAGE: {

					// sd mv @src-page-name *****************
					RepoPageDir srcRepoPage = PageDirModel.get().getRepo();
					String currentSd3 = srcRepoPage.getSubdomain3();

					String srcPageName = srcID.page();

					switch (dstIdType) {
						case PAGE: {
							// sd mv @src-page-name @dst-page-name
							CHECK_SAME_PAGES(srcPageName, dstID.page(), currentSd3);
							return mvPageIn_SelfRepo(usr, cmd, srcRepoPage, srcPageName, dstID.page());
						}
						case SD: {
							// sd mv @src-page-name sd@
							CHECK_SAME_SD3(currentSd3, dstID.sd3(), srcPageName);
							RepoPageDir dstRepo = getDstRepoOrCreateOrFail(cmd, dstID.sd3());
							return mvPageIn_OtherRepo(usr, cmd, srcRepoPage, srcPageName, dstRepo, srcPageName);
						}
						case SD_PAGE: {
							// sd mv @src-page-name sd@dst-page-name
							return mvPageIn_Repo(usr, cmd, srcRepoPage, srcPageName, dstID);
						}
						case WORD:
							return FAIL_USE_STRICT_NAMES(dstID);
						default:
							throw new WhatIsTypeException(srcIdType);
					}
				}

				case SD: {

					// sd mv sd@ *****************

					switch (dstIdType) {
						case SD: {
							// sd mv sd@ sd@
							if (srcID.sd3().equals(dstID.sd3())) {
								return TrmRsp.FAIL("Subdomain SrcID = DstID ( %s = %s ) ", srcID, dstID);
							}
							Sd3EE.checkExistSd3(srcID);
							Sd3EE.checkNotExistSd3(dstID);
							RepoPageDir srcRepo = SdMan.findRepo(srcID.sd3());

							Path dst = srcRepo.renameMe(dstID.sd3());
							String msg = X.fl("Repo '{}' renamed to '{}'", srcRepo.path(), dst);

							return TrmRsp.OK(msg);

						}
						case PAGE:
							// sd mv sd@ @dst-page-name
						case SD_PAGE:
							// sd mv sd@ sd@dst-page-name
							return TrmRsp.FAIL("Illegal DstID '%s'", dstID);
						case WORD:
							return FAIL_USE_STRICT_NAMES(dstID);
						default:
							throw new WhatIsTypeException(srcIdType);
					}

				}

				case SD_PAGE: {

					// sd mv sd@src-page-name *****************

					Sd3EE.checkExistSd3(srcID);
					RepoPageDir srcRepo = SdMan.findRepo(srcID.sd3());

					switch (dstIdType) {
						case PAGE: {
							// sd mv sd@src-page-name @dst-page-name
							CHECK_SAME_PAGES(srcID.page(), dstID.page(), dstID.sd3());
							return mvPageIn_SelfRepo(usr, cmd, srcRepo, srcID.page(), dstID.page());
						}

						case SD:
							// sd mv sd@src-page-name sd@
						case SD_PAGE:
							// sd mv sd@src-page-name sd@dst-page-name

							return mvPageIn_Repo(usr, cmd, srcRepo, srcID.page(), dstID);

						case WORD:
							return FAIL_USE_STRICT_NAMES(dstID);
						default:
							throw new WhatIsTypeException(srcIdType);
					}

				}
				case WORD:
					return FAIL_USE_STRICT_NAMES(dstID);
				default:
					throw new WhatIsTypeException(srcIdType);
			}

		}

	}

//	private static RepoPageDir getDstRepoOrCreateOrFail(TrmRq cmd, String dstSd3, RepoPageDir currentRepo) throws Sd3EE {
//		if (dstSd3.equals(currentRepo.getSubdomain3())) {
//			return currentRepo;
//		}
//		return getDstRepoOrCreateOrFail(cmd, dstSd3);
//	}

	private static RepoPageDir getDstRepoOrCreateOrFail(TrmRq cmd, String dstSd3) throws Sd3EE {
		boolean isExistSd3 = Sd3EE.checkExistSd3(dstSd3, true);
		if (isExistSd3) {
			return SdMan.findRepo(dstSd3);
		}
		if (!cmd.getSeqOpts().hasDouble(SdApi.FLAG_DBL_FORCE, false)) {
			throw FAIL_SD_NOT_EXIST(dstSd3);
		}
		return Sd3EE.createRepoDirInDefaultLocation(dstSd3);
	}

	private static void CHECK_SAME_PAGES(String srcPageName, String dstPageName, String currentSd3) {
		boolean isSelfPage = srcPageName.equals(dstPageName);
		if (isSelfPage) {
			FAIL_SD_ALREADY_CONTAIN_PAGE(currentSd3, srcPageName);
		}
	}

	private static void CHECK_SAME_SD3(String srcSdID, String dstSdID, String pagename) {
		boolean isSelfPage = srcSdID.equals(dstSdID);
		if (isSelfPage) {
			FAIL_SD_ALREADY_CONTAIN_PAGE(srcSdID, pagename);
		}
	}

	private static TrmRsp FAIL_SD_NOT_EXIST(String dstSd3) {
		return TrmRsp.FAIL("Subdomain '%s' not exist ( or use flag '--%s' )", dstSd3, SdApi.FLAG_DBL_FORCE);
	}

	private static TrmRsp FAIL_SD_ALREADY_CONTAIN_PAGE(String currentSd3, String srcPageName) {
		return TrmRsp.FAIL("Page '%s' already in subdomain '%s' ", srcPageName, currentSd3);
	}

	private static TrmRsp FAIL_USE_STRICT_NAMES(Sd3ID srcID) {
		return TrmRsp.FAIL("Use strict sd-names in SDID '%s' e.g. >> %s", srcID, ARR.as(Sd3ID.EXAMPLE_STRICT));
	}

	public static TrmRsp mvPageIn_Repo(NetUsrId usr, TrmRq cmd, RepoPageDir srcRepoPage, String srcPageName, Sd3ID dstID) throws Sd3EE {
		String dstSd3 = dstID.sd3();
		String dstPageName = dstID.page();
		String currentSd3 = srcRepoPage.getSubdomain3();
		boolean isSelfSubdomains = currentSd3.equals(dstSd3);
		if (isSelfSubdomains) {
			CHECK_SAME_PAGES(srcPageName, dstPageName, currentSd3);
			return mvPageIn_SelfRepo(usr, cmd, srcRepoPage, srcPageName, dstPageName);
		}
		RepoPageDir dstRepo = getDstRepoOrCreateOrFail(cmd, dstSd3);
		return mvPageIn_OtherRepo(usr, cmd, srcRepoPage, srcPageName, dstRepo, dstPageName);
	}

	public static TrmRsp mvPageIn_OtherRepo(NetUsrId usr, TrmRq cmd, RepoPageDir srcRepoPage, String srcPageName, RepoPageDir dstRepoPage, String dstPageName) throws Sd3EE {

		String srcSd3 = srcRepoPage.getSubdomain3();
		String dstSd3 = dstRepoPage.getSubdomain3();

		Sd3EE.checkExistPage(srcSd3, srcPageName);
		Sd3EE.checkNotExistPage(dstSd3, dstPageName);

		if (srcRepoPage.equals(dstRepoPage)) {
			Path page = srcRepoPage.getPageDir(srcPageName);
			srcRepoPage.moveToMe(page);
		} else {
			String rnd = RANDOM.ALPHA("renamed-paged-", 20);
			Path rndPath = srcRepoPage.renamePage(srcPageName, rnd);
			rndPath = dstRepoPage.moveToMe(rndPath);
			dstRepoPage.renamePage(rnd, dstPageName);
		}

		if (cmd.getUserAgent() == TrmRq.UA.WEB) {
			GoToSd.goTo(dstSd3, dstPageName);
		}
		String msg = X.fl("SubDomain '{}' >> '{}'. Page '{}' moved to '{}'", srcSd3, dstSd3, srcPageName, dstPageName);
		return TrmRsp.OK(msg);
	}

	public static TrmRsp mvPageIn_SelfRepo(NetUsrId usr, TrmRq cmd, RepoPageDir srcRepoPage, String srcPageName, String dstPageName) throws Sd3EE {

		String sd3 = srcRepoPage.getSubdomain3();
		Sd3EE.checkExistPage(sd3, srcPageName);
		Sd3EE.checkNotExistPage(sd3, dstPageName);

		srcRepoPage.renamePage(srcPageName, dstPageName);
		if (cmd.getUserAgent() == TrmRq.UA.WEB) {
			GoToSd.goTo(sd3, dstPageName);
		}
		String msg = X.fl("SubDomain '{}'. Page '{}' renamed to '{}'", sd3, srcPageName, dstPageName);
		return TrmRsp.OK(msg);

	}


}
