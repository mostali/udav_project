package zklogapp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpc.fs.DirDiff;
import mpc.fs.UF;
import mpu.IT;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;
import zk_com.base.*;
import zk_com.base_ctr.Div0;
import zk_com.base_ctr.Div0Next;
import zk_com.sun_editor.IPerPage;
import zk_form.WithLogo;
import zk_form.events.IBoolEvent;
import zk_form.head.StdHeadLib;
import zk_notes.coms.PrettyCodeXml;
import zk_notes.control.NoteLogo;
import zk_os.sec.ROLE;
import zk_page.ZKPage;
import zk_page.ZKS;
import zk_page.core.PageRoute;
import zk_page.core.PageSP;
import zk_page.core.SpVM;
import zk_page.with_com.WithSearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@PageRoute(sd3 = "view", pagename = "dirs", role = ROLE.ANONIM)
public class DirDiffViewPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {//, WithLogo

	public DirDiffViewPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Tbx tbxDir1 = (Tbx) new Tbx().placeholder("/tmp/dir1").width(40.0).height(30);
	Tbx tbxDir2 = (Tbx) new Tbx().placeholder("/tmp/dir2").width(40.0).height(30);

	//	Tbxm tbxmJson = (Tbxm) new Tbxm().placeholder("set json data").bgcolor(EColor.WHITE.nextColor()).inlineBlock().width(49.0).height(770);
	//	Tbxm xsdPan = (Tbxm) new Tbxm().placeholder("set xsd").width(50.0).height(770);
	Div0Next outNext = (Div0Next) new Div0Next().accumulateSize(10).revert().inlineBlock().width(100.0);

	Tbxm outErrPan = (Tbxm) new Tbxm().width(100.0).height(0);

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) {
			return first;
		}
		NoteLogo noteLogo = new NoteLogo();
		return noteLogo;
	}

	@SneakyThrows
	public void buildPageImpl() {

		ZKS.PADDING0(window);
		ZKS.MARGIN(window, "30px 0 0 0");

		window.appendChild(getLogoOrAdd());

		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.JS_CODEMIRROR_6_65_7_PERL);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7);
		ZKPage.renderHeadRsrcs(window, StdHeadLib.CSS_CODEMIRROR_6_65_7_THEME_ABBOTT);

		ZKPage.renderHeadRsrcs(window, PrettyCodeXml.HEAD_RSCS);

		IBoolEvent.initNewAndAppend(window);

		//

		Bt btDir1 = new Bt("Dir 1").onCLICK(e -> update(true));
		window.appendChild(btDir1);
		window.appendChild(tbxDir1);

		Bt btDir2 = new Bt("Dir 2").onCLICK(e -> update(true));
		window.appendChild(btDir2);
		window.appendChild(tbxDir2);

		window.appendChild(outErrPan);

		//

//		window.appendChild(tbxmJson);
		window.appendChild(outNext);

		//


