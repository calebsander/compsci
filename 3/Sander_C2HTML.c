/*C2HTML
	Caleb Sander
	09/15/2015
	Takes in C programs and outputs them with HTML formatting
*/

#include <stdbool.h>
#include <stdio.h>

/*Whether a block comment has started but not yet finished at the current
	posititon in the input*/
bool gInBlockComment = false;
//Whether a line comment has started but not yet finished
bool gInLineComment = false;
//Whether a string literal has started but not yet finished
bool gInString = false;
//Whether a character literal has started but not yet finished
bool gInCharacter = false;

/*Takes in the next input char and prints the necessary HTML formatting and
	updates the global state variables if necessary
	inputChar is the char to process*/
void process(char inputChar) {
	char nextChar; //used when checking for two-character sequences
	//Go through the different cases that need special attention
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
			//Skip checking the next character because it is escaped
			putchar(inputChar);
			putchar(getchar());
			break;
		case '"': //start or end of string (if not nested in comment or character)
			//If in '"', then just print it normally
			if (gInCharacter || gInBlockComment || gInLineComment) putchar(inputChar);
			else {
				gInString = !gInString; //we have either started or stopped a string
				if (gInString) {
					printf("<b>");
					putchar(inputChar);
				}
				else {
					putchar(inputChar);
					printf("</b>");
				}
			}
			break;
		case '\'': //start or end of character (if not nested in a string)
			putchar(inputChar);
			//Only start/stop a character literal if not inside a string or comment
			if (!(gInString || gInBlockComment || gInLineComment))
				gInCharacter = !gInCharacter;
			break;
		case '/': //potentially the start of a comment
			//We need a second character in order to be able to decide if it is a comment
			nextChar = getchar();
			if (nextChar == '/') { //potential start of line comment
				//Can't be nested
				if (!(gInBlockComment || gInLineComment || gInCharacter || gInString)) {
					printf("<i>");
					gInLineComment = true;
				}
				putchar(inputChar);
				putchar(nextChar);
			}
			else if (nextChar == '*' &&
			//Can't be nested
			!(gInBlockComment || gInLineComment || gInCharacter || gInString)) {
				printf("<i>");
				putchar(inputChar);
				putchar(nextChar);
				gInBlockComment = true;
			}
			else {
				putchar(inputChar); //print the slash normally
				/*if the next character wasn't special or we are already in a block comment,
					make sure to check to see whether the '*' needs special processing*/
				process(nextChar);
			}
			break;
		case '*': //potentially the end of a block comment
			nextChar = getchar();
			/*gInBlockComment should never be false when this happens,
				but try to handle bad entry gracefully (no unmatched </i>)*/
			if (nextChar == '/' && gInBlockComment) {
				putchar(inputChar);
				putchar(nextChar);
				printf("</i>");
				gInBlockComment = false;
			}
			else { //just a normal next character
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
	//Iterate through input stream until finding an EOF character
	char inputChar;
	while ((inputChar = getchar()) != EOF) process(inputChar);
	printf("</pre><!--By the way, the 80 character line limit is annoying.-->\n");
}