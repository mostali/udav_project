package zklogapp;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@PageRoute(sd3 = "view", pagename = "props", role = ROLE.ANONIM)
public class PropsDiffViewPageSP extends PageSP implements IPerPage, WithLogo, WithSearch {
	public PropsDiffViewPageSP(Window window, SpVM spVM) {
		super(window, spVM);
	}

	Tbx tbxFile1 = (Tbx) new Tbx().placeholder("/tmp/config1.properties").width(40.0).height(30);
	Tbx tbxFile2 = (Tbx) new Tbx().placeholder("/tmp/config2.properties").width(40.0).height(30);

	Div0Next outNext = (Div0Next) new Div0Next().accumulateSize(10).revert().inlineBlock().width(100.0);
	Tbxm outErrPan = (Tbxm) new Tbxm().width(100.0).height(0);

	@Override
	public LogoCom getLogoDefault() {
		LogoCom first = LogoCom.findFirst(null);
		if (first != null) return first;
		return new NoteLogo();
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

		Bt btFile1 = new Bt("File 1").onCLICK(e -> update(true));
		window.appendChild(btFile1);
		window.appendChild(tbxFile1);

		Bt btFile2 = new Bt("File 2").onCLICK(e -> update(true));
		window.appendChild(btFile2);
		window.appendChild(tbxFile2);

		window.appendChild(outErrPan);
		window.appendChild(outNext);
	}

	@SneakyThrows
	private void update(boolean... force) {
		outErrPan.setValue(""); // Очистка предыдущих ошибок
		try {
			Path file1 = Paths.get(IT.NE(tbxFile1.getValue(), "укажите путь к File 1"));
			Path file2 = Paths.get(IT.NE(tbxFile2.getValue(), "укажите путь к File 2"));

			if (!Files.isRegularFile(file1)) throw new IllegalArgumentException("File 1 не найден или не является файлом: " + file1);
			if (!Files.isRegularFile(file2)) throw new IllegalArgumentException("File 2 не найден или не является файлом: " + file2);

			Properties props1 = loadProperties(file1);
			Properties props2 = loadProperties(file2);

			List<String>[] diff = compareProperties(props1, props2);

			PropsDiffPanel child = new PropsDiffPanel(props1, props2, diff);
			child.font_size(14);

			outNext().appendChildNext(child);
			// Выводим сводку по отличающимся ключам
			outNext().appendChildNext(Div0Next.buildMultilineDiv(diff[2]));
			outNext().replaceNextDiv();

		} catch (Exception e) {
			outErrPan.setValue("❌ Ошибка: " + e.getMessage());
		}
	}

	private Div0Next outNext() {
		return outNext;
	}

	private Properties loadProperties(Path path) throws IOException {
		Properties props = new Properties();
		try (InputStream is = Files.newInputStream(path)) {
			props.load(is);
		}
		return props;
	}

	/**
	 * Сравнивает два набора Properties и возвращает массив списков ключей:
	 * [0] - только в файле 1
	 * [1] - только в файле 2
	 * [2] - ключи есть в обоих, но значения различаются
	 * [3] - ключи и значения полностью совпадают
	 */
	private List<String>[] compareProperties(Properties p1, Properties p2) {
		Set<String> keys1 = p1.stringPropertyNames();
		Set<String> keys2 = p2.stringPropertyNames();

		List<String> onlyIn1 = keys1.stream().filter(k -> !keys2.contains(k)).sorted().collect(Collectors.toList());
		List<String> onlyIn2 = keys2.stream().filter(k -> !keys1.contains(k)).sorted().collect(Collectors.toList());
		List<String> different = keys1.stream()
				.filter(k -> keys2.contains(k) && !Objects.equals(p1.getProperty(k), p2.getProperty(k)))
				.sorted().collect(Collectors.toList());
		List<String> identical = keys1.stream()
				.filter(k -> keys2.contains(k) && Objects.equals(p1.getProperty(k), p2.getProperty(k)))
				.sorted().collect(Collectors.toList());

		return new List[]{onlyIn1, onlyIn2, different, identical};
	}

	/**
	 * Компонент для отображения разницы между двумя .properties файлами.
	 * Показывает ключи в 4-х вертикальных столбцах.
	 */
	@RequiredArgsConstructor
	public static class PropsDiffPanel extends Div0 {
		final Properties props1;
		final Properties props2;
		final List<String>[] diff;

