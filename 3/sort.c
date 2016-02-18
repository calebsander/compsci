#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define SORT_ITEMS 100
#define SCRAMBLE_TIMES 10000

//Swap the elements at two indices of an array
void swap(int *elements, unsigned int index1, unsigned int index2) {
	const int temp = elements[index1];
	elements[index1] = elements[index2];
	elements[index2] = temp;
}

//malloc, fill, and scramble an array with the numbers from 1 to SORT_ITEMS inclusive
int *makeUnsorted() {
	int *array = malloc(sizeof(*array) * SORT_ITEMS);
	for (unsigned int i = 0; i < SORT_ITEMS; i++) array[i] = i + 1;
	for (unsigned int i = 0; i < SCRAMBLE_TIMES; i++) swap(array, rand() % SORT_ITEMS, rand() % SORT_ITEMS);
	return array;
}

//Print out an array's elements
void printArray(int *array, unsigned int length) {
	for (unsigned int i = 0; i < length; i++) {
		printf("%d", array[i]);
		if (i + 1 != length) printf(", ");
	}
	putchar('\n');
}
//Sort an array in place using linear insertion sort
void insertionSort(int *array, unsigned int length) {
	for (unsigned int i = 0; i < length; i++) { //i is the index of the item currently being inserted (i have been inserted before)
		const int beingInserted = array[i];
		unsigned int insertIndex = 0;
		while (insertIndex < i && array[insertIndex] < beingInserted) insertIndex++; //find the correct place to insert the new element
		memmove(array + insertIndex + 1, array + insertIndex, (i - insertIndex) * sizeof(*array)); //shift remaining sorted elements right one
		array[insertIndex] = beingInserted;
	}
}
//Sort an array in place using binary insertion sort
void binaryInsertionSort(int *array, unsigned int length) {
	for (unsigned int i = 0; i < length; i++) { //i is the index of the item currently being inserted (i have been inserted before)
		const int beingInserted = array[i];
		unsigned int minIndex = 0, maxIndex = i + 1; //inclusive, exclusive
		unsigned int insertIndex;
		while (true) { //keep going until locating the insertion point or finding an equal place
			if (maxIndex == minIndex + 1) { //only one slot left
				insertIndex = minIndex;
				break;
			}
			insertIndex = minIndex + (maxIndex - 1 - minIndex) / 2;
			if (array[insertIndex] == beingInserted) break; //we can insert here since it's equal
			if (array[insertIndex] < beingInserted) minIndex = insertIndex + 1; //we are too early
			else maxIndex = insertIndex + 1; //array[insertIndex] > beingSorted => we are too late
		}
		memmove(array + insertIndex + 1, array + insertIndex, (i - insertIndex) * sizeof(*array)); //see insertionSort()
		array[insertIndex] = beingInserted;
	}
}
//Sort an array in place using selection sort
void selectionSort(int *array, unsigned int length) {
	for (unsigned int i = 0; i < length; i++) { //i is the index of the first unsorted element
		int min, minIndex;
		for (unsigned int selectIndex = i; selectIndex < length; selectIndex++) {
			if (selectIndex == i || array[selectIndex] < min) {
				min = array[selectIndex];
				minIndex = selectIndex;
			}
		}
		swap(array, minIndex, i);
	}
}
//malloc space for and fill an array merging two sorted arrays
int *mergeSortedArrays(int *array1, int *array2, unsigned int length1, unsigned int length2) {
	const unsigned int newLength = length1 + length2;
	int *newArray = malloc(sizeof(*newArray) * newLength);
	unsigned int newIndex = 0;
	unsigned int i1 = 0, i2 = 0;
	while (newIndex < newLength) {
		if (i1 < length1) {
			if (i2 < length2) { //elements in both, so pick min
				if (array1[i1] < array2[i2]) {
					newArray[newIndex] = array1[i1];
					i1++;
				}
				else {
					newArray[newIndex] = array2[i2];
					i2++;
				}
			}
			else { //no elements remaining in 2, so insert from 1
				newArray[newIndex] = array1[i1];
				i1++;
			}
		}
		else { //no elements remaining in 1, so insert from 2
			newArray[newIndex] = array2[i2];
			i2++;
		}
		newIndex++;
	}
	return newArray;
}
//Returns whether an array is sorted
bool isSorted(int *array, unsigned int length) {
	for (unsigned int i = 1; i < length; i++) {
		if (array[i - 1] > array[i]) return false;
	}
	return true;
}

int main() { //won't write anything to stderr unless there is a problem
	srand(time(NULL));
	int *forInsertion = makeUnsorted();
	puts("Insertion");
	printArray(forInsertion, SORT_ITEMS);
	insertionSort(forInsertion, SORT_ITEMS);
	printArray(forInsertion, SORT_ITEMS);
	assert(isSorted(forInsertion, SORT_ITEMS));
	free(forInsertion);
	int *forBinaryInsertion = makeUnsorted();
	puts("\nBinary Insertion");
	printArray(forBinaryInsertion, SORT_ITEMS);
	binaryInsertionSort(forBinaryInsertion, SORT_ITEMS);
	printArray(forBinaryInsertion, SORT_ITEMS);
	assert(isSorted(forBinaryInsertion, SORT_ITEMS));
	free(forBinaryInsertion);
	int *forSelection = makeUnsorted();
	puts("\nSelection");
	printArray(forSelection, SORT_ITEMS);
	selectionSort(forSelection, SORT_ITEMS);
	printArray(forSelection, SORT_ITEMS);
	assert(isSorted(forSelection, SORT_ITEMS));
	free(forSelection);
	int *merge1 = makeUnsorted();
	for (unsigned int i = 0; i < SORT_ITEMS; i++) merge1[i] += 50;
	int *merge2 = makeUnsorted();
	for (unsigned int i = 0; i < SORT_ITEMS; i++) merge2[i] <<= 1;
	insertionSort(merge1, SORT_ITEMS);
	insertionSort(merge2, SORT_ITEMS);
	int *merged = mergeSortedArrays(merge1, merge2, SORT_ITEMS, SORT_ITEMS);
	puts("\nMerged");
	printArray(merge1, SORT_ITEMS);
	printArray(merge2, SORT_ITEMS);
	printArray(merged, SORT_ITEMS * 2);
	assert(isSorted(merged, SORT_ITEMS * 2));
	free(merge1);
	free(merge2);
	free(merged);
}