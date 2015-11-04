#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include "getLine.h"

#define DEFAULT_START_LENGTH 16 //allocation size for new GrowableString

//Stores a string that can easily be resized
typedef struct {
	char *string; //'\0'-terminated
	unsigned int length; //doesn't include '\0'
	unsigned int allocatedLength; //number of chars to allocate for string
} GrowableString;

//Allocate the necessary memory to store the desired length
void allocateCharacters(GrowableString *string) {
	unsigned int allocSize = sizeof(*(string->string)) * string->allocatedLength;
	if (string->string) string->string = realloc(string->string, allocSize);
	else string->string = malloc(allocSize);
}
//If memory needs to be expanded, allocate twice as much as necessary
void allocateExtra(GrowableString *string) {
	unsigned int necessarySize = string->length + 1; //need to store '\0' too
	if (necessarySize > string->allocatedLength) {
		//I don't always realloc
		//but when I do, I prefer to realloc dos times as much
		string->allocatedLength = necessarySize * 2;
		allocateCharacters(string);
	}
}
//Make an empty GrowableString
GrowableString *emptyString() {
	GrowableString *string = malloc(sizeof(*string));
	string->string = NULL; //signal that it needs to be allocated
	string->length = 0;
	string->allocatedLength = DEFAULT_START_LENGTH;
	allocateCharacters(string);
	*(string->string) = '\0';
	return string;
}
//Make a GrowableString to wrap an already malloc'd string
GrowableString *newStringFromMallocd(char *mallocdString) {
	GrowableString *string = malloc(sizeof(*string));
	string->string = mallocdString;
	unsigned int length = strlen(mallocdString);
	string->length = length;
	string->allocatedLength = length + 1;
	return string;
}
//Free the GrowableString memory and the allocated string
void freeString(GrowableString *string) {
	free(string->string);
	free(string);
}

void concat(GrowableString *onto, char from) {
	char *insertionIndex = onto->string + onto->length;
	onto->length++;
	allocateExtra(onto);
	*insertionIndex = from;
	*(insertionIndex + 1) = '\0';
}
/*Replaces a section of a string with a new string; strategy:
	We have "aaaxxbbb\0" where xx is the part to replace. We want:
	        "aaayyybbb\0"
	1. Move bbb\0 to the new position:
	        "aaaxx?bbb\0"
	2. Insert replacement at the position (without '\0')
*/
void insertAt(GrowableString *string, unsigned int index, unsigned int deleteLength, char *insertString) {
	const unsigned int insertLength = strlen(insertString);
	string->length += insertLength;
	string->length -= deleteLength;
	allocateExtra(string);
	const unsigned int indexAfterInsertion = index + insertLength;
	memmove(string->string + indexAfterInsertion, string->string + index + deleteLength, (string->length + 1 - indexAfterInsertion) * sizeof(char)); //copy null byte too
	memcpy(string->string + index, insertString, insertLength * sizeof(char));
}

typedef enum {
	QUIT, NEXT, RESCAN, START, INVALID
} Flag;
typedef struct {
	char *from, *to;
	bool atStart, atEnd;
} ReplacementRule;
typedef struct {
	Flag rule, meta;
} Flags;

