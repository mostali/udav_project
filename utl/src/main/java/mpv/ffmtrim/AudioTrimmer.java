package mpv.ffmtrim;

import mpc.log.L;
import mpu.IT;
import mpu.X;
import mpu.core.ARR;
import mpu.core.RW;
import mpu.pare.Tuple;
import mpu.str.TKN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class AudioTrimmer {

	public static String calculateDuration(String startTime, String endTime) {
		String[] start = startTime.split(":");
		String[] end = endTime.split(":");

		int startSec = Integer.parseInt(start[0]) * 3600 +
				Integer.parseInt(start[1]) * 60 +
				Integer.parseInt(start[2]);

		int endSec = Integer.parseInt(end[0]) * 3600 +
				Integer.parseInt(end[1]) * 60 +
				Integer.parseInt(end[2]);

		int diff = endSec - startSec;

		int hours = diff / 3600;
		int minutes = (diff % 3600) / 60;
		int seconds = diff % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static void main(String[] args) {

		String trimList = "/home/dav/Загрузки/trimlist.ffl";
		List<String> cmd = RW.readLines(trimList).stream().filter(X::notBlank).collect(Collectors.toList());
		List<Tuple> cmdT = cmd.stream().map(s -> {
			String[] start = TKN.two(s, " ");
			String[] end = TKN.two(start[1], " ");
			String[] dst = TKN.two(end[1], " ");
			String src = dst[1];
			return Tuple.ofObjs(src, dst[0], start[0], end[0]);
		}).collect(Collectors.toList());

		String wd = "/home/dav/Загрузки/";
		String wd0 = "/home/dav/Загрузки/Dueshman/";
		(cmdT).forEach(c -> trimWith(wd + c.obs[0], wd0 + c.obs[1], (String) c.obs[2], (String) c.obs[3]));

		X.exit(cmdT);
		String inputFile = "/home/dav/Загрузки/Dueshman - Албашский вестник #65.mp3";
		String outputFile = "/home/dav/Загрузки/Dueshman/dm65-1.mp3";
		String startTime = "00:08:31";
		String endTime = "00:18:36";

		trimWith(inputFile, outputFile, startTime, endTime);
	}

	private static void trimWith(String inputFile, String outputFile, String startTime, String endTime) {

		String duration = calculateDuration(startTime, endTime);

		boolean success = trimAudio(inputFile, outputFile, startTime, duration);

		IT.state(success, "Ошибка при обрезке аудио");

		L.info("Trim successfully with :" + ARR.as(inputFile, outputFile, startTime, endTime));
	}


	public static boolean trimAudio(String inputPath, String outputPath,
									String startTime, String duration) {

		try {
			// Проверяем, установлен ли ffmpeg
			Process checkProcess = Runtime.getRuntime().exec("which ffmpeg");
			checkProcess.waitFor();
			if (checkProcess.exitValue() != 0) {
				System.err.println("FFmpeg не установлен. Установите: sudo apt install ffmpeg");
				return false;
			}

			// Формируем команду для обрезки
			String[] command = {
					"ffmpeg",
					"-i", inputPath,
					"-ss", startTime,    // начальное время (например "00:00:10")
					"-t", duration,      // длительность (например "00:00:30")
					"-acodec", "copy",   // копируем кодек без перекодирования
					outputPath,
					"-y"                 // перезаписывать выходной файл
			};

			System.out.println("Выполняется команда: " + String.join(" ", command));

			Process process = new ProcessBuilder(command)
					.redirectErrorStream(true)
					.start();

			// Чтение вывода процесса
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			int exitCode = process.waitFor();
			return exitCode == 0;

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

}