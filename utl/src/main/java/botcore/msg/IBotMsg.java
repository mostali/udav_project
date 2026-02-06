package botcore.msg;

import botcore.BotRoute;
import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpu.core.EQ;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public interface IBotMsg {//extends ICleanMessage {

	IBotMsg anywayFormat();

	default void initSend() {
	}

	@RequiredArgsConstructor
	enum ParseMode {
		HTML("html"), MD("Markdown"), MD2("MarkdownV2"), NONE(null);
		public final String mode;

		public static ParseMode valueOfMode(String parseMode) {
			return Stream.of(values()).filter(m -> EQ.equalsUnsafe(parseMode, m.mode)).findAny().get();
		}

		public Serializable sendMessage(BotRoute botRoute, String message, Object... args) {
			switch (this) {
				case MD:
					return botRoute.sendMsg_MD(message, args);
				case MD2:
					return botRoute.sendMsg_MD2(message, args);
				case HTML:
					return botRoute.sendMsg_HTML(message, args);
				case NONE:
					return botRoute.sendMsg_STRING(message, args);
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

//	 "*Жирный текст*\n" +
//			 "_Курсив_\n" +
//			 "`Моноширинный`\n" +
//			 "```Блок кода```\n" +
//			 "[Ссылка](https://example.com)\n" +
//			 "||Спойлер||"

	// Использование
//	String xmlContent = "<root><message>Hello</message></root>";
//	String escapedXml = escapeXml(xmlContent);
	static String escapeXml(String xml) {
		return xml.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

}
