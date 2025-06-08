package zk_notes.node.core;

import mpc.exception.WhatIsTypeException;

//NoteViewType/Text
public enum NVT {
	TEXT, //
	WYSIWYG, //
	HTML_WIN, //
	HTML, //
	MD_WIN, //
	MD, //
	//		PDF_WIN//
//		PRETTYCODE_WIN,
	PRETTYCODE;

	public String nameHu() {
		switch (this) {
			default:
				return name();
		}
	}

	public boolean isExt() {
		switch (this) {
			case TEXT:
			case HTML:
			case WYSIWYG:
				return false;
			default:
				return true;
		}
	}

	public boolean isWindowMode() {
		switch (this) {
			case WYSIWYG:
			case TEXT:
			case HTML_WIN:
			case MD_WIN:
				return true;
			case HTML:
			case MD:
			case PRETTYCODE:
				return false;
			default:
				throw new WhatIsTypeException(this);
		}
	}
}
