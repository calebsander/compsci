#include <stdbool.h>
#include "position.h"

typedef struct queue Queue;

Queue *makeEmptyQueue();
bool isEmpty(Queue *queue);
void freeQueue(Queue *queue);
void push(Queue *queue, Position *element);
Position *peek(Queue *queue);
Position *pop(Queue *queue);
