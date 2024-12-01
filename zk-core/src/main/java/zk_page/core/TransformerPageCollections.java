package zk_page.core;

import java.util.ArrayList;
import java.util.List;

public class TransformerPageCollections {
	public static class ComponentFinder {

		public static List<int[]> findClosestComponents(List<int[]> coms, int[] point, int maxLen) {
			List<int[]> closestComponents = new ArrayList<>();

			for (int[] com : coms) {
				// Вычисляем расстояние между компонентом и точкой
				double distance = Math.sqrt(Math.pow(com[0] - point[0], 2) + Math.pow(com[1] - point[1], 2));

				// Проверяем, находится ли компонент в пределах maxLen
				if (distance <= maxLen) {
					closestComponents.add(com);
				}
			}

			return closestComponents;
		}

		public static void main(String[] args) {
			List<int[]> components = new ArrayList<>();
			components.add(new int[]{10, 10});
			components.add(new int[]{20, 20});
			components.add(new int[]{30, 30});

			int[] point = {13, 13};
			int maxLen = 5;

			List<int[]> result = findClosestComponents(components, point, maxLen);

			// Выводим результаты
			for (int[] com : result) {
				System.out.println("Closest Component: (" + com[0] + ", " + com[1] + ")");
			}
		}
	}

	public static class GridAligner {

		public static List<int[]> alignToGrid(List<int[]> coms, int gridpx) {
			List<int[]> alignedComponents = new ArrayList<>();

			for (int[] com : coms) {
				// Вычисляем новые координаты с учетом ширины сетки
				int alignedX = (int) Math.round((double) com[0] / gridpx) * gridpx;
				int alignedY = (int) Math.round((double) com[1] / gridpx) * gridpx;

				// Добавляем выровненные координаты в новый список
//				alignedComponents.add(new int[]{alignedX, alignedY});
				com[0] = alignedX;
				com[1] = alignedY;
			}

			return alignedComponents;
		}

		public static void main(String[] args) {
			List<int[]> components = new ArrayList<>();
			components.add(new int[]{12, 15});
			components.add(new int[]{25, 30});
			components.add(new int[]{37, 45});

			int gridpx = 10;

			List<int[]> result = alignToGrid(components, gridpx);

			// Выводим результаты
			for (int[] com : result) {
				System.out.println("Aligned Component: (" + com[0] + ", " + com[1] + ")");
			}
		}
	}

	public static class OffsetAdder {

		public static List<int[]> addOffset(List<int[]> coms, int[] offset) {
			List<int[]> updatedComponents = new ArrayList<>();

			for (int[] com : coms) {
				// Проверяем, что смещение имеет длину 2
				if (offset.length != 2) {
					throw new IllegalArgumentException("Offset array must have exactly two elements for x and y.");
				}

				// Добавляем смещение к координатам
				int updatedX = com[0] + offset[0];
				int updatedY = com[1] + offset[1];

				// Добавляем обновленные координаты в новый список
//				updatedComponents.add(new int[]{updatedX, updatedY});
				com[0] = updatedX;
				com[1] = updatedY;
			}

			return updatedComponents;
		}

		public static void main(String[] args) {
			List<int[]> components = new ArrayList<>();
			components.add(new int[]{10, 20});
			components.add(new int[]{30, 40});
			components.add(new int[]{50, 60});

			int[] offset = {5, -10};

			List<int[]> result = addOffset(components, offset);

			// Выводим результаты
			for (int[] com : result) {
				System.out.println("Updated Component: (" + com[0] + ", " + com[1] + ")");
			}
		}
	}
}
