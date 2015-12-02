#include <stdio.h>
#include <stdlib.h>
#include "deque.h"

#define DEFAULT_SIZE 100

struct deque {
	int *elements;
	unsigned int start, end;
};
/*
	Stored elements begin at index start and finish at index end,
	wrapping around if end < start; can store at most DEFAULT_SIZE - 1 elements
*/

Deque *makeEmptyDeque() {
	Deque *deque = malloc(sizeof(*deque));
	deque->elements = malloc(sizeof(*(deque->elements)) * DEFAULT_SIZE);
	deque->start = deque->end = 0;
	return deque;
}

void addWrap(unsigned int *value) {
	*value = (*value + 1) % DEFAULT_SIZE;
}
void subtractWrap(unsigned int *value) {
	*value = (*value + DEFAULT_SIZE - 1) % DEFAULT_SIZE; //make sure we don't have overflow and correct for % not really doing modulus
}
bool isEmptyDeque(Deque *deque) {
	return deque->start == deque->end;
}
void pushFront(int element, Deque *deque) {
	subtractWrap(&(deque->start));
	deque->elements[deque->start] = element;
}
void pushBack(int element, Deque *deque) {
	deque->elements[deque->end] = element;
	addWrap(&(deque->end));
}
void errorIfEmpty(Deque *deque) {
	if (isEmptyDeque(deque)) {
		fputs("Cannot peek on empty deque\n", stderr);
		exit(EXIT_FAILURE);
	}
}
int peekFront(Deque *deque) {
	errorIfEmpty(deque);
	return deque->elements[deque->start];
}
int peekBack(Deque *deque) {
	errorIfEmpty(deque);
	return deque->elements[deque->end - 1];
}
int popFront(Deque *deque) {
	const int value = peekFront(deque);
	addWrap(&(deque->start));
	return value;
}
int popBack(Deque *deque) {
	const int value = peekBack(deque);
	subtractWrap(&(deque->end));
	return value;
}
void freeDeque(Deque *deque) {
	free(deque->elements);
	free(deque);
}