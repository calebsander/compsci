#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "position.h"

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
		Position *position = malloc(sizeof(*position));
		position->values = positionString;
		return position;
	}
	else {
		fputs("Position string has wrong size\n", stderr);
		exit(1);
		return NULL; //unreachable
	}
}