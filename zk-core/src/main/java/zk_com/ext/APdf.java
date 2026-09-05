package zk_com.ext;//package zk_com.ext;

import lombok.SneakyThrows;
import mpu.core.RW;
import org.zkoss.io.NullInputStream;
import org.zkoss.lang.SystemException;
import org.zkoss.util.media.ContentTypes;
import org.zkoss.util.media.Media;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;

public class APdf implements Media {
	protected static final InputStream DYNAMIC_STREAM = new NullInputStream();
	private final byte[] _data;
	protected final transient InputStream _isdata;
	private final URL _url;
	private final File _file;
	private String _format;
	private String _ctype;
	private final String _name;

	public APdf(String name, byte[] data) throws IOException {
		if (data == null) {
			throw new IllegalArgumentException("null data");
		} else {
			this._name = name;
			this._data = data;
			this._isdata = null;
			this._url = null;
			this._file = null;
		}
	}

	public APdf(String name, InputStream isdata) throws IOException {
		if (isdata == null) {
			throw new IllegalArgumentException("stream cannot be null");
		} else {
			this._name = name;
			this._isdata = isdata;
			this._data = null;
			this._url = null;
			this._file = null;
		}
	}

	public APdf(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url cannot be null");
		} else {
			this._name = this.getName(url);
			this._url = url;
			this._isdata = DYNAMIC_STREAM;
			this._data = null;
			this._file = null;
		}
	}

	public APdf(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null");
		} else {
			this._name = file.getName();
			this._file = file;
			this._isdata = DYNAMIC_STREAM;
			this._data = null;
			this._url = null;
		}
	}

	public APdf(String filename) {
		this(new File(filename));
	}

	public APdf(InputStream is) throws IOException {
		this((String) null, (InputStream) is);
	}

	public String getName() {
		return this._name;
	}

	private String getName(URL url) {
		String name = url.getPath();
		if (name != null) {
			int i = name.lastIndexOf(File.pathSeparatorChar);
			if (i >= 0) {
				name = name.substring(i + 1);
			}

			if (File.pathSeparatorChar != '/') {
				int j = name.lastIndexOf(47);
				if (j >= 0) {
					name = name.substring(j + 1);
				}
			}
		}

		return name;
	}

	public String getContentType() {
		if (this._ctype == null) {
			this._ctype = getContentType(this.getFormat());
		}

		return this._ctype;
	}

	private static String getContentType(String format) {
		String ctype = ContentTypes.getContentType(format);
		return ctype != null ? ctype : "application/pdf";
	}

	public InputStream getStreamData() {
		try {
			if (this._url != null) {
				InputStream is = this._url.openStream();
				return is != null ? new BufferedInputStream(is) : null;
			}

			if (this._file != null) {
				return new BufferedInputStream(new FileInputStream(this._file));
			}
		} catch (IOException ex) {
			throw new SystemException("Unable to read " + (this._url != null ? this._url.toString() : this._file.toString()), ex);
		}

		return (InputStream) (this._isdata != null ? this._isdata : new ByteArrayInputStream(this._data));
	}

	public final String getStringData() {
		throw this.newIllegalStateException();
	}

	public final Reader getReaderData() {
		throw this.newIllegalStateException();
	}

	private final IllegalStateException newIllegalStateException() {
		return new IllegalStateException(this._isdata != null ? "Use getStreamData() instead" : "Use getByteData() instead");
	}

	public String getFormat() {
		if (this._format == null) {
			this._format = this.getFormatByName(this._name);
		}

		return this._format;
	}

	private String getFormatByName(String name) {
		if (name != null) {
			int j = name.lastIndexOf(46) + 1;
			int k = name.lastIndexOf(47) + 1;
			if (j > k && j < name.length()) {
				return name.substring(j);
			}
		}

		return null;
	}

	public boolean isContentDisposition() {
		return true;
	}

	public boolean isBinary() {
		return true;
	}

	public final boolean inMemory() {
		return this._data != null;
	}

	public byte[] getByteData() {
		if (this._data == null) {
			throw new IllegalStateException("Use getStreamData() instead");
		} else {
			return this._data;
		}
	}
}
