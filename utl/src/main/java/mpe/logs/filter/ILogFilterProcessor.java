package mpe.logs.filter;

import mpc.str.condition.LogGetterDate;
import mpe.logs.filter.merger.LogFile;
import mpe.logs.filter.merger.LogLineBlock;

import java.util.List;
import java.util.stream.Collectors;

public interface ILogFilterProcessor {

	List<String> processFile(LogGetterDate logGetterDate, String file, boolean explodeMultiline);

//	default List<String> processBlocksToLines(LogGetterDate logGetterDate, List<LogLineBlock> lines, boolean explodeMultiline) {
//		return processLines(logGetterDate, lines.stream().map(LogLineBlock::toStringLines).collect(Collectors.toList()), explodeMultiline);
//	}

	default LogLineBlock[] processBlocksToBlocks(LogGetterDate logGetterDate, String file, List<LogLineBlock> lineBlocks, boolean collapseMultilineToSingleLine) {
		List<String> lines = processLines(logGetterDate, lineBlocks.stream().map(LogLineBlock::toStringLines).collect(Collectors.toList()), false);
		boolean explodeMultiline = collapseMultilineToSingleLine ? false : true;
		LogLineBlock[] logLineBlocks = LogFile.convertToLinesBlock(file, logGetterDate, lines, explodeMultiline);
		return logLineBlocks;
	}

	List<String> processLines(LogGetterDate logGetterDate, List<String> lines, boolean explodeMultiline);

}
