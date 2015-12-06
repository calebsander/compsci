#include <stdio.h>
#include "deque.h"

int main() {
	Deque *deque = makeEmptyDeque();
	printf("Empty: %d\n", isEmptyDeque(deque));
	pushFront(1, deque);
	printf("Empty: %d\n", isEmptyDeque(deque));
	pushBack(10, deque);
	pushBack(10, deque);
	printf("Front: %d\n", popFront(deque));
	printf("Front: %d\n", popFront(deque));
	printf("Front: %d\n", popFront(deque));
	printf("Empty: %d\n", isEmptyDeque(deque));
	pushFront(1, deque);
	pushBack(10, deque);
	pushBack(10, deque);
	printf("Back: %d\n", popBack(deque));
	printf("Back: %d\n", popBack(deque));
	printf("Back: %d\n", popBack(deque));
	pushBack(-1, deque);
	pushFront(1, deque);
	freeDeque(deque);
}