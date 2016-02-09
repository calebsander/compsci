#include <assert.h>
#include <stdlib.h>
#include <time.h>
#include "priorityQueue.h"

#define QUEUE_ITEMS 20
int main() {
	PriorityQueue *queue = makeEmptyPriorityQueue();
	int itemsToSort[QUEUE_ITEMS];
	for (unsigned int i = 0; i < QUEUE_ITEMS; i++) itemsToSort[i] = i << 1;
	srand(time(NULL));
	for (unsigned int i = 0; i < 1000; i++) swap(itemsToSort, rand() % QUEUE_ITEMS, rand() % QUEUE_ITEMS); //scramble the set of items by swapping 1000 randomly-chosen pairs
	for (unsigned int i = 0; i < QUEUE_ITEMS; i++) {
		enqueue(queue, itemsToSort[i]);
		assert(validOrdering(queue));
	}
	assert(queueSize(queue) == QUEUE_ITEMS);
	printQueue(queue);
	for (unsigned int i = 0; i < QUEUE_ITEMS; i++) {
		assert(dequeue(queue) == (19 - i) << 1);
		assert(queueSize(queue) == QUEUE_ITEMS - 1 - i);
	}
	freePriorityQueue(queue);
}