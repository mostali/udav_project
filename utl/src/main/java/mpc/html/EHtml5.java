package mpc.html;

import mpc.str.STR;

//"https://developer.mozilla.org/en-US/docs/Web/HTML/Element"
public enum EHtml5 {
	address, article, aside, footer, header, h1, h2, h3, h4, h5, h6, hgroup, main, body, nav, section, blockquote, cite, dd, dt, dl, div, figcaption, figure, hr, li, ol, p, pre, ul, a, re, abbr, b, bdi, bdo, br, code, data, time, dfn, em, i, kbd, mark, q, rb, ruby, rp, rt, rtc, s, del, ins, samp, small, span, strong, sub, sup, u, var, wbr, area, map, audio, source, ediaStrea, img, track, video, embed, iframe, object, param, picture, canvas, noscript, script, style, caption, col, colgroup, table, tbody, tr, td, tfoot, th, thead, button, datalist, option, fieldset, label, form, input, legend, meter, optgroup, select, output, progress, textarea, details, dialog, menu, summary, slot, template, acronym, applet, basefont, bgsound, big, blink, center, command, content, dir, element, font, frame, frameset, image, isindex, keygen, listing, marquee, menuitem, multicol, nextid, nobr, noembed, noframes, plaintext, shadow, spacer, strike, tt, xmp;

	public boolean isTagName(String tagName) {
		return name().equalsIgnoreCase(tagName);
	}

	public String wrap(Object data) {
		return STR.wrapTag(data, name());
	}

	public String wrap(String data, Object... tagAttributes) {
		return STR.wrapTag(data, name(), tagAttributes);
	}
}
