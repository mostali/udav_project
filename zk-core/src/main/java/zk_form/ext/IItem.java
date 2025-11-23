package zk_form.ext;

public interface IItem<T> {
	static IItem<CharSequence> of(CharSequence item) {
		return new IItem() {
			@Override
			public String getLabelName() {
				return item.toString();
			}

			@Override
			public CharSequence getSrcItem() {
				return item;
			}
		};
	}

	static IItem<? extends Enum> of(Enum en) {
		return new IItem() {
			@Override
			public String getLabelName() {
				return en.name();
			}

			@Override
			public Enum getSrcItem() {
				return en;
			}
		};
	}

	String getLabelName();

	T getSrcItem();
}
