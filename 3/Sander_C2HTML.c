#include <stdbool.h>
#include <stdio.h>

bool gInBlockComment = false; //whether a block comment has started but not yet finished at the current posititon in the input
bool gInLineComment = false; //whether a line comment has started but not yet finished
bool gInString = false; //whether a string literal has started but not yet finished
bool gInCharacter = false; //whether a character literal has started but not yet finished

void process(char inputChar) {
	char nextChar;
	switch (inputChar) { //go through the different cases that need special attention
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
			if (gInCharacter) putchar(inputChar); //if in '"', then just print it normally
			else {
				gInString = !gInString; //we have either started or stopped a string
				if (gInString) {
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
			if (!gInString) gInCharacter = !gInCharacter; //only start/stop a character literal if not inside a string
			break;
		case '/': //potentially the start of a comment
			nextChar = getchar(); //we need a second character in order to be able to decide
			if (nextChar == '/') { //potential start of line comment
				if (!gInLineComment) { //can't do another line comment on the same line
					printf("<i>");
					gInLineComment = true;
				}
				putchar(inputChar);
				putchar(nextChar);
			}
			else if (nextChar == '*' && !gInBlockComment) { //if we are already in a block comment, make sure to check to see whether the '*' is part of the end signature (process again)
				printf("<i>");
				putchar(inputChar);
				putchar(nextChar);
				gInBlockComment = true;
			}
			else {
				putchar(inputChar); //print the slash normally
				process(nextChar); //the next character could conceivably also matter for the formatting
			}
			break;
		case '*': //potentially the end of a block comment
			nextChar = getchar();
			if (nextChar == '/' && gInBlockComment) { //gInBlockComment should never be false when this happens, but try to handle bad entry gracefully (no unmatched </i>)
				putchar(inputChar);
				putchar(nextChar);
				printf("</i>");
				gInBlockComment = false;
			}
			else {
				putchar(inputChar);
				process(nextChar); //see else block in case '/'
			}
			break;
		case '\n': //would be the end of line comment
			if (gInLineComment) {
				printf("</i>");
				gInLineComment = false;
			}
			putchar(inputChar);
			break;
		default: //normal characters should just be printed
			putchar(inputChar);
	}
}

int main() {
	printf("<pre>\n"); //wrap the code in <pre> tags
	char inputChar;
	while ((inputChar = getchar()) != EOF) process(inputChar); //iterate through stream until finding an EOF character
	printf("</pre>\n");
}