package zk_os.web;

import lombok.RequiredArgsConstructor;
import mpc.env.APP;
import mpc.env.CErrorLog;
import mpc.env.Env;
import mpc.fs.UUFS;
import mpc.fs.fd.EFT;
import mpc.fs.path.IPath;
import mpc.map.BootContext;
import mpu.X;
import mpu.core.ENUM;
import mpu.core.QDate;
import mpu.str.Hu;
import mpv.byteunit.ByteUnit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mpe.call_msg.core.NodeID;
import zk_notes.node_state.ObjState;
import zk_notes.node_state.impl.PageState;
import zk_notes.node_state.impl.PlaneState;
import zk_os.sec.SecApp;
import zk_os.sec.UO;
import zk_os.walkers.PagesWalker;
import zk_os.walkers.PlaneWalker;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//напиши  java class SitemapBuilder который генерирует sitemap.xml
//
//В классе два метода + вложенный Changefreq типа enum
//
//Метод №1 - addPage(String subdomain,String pagename,Date lastmod, ChangefreqType changefreq, double priority)
//
//Метод №2 - String build()
//
//напиши  java class SitemapBuilder который генерирует sitemap.xml
//
//В классе два метода + вложенный Changefreq типа enum
//
//Метод №1 - addPage(String subdomain,String pagename,Date lastmod, ChangefreqType changefreq, double priority)
//
//Метод №2 - String build()

public class SitemapBuilder {

	public static final Logger L = LoggerFactory.getLogger(SitemapBuilder.class);

	public static final String FN_SITEMAP_XML = "sitemap.xml";
	public static final long CACHE_MAX_MS = TimeUnit.DAYS.toMillis(1);
//	private static final long CACHE_MAX_MS = TimeUnit.SECONDS.toMillis(10);

	/**
	 * Пример использования класса
	 */
	public static void main(String[] args) {
		BootContext.init(args);
//		AppIni
		// Создаем билдер для сайта example.com с использованием HTTPS
		SitemapBuilder builder = new SitemapBuilder(APP.APP_HOST, APP.USE_HTTPS);

		// Добавляем главную страницу (без поддомена)
		builder.addPage(null, "/", new Date(), Changefreq.DAILY, 1.0);

		// Добавляем страницу "О нас" (без поддомена)
		builder.addPage("", "/about", new Date(), Changefreq.MONTHLY, 0.8);

		// Добавляем страницу блога (с поддоменом blog)
		builder.addPage("blog", "/java-tutorial", new Date(), Changefreq.WEEKLY, 0.7);

		// Добавляем страницу магазина (с поддоменом shop)
		builder.addPage("shop", "/products", new Date(), Changefreq.DAILY, 0.9);

		// Добавляем страницу без указания поддомена
		builder.addPage(null, "contact", new Date(), Changefreq.YEARLY, 0.5);

		// Генерируем XML
		String sitemapXml = builder.buildContentXml();
		System.out.println(sitemapXml);

		System.out.println("\nВсего записей в sitemap: " + builder.getEntryCount());

		// Пример с HTTP протоколом
		System.out.println("\n=== Пример с HTTP ===");
		SitemapBuilder httpBuilder = new SitemapBuilder("mysite.ru", false);
		httpBuilder.addPage("api", "/docs", new Date(), Changefreq.NEVER, 0.3);
		String httpSitemap = httpBuilder.buildContentXml();
		System.out.println(httpSitemap);
	}

	private final List<SitemapEntry> entries = new ArrayList<>();
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final String host;
	private final boolean useHttps;

	public SitemapBuilder buildAndWriteIfExpired() {
		IPath sitemapPath = IPath.of(getSitemapPath(), EFT.FILE);
		if (sitemapPath.fdExist()) {
			long diffabs = sitemapPath.fCreated().diffabs();
			if (diffabs < CACHE_MAX_MS) {
				if (L.isInfoEnabled()) {
					L.info("Generate sitemap.xml [SKIPPED], next launch in {}", Hu.MS(CACHE_MAX_MS - diffabs));
				}
				return this;
			}
		}

		String buildXml = buildContentXml();

		sitemapPath.fWriteIn(buildXml);

		Long l = sitemapPath.fdSize0();

		double mb = ByteUnit.BYTE.toMB(l);
		if (mb >= 50) {
			CErrorLog.error("Sitemap.xml large 50Mb");
		} else {
			if (L.isInfoEnabled()) {
				L.info("Sitemap.xml size '{}' - it ok size", Hu.KB_TB(l));
			}
		}

		return this;
	}