		@Override
		protected void init() {
			super.init();
			renderPropsDiffPanel(this, diff);
		}

		private void renderPropsDiffPanel(Div0 container, List<String>[] diff) {
			List<String> onlyIn1 = (diff != null && diff.length > 0) ? diff[0] : Collections.emptyList();
			List<String> onlyIn2 = (diff != null && diff.length > 1) ? diff[1] : Collections.emptyList();
			List<String> different = (diff != null && diff.length > 2) ? diff[2] : Collections.emptyList();
			List<String> identical = (diff != null && diff.length > 3) ? diff[3] : Collections.emptyList();

			Hbox mainRow = new Hbox();
			mainRow.setWidth("100%");
			mainRow.setSpacing("10px");

			mainRow.appendChild(buildSectionColumn(onlyIn1, "🔴 Только в File 1", "#fff5f5", true, false));
			mainRow.appendChild(buildSectionColumn(onlyIn2, "🔵 Только в File 2", "#f5f9ff", false, true));
			mainRow.appendChild(buildSectionColumn(different, "⚠️ Различаются значения", "#fff3e0", true, true));
			mainRow.appendChild(buildSectionColumn(identical, "✅ Одинаковые", "#f0f7f0", true, true));

			container.appendChild(mainRow);
		}

		private Component buildSectionColumn(List<String> keys, String title, String bg, boolean showFile1, boolean showFile2) {
			Div0 wrapper = Div0.of();
			wrapper.setWidth("100%");
			wrapper.setStyle("background: " + bg + "; padding: 10px; border-radius: 8px; border: 1px solid #ddd; box-sizing: border-box;");

			wrapper.appendChild(Xml.H(6, title + "  <span style='color:#888; font-weight:normal; font-size:0.9em;'>(" + keys.size() + ")</span>"));
			wrapper.appendChild(Xml.HR());

			if (keys.isEmpty()) {
				wrapper.appendChild(new Lb("Пусто").setSTYLE("color:#999; font-style:italic; padding:10px 0; font-size: 0.9em;"));
				return wrapper;
			}

			Div0 list = Div0.of();
			list.setWidth("100%");

			for (String key : keys) {
				Div0 fileRow = Div0.of();
				fileRow.setWidth("100%");
				fileRow.setStyle("padding: 6px 0; border-bottom: 1px dashed #ccc; cursor: help;");

				// Название ключа
				Lb keyLabel = new Lb(key);
				keyLabel.setStyle("font-weight: 600; font-size: 0.9em; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 2px; color: #333;");
				fileRow.appendChild(keyLabel);

				// Мета-данные (значения)
				StringBuilder meta = new StringBuilder();
				StringBuilder tooltip = new StringBuilder("Key: ").append(key);

				if (showFile1 && props1.containsKey(key)) {
					String v1 = props1.getProperty(key);
					tooltip.append("\n📄1: ").append(v1);
					if (showFile2 && props2.containsKey(key) && !Objects.equals(v1, props2.getProperty(key))) {
						meta.append("📄1: ").append(truncate(v1, 45)).append(" | ");
						String v2 = props2.getProperty(key);
						tooltip.append("\n📄2: ").append(v2);
						meta.append("📄2: ").append(truncate(v2, 45));
					} else {
						meta.append(truncate(v1, 90));
					}
				} else if (showFile2 && props2.containsKey(key)) {
					String v2 = props2.getProperty(key);
					tooltip.append("\n📄2: ").append(v2);
					meta.append(truncate(v2, 90));
				}

				Div0 metaDiv = Div0.of();
				metaDiv.setStyle("color: #999;");
				Lb metaLbl = new Lb(meta.toString());
				metaLbl.setStyle("color: #999; font-size: 0.8em; word-break: break-all;");
				metaDiv.appendChild(metaLbl);
				fileRow.appendChild(metaDiv);

				fileRow.setTooltiptext(tooltip.toString());
				list.appendChild(fileRow);
			}
			wrapper.appendChild(list);
			return wrapper;
		}

		private String truncate(String s, int len) {
			if (s == null) return "";
			return s.length() > len ? s.substring(0, len) + "..." : s;
		}
	}
}