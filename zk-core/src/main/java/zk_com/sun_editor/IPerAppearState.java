package zk_com.sun_editor;

public interface IPerAppearState {

	default TypeState typeState() {
		return TypeState.SITE;
	}

	enum TypeState {
		SITE, DOMAIN, PAGE, FORM
	}
}
