#include <stdbool.h>

typedef struct priorityQueue PriorityQueue;

PriorityQueue *makeEmptyPriorityQueue();
bool isEmpty(PriorityQueue *queue);
void enqueue(PriorityQueue *queue, int item);
int peek(PriorityQueue *queue);
int dequeue(PriorityQueue *queue);
void printQueue(PriorityQueue *queue);
bool validOrdering(PriorityQueue *queue);
void freePriorityQueue(PriorityQueue *array);
unsigned int queueSize(PriorityQueue *queue);
void swap(int *elements, unsigned int index1, unsigned int index2);