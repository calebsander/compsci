#include <stdio.h>
#include <stdlib.h>
#include "queue.h"
#include "position.h"

#define HEIGHT_ARG 1
#define WIDTH_ARG 2
#define MAX_LENGTH_ARG (argc - 3)
#define INITIAL_ARG (argc - 2)
#define GOAL_ARG (argc - 1)
#define MIN_DIMENSION 2
#define DEFAULT_DIMENSION 3

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
	Position *initial = parsePosition(argv[INITIAL_ARG]), *goal = parsePosition(argv[GOAL_ARG]);
	free(initial);
	free(goal);
}