package zk_notes.node.core;

//NoteViewType/Text
public enum NVT {
	TEXT, //
	WYSIWYG, //
	DIR, //
	TREE_NODE, //
	HTML_WIN, //
	HTML, //
	MD_WIN, //
	MD, //
	//		PDF_WIN//
//		PRETTYCODE_WIN,
	CODE;

	public static final String KEY = "nvt";

	public String nameHu() {
		switch (this) {
			default:
				return name();
		}
	}

	public boolean isPrimaryInMenu() {
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
			default:
				return false;
		}
	}

}
