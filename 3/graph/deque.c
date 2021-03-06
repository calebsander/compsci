#include <stdio.h>
#include <stdlib.h>
#include "deque.h"

typedef struct node Node;
struct node {
	DequeType payload;
	Node *prev, *next;
};
struct deque {
	Node *first, *last; //(!first) == (!last)
};

Deque *makeEmptyDeque() {
	Deque *deque = calloc(1, sizeof(*deque));
	return deque;
}

bool isEmptyDeque(Deque *deque) {
	return !deque->first;
}
void freeDeque(Deque *deque) {
	Node *toFree = deque->first;
	while (toFree) {
		Node *toFreeNext = toFree->next;
		free(toFree);
		toFree = toFreeNext;
	}
	free(deque);
}
Node *makeNode(DequeType payload) {
	Node *node = malloc(sizeof(*node));
	node->payload = payload;
	return node;
}
void pushFront(DequeType element, Deque *deque) {
	Node *newNode = makeNode(element);
	newNode->prev = NULL;
	newNode->next = deque->first;
	if (deque->last) deque->first->prev = newNode;
	else deque->last = newNode;
	deque->first = newNode;
}
void pushBack(DequeType element, Deque *deque) {
	Node *newNode = makeNode(element);
	newNode->prev = deque->last;
	newNode->next = NULL;
	if (deque->first) deque->last->next = newNode;
	else deque->first = newNode;
	deque->last = newNode;
}
void errorIfEmpty(Deque *deque) {
	if (isEmptyDeque(deque)) {
		fputs("Cannot peek on empty deque\n", stderr);
		exit(EXIT_FAILURE);
	}
}
DequeType peekFront(Deque *deque) {
	errorIfEmpty(deque);
	return deque->first->payload;
}
DequeType peekBack(Deque *deque) {
	errorIfEmpty(deque);
	return deque->last->payload;
}
DequeType popFront(Deque *deque) {
	DequeType result = peekFront(deque);
	if (deque->first->next) deque->first->next->prev = NULL;
	else deque->last = NULL;
	Node *toFree = deque->first;
	deque->first = toFree->next;
	free(toFree);
	return result;
}
DequeType popBack(Deque *deque) {
	DequeType result = peekBack(deque);
	if (deque->last->prev) deque->last->prev->next = NULL;
	else deque->first = NULL;
	Node *toFree = deque->last;
	deque->last = toFree->prev;
	free(toFree);
	return result;
}