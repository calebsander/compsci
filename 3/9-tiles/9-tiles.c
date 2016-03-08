#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include "queue.h"
#include "hashMap.h"
#include "position.h"

#define HEIGHT_ARG 1
#define WIDTH_ARG 2
#define MAX_LENGTH_ARG (argc - 3)
#define INITIAL_ARG (argc - 2)
#define GOAL_ARG (argc - 1)
#define MIN_DIMENSION 2
#define DEFAULT_DIMENSION 3

void newPosition(HashMap *map, Queue *searchQueue, Position *moved, Position *from, const unsigned short currentLength) {
	if (contains(map, moved)) freePosition(moved);
	else {
		put(map, moved, from, currentLength + 1);
		push(searchQueue, moved);
	}
}
int main(int argc, char **argv) {
	int height, width;
	if (argc == 4) height = width = DEFAULT_DIMENSION; //no dimensions specified
	else if (argc == 6) { //dimensions specified
		height = atoi(argv[HEIGHT_ARG]);
		width = atoi(argv[WIDTH_ARG]);
		if (height < MIN_DIMENSION || width < MIN_DIMENSION) {
			fputs("Height and width must be at least 2\n", stderr);
			exit(1);
		}
	}
	else {
		fputs("Invalid argument syntax\n", stderr);
		exit(1);
	}
	setDimensions((unsigned int)height, (unsigned int)width);
	int maxLength = atoi(argv[MAX_LENGTH_ARG]);
	if (maxLength < 1) {
		fputs("MAXLENGTH must be positive\n", stderr);
		exit(1);
	}
	Position *initial = parsePosition(argv[INITIAL_ARG]), *goal = parsePosition(argv[GOAL_ARG]);
	Queue *searchQueue = makeEmptyQueue();
	HashMap *map = makeEmptyMap();
	push(searchQueue, goal);
	put(map, goal, NULL, 0);
	Position *newInitial = NULL;
	while (!isEmpty(searchQueue)) {
		Position *p = pop(searchQueue);
		if (equals(p, initial)) {
			assert(contains(map, p));
			newInitial = p;
			break;
		}
		else {
			const unsigned short currentLength = getLength(map, p);
			if (currentLength < maxLength) {
				const unsigned char possibilities = possibleMoves(p);
				if (up(possibilities)) newPosition(map, searchQueue, moveUp(p), p, currentLength);
				if (left(possibilities)) newPosition(map, searchQueue, moveLeft(p), p, currentLength);
				if (down(possibilities)) newPosition(map, searchQueue, moveDown(p), p, currentLength);
				if (right(possibilities)) newPosition(map, searchQueue, moveRight(p), p, currentLength);

			}
		}
	}
	if (contains(map, initial)) {
		for (Position *lookup = initial; lookup; lookup = getFrom(map, lookup)) printPosition(lookup);
	}
	freeEntries(map, initial, goal);
	if (newInitial) freePosition(newInitial); //position equivalent to initial exists in HashMap so must be explicitly deleted
	free(initial);
	free(goal);
	freeQueue(searchQueue);
	freeMap(map);
}
