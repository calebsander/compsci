#include <stdio.h>

/*ungetchar(char c) {
	unget(c, stdin) // Unread char read from stdin
}*/
typedef char boolean;
#define FALSE 0
#define TRUE  1

//What we are currently inside of
boolean blockComment = FALSE;
boolean lineComment = FALSE;
boolean string = FALSE;
boolean character = FALSE;

char nextChar;
void process(char inputChar) {
	switch (inputChar) {
		case '<':
			printf("&lt;");
			break;
		case '>':
			printf("&gt;");
			break;
		case '&':
			printf("&amp;");
			break;
		case '\\':
			putchar(inputChar);
			putchar(getchar()); //skip checking the next character because it is escaped
			break;
		case '"': //start or end of string
			if (character) putchar(inputChar);
			else {
				string = !string;
				if (string) {
					printf("<b>");
					putchar(inputChar);
				}
				else {
					putchar(inputChar);
					printf("</b>");
				}
			}
			break;
		case '\'':
			if (string) putchar(inputChar);
			else {
				putchar(inputChar);
				character = !character;
			}
			break;
		case '/':
			nextChar = getchar();
			if (nextChar == '/') {
				if (!lineComment) { //can't do another line comment on the same line
					printf("<i>");
					lineComment = TRUE;
				}
				putchar(inputChar);
				putchar(nextChar);
			}
			else if (nextChar == '*' && !blockComment) { //if we are already in a block comment, make sure to check to see whether the '*' is part of the end signature (process again)
				printf("<i>");
				putchar(inputChar);
				putchar(nextChar);
				blockComment = TRUE;
			}
			else {
				putchar(inputChar);
				process(nextChar);
			}
			break;
		case '*':
			nextChar = getchar();
			if (nextChar == '/' && blockComment) { //blockComment should never be false when this happens, but try to handle bad entry gracefully (no unmatched </i>)
				putchar(inputChar);
				putchar(nextChar);
				printf("</i>");
				blockComment = FALSE;
			}
			else {
				putchar(inputChar);
				process(nextChar);
			}
			break;
		case '\n': //check for end of line comment
			if (lineComment) {
				printf("</i>");
				lineComment = FALSE;
			}
			putchar(inputChar);
			break;
		default:
			putchar(inputChar);
	}
}

main() {
	printf("<pre>\n");
	char inputChar;
	while ((inputChar = getchar()) != EOF) process(inputChar);
	printf("\n</pre>\n");
}