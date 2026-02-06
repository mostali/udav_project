package mpe.core;

import mpu.core.ARG;

import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

//Preferences Utility
public class UPref {

	public static Preferences createPreferences(boolean isUserOrSystem, Class nodeClazz) {
		return isUserOrSystem ?
				(nodeClazz == null ? Preferences.userRoot() : Preferences.userNodeForPackage(nodeClazz)) :
				(nodeClazz == null ? Preferences.systemRoot() : Preferences.systemNodeForPackage(nodeClazz));
	}

	/**
	 * ***************************************************************************
	 * >>> REMOVE
	 * ***************************************************************************
	 */
	public static boolean remove(boolean isUserOrSystem, Class nodeClazz, String key, boolean... isQuickly_Or_throwNpe) {
		return remove(createPreferences(isUserOrSystem, nodeClazz), key, isQuickly_Or_throwNpe);
	}

	public static boolean remove(Preferences preferences, String key, boolean... isQuickly_Or_throwNpe) {
		if (isStored(preferences, key)) {
			preferences.remove(key);
			return true;
		}
		if (ARG.isDefEqTrue(isQuickly_Or_throwNpe)) {
			return false;
		}
		throw new NullPointerException(String.format("Preferences contain't key '%s', [%s]", key, preferences));

	}

	/**
	 * ***************************************************************************
	 * >>> CONTAINS
	 * ***************************************************************************
	 */
	public static boolean isStored(boolean isUserOrSystem, Class nodeClazz, String key) {
		return isStored(createPreferences(isUserOrSystem, nodeClazz), key);
	}

	public static boolean isStored(Preferences preferences, String key) {
		try {
			for (String existKey : preferences.keys()) {
				if (Objects.equals(existKey, key)) {
					return true;
				}
			}
		} catch (BackingStoreException e) {
			throw new IllegalStateException(e);
		}
		return false;
	}

	/**
	 * ***************************************************************************
	 * >>> PUT
	 * ***************************************************************************
	 */
	public static <T> void put(Class nodeClazz, Class<T> type, String key, T value) {
		put(true, nodeClazz, type, key, value);
	}

	public static <T> void put_(Class nodeClazz, Class<T> type, String key, T value) {
		put(false, nodeClazz, type, key, value);
	}

	public static <T> void put(boolean isUserOrSystem, Class nodeClazz, Class<T> type, String key, T value) {
		Preferences pref = createPreferences(isUserOrSystem, nodeClazz);
		if (String.class == type) {
			pref.put(key, (String) value);
		} else if (Boolean.class == type) {
			pref.putBoolean(key, (Boolean) value);
		} else if (Integer.class == type) {
			pref.putInt(key, (Integer) value);
		} else {
			throw new UnsupportedOperationException("need put impl");
		}
	}

	/**
	 * ***************************************************************************
	 * >>> GET
	 * ***************************************************************************
	 */
	public static <T> T get(Class nodeClazz, Class<T> type, String key, T... defaultValue) {
		return get(true, nodeClazz, type, key, defaultValue);
	}

	public static <T> T get_(Class nodeClazz, Class<T> type, String key, T... defaultValue) {
		return get(true, nodeClazz, type, key, defaultValue);
	}

	public static <T> T get(boolean isUserOrSystem, Class nodeClazz, Class<T> type, String key, T... defaultValue) {
		T defValue = defaultValue == null ? null : defaultValue.length > 0 ? defaultValue[0] : null;
		Preferences pref = createPreferences(isUserOrSystem, nodeClazz);
		if (!UPref.isStored(pref, key)) {
			return defValue;
		}
		if (String.class == type) {
			return (T) pref.get(key, (String) defValue);
		}
		try {
			if (Boolean.class == type) {
				return (T) (Boolean) pref.getBoolean(key, (Boolean) defValue);
			} else if (Integer.class == type) {
				return (T) (Integer) pref.getInt(key, (Integer) defValue);
			} else {
				throw new UnsupportedOperationException("need get impl");
			}
		} catch (NullPointerException ex) {
			return defValue;
		}

	}

	/**
	 * ***************************************************************************
	 * >>> SIMPLE USER PREFRENCES
	 * ***************************************************************************
	 */
	public static void put_string(Class nodeClazz, String key, String value) {
		Preferences.userNodeForPackage(nodeClazz).put(key, value);
	}

	public static String get_string(Class nodeClazz, String key, String def) {
		return Preferences.userNodeForPackage(nodeClazz).get(key, def);
	}
}
