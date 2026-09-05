//package mp.utilspoi;
//
//import mpu.X;
//
//import java.util.*;
//
//public class TableBlockSplitter implements Iterable<TableBlockSplitter.CoorBlock> {
//
//	public static void main(String[] args) {
//
//
//	}
//
//	private final List<List<String>> table;
//	private final List<CoorBlock> blocks = new ArrayList<>();
//
//	public TableBlockSplitter(List<List> rows) {
//		this.table = (List) rows;
//		findBlocks();
//	}
//
//	private void findBlocks() {
//		int rowCount = table.size();
//		if (rowCount == 0) {
//			return;
//		}
//
//		boolean[][] visited = new boolean[rowCount][];
//		for (int i = 0; i < rowCount; i++) {
//			visited[i] = new boolean[table.get(i).size()];
//		}
//
//		for (int r = 0; r < rowCount; r++) {
//			for (int c = 0; c < table.get(r).size(); c++) {
//				String value = table.get(r).get(c);
//				if (!visited[r][c] && value != null && !value.trim().isEmpty()) {
//					blocks.add(bfs(r, c, visited));
//				}
//			}
//		}
//	}
//
//	private CoorBlock bfs(int startR, int startC, boolean[][] visited) {
//		int minR = startR, maxR = startR;
//		int minC = startC, maxC = startC;
//
//		List<int[]> points = new ArrayList<>();
//		Queue<int[]> queue = new LinkedList<>();
//
//		queue.add(new int[]{startR, startC});
//		visited[startR][startC] = true;
//
//		int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
//
//		while (!queue.isEmpty()) {
//			int[] curr = queue.poll();
//			int r = curr[0];
//			int c = curr[1];
//			points.add(curr);
//
//			minR = Math.min(minR, r);
//			maxR = Math.max(maxR, r);
//			minC = Math.min(minC, c);
//			maxC = Math.max(maxC, c);
//
//			for (int[] d : directions) {
//				int nr = r + d[0];
//				int nc = c + d[1];
//
//				if (nr >= 0 && nr < table.size() && nc >= 0 && nc < table.get(nr).size()
//						&& !visited[nr][nc]) {
//					String val = table.get(nr).get(nc);
//					if (val != null && !val.trim().isEmpty()) {
//						visited[nr][nc] = true;
//						queue.add(new int[]{nr, nc});
//					}
//				}
//			}
//		}
//		return new CoorBlock(minR, minC, maxR, maxC);
//	}
//
//	@Override
//	public Iterator<CoorBlock> iterator() {
//		return blocks.iterator();
//	}
//
//	public class CoorBlock {
//		private final int minR, minC, maxR, maxC;
//
//		@Override
//		public String toString() {
//			return "CoorBlock{" +
//					"minR=" + minR +
//					", minC=" + minC +
//					", maxR=" + maxR +
//					", maxC=" + maxC +
//					'}' + getRows();
//		}
//
//		public CoorBlock(int minR, int minC, int maxR, int maxC) {
//			this.minR = minR;
//			this.minC = minC;
//			this.maxR = maxR;
//			this.maxC = maxC;
//		}
//
//		public Integer[] getXY() {
//			return new Integer[]{minR, minC};
//		}
//
//		public List<List<String>> getRows() {
//			List<List<String>> result = new ArrayList<>();
//			for (int r = minR; r <= maxR; r++) {
//				List<String> row = new ArrayList<>();
//				for (int c = minC; c <= maxC; c++) {
//					// Проверка на случай рваных строк
//					if (c < table.get(r).size()) {
//						row.add(table.get(r).get(c));
//					} else {
//						row.add("");
//					}
//				}
//				result.add(row);
//			}
//			return result;
//		}
//	}
//}
