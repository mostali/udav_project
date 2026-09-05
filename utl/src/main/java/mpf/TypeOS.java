package mpf;

import java.io.IOException;

public class TypeOS {

	/**
	 * Operating system state flag for error.
	 */
	private static final int INIT_PROBLEM = -1;
	/**
	 * Operating system state flag for neither Unix nor Windows.
	 */
	private static final int OTHER = 0;
	/**
	 * Operating system state flag for Windows.
	 */
	private static final int WINDOWS = 1;
	/**
	 * Operating system state flag for Unix.
	 */
	private static final int UNIX = 2;
	/**
	 * Operating system state flag for Posix flavour Unix.
	 */
	private static final int POSIX_UNIX = 3;

	/**
	 * The operating system flag.
	 */
	public static final int OS;

	static {
		int os = OTHER;
		try {
			String osName = System.getProperty("os.name");
			if (osName == null) {
				throw new IOException("os.name not found");
			}
			osName = osName.toLowerCase();
			// match
			if (osName.indexOf("windows") != -1) {
				os = WINDOWS;
			} else if (osName.indexOf("linux") != -1 || osName.indexOf("sun os") != -1 || osName.indexOf("sunos") != -1
					   || osName.indexOf("solaris") != -1 || osName.indexOf("mpe/ix") != -1
					   || osName.indexOf("freebsd") != -1 || osName.indexOf("irix") != -1
					   || osName.indexOf("digital unix") != -1 || osName.indexOf("unix") != -1
					   || osName.indexOf("mac os x") != -1) {
				os = UNIX;
			} else if (osName.indexOf("hp-ux") != -1 || osName.indexOf("aix") != -1) {
				os = POSIX_UNIX;
			} else {
				os = OTHER;
			}

		} catch (Exception ex) {
			os = INIT_PROBLEM;
		}
		OS = os;
	}

	public static final boolean isWindows() {
		return OS == 1;
	}

	public static final boolean isUnix() {
		return OS == 2;
	}

}