//		showPretty.onCLICK(e -> tbxmJson.setValue(UGson.toStringPretty(tbxmJson.getValue())));

	}

	@SneakyThrows
	private void update(boolean... force) {

//		outNext().appendChildNext(Lb.ERR("asdad"));

		Path dir1 = IT.isDirExist(Paths.get(IT.NE(tbxDir1.getValue(),"set dir1")));
		Path dir2 = IT.isDirExist(Paths.get(IT.NE(tbxDir2.getValue(),"set dir2")));
		List<Path>[] lists = DirDiff.compareDirectories(dir1, dir2);

		DiffPanel child = new DiffPanel(lists);
		child.font_size(14);

		outNext().appendChildNext(child);

		outNext().appendChildNext(Div0Next.buildMultilineDiv(lists[2]));

		outNext().replaceNextDiv();


	}

	private Div0Next outNext() {
		return outNext;
	}


	/**
	 * Компонент для отображения разницы между двумя директориями.
	 * Показывает файлы в 4-х вертикальных столбцах.
	 */
	@RequiredArgsConstructor
	public static class DiffPanel extends Div0 {
		final List<Path>[] lists;

		@Override
		protected void init() {
			super.init();
			renderDiffPanel(this, lists);
		}

		private void renderDiffPanel(Div0 container, List<Path>[] diff) {
			// Безопасное извлечение массивов
			List<Path> onlyIn1 = (diff != null && diff.length > 0) ? diff[0] : Collections.emptyList();
			List<Path> onlyIn2 = (diff != null && diff.length > 1) ? diff[1] : Collections.emptyList();
			List<Path> different = (diff != null && diff.length > 2) ? diff[2] : Collections.emptyList();
			List<Path> identical = (diff != null && diff.length > 3) ? diff[3] : Collections.emptyList();

			// 1️⃣ ОДНА СЕКЦИЯ: Горизонтальная строка
			Hbox mainRow = new Hbox();
			mainRow.setWidth("100%");
			mainRow.setSpacing("10px"); // Расстояние между столбцами

			// Колонка 1: Только в Dir1
			Div0 col1 = Div0.of();
			col1.setWidth("100%");
			col1.appendChild(buildSectionColumn(onlyIn1, "🔴 Только в Dir1", "#fff5f5"));

			// Колонка 2: Только в Dir2
			Div0 col2 = Div0.of();
			col2.setWidth("100%");
			col2.appendChild(buildSectionColumn(onlyIn2, "🔵 Только в Dir2", "#f5f9ff"));

			// Колонка 3: Отличающиеся файлы
			Div0 col3 = Div0.of();
			col3.setWidth("100%");
			col3.appendChild(buildSectionColumn(different, "⚠️ Отличаются", "#fff3e0"));

			// Колонка 4: Одинаковые файлы
			Div0 col4 = Div0.of();
			col4.setWidth("100%");
			col4.appendChild(buildSectionColumn(identical, "✅ Одинаковые", "#f0f7f0"));

			// Собираем всё вместе
			mainRow.appendChild(col1);
			mainRow.appendChild(col2);
			mainRow.appendChild(col3);
			mainRow.appendChild(col4);

			container.appendChild(mainRow);
		}

		/**
		 * Создает секцию (карточку) с заголовком и списком файлов
		 */
		private Component buildSectionColumn(List<Path> paths, String title, String bg) {
			Div0 wrapper = Div0.of();
			wrapper.setWidth("100%");
			wrapper.setStyle("background:" + bg + "; padding: 10px; border-radius: 8px; border: 1px solid #ddd; box-sizing: border-box;");

			// Заголовок секции (чуть меньше шрифт, чтобы влезло)
			wrapper.appendChild(Xml.H(6, title + " <span style='color:#888; font-weight:normal; font-size:0.9em;'>(" + paths.size() + ")</span>"));
			wrapper.appendChild(Xml.HR());

			if (paths.isEmpty()) {
				wrapper.appendChild(new Lb("Пусто").setSTYLE("color:#999; font-style:italic; padding:10px 0; font-size: 0.9em;"));
				return wrapper;
			}

			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

			// Используем Div0 для списка файлов внутри колонки
			Div0 list = Div0.of();
			list.setWidth("100%");

			for (Path p : paths) {
				// ✅ ФИЛЬТР: Пропускаем директории, показываем только файлы
				if (Files.isDirectory(p)) {
					continue;
				}

				// Строка файла (Вертикальный блок)
				Div0 fileRow = Div0.of();
				fileRow.setWidth("100%");
				fileRow.setStyle("padding: 6px 0; border-bottom: 1px dashed #ccc; cursor: help;");

	//			String name = p.getFileName() != null ? p.getFileName().toString() : p.toString();
				String name = UF.fn(p, 2, p.toString());
				String fullPath = p.toAbsolutePath().toString();

				// Данные
				String date = "—";
				String sizeStr = "—";
				try {
					if (Files.exists(p)) {
						BasicFileAttributes a = Files.readAttributes(p, BasicFileAttributes.class);
						date = fmt.format(a.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()));
						sizeStr = formatSize(a.size());
					}
				} catch (IOException ignored) {
				}

				// Tooltip
				fileRow.setTooltiptext("Полный путь: " + fullPath + "\nРазмер: " + p.toFile().length() + " B");

				// 1. Имя файла (на всю ширину, обрезается если длинное)
				Lb nameLabel = new Lb(name);
				nameLabel.setStyle("font-weight: 600; font-size: 0.9em; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 2px; color: #333;");
				fileRow.appendChild(nameLabel);

				// 2. Мета-данные (под именем) - серый цвет, дата и размер рядом
				Div0 metaDiv = Div0.of();
				metaDiv.setStyle("color: #999;"); // Более серый, не выделяющийся

				Lb dateLbl = new Lb(date);
				dateLbl.setStyle("color: #999; margin-right: 8px; font-size: 0.8em;"); // Серый цвет

				Lb sizeLbl = new Lb(sizeStr);
				sizeLbl.setStyle("color: #999; font-size: 0.8em;"); // Серый цвет

				metaDiv.appendChild(dateLbl);
				metaDiv.appendChild(sizeLbl);
				fileRow.appendChild(metaDiv);

				list.appendChild(fileRow);
			}

			wrapper.appendChild(list);
			return wrapper;
		}

		/**
		 * Форматирование размера
		 */
		private String formatSize(long bytes) {
			if (bytes < 1024L) {
				return bytes + " B";
			}
			if (bytes < 1024L * 1024L) {
				return String.format("%.1f KB", bytes / 1024.0);
			}
			if (bytes < 1024L * 1024L * 1024L) {
				return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
			}
			return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
		}
	}
}
