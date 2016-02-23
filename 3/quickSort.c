#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
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
//Returns whether an array is sorted
bool isSorted(int *array, unsigned int length) {
	for (int i = 1; i < length; i++) {
		if (array[i - 1] > array[i]) return false;
	}
	return true;
}
//Print out an array's elements
void printArray(int *array, unsigned int length) {
	for (unsigned int i = 0; i < length; i++) {
		printf("%d", array[i]);
		if (i + 1 != length) printf(", ");
	}
	putchar('\n');
}
//Sorts an array using quicksort
void quicksort(int *array, unsigned int start, unsigned int end) {
	if (start + 1 < end) { //more than one element remaining
		const int pivot = array[start]; //use the first element as a pivot
		unsigned int startSearch = start;
		unsigned int endSearch = end;
		while (true) {
			for (startSearch++; startSearch < end && array[startSearch] <= pivot; startSearch++); //find the earliest element greater than the pivot
			for (endSearch--; endSearch > start && pivot <= array[endSearch]; endSearch--); //find the latest element less than the pivot
			if (startSearch >= endSearch) break; //if they have passed each other, we are done
			swap(array, startSearch, endSearch); //swap them so they are in the correct order
		}
		swap(array, start, endSearch); //move pivot element to the between the last switched elements
		//Recursively sort the subarray on each side
		quicksort(array, start, endSearch);
		quicksort(array, endSearch + 1, end);
	}
}
void sort(int *array, unsigned int length) {
	quicksort(array, 0, length);
}

int main() {
	srand(time(NULL));
	int *array = makeUnsorted();
	printArray(array, SORT_ITEMS);
	sort(array, SORT_ITEMS);
	printArray(array, SORT_ITEMS);
	assert(isSorted(array, SORT_ITEMS));
	free(array);
	int *notIndistinctArray = makeUnsorted();
	for (unsigned int i = 0; i < SORT_ITEMS; i++) notIndistinctArray[i] /= 2;
	printArray(notIndistinctArray, SORT_ITEMS);
	sort(notIndistinctArray, SORT_ITEMS);
	printArray(notIndistinctArray, SORT_ITEMS);
	assert(isSorted(notIndistinctArray, SORT_ITEMS));
	free(notIndistinctArray);
}