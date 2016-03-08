#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include "position.h"

#define EMPTY '-'

unsigned int height = 0, width = 0;

struct position {
	char *values;
};

void setDimensions(unsigned int newHeight, unsigned int newWidth) {
	height = newHeight;
	width = newWidth;
}
Position *parsePosition(char *positionString) {
	if (strlen(positionString) == height * width) {
		unsigned int dashCount = 0;
		char *positionChar = positionString;
		for (; *positionChar; positionChar++) {
			if (*positionChar == EMPTY) dashCount++;
		}
		if (dashCount == 1) {
			Position *position = malloc(sizeof(*position));
			position->values = positionString;
			return position;
		}
		else {
			printf("%s\n", positionString);
			fputs("There must be exactly one '-' in the input string\n", stderr);
			exit(1);
		}
	}
	else {
		fputs("Position string has wrong size\n", stderr);
		exit(1);
	}
}
unsigned int hashCode(Position *position) {
	const char *s = position->values;
	unsigned long sum;
        const long prime = 3141592661;

        for (sum = 0; *s; s++) sum = (sum << 4) ^ *s;
        return (unsigned int)(prime * sum);
}
bool equals(Position *position1, Position *position2) {
	return !strcmp(position1->values, position2->values);
}
unsigned int rawIndex(unsigned int y, unsigned int x) {
	assert(y < height && x < width);
	return y * height + x;
}
#define UP 0
#define LEFT 1
#define DOWN 2
#define RIGHT 3
typedef struct {
	unsigned int x, y;
} Index;
Index *findEmpty(Position *position) {
	const char *values = position->values;
	for (unsigned int y = 0; y < height; y++) {
		for (unsigned int x = 0; x < width; x++) {
			if (values[rawIndex(y, x)] == EMPTY) {
				Index *index = malloc(sizeof(*index));
				index->x = x;
				index->y = y;
				return index;
			}
		}
	}
	assert(false);
}
unsigned char possibleMoves(Position *position) { //returns a bitmask of the possible directions the empty square could go
	Index *emptyIndex = findEmpty(position);
	const unsigned int x = emptyIndex->x;
	const unsigned int y = emptyIndex->y;
	free(emptyIndex);
	return (unsigned char)(((y != 0) << UP) | ((x != 0) << LEFT) | ((y != height - 1) << DOWN) | ((x != width - 1) << RIGHT));
	assert(false);
}
bool up(unsigned char possibilities) {
	return possibilities & (1 << UP);
}
bool left(unsigned char possibilities) {
	return possibilities & (1 << LEFT);
}
bool down(unsigned char possibilities) {
	return possibilities & (1 << DOWN);
}
bool right(unsigned char possibilities) {
	return possibilities & (1 << RIGHT);
}
char *strdup(char *orig) {
	char *newString = malloc(sizeof(*newString) * (strlen(orig) + 1));
	strcpy(newString, orig);
	return newString;
}
Position *moveUp(Position *position) {
	Index *emptyIndex = findEmpty(position);
	char *newPositionString = strdup(position->values);
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x)] = newPositionString[rawIndex(emptyIndex->y - 1, emptyIndex->x)];
	newPositionString[rawIndex(emptyIndex->y - 1, emptyIndex->x)] = EMPTY;
	free(emptyIndex);
	return parsePosition(newPositionString);
}
Position *moveLeft(Position *position) {
	Index *emptyIndex = findEmpty(position);
	char *newPositionString = strdup(position->values);
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x)] = newPositionString[rawIndex(emptyIndex->y, emptyIndex->x - 1)];
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x - 1)] = EMPTY;
	free(emptyIndex);
	return parsePosition(newPositionString);
}
Position *moveDown(Position *position) {
	Index *emptyIndex = findEmpty(position);
	char *newPositionString = strdup(position->values);
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x)] = newPositionString[rawIndex(emptyIndex->y + 1, emptyIndex->x)];
	newPositionString[rawIndex(emptyIndex->y + 1, emptyIndex->x)] = EMPTY;
	free(emptyIndex);
	return parsePosition(newPositionString);
}
Position *moveRight(Position *position) {
	Index *emptyIndex = findEmpty(position);
	char *newPositionString = strdup(position->values);
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x)] = newPositionString[rawIndex(emptyIndex->y, emptyIndex->x + 1)];
	newPositionString[rawIndex(emptyIndex->y, emptyIndex->x + 1)] = EMPTY;
	free(emptyIndex);
	return parsePosition(newPositionString);
}
void freePosition(Position *position) {
	free(position->values);
	free(position);
}
void printPosition(Position *position) {
	printf("%s\n", position->values);
}
