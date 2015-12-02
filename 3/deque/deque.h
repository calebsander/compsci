#include <stdbool.h>

typedef struct deque Deque;

Deque *makeEmptyDeque();
bool isEmptyDeque(Deque *deque);
void freeDeque(Deque *deque);
void pushFront(int element, Deque *deque);
void pushBack(int element, Deque *deque);
int peekFront(Deque *deque);
int peekBack(Deque *deque);
int popFront(Deque *deque);
int popBack(Deque *deque);