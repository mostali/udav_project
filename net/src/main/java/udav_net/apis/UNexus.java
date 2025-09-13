package udav_net.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import lombok.Setter;
import mpc.str.condition.StringConditionType;
import mpu.X;
import udav_net.UJsoup;
import udav_net_client.AHttp;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import mpe.ftypes.core.FDate;
import mpc.log.L;
import mpc.str.condition.StringConditionPattern;
import udav_net_client.AConOld;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UNexus {

//	public static void main(String[] args) throws IOException, URISyntaxException, ParseException {
//		String url = "https://URL_NEXUS.ru/content/repositories/RELEASES/branch/project/";
//		StringConditionPattern condition = StringConditionType.CONTAINS.buildCondition("rc");
//		List<ContentItem> itemsByConditionHtml = getItemsByCondition_HTML(url, condition);
//		X.p(itemsByConditionHtml);
//	}

	public static List<UNexus.ContentItem> getItemsByCondition_HTML(String nexusUrl, StringConditionPattern condition) throws IOException, URISyntaxException, ParseException {
		List<UNexus.ContentItem> items = getItemsAll_HTML(nexusUrl).stream().
				filter(i -> condition.matches((Paths.get(i.getRelativePath()).getFileName().toString()))).
				//sorted((Comparator.comparing(NexusApi.ContentItem::getLastModified))).
						sorted((o1, o2) -> o2.getLastModified().compareTo(o1.getLastModified())).
						collect(Collectors.toList());
		return items;
	}

	@Deprecated
	public static List<UNexus.ContentItem> getItemsAll_HTML(String nexusUrl) throws IOException, URISyntaxException, ParseException {
		//List<String> components = UJsoup.select(nexusUrl, 30000, "table tr td a").stream().map(e -> e.attr("href")).collect(Collectors.toList());
		List<UNexus.ContentItem> list = new ArrayList<>();
		Elements els = UJsoup.selectTC(nexusUrl, -1, "table tr td ", -1);
		List<Element> rows4 = new ArrayList();

		for (int i = 0; i < els.size(); i++) {
			if (i == 0) {
				continue;
			}
			rows4.add(els.get(i));
			if (rows4.size() == 4) {
				UNexus.ContentItem ci = new ContentItem();
				String url = rows4.get(0).select("a").attr("href");
				URI uri = new URL(url).toURI();

				ci.setResourceURI(url);
				ci.setRelativePath(uri.getPath());
				ci.setLastModified(FDate.FNEXUSTHTML_EEE_MMM_D_HH_MM_SS_Z_YYYY().parse(rows4.get(1).text()));

				list.add(ci);

				rows4.clear();
			}

		}
		return list;
	}

	public static List<UNexus.ContentItem> getItemsByCondition(String nexusUrl, StringConditionPattern condition) throws IOException {
		List<UNexus.ContentItem> items = getItemsAll(nexusUrl).stream().
				filter(i -> condition.matches((Paths.get(i.getRelativePath()).getFileName().toString()))).
				//sorted((Comparator.comparing(NexusApi.ContentItem::getLastModified))).
						sorted((o1, o2) -> o2.getLastModified().compareTo(o1.getLastModified())).
						collect(Collectors.toList());
		return items;
	}

	public static List<UNexus.ContentItem> getItemsAll(String nexusUrl) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(UNexus.ContentItem.class, new ContentItemAdapter());
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		UNexus.Content content = AConOld.CALL_TC(AHttp.Method.GET, nexusUrl, null, null, UNexus.Content.class, gson, AConOld.HttpHeader.HEADER_ACCEPT_APPLICATION_JSON);
		return content.getData();
	}

	//
	//
	//
	public static class Content {
		@Getter
		@Setter
		private List<ContentItem> data;

		@Override
		public String toString() {
			return "Content{" +
				   "data=" + data +
				   '}';
		}
	}

	public static class ContentItem {
		@Getter
		@Setter
		private String resourceURI, relativePath, text;
		@Getter
		@Setter
		private Boolean leaf;
		@Getter
		@Setter
		private Date lastModified;
		@Getter
		@Setter
		private Integer sizeOnDisk;

		@Override
		public String toString() {
			return "ContentItem{" +
				   "resourceURI='" + resourceURI + '\'' +
				   ", relativePath='" + relativePath + '\'' +
				   ", text='" + text + '\'' +
				   ", leaf=" + leaf +
				   ", lastModified=" + lastModified +
				   ", sizeOnDisk=" + sizeOnDisk +
				   '}';
		}
	}

	static class ContentItemAdapter extends TypeAdapter<ContentItem> {
		@Override
		public ContentItem read(final JsonReader in) throws IOException {
			final ContentItem book = new ContentItem();
			in.beginObject();
			while (in.hasNext()) {
				switch (in.nextName()) {
					case "resourceURI":
						book.setResourceURI(in.nextString());
						break;
					case "relativePath":
						book.setRelativePath(in.nextString());
						break;
					case "text":
						book.setText(in.nextString());
						break;
					case "leaf":
						book.setLeaf(in.nextBoolean());
						break;
					case "lastModified":
						String str = in.nextString();
						try {
							book.setLastModified(FDate.FNEXUS_YYYY_MM_DD_HH_MM_SS_S_Z().parse(str));
						} catch (ParseException e) {
							L.error("Parse field date [{}] with value [{}]", "lastModified", str);
						}
						break;
					case "sizeOnDisk":
						book.setSizeOnDisk(in.nextInt());
						break;
				}
			}
			in.endObject();

			return book;
		}

		@Override
		public void write(JsonWriter writer, ContentItem student) throws IOException {
			writer.beginObject();

			writer.name("resourceURI");
			writer.value(student.getResourceURI());

			writer.name("relativePath");
			writer.value(student.getRelativePath());

			writer.name("text");
			writer.value(student.getText());

			writer.name("leaf");
			writer.value(student.getLeaf());

			writer.name("lastModified");
			writer.value(FDate.FNEXUS_YYYY_MM_DD_HH_MM_SS_S_Z().format(student.getLastModified()));

			writer.name("sizeOnDisk");
			writer.value(student.getSizeOnDisk());

			writer.endObject();
		}

	}
}
