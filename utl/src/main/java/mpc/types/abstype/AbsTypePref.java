package mpc.types.abstype;

import mpe.core.UPref;

public class AbsTypePref<T> {

	private final boolean isUserOrSystem;
	private final Class nodeClazz;
	private final boolean useDefaultValue;

	private final AbsType<T> val;

	public AbsTypePref(boolean isUserOrSystem, Class node, Class<T> type, String name) {
		this(isUserOrSystem, node, type, name, null, false);
	}

	public AbsTypePref(boolean isUserOrSystem, Class node, Class<T> type, String name, T defaultValue) {
		this(isUserOrSystem, node, type, name, defaultValue, true);
	}

	private AbsTypePref(boolean isUserOrSystem, Class node, Class<T> type, String name, T defaultValue, boolean useDefaultValue) {
		this.isUserOrSystem = isUserOrSystem;
		this.useDefaultValue = useDefaultValue;
		this.nodeClazz = node;
		this.val = new AbsType<T>(name, defaultValue, type);
	}

	public <T> boolean isStored() {
		return UPref.isStored(isUserOrSystem, nodeClazz, val.name());
	}

	public <T> boolean remove() {
		return UPref.remove(isUserOrSystem, nodeClazz, val.name());
	}

	public void write(T value) {
		UPref.put(isUserOrSystem, nodeClazz, val.type(), val.name(), value);
	}

	public T get(T... defaultValueImportant) {
		return read(defaultValueImportant);
	}

	public T read(T... defaultValueImportant) {
		boolean hasDefaultValue = false;
		T defaultValue = null;
		if (defaultValueImportant.length > 0) {
			hasDefaultValue = true;
			defaultValue = defaultValueImportant[0];
		} else if (useDefaultValue) {
			hasDefaultValue = true;
			defaultValue = val.val();
		}
		return hasDefaultValue ? UPref.get(isUserOrSystem, nodeClazz, val.type(), val.name(), defaultValue) : UPref.get(isUserOrSystem, nodeClazz, val.type(), val.name());
	}
}
