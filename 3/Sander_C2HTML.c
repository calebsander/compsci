#include <stdio.h>

typedef char boolean; //thanks, C
#define FALSE 0
#define TRUE  1

//Variables to keep track of what we are currently inside of
boolean blockComment = FALSE;
boolean lineComment = FALSE;
boolean string = FALSE;
boolean character = FALSE;

char nextChar;
void process(char inputChar) {
	switch (inputChar) {
		case '<': //must be escaped in HTML
			printf("&lt;");
			break;
		case '>': //see '<'
			printf("&gt;");
			break;
		case '&': //see '<'
			printf("&amp;");
			break;
		case '\\':
			putchar(inputChar);
			putchar(getchar()); //skip checking the next character because it is escaped
			break;
		case '"': //start or end of string (if not nested in a character)
			if (character) putchar(inputChar); //if in '"', then just print it normally
			else {
				string = !string; //we have either started or stopped a string
				if (string) {
					printf("<b>");
					putchar(inputChar);
				}
				else {
					putchar(inputChar); //make sure quotation mark goes inside the <b> tags
					printf("</b>");
				}
			}
			break;
		case '\'': //start or end of character (if not nested in a string)
			putchar(inputChar);
			if (!string) character = !character; //only start/stop a character literal if not inside a string
			break;
		case '/': //potentially the start of a comment
			nextChar = getchar(); //we need a second character in order to be able to decide
			if (nextChar == '/') { //potential start of line comment
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
				putchar(inputChar); //print the slash normally
				process(nextChar); //the next character could conceivably also matter for the formatting
			}
			break;
		case '*': //potentially the end of a block comment
			nextChar = getchar();
			if (nextChar == '/' && blockComment) { //blockComment should never be false when this happens, but try to handle bad entry gracefully (no unmatched </i>)
				putchar(inputChar);
				putchar(nextChar);
				printf("</i>");
				blockComment = FALSE;
			}
			else {
				putchar(inputChar);
				process(nextChar); //see else block in case '/'
			}
			break;
		case '\n': //would be the end of line comment
			if (lineComment) {
				printf("</i>");
				lineComment = FALSE;
			}
			putchar(inputChar);
			break;
		default: //normal characters should just be printed
			putchar(inputChar);
	}
}

main() {
	printf("<pre>\n"); //wrap the code in <pre> tags
	char inputChar;
	while ((inputChar = getchar()) != EOF) process(inputChar); //iterate through stream until finding an EOF character
	printf("</pre>\n");
}