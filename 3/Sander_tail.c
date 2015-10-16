/*
	Caleb Sander
	10/09/2015
	tail.c
	Prints out the last n lines of input
*/

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

int getDigit(char c) {
	if ('0' <= c && c <= '9') return c - '0';
	else {
		fputs("Invalid numerical value\n", stderr);
		exit(EXIT_FAILURE);
	}
}

bool gReachedEOF = false; //keeps track of whether the last line ended in an EOF (no more lines afterwards)
char *readLine(void) {
	if (gReachedEOF) return NULL;
	unsigned int length = 0; //length of string without '\0'
	char *currentLine = malloc((length + 1) * sizeof(char));
	char readChar;
	while ((readChar = getchar()) != EOF && readChar != '\n') { //keep reading until hitting the end of a line or EOF
		currentLine = realloc(currentLine, (++length + 1) * sizeof(char)); //allocate one more byte for the next character
		currentLine[length - 1] = readChar;
	}
	currentLine[length] = '\0';
	if (readChar == EOF) gReachedEOF = true;
	return currentLine;
}

int main(int argc, char **argv) {
	unsigned int linesToPrint = 10;
	char readChar;
	for (; *argv; ++argv) { //iterate over each argument (starting after the command)
		if (*((*argv)++) == '-') {
			linesToPrint = 0;
			for (; (readChar = **argv); (*argv)++) linesToPrint = linesToPrint * 10 + getDigit(readChar);
		}
	}
	char **storedLines = calloc(linesToPrint, sizeof(char*)); //so we can be sure unset lines are NULL
	char *nextLine;
	while ((nextLine = readLine())) { //run until there are no more lines
		free(storedLines[0]); //the first line recorded is no longer needed
		unsigned int i;
		for (i = 1; i < linesToPrint; i++) storedLines[i - 1] = storedLines[i]; //shift each line back (last -> second-to-last)
		storedLines[i - 1] = nextLine; //set the last line
	}
	for (int i = 0; i < linesToPrint; i++) { //print out all the lines and free them
		if (storedLines[i]) printf("%s\n", storedLines[i]);
		free(storedLines[i]);
	}
	free(storedLines);
}