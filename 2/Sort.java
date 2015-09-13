import java.util.ArrayList;
import java.util.Date;

class Sort {
	public static void insertionSort(ArrayList<Integer> input) {
		Integer temp;
		for (int current = 0, previous, shift; current < input.size(); current++) {
			for (previous = 0; previous < current; previous++) {
				if (input.get(current).compareTo(input.get(previous)) < 0) {
					temp = input.get(current);
					for (shift = current; shift > previous; shift--) input.set(shift, input.get(shift - 1));
					input.set(previous, temp);
					break;
				}
			}
		}
	}
	public static void selectionSort(ArrayList<Integer> input) {
		int maxIndex;
		Integer temp;
		for (int current = 0, test; current < input.size(); current++) {
			maxIndex = current;
			for (test = current + 1; test < input.size(); test++) {
				if (input.get(test).compareTo(input.get(maxIndex)) < 0) maxIndex = test;
			}
			temp = input.get(maxIndex);
			input.set(maxIndex, input.get(current));
			input.set(current, temp);
		}
	}
	public static ArrayList<Integer> mergeSort(ArrayList<Integer> input) {
		if (input.size() == 1) return input;
		int splitIndex = input.size() / 2;
		ArrayList<Integer> firstList = mergeSort(new ArrayList<Integer>(input.subList(0, splitIndex)));
		ArrayList<Integer> secondList = mergeSort(new ArrayList<Integer>(input.subList(splitIndex, input.size())));
		ArrayList<Integer> mergedList = new ArrayList<Integer>(firstList.size() + secondList.size());
		int i = 0, j = 0;
		while (i < firstList.size() && j < secondList.size()) {
			if (firstList.get(i).compareTo(secondList.get(j)) > 0) {
				mergedList.add(secondList.get(j));
				j++;
			}
			else {
				mergedList.add(firstList.get(i));
				i++;
			}
		}
		while (i < firstList.size()) {
			mergedList.add(firstList.get(i));
			i++;
		}
		while (j < secondList.size()) {
			mergedList.add(secondList.get(j));
			j++;
		}
		return mergedList;
	}
	public static ArrayList<Integer> quickSort(ArrayList<Integer> input) {
		if (input.size() < 2) return input;
		int pivotIndex = input.size() / 2;
		ArrayList<Integer> lessList = new ArrayList<Integer>();
		ArrayList<Integer> moreList = new ArrayList<Integer>();
		for (int i = 0; i < input.size(); i++) {
			if (i == pivotIndex) continue;
			if (input.get(i).compareTo(input.get(pivotIndex)) > 0) moreList.add(input.get(i));
			else lessList.add(input.get(i));
		}
		lessList = quickSort(lessList);
		lessList.add(input.get(pivotIndex));
		lessList.addAll(quickSort(moreList));
		return lessList;
	}
	public static void bubbleSort(ArrayList<Integer> input) {
		boolean changed = true;
		Integer temp;
		int i;
		while (changed) {
			changed = false;
			for (i = 1; i < input.size(); i++) {
				if (input.get(i).compareTo(input.get(i - 1)) < 0) {
					temp = input.get(i);
					input.set(i, input.get(i - 1));
					input.set(i - 1, temp);
					changed = true;
				}
			}
		}
	}
	public static void main(String[] args) {
		final int elements = 100000;
		ArrayList<Integer> test = new ArrayList<Integer>(elements);
		for (int i = 0; i < elements; i++) test.add((int)(Math.random() * (float)elements));
		ArrayList<Integer> insertionTest = (ArrayList<Integer>)test.clone();
		ArrayList<Integer> selectionTest = (ArrayList<Integer>)test.clone();
		ArrayList<Integer> mergeTest = (ArrayList<Integer>)test.clone();
		ArrayList<Integer> quickTest = (ArrayList<Integer>)test.clone();
		ArrayList<Integer> bubbleTest = (ArrayList<Integer>)test.clone();
		long startTime = new Date().getTime();
		insertionSort(insertionTest);
		long endTime = new Date().getTime();
		long insertionTime = endTime - startTime;
		System.out.println("I");
		startTime = new Date().getTime();
		selectionSort(selectionTest);
		endTime = new Date().getTime();
		long selectionTime = endTime - startTime;
		System.out.println("S");
		startTime = new Date().getTime();
		mergeTest = mergeSort(mergeTest);
		endTime = new Date().getTime();
		long mergeTime = endTime - startTime;
		System.out.println("M");
		startTime = new Date().getTime();
		quickTest = quickSort(quickTest);
		endTime = new Date().getTime();
		long quickTime = endTime - startTime;
		System.out.println("Q");
		startTime = new Date().getTime();
		bubbleSort(bubbleTest);
		endTime = new Date().getTime();
		long bubbleTime = endTime - startTime;
		System.out.println("B");
		/*for (int i = 0; i < test.size(); i++) {
			System.out.print("O: ");
			System.out.print(test.get(i));
			System.out.print("\tI: ");
			System.out.print(insertionTest.get(i));
			System.out.print("\tS: ");
			System.out.print(selectionTest.get(i));
			System.out.print("\tM: ");
			System.out.print(mergeTest.get(i));
			System.out.print("\tQ: ");
			System.out.print(quickTest.get(i));
			System.out.print("\tB: ");
			System.out.println(bubbleTest.get(i));
		}*/
		System.out.print("I: ");
		System.out.print(insertionTime);
		System.out.print("  S: ");
		System.out.print(selectionTime);
		System.out.print("  M: ");
		System.out.print(mergeTime);
		System.out.print("  Q: ");
		System.out.print(quickTime);
		System.out.print("  B: ");
		System.out.println(bubbleTime);
	}
}