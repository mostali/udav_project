package zk_old_core.mdl.pageset;

import mpu.core.ARR;
import mpu.core.ARG;
import mpu.X;
import mpc.exception.WhatIsTypeException;
import mpc.fs.ext.EXT;
import mpc.fs.ext.MapExt;
import mpc.fs.UFS;
import mpc.fs.path.UPath;
import mpu.str.Rt;
import mpu.str.STR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zk_old_core.mdl.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageSet {

	public static final Logger L = LoggerFactory.getLogger(PageSet.class);

	public static void main(String[] args) {
		//		PageSet pageSet = PageSet.explodePageDirModel(PageDirModel.of("/home/dav/.data/web_bea_local/repo_pages/master_repo/index"));
		//		P.exit(pageSet.getIForms());
	}

	final List<FdModel> undefinedDirEntity = new ArrayList<>();
	final List<FdModel> undefinedFileEntity = new ArrayList<>();

	private final PageHeadSet headSet = new PageHeadSet();
	private final PageBodySet bodySet = new PageBodySet();

	//	public static PageSet explodePageDirModel(Path pageDir) {
	//		MapExt mapExt = MapExt.ofNatural(pageDir);
	//		return explodePageDirModel(mapExt.map());
	//	}

	public static PageSet explodePageDirModel(Map<Path, EXT> pageDir) {
		PageSet pageSet = new PageSet();

		for (Map.Entry<Path, EXT> pathEntry : pageDir.entrySet()) {
			Path path = pathEntry.getKey();
			if (pathEntry.getValue() == null) {//IT DIR

				fillPageSetDir(pageSet, path);

			} else {

				fillPageSetFile(pageSet, path, pathEntry.getValue());

			}
		}

		if (L.isWarnEnabled()) {
			if (X.notEmpty(pageSet.undefinedDirEntity)) {
				L.warn("After init PageSet: undefinedDirEntity:\n" + Rt.buildReport(pageSet.undefinedDirEntity));
			}
			if (X.notEmpty(pageSet.undefinedFileEntity)) {
				L.warn("After init PageSet: undefinedFileEntity:\n" + Rt.buildReport(pageSet.undefinedFileEntity));
			}
		}
		return pageSet;
	}

	public static void fillPageSetDir(PageSet pageSet, Path path) {
		String filename = path.getFileName().toString();
		if (filename.startsWith(FdModel.SKIP_PFX)) {
			return;
		}
		switch (filename) {
			case "head": {
				pageSet.headSet.addDir(path);
				break;
			}
			case "body": {
				pageSet.bodySet.addDir(path);
				break;
			}
			default:
				pageSet.undefinedDirEntity.add(UndefinedFdModel.of(path));
				break;
		}
	}

	public static void fillPageSetFile(PageSet pageSet, Path path, EXT ext) {

		String filename = path.getFileName().toString();

		if (filename.startsWith(FdModel.SKIP_PFX)) {
			return;
		}

		String[] fn_ext = EXT.twoRq(filename, null);
		if (fn_ext == null) {
			if (L.isWarnEnabled()) {
				L.warn("WARN: SKIP build component: from path '{}' ( FILE WITHOUT EXT )", STR.pfile(path));
			}
			pageSet.undefinedFileEntity.add(UndefinedFdModel.of(path));
			return;
		}

		PagePostion pagePosition = PagePostion.ofFilename(fn_ext[0], null);
		if (pagePosition == null) {
			if (L.isWarnEnabled()) {
				L.warn("WARN: SKIP build component: from path '{}' ( UNKNOWN PAGE POSITION NAME )", STR.pfile(path));
			}
			pageSet.undefinedFileEntity.add(UndefinedFdModel.of(path));
			return;
		}

		switch (pagePosition) {
			case HEAD:
				pageSet.headSet.addFileEntity(new HeadFileModel(path));
				return;
			case HEADER:
			case BODY:
			case FOOTER:
				pageSet.bodySet.addFileEntity(new FormFileModel(path, pagePosition));
				return;
			default:
				throw new WhatIsTypeException(pagePosition);
		}

	}

	transient List<IFormModel> fdFormModels;
	transient List<HeadFileModel> fdHeadModels;

	public List<IFormModel> getIForms(boolean... fresh) {
		if (fdFormModels != null && ARG.isDefNotEqTrue(fresh)) {
			return fdFormModels;
		}
		return this.fdFormModels = bodySet.getIForms();
	}

	public List<HeadFileModel> getIStaticHeads(boolean... fresh) {
		if (fdHeadModels != null && ARG.isDefNotEqTrue(fresh)) {
			return fdHeadModels;
		}
		this.fdHeadModels = headSet.getIHeads();
		{
			{
				if (false) {
					List<IFormModel> forms = getIForms();
					for (IFormModel form : forms) {
						Path head = form.fd().path("head");
						if (UFS.existDir(head)) {
							this.fdHeadModels.addAll(HeadFileModel.extractDir(head));
						}
					}
				}
			}
		}
		return this.fdHeadModels;
	}

	public static class PageLayoutSet {
		final List<IFormModel> header = new ArrayList<>();
		final List<IFormModel> body = new ArrayList<>();
		final List<IFormModel> footer = new ArrayList<>();

		public void add(IFormModel f) {
			switch (f.getLayoutPosition()) {
				case HEADER:
					header.add(f);
					break;
				case BODY:
					body.add(f);
					break;
				case FOOTER:
					footer.add(f);
					break;
				default:
					throw new WhatIsTypeException(f.getLayoutPosition());
			}
		}

		public List<IFormModel> getAll() {
			return ARR.mergeAll(new ArrayList(), header, body, footer);
		}
	}

	public static class PageBodySet extends FdSet<IFormModel> {
		public List<IFormModel> getIForms() {
			PageLayoutSet pageLayoutSet = new PageLayoutSet();
			if (X.notEmpty(fileEntities)) {
				for (IFormModel fileEntity : fileEntities) {
					if (fileEntity.fd().name().startsWith(FdModel.SKIP_PFX)) {
						continue;
					} else {
						pageLayoutSet.add(fileEntity);
					}
				}
			}
			if (X.notEmpty(dirEntities)) {
				for (MapExt dir : dirEntities) {
					if (dir.isEmpty()) {
						continue;
					}
					dir.map().forEach((p, e) -> {
						if (!UPath.startsWith(p, FdModel.SKIP_PFX)) {
							PagePostion layoutPostion = PagePostion.ofFilename(p.getFileName().toString(), PagePostion.BODY);
							if (e == null) {
								pageLayoutSet.add(new FormDirModel(p, layoutPostion));
							} else {
								pageLayoutSet.add(new FormFileModel(p, layoutPostion));
							}
						}
					});
				}
			}
			return pageLayoutSet.getAll();
		}
	}

	public static class PageHeadSet extends FdSet<HeadFileModel> {
		public List<HeadFileModel> getIHeads() {
			List total = ARR.ar();
			if (X.notEmpty(fileEntities)) {
				total.addAll(fileEntities);
			}
			if (X.notEmpty(dirEntities)) {
				List<HeadFileModel> all = HeadFileModel.extractDirs(dirEntities);
				total.addAll(all);
			}
			return total;
		}
	}


	public static class FdSet<F> {
		final List<F> fileEntities = new ArrayList<>();
		final List<MapExt> dirEntities = new ArrayList();

		public void addFileEntity(F formFileModel) {
			fileEntities.add(formFileModel);
		}

		public void addDir(Path path) {
			//Map<Path, EXT> map = MapExt.of(path).map();
			dirEntities.add(MapExt.ofNatural(path));
		}
	}
}
