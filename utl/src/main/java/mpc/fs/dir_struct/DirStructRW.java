package mpc.fs.dir_struct;

import lombok.SneakyThrows;
import mpc.fs.fd.FILE;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DirStructRW {

	/**
	 * *************************************************************
	 * ---------------------------- READ -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public static String readContent(Path rootDir, String child1_file, int... line_num) {
		Path childFile = child(rootDir, child1_file);
		return FILE.of(childFile).readLine(line_num);
	}

	@SneakyThrows
	public static String readContent(Path rootDir, String child1_dir, String child2_file, int... line_num) {
		Path childFile = child(rootDir, child1_dir, child2_file);
		return FILE.of(childFile).readLine(line_num);
	}

	@SneakyThrows
	public static String readContent(Path rootDir, String child1_dir, String child2_dir, String child3_file, int... line_num) {
		Path childDirFile = child(rootDir, child1_dir, child2_dir, child3_file);
		return FILE.of(childDirFile).readLine(line_num);
	}

	@SneakyThrows
	public static String readLine(Path rootDir, String child1_file, int... line_num) {
		Path childFile = child(rootDir, child1_file);
		return FILE.of(childFile).readLine(line_num);
	}

	@SneakyThrows
	public static String readLine(Path rootDir, String child1_dir, String child2_file, int... line_num) {
		Path childFile = child(rootDir, child1_dir, child2_file);
		return FILE.of(childFile).readLine(line_num);
	}

	@SneakyThrows
	public static String readLine(Path rootDir, String child1_dir, String child2_dir, String child3_file, int... line_num) {
		Path childDirFile = child(rootDir, child1_dir, child2_dir, child3_file);
		return FILE.of(childDirFile).readLine(line_num);
	}

	/**
	 * *************************************************************
	 * ---------------------------- WRITE -----------------------
	 * *************************************************************
	 */
	@SneakyThrows
	public static Path writeToChild(Path rootDir, String child1_file, String content, Boolean mkDirs_mkdir_orNot) {
		return writeToChild_(rootDir, child1_file, content, mkDirs_mkdir_orNot);
	}

	public static Path writeToChild_(Path rootDir, String child1_file, String content, Boolean mkDirs_mkdir_orNot) throws IOException {
		Path childFile = child(rootDir, child1_file);
		FILE.of(childFile).writeContent(content, mkDirs_mkdir_orNot, StandardOpenOption.CREATE);
		return childFile;
	}

	@SneakyThrows
	public static Path writeToChild2(Path rootDir, String child1_dir, String child2_file, String content, Boolean mkDirs_mkdir_orNot) {
		return writeToChild2_(rootDir, child1_dir, child2_file, content, mkDirs_mkdir_orNot);
	}

	public static Path writeToChild2_(Path rootDir, String child1_dir, String child2_file, String content, Boolean mkDirs_mkdir_orNot) throws IOException {
		Path childFile = child(rootDir, child1_dir, child2_file);
		FILE.of(childFile).writeContent(content, mkDirs_mkdir_orNot, StandardOpenOption.CREATE);
		return childFile;
	}

	@SneakyThrows
	public static Path writeToChild3(Path rootDir, String child1_dir, String child2_dir, String child3_file, String content, Boolean mkDirs_mkdir_orNot) {
		return writeToChild3_(rootDir, child1_dir, child2_dir, child3_file, content, mkDirs_mkdir_orNot);
	}

	public static Path writeToChild3_(Path rootDir, String child1_dir, String child2_dir, String child3_file, String content, Boolean mkDirs_mkdir_orNot) throws IOException {
		Path childDirFile = child(rootDir, child1_dir, child2_dir, child3_file);
		FILE.of(childDirFile).writeContent(content, mkDirs_mkdir_orNot, StandardOpenOption.CREATE);
		return childDirFile;
	}

	/**
	 * *************************************************************
	 * ---------------------------- CHILD -----------------------
	 * *************************************************************
	 */
	public static Path child(Path rootDir, String child1_file) {
		return rootDir.resolve(child1_file);
	}

	public static Path child(Path rootDir, String child1_dir, String child2_file) {
		return rootDir.resolve(child1_dir).resolve(child2_file);
	}

	public static Path child(Path rootDir, String child1_dir, String child2_dir, String child3_file) {
		return rootDir.resolve(child1_dir).resolve(child2_dir).resolve(child3_file);
	}

}
