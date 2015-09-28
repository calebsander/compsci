//Useful for making sure that programs don't excede allowed line length

#include <stdio.h>

#define MAX_LINE_LENGTH 80
void checkLineLength(int length, int line) {
	//printf("%d\n", length);
	if (length > MAX_LINE_LENGTH) printf("!!!%d\n", line);
}

int main() {
	int lineLength = 0;
	int lineCount = 1;
	char inputChar;
	while ((inputChar = getchar()) != EOF) {
		if (inputChar == '\n') {
			checkLineLength(lineLength, lineCount);
			lineLength = 0;
			lineCount++;
		}
		else lineLength++;
	}
	checkLineLength(lineLength, lineCount);
}