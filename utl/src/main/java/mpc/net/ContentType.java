package mpc.net;

import lombok.RequiredArgsConstructor;
import mpu.core.ARG;
import mpc.exception.RequiredRuntimeException;

@RequiredArgsConstructor
public enum ContentType {
	TXT_CSS("text/css"),
	TXT_JS("text/javascript"),//
	//	TXT_JS("application/javascript"),//
	IMG_ICON("image/x-icon"), IMG_PNG("image/png"), IMG_GIF("image/gif"), IMG_WEBP("image/webp"),//
	MP3("audio/mpeg"),//
	VIDEO_FLV("video/x-flv"), VIDEO_MP4("video/mp4"), APP_m3u8("application/x-mpegURL"),//
	VIDEO_ts("video/MP2T"), VIDEO_3gp("video/3gpp"), VIDEO_mov("video/quicktime"),//
	VIDEO_avi("video/x-msvideo"), VIDEO_wmv("video/wmv"),
	APP_XML("application/xml"), //
	TEXT_XML("text/xml"), //


	//
	;

	public final String mimeType;

	public static ContentType ofExt(String ext, ContentType... defRq) {
		return ofExt(ext, true, defRq);
	}

	public static ContentType ofExt(String ext, boolean appOrTextXml, ContentType... defRq) {
		switch (ext) {
			case "css":
				return TXT_CSS;
			case "js":
				return TXT_JS;

			case "xml":
				return appOrTextXml ? APP_XML : TEXT_XML;

			case "png":
			case "jpg":
			case "jpeg":
			case "bmp":
				return IMG_PNG;
			case "webp":
				return IMG_WEBP;
			case "gif":
				return IMG_GIF;
			case "ico":
				return IMG_ICON;
			case "mp3":
				return MP3;
			case "flv":
				return VIDEO_FLV;
			case "mp4":
				return VIDEO_MP4;
			case "m3u8":
				return APP_m3u8;
			case "ts":
				return VIDEO_ts;
			case "3gp":
				return VIDEO_3gp;
			case "mov":
				return VIDEO_mov;
			case "avi":
				return VIDEO_avi;
			case "wmv":
				return VIDEO_wmv;


			default:
				return ARG.toDefThrow(() -> new RequiredRuntimeException("ContentType not found from ext '%s'", ext), defRq);
		}

	}

}
