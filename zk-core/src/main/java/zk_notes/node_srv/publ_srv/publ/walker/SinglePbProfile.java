//package zkbea_apps.publ.walker;
//
//import lombok.RequiredArgsConstructor;
//import mpc.exception.NI;
//import mpc.exception.WhatIsTypeException;
//import mpc.fs.UF;
//import mpc.fs.ext.GEXT;
//import mpc.fs.fd.EFT;
//import mpc.log.L;
//
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//public class SinglePbProfile {
//
//	final String plName;
//
//	final List<String> playlistDataValue;
//
//	public List<String> searchFiles() {
//
//		List<String> filesTotal = new ArrayList<>();
//
//		for (String line : playlistDataValue) {
//
//			if (NodePublist.PfxNode.hasKeyStart(line)) {
//
//				NodePbProc.PlLine plLine = new NodePbProc.PlLine(line);
//
//				if (plLine.hasErrors()) {
//					L.error("AudioProfile [{}] has error with line [{}]", plName, plLine.getMultiOrSingleErrorOrNull());
//					continue;
//				}
//
//				List<String> collection = plLine.searchListFiles();
//
//				filesTotal.addAll(collection);
//
//			} else {
//
//				List<String> files = extractedAsFile(line);
//
//				filesTotal.addAll(files);
//
//			}
//
//		}
//
//		int sizeBefore = filesTotal.size();
//		Set<String> total = new LinkedHashSet(filesTotal);
//		if (sizeBefore != total.size()) {
////				L.warn("AudioProfile [{}] has twins", UserCol.toKey());
//		}
//		return total.stream().collect(Collectors.toList());
//	}
//
//
//	private List<String> extractedAsFile(String line) {
//		List<String> files = new ArrayList<>();
//
//		EFT eft = EFT.of(line, null);
//		if (eft == null) {
//			if (L.isWarnEnabled()) {
//				L.warn("APL line wo eft: " + UF.ln(line));
//			}
//			return files;
//		}
//		switch (eft) {
//			case FILE:
//				boolean isAudio = GEXT.AUDIO.has(line);
//				if (!isAudio) {
//					if (L.isWarnEnabled()) {
//						L.warn("APL line no audio file: " + UF.ln(line));
//					}
//					return files;
//				}
//
//				if (L.isWarnEnabled()) {
//					L.warn("APL found file: " + UF.ln(line));
//				}
//
//				files.add(line);
//				return files;
//
//			case DIR:
//				NI.stop("ni");
//				break;
//
//			default:
//				throw new WhatIsTypeException(eft);
//
//		}
//		return files;
//	}
//
//}
