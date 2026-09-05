package mpf.arc;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class UnpackZip {

	public static Path unpack(String fileZip, String... dirDest) throws IOException {
		String dirDst = dirDest.length > 0 ? dirDest[0] : Paths.get(fileZip).toAbsolutePath().getParent().toString();
		java.util.zip.ZipFile zipFile = new ZipFile(fileZip);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(dirDst, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					InputStream in = zipFile.getInputStream(entry);
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					out.close();
				}
			}
		} finally {
			zipFile.close();
		}
		return Paths.get(dirDst);
	}

}
