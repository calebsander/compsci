#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "player.h"

bool gReachedEOF = false; //keeps track of whether the last line ended in an EOF (no more lines afterwards)
char *getLine(void) {
	if (gReachedEOF) return NULL;
	unsigned int length = 0; //length of string without '\0'
	unsigned int allocatedSize = 1;
	char *currentLine = malloc((allocatedSize) * sizeof(*currentLine));
	char readChar;
	while ((readChar = getchar()) != EOF && readChar != '\n') { //keep reading until hitting the end of a line or EOF
		if (length + 1 == allocatedSize) {
			allocatedSize *= 2;
			currentLine = realloc(currentLine, allocatedSize * sizeof(*currentLine)); //allocate one more byte for the next character
		}
		length++;
		currentLine[length - 1] = readChar;
	}
	currentLine[length] = '\0';
	if (readChar == EOF) gReachedEOF = true;
	return currentLine;
}

#define NUM_PLAYERS 2
int main() {
	puts("Welcome to Sticks!");
	Player players[NUM_PLAYERS];
	char *typeString;
	for (unsigned int i = 0; i < NUM_PLAYERS; i++) {
		printf("Enter name of player %d: ", i + 1);
		if (!(players[i].name = getLine())) return EXIT_FAILURE;
		printf("What type of player is ");
		printf("%s", players[i].name);
		printf("? ");
		typeString = getLine();
		players[i].type = playerTypeFromString(typeString);
		free(typeString);
	}
	printf("How many sticks should we start with? ");
	unsigned int sticksLeft;
	scanf("%u", &sticksLeft);
	unsigned int turn = 0;
	while (sticksLeft) {
		sticksLeft -= genNextMove(&players[turn % NUM_PLAYERS], sticksLeft);
		turn++;
	}
	printf("No more sticks! ");
	printf("%s", players[(turn + 1) % NUM_PLAYERS].name);
	printf(", you lose. Oops. ");
	printf("%s", players[turn % NUM_PLAYERS].name);
	puts(", you win. Yay!");
}