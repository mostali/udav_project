package zk_page.core;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.env.AppProfile;
import mpc.exception.NI;
import mpc.log.L;
import mpc.rfl.UReflScanner;
import mpc.str.condition.StringConditionType;
import mpu.IT;
import mpu.core.ARG;
import mpu.core.ARR;
import mpu.pare.Pare3;
import mpu.str.JOIN;
import mpu.str.Sb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Window;
import utl_rest.StatusException;
import zk_notes.control.NotesPSP;
import zk_os.AppZosWeb;
import zk_os.db.net.WebUsr;
import zk_os.sec.Sec;
import zk_pages.LogPSP;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@RequiredArgsConstructor
public class FinderPSP {

	public static final Logger L = LoggerFactory.getLogger(AppZosWeb.class);

	public static final Map<PageRoute, Class> _HANDLERS = new ConcurrentHashMap<>();
	public static final String PATTERN_ANY = ".*";

	final PagePathInfo ppi;
	final SpVM spVM;
	final Window window;
	final String sd3, path, pagename;

	public FinderPSP(SpVM spVM, Window window) {
		this.ppi = spVM.ppi();
		this.spVM = spVM;
		this.window = window;

		this.sd3 = spVM.ppi().subdomain3();
		this.path = spVM.ppi().path();
		this.pagename = spVM.ppi().pagename();
	}

	public static void regPageEntity(String... packages) {
		List<Class> pages = UReflScanner.getAllPackageClassViaClassgraph(packages, PageRoute.class);
		regPageEntity(pages);
	}

	public static void regPageEntity(Class... pages) {
		regPageEntity(ARR.as(pages));
	}

	public static void regPageEntity(List<Class> pages) {
		for (Class page : pages) {
			PageRoute pageRoute = (PageRoute) page.getAnnotation(PageRoute.class);
			IT.NN(pageRoute, page.getSimpleName());
			IT.notEmptyAny(pageRoute.sd3(), pageRoute.pagename());
			if (L.isInfoEnabled()) {
				L.info(">>> >>> Reg PageSP {}@{}, handler:{}", pageRoute.sd3(), pageRoute.pagename(), page);
			}
			_HANDLERS.put(pageRoute, page);
		}
	}

	@SneakyThrows
	private PageSP findPageImpl() {

		PageSP pageSP = null;

		pageSP = findPage_System();
		if (pageSP != null) {
			return pageSP;
		}

		if (AppProfile.prod.isEnable()) {
			return NotesPSP.PSP_CLASS_PAGE_BUILDER.apply(NotesPSP.class, spVM);
		}

		pageSP = findPage_NativeViaAno();
		if (pageSP != null) {
			return pageSP;
		}

		NotesPSP notesPSP = (NotesPSP) NotesPSP.PSP_CLASS_PAGE_BUILDER.apply(NotesPSP.class, spVM);
		if (notesPSP != null) {
			return notesPSP;
		}

		return pageSP;
	}

	@SneakyThrows
	public PageSP findPage(PageSP... defRq) {
		Exception ex = null;
		try {
			PageSP pageSP = findPageImpl();
			if (pageSP != null) {
				return pageSP;
			}
		} catch (Exception e) {
			ex = e;
		}
		if (ex != null) {
			Exception finalEx = ex;
			return ARG.toDefThrow(() -> new PspNotFoundException(finalEx, "PageSP '%s' not found (has errors)", ppi), defRq);
		} else {
			return ARG.toDefThrow(() -> new PspNotFoundException("PageSP '%s' not found", ppi), defRq);
		}
	}

	public class PspNotFoundException extends IllegalArgumentException {
		public PspNotFoundException() {
			super();
		}

		public PspNotFoundException(String message) {
			super(message);
		}

		public PspNotFoundException(String message, Object... args) {
			this(String.format(message, args));
		}

		public PspNotFoundException(Throwable throwable, String message) {
			super(message, throwable);
		}

		public PspNotFoundException(Throwable throwable, String message, Object... args) {
			this(throwable, String.format(message, args));
		}
	}

	public static Function<String, PageSP> finderPSP_outer;

	private PageSP findPage_System() {
		if (finderPSP_outer != null) {
			PageSP page = finderPSP_outer.apply(pagename);
			if (page != null) {
				return page;
			}
		}
		switch (pagename) {

			case PageSP.PAGENAME_LOGS:
				return new LogPSP(window, spVM);

			default:
				if (true) {
					return null;
				}
		}
		switch (pagename) {

			case "@@status":
				WebUsr usr = Sec.getUser();

				Pare3<Sb, Sb, Sb> info = spVM.showDebugLog();

				Sb sbCfg = info.key();
				Sb sbRequest = info.val();
				Sb sbSec = info.ext();

				Sb sbUsr = new Sb(usr.toString());

				String sb = JOIN.allByNL(ARR.as(sbUsr, sbSec, sbRequest, sbCfg));
				throw StatusException.OK(sb);

			default:
				return null;
		}
	}

	@SneakyThrows
	private PageSP findPage_NativeViaAno() {

		Class page = null;
		PageRoute ano = null;

		PAGE_FOUND:
		for (Map.Entry<PageRoute, Class> pageEntity : _HANDLERS.entrySet()) {

			ano = pageEntity.getKey();

			StringConditionType sd3_sct = ano.sd3_eqt();
			if (!sd3_sct.matches(sd3, ano.sd3())) {
				continue;
			}

			StringConditionType sct = ano.eqt();
			if (!sct.matches(pagename, ano.pagename())) {
				continue;
			}
			page = pageEntity.getValue();

		}

		PageSP pageSP = null;
		if (page != null) {
			PageSP.checkClassPageSP(page);
			pageSP = PageSP.PSP_CLASS_PAGE_BUILDER.apply(page, spVM);
		}

		if (L.isInfoEnabled()) {
			String status = pageSP == null ? "NOT" : "OK";
			String simpleName = pageSP == null ? "" : " '" + page.getSimpleName() + "'";
			L.info("Found {} pageSP{} for sd3={}/{}, pagename={}/{}", status, simpleName, sd3, ano.sd3_eqt(), pagename, ano.eqt());
		}
		return pageSP;
	}

}