	public Path getSitemapPath() {
		return Env.RPA.resolve(FN_SITEMAP_XML);
	}

	// Вложенный enum для частоты изменений
	public enum Changefreq {
		ALWAYS, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY, NEVER
	}

	// Внутренний класс для хранения данных о странице
	private static class SitemapEntry {
		final String url;
		final Date lastmod;
		final Changefreq changefreq;
		final double priority;

		SitemapEntry(String url, Date lastmod, Changefreq changefreq, double priority) {
			this.url = url;
			this.lastmod = lastmod;
			this.changefreq = changefreq;
			this.priority = priority;
		}
	}

	public SitemapBuilder() {
		this(APP.APP_HOST, APP.USE_HTTPS);
	}

	/**
	 * Конструктор SitemapBuilder
	 *
	 * @param host     основной домен (например, "example.com")
	 * @param useHttps использовать HTTPS протокол
	 */
	public SitemapBuilder(String host, boolean useHttps) {
		this.host = host;
		this.useHttps = useHttps;
	}

	/**
	 * Метод для добавления страницы в sitemap
	 *
	 * @param subdomain  поддомен (например, "blog" или "shop")
	 * @param pagename   название страницы (например, "/about" или "post-1")
	 * @param lastmod    дата последнего изменения
	 * @param changefreq частота изменений
	 * @param priority   приоритет (от 0.0 до 1.0)
	 */
	public void addPage(String subdomain, String pagename, Date lastmod, Changefreq changefreq, double priority) {

		if (L.isInfoEnabled()) {
			L.info("Add page '{}/{} -> {} / {} / {} ' to sitemap.xml", subdomain, pagename, lastmod, changefreq, priority);
		}
		// Формируем базовый URL
		String protocol = useHttps ? "https://" : "http://";
		String baseUrl = protocol + host;

		// Добавляем поддомен если указан
		if (X.notEmpty(subdomain)) {
			baseUrl = protocol + subdomain + "." + host;
		}

		// Нормализуем путь к странице
		String normalizedPagename = pagename.startsWith("/") ? pagename : "/" + pagename;

		String url = baseUrl + normalizedPagename;

		// Проверяем и корректируем приоритет
		double validatedPriority = Math.max(0.01, Math.min(1.0, priority));

		entries.add(new SitemapEntry(url, lastmod, changefreq, validatedPriority));
	}

	/**
	 * Метод для генерации XML содержимого sitemap
	 *
	 * @return строка с XML содержимым sitemap
	 */
	public String buildContentXml() {

		QDate startDate = QDate.now();
		if (L.isInfoEnabled()) {
			L.info("Generate sitemap.xml [START] at {}", startDate.f(QDate.F.MONO17NF));
		}

		if (X.empty(entries)) {
			runSiteWalker(this);

		}

		StringBuilder xml = new StringBuilder();

		// XML заголовок
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

		// Добавляем все URL
		for (SitemapEntry entry : entries) {

			xml.append("  <url>\n");
			xml.append("    <loc>").append(escapeXml(entry.url)).append("</loc>\n");

			if (entry.lastmod != null) {
				xml.append("    <lastmod>").append(DATE_FORMAT.format(entry.lastmod)).append("</lastmod>\n");
			}

			if (entry.changefreq != null) {
				xml.append("    <changefreq>").append(entry.changefreq.name().toLowerCase()).append("</changefreq>\n");
			}

			xml.append("    <priority>").append(String.format(Locale.US, "%.1f", entry.priority)).append("</priority>\n");
			xml.append("  </url>\n");

		}

		xml.append("</urlset>");

		if (L.isInfoEnabled()) {
			L.info("Generate sitemap.xml [FINISH] at {}, in {}", QDate.now().f(QDate.F.MONO17NF), Hu.MS(startDate.diffabs()));
		}

		return xml.toString();
	}

