package zk_old_core.sd.core;

import lombok.SneakyThrows;
import mpu.core.ARG;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpu.pare.Pare;
import zk_old_core.AppZosCore_Old;
import zk_page.core.PagePathInfo;

import java.nio.file.Path;

public class SdRsrc {
	@SneakyThrows
	public static Path getParentLocation(String requestRsrcPath, PagePathInfo ppi) {
		LocRsrc locRsrc = LocRsrc.of(requestRsrcPath, null);
		if (locRsrc == null) {
			return null;
		}
		Path parent = LocRsrc.getParentOfStdLocation(locRsrc, ppi, null);
		return parent;
	}

	public enum LocRsrc {
		PAGE_ASSETS, PAGE_UPLOADS, SD_ASSETS, SD_UPLOADS, SITE_UPLOADS, SITE_ASSETS;

		public static final String DIR_PAGE_ASSETS = "@assets/";
		public static final String DIR_SD_ASSETS = "@@assets/";
		public static final String DIR_PAGE_UPLOADS = "@uploads/";
		public static final String DIR_SD_UPLOADS = "@@uploads/";

		public static LocRsrc of(String requestRsrcPath, LocRsrc... defRq) {
			if (requestRsrcPath.startsWith(DIR_PAGE_ASSETS)) {
				return LocRsrc.PAGE_ASSETS;
			} else if (requestRsrcPath.startsWith(DIR_SD_ASSETS)) {
				return LocRsrc.SD_ASSETS;
			} else if (requestRsrcPath.startsWith(AppZosCore_Old.rpaAssetsDir_)) {
				return LocRsrc.SITE_ASSETS;
			} else if (requestRsrcPath.startsWith(DIR_PAGE_UPLOADS)) {
				return LocRsrc.PAGE_UPLOADS;
			} else if (requestRsrcPath.startsWith(DIR_SD_UPLOADS)) {
				return LocRsrc.SD_UPLOADS;
			} else if (requestRsrcPath.startsWith(AppZosCore_Old.rpaUploadsDir_)) {
				return LocRsrc.SITE_UPLOADS;
			}
			return ARG.toDefThrow(() -> new RequiredRuntimeException("LocRsrc not found from path '%s'", requestRsrcPath), defRq);
		}

		public static LocRsrc ofShortCmd(String cmd, LocRsrc... defRq) {
			switch (cmd) {
				case "@ass":
					return PAGE_ASSETS;
				case "@@ass":
					return SD_ASSETS;
				case "@@@ass":
					return SITE_ASSETS;
				case "@up":
					return PAGE_UPLOADS;
				case "@@up":
					return SD_UPLOADS;
				case "@@@up":
					return SITE_UPLOADS;
				default:
					return ARG.toDefThrow(() -> new RequiredRuntimeException("LocRsrc Not found by value '%s'", cmd), defRq);
			}
		}

		public Path getParentOfStdLocation(PagePathInfo ppi) {
			return getParentOfStdLocation(this, ppi, null);
		}


		@SneakyThrows
		public static Path getParentOfStdLocation(LocRsrc locRsrc, PagePathInfo ppi, String sd3) {
			switch (locRsrc) {
				case PAGE_ASSETS:
				case PAGE_UPLOADS: {
					Pare<RepoPageDir, Path> pageDir = SdMan.findPage(IT.NN(ppi, "set ppi"));
					return locRsrc.getParentOfStdLocationForPageOrSd(pageDir.val());
				}
				case SD_ASSETS:
				case SD_UPLOADS: {
					if (sd3 == null) {
						IT.NN(ppi, "set ppi or sd3");
						sd3 = ppi.subdomain3();
					} else if (sd3.isEmpty()) {
						sd3 = SdMan.ROOT_SD3_DIR;
					}
					return locRsrc.getParentOfStdLocationForPageOrSd(SdMan.findRepo(sd3).path());
				}
				case SITE_ASSETS:
					return AppZosCore_Old.getRpaAssetsPath();
				case SITE_UPLOADS:
					return AppZosCore_Old.getRpaUploadPath();
				default:
					throw new WhatIsTypeException(locRsrc);
			}
		}

		public Path getParentOfStdLocationForPageOrSd(Path pageOrRepoDir) {
			switch (this) {
				case PAGE_ASSETS:
					return pageOrRepoDir.resolve(DIR_PAGE_ASSETS);
				case PAGE_UPLOADS:
					return pageOrRepoDir.resolve(DIR_PAGE_UPLOADS);
				case SD_ASSETS:
					return pageOrRepoDir.resolve(DIR_SD_ASSETS);
				case SD_UPLOADS:
					return pageOrRepoDir.resolve(DIR_SD_UPLOADS);
				case SITE_ASSETS:
					return AppZosCore_Old.getRpaAssetsPath();
				case SITE_UPLOADS:
					return AppZosCore_Old.getRpaUploadPath();
				default:
					throw new WhatIsTypeException(this);
			}
		}

		public String nameru() {
			switch (this) {
				case PAGE_ASSETS:
					return "PageAssets";
				case PAGE_UPLOADS:
					return "PageUploads";
				case SD_ASSETS:
					return "DomainAssets";
				case SD_UPLOADS:
					return "DomainUploads";
				case SITE_ASSETS:
					return "SiteAssets";
				case SITE_UPLOADS:
					return "SiteUploads";
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}
}
