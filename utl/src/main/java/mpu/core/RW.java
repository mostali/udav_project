package mpu.core;

import lombok.SneakyThrows;
import mpc.exception.FIllegalStateException;
import mpc.fs.FileLines;
import mpc.fs.UFS;
import mpc.fs.tmpfile.TmpFileOperation;
import mpu.X;
import mpu.IT;
import mpc.exception.RequiredRuntimeException;
import mpc.exception.WhatIsTypeException;
import mpc.json.GsonMap;
import mpc.json.UGson;
import mpc.types.ruprops.RuProps;
import mpc.str.sym.SYM;
import mpu.str.STR;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllBytes;

//Читаем/Пишем файлы
//Read/Write
//_ d в конце имени метода означает что метод кидает проверяемую ошибку (старый подход до ломбука)
//Древний код
public class RW {

	/**
	 * *************************************************************
	 * ---------------------------- READ AS -----------------------
	 * *************************************************************
	 */

	public static <T> T readAs(Path path, Class<T> cnt, T... defRq) {
		try {
			if (cnt == String.class) {
				return (T) RW.readContent_(path);
			} else if (cnt == InputStream.class) {
				return (T) new FileInputStream(path.toFile());
			} else if (cnt == byte[].class || cnt == byte.class) {
				return (T) RW.readBytes(path);
			} else if (cnt == List.class) {
				return (T) RW.readLines_(path);
			} else if (cnt == Properties.class) {
				return (T) RW.readProperties_(path);
			} else if (cnt == RuProps.class) {
				return (T) RW.readRuProps(path);
			} else if (cnt == FileLines.class) {
				return (T) FileLines.of(path);
			} else {
				throw new WhatIsTypeException(cnt);
			}
		} catch (Exception ex) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			return X.throwException(ex);
		}
	}

	public static byte[] readBytes(Path path, byte[]... defRq) {
		try {
			return Files.readAllBytes(path);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "read bytes from file '%s'", path), defRq);
		}
	}

	public static List<String> readLines(Path file, List<String>... defRq) {
		return readAs(file, List.class, defRq);
	}

	@SneakyThrows
	public static List<String> readLines(Path file, int countStartOrEnd, boolean... naturalSortIfTail) {
		List<String> result = new ArrayList<>();
		if (countStartOrEnd == 0) {
			return result;
		}
		if (countStartOrEnd > 0) {
			try (BufferedReader brTest = new BufferedReader(new FileReader(file.toFile()))) {
				while (--countStartOrEnd >= 0) {
					result.add(brTest.readLine());
				}
			}
		} else {
			countStartOrEnd = Math.abs(countStartOrEnd);
			try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file.toFile(), Charset.defaultCharset())) {
				String line = "";
				while ((line = reader.readLine()) != null && result.size() < countStartOrEnd) {
					result.add(line);
				}
			}
			if (ARG.isDef(naturalSortIfTail)) {
				Collections.reverse(result);
			}
		}

		return result;

	}


	/**
	 * *************************************************************
	 * ---------------------------- PROPERTIES -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public static Properties readProperties(Path file_properties) {
		return readProperties_(file_properties);
	}

	public static Properties readProperties_(Path file_properties) throws IOException {
		try (InputStream is = Files.newInputStream(file_properties)) {
			return readProperties_(is);
		}
	}

	public static RuProps readRuProps(Path path) {
		return RuProps.of(path);
	}

	public static Properties readProperties_(InputStream is) throws IOException {
		Properties props = new Properties();
		props.load(is);
		return props;
	}

	public static void writeProperties_(Path file, Properties properties, String... comment) throws IOException {
		try (OutputStream out = Files.newOutputStream(file)) {
			properties.store(out, ARG.toDefOrNull(comment));
		}
	}

	public static void writeRuProps_(Path file, RuProps properties) throws IOException {
		properties.writeMap_(file);
	}

	public static void writeRuProps(Path file, Map properties, Boolean mkdirs_mkdir_orNot) {
		RuProps.writeMap(file, properties, mkdirs_mkdir_orNot);
	}

	public static GsonMap readGsonMap(Path path, boolean... createIfNotExist) {
		return GsonMap.read(path, createIfNotExist);
	}

	public static GsonMap readGsonMap(Path path, Charset charset, boolean... createIfNotExist) {
		return GsonMap.read(path, charset, createIfNotExist);
	}

	public static void writeGsonMap(Path path, GsonMap gsonMap, boolean... createIfNotExist) {
		GsonMap.write(path, gsonMap, false, ARG.isDefEqTrue(createIfNotExist));
	}

	/**
	 * *************************************************************
	 * ---------------------------- LINE -----------------------
	 * *************************************************************
	 */

	public static String readLine(Path file, int index, String... defRq) {
		try {
			return readLines_(file).get(index);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	//TODO read line -0 not work
	public static String readLineOpt(Path file, int index, String... defRq) {
		try {
			List<String> lines = readLines(file, index < 0 ? index - 1 : index + 1);
			return ARRi.last(lines);
		} catch (Exception ex) {
			return ARG.toDefThrow(ex, defRq);
		}
	}

	@SneakyThrows
	public static List<String> read(Path file) {
		return readLines_(file);
	}

	public static List<String> readLines_(Path file) throws IOException {
		String str = readContent_(file);
		return Arrays.asList(StringUtils.split(str, SYM.NEWLINE));
	}

	public static List<String> readLines_(Path file, String lineSeparator) throws IOException {
		String str = readContent_(file);
		return Arrays.asList(StringUtils.split(str, lineSeparator));
	}

	public static List<String> readLines_(Path file, Charset... charset) throws IOException {
		return ARG.isDefNNF(charset) ? Files.readAllLines(file, charset[0]) : Files.readAllLines(file);
	}

	/**
	 * *************************************************************
	 * ---------------------------- STRING -----------------------
	 * *************************************************************
	 */

	@SneakyThrows
	public static String readContent(Path file) {
		return readContent_(file);
	}

	public static String readContent_(Path file) throws IOException {
		String fileString = new String(readAllBytes(file), StandardCharsets.UTF_8);
		return fileString;
	}


	public static String readContent_(String file) throws IOException {
		return readContent_(Paths.get(file), (Charset) null);
	}

	public static String readString(Path file, String... defRq) {
		return readContent_(file, Charset.defaultCharset(), defRq);
	}

	public static String readContent_(Path file, Charset charset, String... defRq) {
		try {
			return readContent_(file, charset);
		} catch (Exception e) {
			if (ARG.isDef(defRq)) {
				return ARG.toDef(defRq);
			}
			throw new RequiredRuntimeException(e, "Error read content from file '%s'", file);
		}
	}

	public static String readContent_(Path file, Charset charset) throws IOException {
		Charset charset_ = ARG.toDefNNF(Charset.defaultCharset(), charset);
		String fileString = new String(readAllBytes(file), charset_);
		return fileString;
	}

	/**
	 * *************************************************************
	 * ---------------------------- WRITE -----------------------
	 * *************************************************************
	 */

	public static void write_Append_(Path file, @NotNull CharSequence content) throws IOException {
		write_(file, content, (Charset) null, StandardOpenOption.APPEND);
	}

	@SneakyThrows
	public static void writeAppend(Path file, @NotNull CharSequence content) {
		write_(file, content, (Charset) null, StandardOpenOption.APPEND);
	}


	public static Path write_AppendOrCreateNew_(Path file, @NotNull CharSequence seq) throws IOException {
		if (Files.exists(file)) {
			return write_(file, seq, StandardOpenOption.APPEND);
		} else {
			return write_(file, seq, StandardOpenOption.CREATE_NEW);
		}
	}

	@SneakyThrows
	public static void writePrettyJson(Path file, @NotNull CharSequence content, OpenOption... openOptions) {
		write(file, UGson.toStringPretty(content.toString()), openOptions);
	}

	public static void writeViaTmp(Path file, @NotNull CharSequence content) {
		new TmpFileOperation() {
			@Override
			public void doOperationImpl(Path tmpFile) {
				write(tmpFile, content);
				UFS.MV.move(tmpFile, file, null);
			}
		}.doOperation();
	}

	@SneakyThrows
	public static void write(Path file, @NotNull CharSequence content, OpenOption... openOptions) {
		write_(file, content, (Charset) null, openOptions);
	}

	@SneakyThrows
	public static void write(Path file, @NotNull CharSequence content, Boolean mkdirs_mkdir_orNot, OpenOption... openOption_orNull) {
		write_(file, content, mkdirs_mkdir_orNot, openOption_orNull);
	}


	public static void write_(Path file, @NotNull CharSequence content, Boolean mkdirs_mkdir_orNot, OpenOption... openOptions) throws IOException {
		if (mkdirs_mkdir_orNot != null && file.getParent() != null) {
			UFS.MKDIR.mkdirsIfNotExistImpl(file.getParent(), mkdirs_mkdir_orNot);
		}
		write_(file, content, openOptions);
	}


	/**
	 * *************************************************************
	 * ---------------------------- Lines -----------------------
	 * *************************************************************
	 */
	public static Path write_(Path file, Iterable<String> lines, Charset charset, OpenOption... options) throws IOException {
		return Files.write(file, lines, STR.defaultCharset(charset), options);
	}

	public static void write_(Path file, Iterable<String> lines, OpenOption... options) throws IOException {
		Files.write(file, lines, options);
	}

	/**
	 * *************************************************************
	 * ---------------------------- String -----------------------
	 * *************************************************************
	 */
	public static Path write_(Path file, CharSequence seq, OpenOption... openOption) throws IOException {
		return write_(file, seq.toString().getBytes(), openOption);
	}

	public static Path write_(Path file, CharSequence seq, Charset charset, OpenOption... openOption) throws IOException {
		byte[] bytes = charset == null ? seq.toString().getBytes() : seq.toString().getBytes(charset);
		return write_(file, bytes, openOption);
	}

	/**
	 * *************************************************************
	 * ---------------------------- Bytes -----------------------
	 * *************************************************************
	 */
	public static Path write_(Path file, byte[] bytes, OpenOption... openOption) throws IOException {
		return Files.write(file, bytes, openOption);
	}

	public static Path write_img_(Path file, byte[] bytes, String img_ext) throws IOException {
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
		IT.state(ImageIO.write(img, img_ext, file.toFile()));
		return file;
	}

	/**
	 * *************************************************************
	 * ---------------------------- InputStream -----------------------
	 * *************************************************************
	 */
	public static Path write_(Path file, InputStream inputStream, CopyOption... options) throws IOException {
		Files.copy(inputStream, file, options);
		return file;
	}

	@SneakyThrows
	public static List<String> readLines(InputStream inputStream) {
		return IOUtils.readLines(inputStream);
	}

	@SneakyThrows
	public static List<String> readLines(Reader reader) {
		return IOUtils.readLines(reader);
	}

	public static List<String> readLines(String file) {
		return readLines(Paths.get(file));
	}

	@SneakyThrows
	public static List<String> readLines(Path file) {
		return IOUtils.readLines(new FileReader(file.toFile()));
	}

	@SneakyThrows
	public static void writeBytes(Path path, byte[] data) {
		try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
			fos.write(data);
		}
	}

	public static void writeLines(Path path, List<String> lines) {
		write(path, lines.stream().collect(Collectors.joining(System.lineSeparator())));
	}

	public static void deleteDir(Path dir, boolean... throwIfNoDelete) {
		Exception ex;
		if (UFS.existDir(dir)) {
			try {
				FileUtils.deleteDirectory(dir.toFile());
				return;
			} catch (Exception e) {
				ex = e;
			}
		} else {
			if (UFS.exist(dir)) {
				ex = new FIllegalStateException("DIR '%s' is not dir", dir);
			} else {
				ex = new FIllegalStateException("DIR '%s' not exist", dir);
			}
		}
		if (ARG.isDefEqTrue(throwIfNoDelete)) {
			throw new RequiredRuntimeException(ex, "Error delete DIR");
		}
	}

	public static void deleteFile(Path file, boolean... throwIfNoDelete) {
		Exception ex;
		if (UFS.existFile(file)) {
			try {
				Files.delete(file);
				return;
			} catch (Exception e) {
				ex = e;
			}
		} else {
			if (UFS.exist(file)) {
				ex = new FIllegalStateException("FILE '%s' is not file", file);
			} else {
				ex = new FIllegalStateException("FILE '%s' not exist", file);
			}
		}
		if (ARG.isDefEqTrue(throwIfNoDelete)) {
			throw new RequiredRuntimeException(ex, "Error delete FILE");
		}
	}

	public static class Serializable2String {
		public static Serializable deserializable(String s) throws IOException, ClassNotFoundException {
			byte[] data = Base64.getDecoder().decode(s);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			try {
				return (Serializable) ois.readObject();
			} finally {
				ois.close();
			}
		}

		public static String serializable(Serializable o) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(o);
				return Base64.getEncoder().encodeToString(baos.toByteArray());
			} finally {
				oos.close();
			}
		}
	}

	public static class Serializable2File {

		public static void serialize(String file, Serializable serializable, boolean... createParentDirIfNotExist) throws IOException {
			serialize(new File(file), serializable, createParentDirIfNotExist);
		}

		public static void serialize(File file, Serializable serializable, boolean... createParentDirIfNotExist) throws IOException {
			if (ARG.isDefEqTrue(createParentDirIfNotExist)) {
				file.getParentFile().mkdirs();
			} else if (ARG.isDefEqFalse(createParentDirIfNotExist)) {
				file.getParentFile().mkdir();
			}
			serializeImpl(serializable, new FileOutputStream(file));
		}

		public static <S extends Serializable> S deserialize(String path) throws IOException {
			return deserializeImpl(new FileInputStream(path));
		}

		public static <S extends Serializable> S deserialize(File path) throws IOException {
			return deserializeImpl(new FileInputStream(path));
		}

		/**
		 * Copy from com.google.api.client.util.IOUtils
		 */
		private static <S extends Serializable> S deserializeImpl(InputStream inputStream) throws IOException {
			Serializable var1;
			try {
				var1 = (Serializable) (new ObjectInputStream(inputStream)).readObject();
			} catch (ClassNotFoundException var6) {
				IOException ioe = new IOException("Failed to deserialize object");
				ioe.initCause(var6);
				throw ioe;
			} finally {
				inputStream.close();
			}
			return (S) var1;
		}

		/**
		 * Copy from com.google.api.client.util.IOUtils
		 */
		private static void serializeImpl(Object value, OutputStream outputStream) throws IOException {
			try {
				new ObjectOutputStream(outputStream).writeObject(value);
			} finally {
				outputStream.close();
			}

		}
	}
}
