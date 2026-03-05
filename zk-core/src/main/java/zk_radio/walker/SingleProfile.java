package zk_radio.walker;

import lombok.RequiredArgsConstructor;
import mpc.exception.NI;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.ext.GEXT;
import mpc.fs.fd.EFT;
import mpc.log.L;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SingleProfile {

	final String plName;

	final List<String> playlistDataValue;

	public List<String> searchFiles() {

		List<String> filesTotal = new ArrayList<>();

		for (String line : playlistDataValue) {

			if (NodePlaylist.PfxNode.hasKeyStart(line)) {

				NodePlProc.PlLine plLine = new NodePlProc.PlLine(line);

				if (plLine.hasErrors()) {
					L.error("AudioProfile [{}] has error with line [{}]", plName, plLine.getMultiOrSingleErrorOrNull());
					continue;
				}

				List<String> collection = plLine.searchListFiles();

				filesTotal.addAll(collection);

			} else {

				List<String> files = extractedAsFile(line);

				filesTotal.addAll(files);

			}

		}

		int sizeBefore = filesTotal.size();
		Set<String> total = new LinkedHashSet(filesTotal);
		if (sizeBefore != total.size()) {
//				L.warn("AudioProfile [{}] has twins", UserCol.toKey());
		}
		return total.stream().collect(Collectors.toList());
	}


	private List<String> extractedAsFile(String line) {
		List<String> files = new ArrayList<>();

		EFT eft = EFT.of(line, null);
		if (eft == null) {
			if (L.isWarnEnabled()) {
				L.warn("APL line wo eft: " + UF.ln(line));
			}
			return files;
		}
		switch (eft) {
			case FILE:
				boolean isAudio = GEXT.AUDIO.has(line);
				if (!isAudio) {
					if (L.isWarnEnabled()) {
						L.warn("APL line no audio file: " + UF.ln(line));
					}
					return files;
				}

				if (L.isWarnEnabled()) {
					L.warn("APL found file: " + UF.ln(line));
				}

				files.add(line);
				return files;

			case DIR:
//						GEXT.AUDIO.ls(line)
				NI.stop("ni");
				break;

			default:
				throw new WhatIsTypeException(eft);

		}
		return files;
	}


//	public static class UserCol {
//
//		public static final String DEL_MAIN = " @@@";
//		public static final String DEL = " @@@ ";
//
//
//		public static String toKey(String playlist) {
//			String[] userColKey = getUserColModel(playlist);
//			return userColKey[0] + DEL + userColKey[1];
//		}
//
//		public static String toKeyMain() {
//			return LOAD_LOGIN() + DEL_MAIN;
//		}
//
//		private static String[] getUserColModel(String playlist) {
//			return new String[]{LOAD_LOGIN(), playlist};
//		}
//
//		private static String LOAD_LOGIN() {
//			return WebUsr.login();
//		}
//
//		public static String cutProfileName(String key) {
//			return TKN.lastGreedy(key, DEL);
//		}
//	}
}