ReplacementRule *parseRule(char *fromString, char *toString) {
	ReplacementRule *rule = malloc(sizeof(*rule));
	if (*fromString == '^') {
		rule->atStart = true;
		fromString++;
	}
	else rule->atStart = false;
	rule->atEnd = false;
	GrowableString *normalizedFromString = emptyString();
	char nextChar;
	for (; *fromString; fromString++) {
		switch (*fromString) {
			case '@':
				nextChar = *(fromString + 1);
				if (nextChar) {
					concat(normalizedFromString, nextChar);
					fromString++;
				}
				else concat(normalizedFromString, *fromString);
				break;
			case '#':
				nextChar = *(fromString + 1);
				if (nextChar) concat(normalizedFromString, *fromString);
				else rule->atEnd = true;
				break;
			default:
				concat(normalizedFromString, *fromString);
		}
	}
	rule->from = normalizedFromString->string;
	free(normalizedFromString);
	rule->to = toString;
	return rule;
}
void freeRule(ReplacementRule *rule) {
	free(rule->from);
	free(rule);
}
const char *CORRECT_SYNTAX = "Syntax:\t./FandR [-SRNQsrnq] [FROM TO]*\n";
void argumentError() {
	fputs(CORRECT_SYNTAX, stderr);
	exit(EXIT_FAILURE);
}
bool isUpperCase(char c) {
	return 'A' <= c && c <= 'Z';
}
bool isLowerCase(char c) {
	return 'a' <=c && c <= 'z';
}
Flags parseFlags(char *string) {
	if (string[0] != '-') argumentError();
	Flags flags = {INVALID, INVALID};
	char lowerChar;
	Flag processedFlag;
	for (string++; *string; string++) {
		if (isLowerCase(*string)) lowerChar = *string;
		else lowerChar = *string - 'A' + 'a';
		switch (lowerChar) {
			case 'q':
				processedFlag = QUIT;
				break;
			case 'n':
				processedFlag = NEXT;
				break;
			case 'r':
				processedFlag = RESCAN;
				break;
			case 's':
				processedFlag = START;
				break;
			default:
				processedFlag = INVALID;
		}
		if (processedFlag == INVALID) argumentError();
		if (isUpperCase(*string)) flags.meta = processedFlag;
		else if (isLowerCase(*string)) flags.rule = processedFlag;
	}
	return flags;
}
unsigned int getNextIndex(Flag flag, unsigned int insertionIndex, unsigned int insertionLength) {
	switch (flag) {
		case NEXT:
			return insertionIndex + insertionLength;
		case RESCAN:
			return insertionIndex;
		default:
			return 0; //go back to beginning for START
	}
}
bool runReplacement(char *line, ReplacementRule *rule, Flag flag) { //returns success
	unsigned int index = 0;
	bool changedLastTime = true;
	bool anchorConditionsMet;
	bool success = false;
	const unsigned int fromLength = strlen(rule->from);
	const unsigned int insertionLength = strlen(rule->to);
	GrowableString *wrappedLine = newStringFromMallocd(line);
	while (changedLastTime) {
		anchorConditionsMet = true;
		if (rule->atStart && !strncmp(rule->from, wrappedLine->string, fromLength)) anchorConditionsMet = false;
		else if (rule->atEnd && !strncmp(rule->from, wrappedLine->string + strlen(wrappedLine->string) - fromLength, fromLength)) anchorConditionsMet = false;
		printf("Anchor: %d\n", anchorConditionsMet);
		if (anchorConditionsMet) {
			char *foundIndex = strstr(wrappedLine->string + index, rule->from);
			printf("Index: %d\n", (int)(foundIndex ? foundIndex - wrappedLine->string : -1));
			if (foundIndex) {
				unsigned int insertionIndex = foundIndex - wrappedLine->string;
				insertAt(wrappedLine, insertionIndex, fromLength, rule->to);
				changedLastTime = flag != QUIT;
				success = true;
				index = getNextIndex(flag, insertionIndex, insertionLength);
			}
			else changedLastTime = false;
		}
		else changedLastTime = false;
	}
	free(wrappedLine);
	return success;
}

int main(int argc, char **argv) {
	argc -= 1; //don't count argument to run the program
	argv++;
	Flags flags = {NEXT, NEXT};
	if (argc % 2) { //options flag
		flags = parseFlags(*argv);
		if (flags.meta == INVALID) flags.meta = NEXT;
		if (flags.rule == INVALID) flags.rule = NEXT;
		argv++;
	}
	unsigned int numRules = (argc / 2);
	ReplacementRule **rules = malloc(sizeof(*rules) * numRules);
	for (unsigned int i = 0; argv[i * 2]; i++) {
		rules[i] = parseRule(argv[i * 2], argv[i * 2 + 1]);
		printf("Rule: %s, %s, %d, %d\n", rules[i]->from, rules[i]->to, rules[i]->atStart, rules[i]->atEnd);
	}
	char *origLine = "aaabbb";
	char *line = malloc(strlen(origLine) + 1);
	strcpy(line, origLine);
	unsigned int index = 0;
	bool changedLastTime = false;
	while (index != numRules && !(flags.meta == QUIT && changedLastTime)) {
		printf("\nRule Index: %u\n", index);
		changedLastTime = runReplacement(line, rules[index], flags.rule);
		if (changedLastTime) index = getNextIndex(flags.meta, index, 1);
		else index++;
		printf("Changed: %d ", changedLastTime);
		printf("Replaced: %s\n", line);
	}
	free(line);
	for (unsigned int i = 0; i < numRules; i++) freeRule(rules[i]);
	free(rules);
}