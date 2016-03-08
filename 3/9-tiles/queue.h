#include <stdbool.h>
#include "position.h"

typedef struct queue Queue;

Queue *makeEmptyQueue();
bool isEmptyQueue(Queue *queue);
void freeQueue(Queue *queue);
void push(Position *element, Queue *queue);
Position *peek(Queue *queue);
Position *pop(Queue *queue);