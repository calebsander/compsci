#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include "priorityQueue.h"

#define DEFAULT_CAPACITY 10
struct priorityQueue {
	int *elements;
	unsigned int size;
	unsigned int maxSize;
};

void allocateElements(PriorityQueue *array) {
	array->elements = realloc(array->elements, sizeof(*(array->elements)) * array->maxSize);
}
void push(PriorityQueue *array, int element) {
	if (array->size == array->maxSize) {
		array->maxSize *= 2;
		allocateElements(array);
	}
	array->elements[array->size] = element;
	array->size++;
}
void pop(PriorityQueue *array) {
	array->size--;
}
bool arrayEquals(PriorityQueue *array1, PriorityQueue *array2) {
	if (array1->size != array2->size) return false;
	else {
		const unsigned int count = array1->size;
		const int *one = array1->elements;
		const int *two = array2->elements;
		for (unsigned int i = 0; i < count; i++) {
			if (one[i] != two[i]) return false;
		}
		return true;
	}
}
void freePriorityQueue(PriorityQueue *array) {
	free(array->elements);
	free(array);
}
void swap(int *elements, unsigned int index1, unsigned int index2) {
	const int temp = elements[index1];
	elements[index1] = elements[index2];
	elements[index2] = temp;
}

PriorityQueue *makeEmptyPriorityQueue() {
	PriorityQueue *newArray = malloc(sizeof(*newArray));
	newArray->maxSize = DEFAULT_CAPACITY;
	newArray->elements = NULL;
	allocateElements(newArray);
	newArray->size = 0;
	return newArray;
}
bool isEmpty(PriorityQueue *queue) {
	return !queue->size;
}
unsigned int getRightChild(unsigned int i);
bool isLeaf(unsigned int i, unsigned int length) {
	return length < getRightChild(i);
}
bool hasRightChild(unsigned int i, unsigned int length) {
	return length > getRightChild(i);
}
unsigned int getLeftChild(unsigned int i) {
	return (i << 1) + 1;
}
unsigned int getRightChild(unsigned int i) {
	return (i << 1) + 2;
}
unsigned int getAncestor(unsigned int i, unsigned int n) {
	return ((i + 1) >> n) - 1;
}
unsigned int getParent(unsigned int i) {
	return getAncestor(i, 1);
}
void enqueue(PriorityQueue *queue, int item) {
	push(queue, item);
	unsigned int currentPosition = queue->size - 1;
	while (currentPosition) {
		if (queue->elements[currentPosition] <= queue->elements[getParent(currentPosition)]) break;
		swap(queue->elements, currentPosition, getParent(currentPosition));
		currentPosition = getParent(currentPosition);
	}
}
int peek(PriorityQueue *queue) {
	return queue->elements[0];
}
int dequeue(PriorityQueue *queue) {
	const int result = peek(queue);
	swap(queue->elements, 0, queue->size - 1);
	pop(queue);
	unsigned int currentPosition = 0;
	while (!isLeaf(currentPosition, queue->size)) {
		const bool greaterThanLeft = queue->elements[getLeftChild(currentPosition)] <= queue->elements[currentPosition];
		const bool greaterThanRight = !hasRightChild(currentPosition, queue->size) || queue->elements[getRightChild(currentPosition)] <= queue->elements[currentPosition];
		if (greaterThanLeft && greaterThanRight) break;
		unsigned int maxChild;
		if (!hasRightChild(currentPosition, queue->size) || queue->elements[getLeftChild(currentPosition)] > queue->elements[getRightChild(currentPosition)]) maxChild = getLeftChild(currentPosition);
		else maxChild = getRightChild(currentPosition);
		swap(queue->elements, currentPosition, maxChild);
		currentPosition = maxChild;
	}
	return result;
}

void printSubTree(PriorityQueue *queue, unsigned int position) {
	if (!(position && isLeaf(position, queue->size))) {
		printf("%d -> ", queue->elements[position]);
		if (!isLeaf(position, queue->size)) {
			printf("%d", queue->elements[getLeftChild(position)]);
			if (hasRightChild(position, queue->size)) {
				printf(" %d\n", queue->elements[getRightChild(position)]);
				printSubTree(queue, getRightChild(position));
			}
			else putchar('\n');
			printSubTree(queue, getLeftChild(position));
		}
	}
}
void printQueue(PriorityQueue *queue) {
	printSubTree(queue, 0);
}
bool validSubTree(PriorityQueue *queue, unsigned int position) {
	if (isLeaf(position, queue->size)) return true;
	if (queue->elements[position] < queue->elements[getLeftChild(position)]) return false;
	if (!validSubTree(queue, getLeftChild(position))) return false;
	if (hasRightChild(position, queue->size)) {
		if (queue->elements[position] < queue->elements[getRightChild(position)]) return false;
		if (!validSubTree(queue, getRightChild(position))) return false;
	}
	return true;
}
bool validOrdering(PriorityQueue *queue) {
	return validSubTree(queue, 0);
}
unsigned int queueSize(PriorityQueue *queue) {
	return queue->size;
}