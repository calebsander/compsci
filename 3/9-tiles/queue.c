#include <stdio.h>
#include <stdlib.h>
#include "queue.h"

#define DEFAULT_SIZE 1024

struct queue {
	Position **elements;
	unsigned int start, end;
	unsigned int allocatedSize;
};
/*
	Stored elements begin at index start and finish at index end,
	wrapping around if end < start; can store at most queue->allocatedSize - 1 elements
*/

Queue *makeEmptyQueueOfSize(unsigned int size) {
	Queue *queue = malloc(sizeof(*queue));
	queue->elements = malloc(sizeof(*(queue->elements)) * size);
	queue->start = queue->end = 0;
	queue->allocatedSize = size;
	return queue;
}
Queue *makeEmptyQueue() {
	return makeEmptyQueueOfSize(DEFAULT_SIZE);
}

void resizeQueue(Queue *queue) {
	Queue *newQueue = makeEmptyQueueOfSize(queue->allocatedSize << 1);
	while (!isEmpty(queue)) push(newQueue, pop(queue));
	free(queue->elements);
	queue->elements = newQueue->elements;
	queue->start = newQueue->start;
	queue->end = newQueue->end;
	queue->allocatedSize = newQueue->allocatedSize;
	free(newQueue);
}
void addWrap(unsigned int *value, Queue *queue) {
	*value = (*value + 1) % queue->allocatedSize;
}
bool isEmpty(Queue *queue) {
	return queue->start == queue->end;
}
void push(Queue *queue, Position *element) {
	unsigned int end = queue->end;
	addWrap(&end, queue);
	if (end == queue->start) resizeQueue(queue); //queue cannot fit another element
	queue->elements[queue->end] = element;
	addWrap(&(queue->end), queue);
}
void errorIfEmpty(Queue *queue) {
	if (isEmpty(queue)) {
		fputs("Cannot peek on empty queue\n", stderr);
		exit(EXIT_FAILURE);
	}
}
Position *peek(Queue *queue) {
	errorIfEmpty(queue);
	return queue->elements[queue->start];
}
Position *pop(Queue *queue) {
	Position *value = peek(queue);
	addWrap(&(queue->start), queue);
	return value;
}
void freeQueue(Queue *queue) {
	free(queue->elements);
	free(queue);
}
