#ifndef __POSITION_H__
	#define __POSITION_H__
	#include <stdbool.h>

	typedef struct position Position;

	void setDimensions(unsigned int height, unsigned int width);
	Position *parsePosition(char *positionString);
	unsigned int hashCode(Position *position);
	bool equals(Position *position1, Position *position2);
	unsigned char possibleMoves(Position *position); //bits represent which of the 4 directions the '-' can be moved in
	unsigned char possibleMoves(Position *position);
	bool up(unsigned char possibilities);
	bool left(unsigned char possibilities);
	bool down(unsigned char possibilities);
	bool right(unsigned char possibilities);
	Position *moveUp(Position *position);
	Position *moveLeft(Position *position);
	Position *moveDown(Position *position);
	Position *moveRight(Position *position);
	void freePosition(Position *position);
	void printPosition(Position *position);
	char *strdup(char *orig);
#endif
