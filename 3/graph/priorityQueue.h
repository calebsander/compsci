#include <stdbool.h>
#include "vertex.h"

typedef struct vertexDistance VertexDistance;
typedef struct priorityQueue PriorityQueue;

PriorityQueue *makeEmptyPriorityQueue();
VertexDistance *makeVertexDistance(Vertex *vertex, int distance);
Vertex *getVertex(VertexDistance *item);
int getDistance(VertexDistance *item);
bool isEmptyQueue(PriorityQueue *queue);
void enqueue(PriorityQueue *queue, VertexDistance *item);
VertexDistance *dequeue(PriorityQueue *queue);
void freePriorityQueue(PriorityQueue *queue);
unsigned int queueSize(PriorityQueue *queue);