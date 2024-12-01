package zk_form.string_analyze;

import lombok.RequiredArgsConstructor;
import mpu.str.SPLIT;

import java.util.List;

@RequiredArgsConstructor
public class StringInfo {

	public final String org;

	List<String> lines;

	public List<String> getLines() {
		return lines != null ? lines : (lines = SPLIT.allByNL(org));
	}

	public int getMaxlineLength() {
		List<String> lines = getLines();
		return lines.size() == 0 ? 0 : lines.stream().mapToInt(l -> l.length()).max().getAsInt();
	}

	public static BtWidth getBigType(String cat) {
		double check = cat.length() / 12;
		for (BtWidth value : BtWidth.values()) {
			if (check < value.getThink()) {
				return value;
			}
		}
		return BtWidth.MAX;
	}

//	public static int getLines(String cat) {
//		return cat.split("\n").length;
//	}

	public static StringInfo of(String content) {
		return new StringInfo(content);
	}
}
