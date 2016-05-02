#include <assert.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include "priorityQueue.h"

#define DEFAULT_CAPACITY 10
struct vertexDistance {
	Vertex *vertex;
	int distance;
};
struct priorityQueue {
	VertexDistance **elements;
	unsigned int size;
	unsigned int maxSize;
};

void allocateElements(PriorityQueue *queue) {
	queue->elements = realloc(queue->elements, sizeof(*(queue->elements)) * queue->maxSize);
}
void push(PriorityQueue *queue, VertexDistance *element) {
	if (queue->size == queue->maxSize) {
		queue->maxSize <<= 1;
		allocateElements(queue);
	}
	queue->elements[queue->size] = element;
	queue->size++;
}
void pop(PriorityQueue *queue) {
	queue->size--;
}
void freePriorityQueue(PriorityQueue *queue) {
	assert(isEmptyQueue(queue));
	free(queue->elements);
	free(queue);
}
void swap(VertexDistance **elements, unsigned int index1, unsigned int index2) {
	VertexDistance *temp = elements[index1];
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
VertexDistance *makeVertexDistance(Vertex *vertex, int distance) {
	VertexDistance *result = malloc(sizeof(*result));
	result->vertex = vertex;
	result->distance = distance;
	return result;
}
Vertex *getVertex(VertexDistance *item) {
	return item->vertex;
}
int getDistance(VertexDistance *item) {
	return item->distance;
}
bool higherPriority(VertexDistance *item1, VertexDistance *item2) {
	return item1->distance <= item2->distance;
}
bool isEmptyQueue(PriorityQueue *queue) {
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
void enqueue(PriorityQueue *queue, VertexDistance *item) {
	push(queue, item);
	unsigned int currentPosition = queue->size - 1;
	while (currentPosition) {
		if (higherPriority(queue->elements[getParent(currentPosition)], queue->elements[currentPosition])) break;
		swap(queue->elements, currentPosition, getParent(currentPosition));
		currentPosition = getParent(currentPosition);
	}
}
VertexDistance *peek(PriorityQueue *queue) {
	return queue->elements[0];
}
VertexDistance *dequeue(PriorityQueue *queue) {
	VertexDistance *result = peek(queue);
	swap(queue->elements, 0, queue->size - 1);
	pop(queue);
	unsigned int currentPosition = 0;
	while (!isLeaf(currentPosition, queue->size)) {
		const bool greaterThanLeft = higherPriority(queue->elements[currentPosition], queue->elements[getLeftChild(currentPosition)]);
		const bool greaterThanRight = !hasRightChild(currentPosition, queue->size) || higherPriority(queue->elements[currentPosition], queue->elements[getRightChild(currentPosition)]);
		if (greaterThanLeft && greaterThanRight) break;
		unsigned int maxChild;
		if (hasRightChild(currentPosition, queue->size)) {
			if (higherPriority(queue->elements[getLeftChild(currentPosition)], queue->elements[getRightChild(currentPosition)])) maxChild = getLeftChild(currentPosition);
			else maxChild = getRightChild(currentPosition);
		}
		else maxChild = getLeftChild(currentPosition);
		swap(queue->elements, currentPosition, maxChild);
		currentPosition = maxChild;
	}
	return result;
}

unsigned int queueSize(PriorityQueue *queue) {
	return queue->size;
}