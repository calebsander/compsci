#ifndef DEQUE_INCLUDED
	#define DEQUE_INCLUDED
	#include <stdbool.h>
	#include "vertex.h"

	typedef Vertex *DequeType;
	typedef struct deque Deque;

	Deque *makeEmptyDeque();
	bool isEmptyDeque(Deque *deque);
	void freeDeque(Deque *deque);
	void pushFront(DequeType element, Deque *deque);
	void pushBack(DequeType element, Deque *deque);
	DequeType peekFront(Deque *deque);
	DequeType peekBack(Deque *deque);
	DequeType popFront(Deque *deque);
	DequeType popBack(Deque *deque);
#endif