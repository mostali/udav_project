package udav_net_exp.translator;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import udav_net_client.AHttp;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import mpu.core.ARG;
import mpc.log.L;

import java.io.IOException;

//docker run -ti --rm -p 5000:5000 libretranslate/libretranslate --frontend-language-source en --frontend-language-target ru --load-only=en,ru
public class ULibreTranslator {

	//	public static void main(String[] args) throws IOException {
	//		String q = "If we require timely disposal of the file system resources, then we should use a try-with-resources statement. This way we make sure the stream will be closed right after the stream operations are completed.";
	//		String source = "en";
	//		String target = "ru";
	//		String format = "text";
	//
	//		//		String json = U.f("{q:\"%s\",source:\"%s\",target:\"%s\",format:\"%s\"}", q, source, target, format);
	//		String json = new Gson().toJson(new TltRequest(q, source, target, format));
	//
	//		TltResponse resp = UNet.POST("http://localhost:500/translate", null, json, TltResponse.class);
	//		P.p(resp.getResult());
	//	}

	public static final boolean TRANSLATE_ENABLE = false;

	@RequiredArgsConstructor
	public static class TltRequest {
		public final String q;
		public final String source;
		public final String target;
		public final String format;
	}

	public static class TltResponse {
		public String translatedText;

		public String getResult() {
			return StringEscapeUtils.unescapeJava(translatedText);
		}
	}

	public static String en2ru(String serviceUrl, String en, boolean... checkCustomWords) throws IOException {
		if (!TRANSLATE_ENABLE) {
			return en;
		}
		if (ARG.isDefEqTrue(checkCustomWords)) {
			String ru = BaseTranslate.en2ru(en);
			if (ru != null) {
				if (L.isInfoEnabled()) {
					L.info("EN/RU:Local:" + en + ":" + ru);
				}
				return ru;
			}
		}
		String q = en;
		String source = "en";
		String target = "ru";
		String format = "text";

		String json = new Gson().toJson(new TltRequest(q, source, target, format));

		TltResponse resp = AHttp.POST(serviceUrl, null, json, TltResponse.class);

		return resp.getResult();
	}

	public static class BaseTranslate {
		public static String en2ru(String en) {
			String[] two = StringUtils.split(en, " ");
			if (two.length != 2) {
				return null;
			}
			switch (two[1]) {
				case "Conclusion":
					return "Заключение";
				case "Overview":
					return "Обзор";
				case "Examples":
					return "Примеры";
				case "Introduction":
					return "Введение";
				default:
					return null;
			}
		}
	}
}
