package mpf.arc;

import mpc.fs.UF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UnpackJar {

	//	public static void main(String[] args) throws IOException {
	//		unpack("./t.war", "./t");
	//	}

	public static Path unpack(String jarPath, String... destDir) throws IOException {
		File file = new File(jarPath).getAbsoluteFile();
		String dstDir = null;
		if (destDir.length > 0) {
			dstDir = destDir[0];
		} else {
			dstDir = file.getParent();
		}
		dstDir = UF.normDir(dstDir);
		JarFile jar = new JarFile(file);
		// first get all directories,
		// then make those directory on the destination Path
		for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) enums.nextElement();
			String fileName = dstDir + entry.getName();
			File f = new File(fileName);
			if (fileName.endsWith("/")) {
				f.mkdirs();
			}
		}
		//create files
		for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) enums.nextElement();
			String fileName = dstDir + entry.getName();
			File f = new File(fileName);
			if (!fileName.endsWith("/")) {
				InputStream is = jar.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(f);
				while (is.available() > 0) {
					fos.write(is.read());
				}
				fos.close();
				is.close();
			}
		}
		return Paths.get(dstDir);
	}

}