	/**
	 * Вспомогательный метод для экранирования XML специальных символов
	 */
	private String escapeXml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
	}

	/**
	 * Дополнительный метод для очистки всех записей
	 */
	public void clear() {
		entries.clear();
	}

	/**
	 * Дополнительный метод для получения количества записей
	 */
	public int getEntryCount() {
		return entries.size();
	}


	//
	//

	private static void runSiteWalker(SitemapBuilder sitemapBuilder) {
		new SitemapWalker(sitemapBuilder).walk();
	}

	@RequiredArgsConstructor
	public static class SitemapWalker {

		public static final String HOME = "/";

		public static final double SITEMAP_PRORITY_ROOTHOME = 1.0;
		public static final double SITEMAP_PRORITY_ROOTPAGE = 0.3;

		public static final double SITEMAP_PRORITY_PLANEHOME = 0.9;
		public static final double SITEMAP_PRORITY_PLANEPAGE = 0.2;

		public static final String PROP_SITEMAP_PRIORITY = "sitemap.priority";
		public static final String PROP_SITEMAP_FREQ = "sitemap.freq";
		public static final String PROP_SITEMAP_UPDATED = "updated";

		//		public static final Changefreq DEFAULT_CHANGEFREQ = Changefreq.YEARLY;
		public static final Changefreq DEFAULT_CHANGEFREQ = null;

		final SitemapBuilder sitemapBuilder;

		public void walk() {
			new PlaneWalker() {
				@Override
				protected Boolean walkPlane(String plane, Path dir, PlaneState planeState) {
					new PagesWalker(plane) {
						@Override
						protected Boolean walkPage(String pagename, PageState pageState) {

							String forPlane = NodeID.unwrapPlane(plane);

							pagename = NodeID.unwrapPage(pagename);
							pagename = X.empty(pagename) ? HOME : pagename;

							Date lastmod = choiceLastmodify(forPlane, pagename, planeState, pageState);

							Changefreq changefreq = choiceFreq(forPlane, pagename, planeState, pageState);

							double priority = choicePriority(forPlane, pagename, planeState, pageState);

							if (pagename.startsWith("@@")) {
								if (L.isInfoEnabled()) {
									L.info("Page '{}' skipped, because is [system page]", pagename);
								}
							} else {

								if (!UO.VIEW.isAllowed(pageState)) {
									if (L.isInfoEnabled()) {
										String user = pageState.get_USER(null);
										List<String> secPropList = UO.VIEW.getPropList(pageState);
										L.info("Page '{}' skipped, because is [access denied] / User:{} / Permissions: {}", pagename, user, secPropList);
									}
								} else {
									sitemapBuilder.addPage(forPlane, pagename, lastmod, changefreq, priority);
								}

							}

							return true;
						}
					}.withSysPages(false).withSkipSecurity().doWalk();
					return true;
				}
			}.withIndex(true).withSystemPlanes(false).withUserDomain(true).withSkipSecurity().doWalk();
		}

		//
		//

		private Date choiceLastmodify(String plane, String pagenameOrHome, ObjState planeState, ObjState pageState) {
//			Changefreq changefreq = QDate.of(pageState.get("")).valueOf();
//			if (changefreq == null) {
//				changefreq = ENUM.valueOf(planeState.get(PROP_SITEMAP_FREQ, null), Changefreq.class, true, null);
//			}
//			changefreq = changefreq == null ? DEFAULT_CHANGEFREQ : changefreq;
			Date[] minMaxFileModificationDatesNIO = UUFS.getMinMaxFileModificationDatesNIO(pageState.toPath());
			return minMaxFileModificationDatesNIO[1];
		}

		//
		//

		private Changefreq choiceFreq(String plane, String pagenameOrHome, ObjState planeState, ObjState pageState) {
			Changefreq changefreq = ENUM.valueOf(pageState.get(PROP_SITEMAP_FREQ, null), Changefreq.class, true, null);
			if (changefreq == null) {
				changefreq = ENUM.valueOf(planeState.get(PROP_SITEMAP_FREQ, null), Changefreq.class, true, null);
			}
			changefreq = changefreq == null ? DEFAULT_CHANGEFREQ : changefreq;
			return changefreq;
		}


		//
		//

		private double choicePriority(String plane, String pagenameOrHome, ObjState planeState, ObjState pageState) {
			boolean rootPlane = X.empty(plane);
			boolean homePage = HOME.equals(pagenameOrHome);
			if (rootPlane) {
				return homePage ? SITEMAP_PRORITY_ROOTHOME : getPiorityOrDefault(planeState, pageState, SITEMAP_PRORITY_ROOTPAGE);
			} else {
				return homePage ? SITEMAP_PRORITY_PLANEHOME : getPiorityOrDefault(planeState, pageState, SITEMAP_PRORITY_PLANEPAGE);
			}
		}

		private @NotNull Double getPiorityOrDefault(ObjState planeState, ObjState pageState, Double defaultIfNot) {
			Double prio = (Double) pageState.getAs(PROP_SITEMAP_PRIORITY, Double.class, null);
			if (prio == null) {
				prio = (Double) planeState.getAs(PROP_SITEMAP_PRIORITY, Double.class, null);
			}
			prio = prio == null ? defaultIfNot : prio;
			return prio;
		}
	}

}