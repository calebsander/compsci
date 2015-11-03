#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include "getLine.h"

typedef struct {
	char *string; //'\0'-terminated
	unsigned int length; //doesn't include '\0'
	unsigned int allocatedLength;
} GrowableString;

void allocateCharacters(GrowableString *string) {
	unsigned int allocSize = sizeof(*(string->string)) * string->allocatedLength;
	if (string->string) string->string = realloc(string->string, allocSize);
	else string->string = malloc(allocSize);
}
void allocateExtra(GrowableString *string) {
	unsigned int necessarySize = string->length + 1;
	if (necessarySize > string->allocatedLength) { //I don't always realloc
		string->allocatedLength = necessarySize * 2; //but when I do, I prefer to realloc dos as much
		allocateCharacters(string);
	}
}
GrowableString *emptyString() {
	GrowableString *string = calloc(sizeof(*string), 1);
	string->allocatedLength = 16;
	allocateCharacters(string);
	*(string->string) = '\0';
	return string;
}
GrowableString *newStringFromMallocd(char *mallocdString) {
	GrowableString *string = malloc(sizeof(*string));
	string->string = mallocdString;
	unsigned int length = strlen(mallocdString);
	string->length = length;
	string->allocatedLength = length + 1;
	return string;
}

void concat(GrowableString *onto, char from) {
	char *insertionIndex = onto->string + onto->length;
	onto->length++;
	allocateExtra(onto);
	*insertionIndex = from;
	*(insertionIndex + 1) = '\0';
}
void insertAt(GrowableString *string, unsigned int index, unsigned int deleteLength, char *insertString) {
	unsigned int insertLength = strlen(insertString);
	string->length += insertLength;
	string->length -= deleteLength;
	allocateExtra(string);
	unsigned int indexAfterInsertion = index + insertLength;
	memcpy(string->string + indexAfterInsertion, string->string + index + deleteLength, string->length + 1 - indexAfterInsertion); //copy null byte too
}

typedef enum {
	QUIT,
	NEXT,
	RESCAN,
	START,
	INVALID
} Flag;
typedef struct {
	char *from;
	char *to;
	bool atStart, atEnd;
} ReplacementRule;
typedef struct {
	Flag rule, meta;
} Flags;

ReplacementRule *parseRule(char *fromString, char *toString) {
	ReplacementRule *rule = malloc(sizeof(*rule));
	if (fromString[0] == '^') {
		rule->atStart = true;
		fromString++;
	}
	else rule->atStart = false;
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
				if (nextChar) {
					concat(normalizedFromString, *fromString);
					concat(normalizedFromString, nextChar);
					fromString++;
				}
				else rule->atEnd = true;
				break;
			default:
				concat(normalizedFromString, *fromString);
		}
	}
	rule->from = normalizedFromString->string;
	rule->to = toString;
	return rule;
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
bool runReplacement(char *line, ReplacementRule *rule, Flag flag) { //returns success
	unsigned int index = 0;
	bool changedLastTime = true;
	bool anchorConditionsMet;
	bool success = false;
	const unsigned int fromLength = strlen(rule->from);
	GrowableString *wrappedLine = newStringFromMallocd(line);
	while (changedLastTime) {
		anchorConditionsMet = true;
		if (rule->atStart && !strncmp(rule->from, line, fromLength)) anchorConditionsMet = false;
		else if (rule->atEnd && !strncmp(rule->from, line + strlen(line) - fromLength, fromLength)) anchorConditionsMet = false;
		if (anchorConditionsMet) {
			char *foundIndex = strstr(line + index, rule->from);
			if (foundIndex) {
				insertAt(wrappedLine, foundIndex - line, fromLength, rule->to);
				changedLastTime = true;
				switch (flag) {
					case QUIT:
						changedLastTime = false;
						break;
					case NEXT:
						index = foundIndex - line + strlen(rule->to);
						break;
					case RESCAN:
						index = foundIndex - line;
						break;
					case START:
						index = 0;
						break;
					case INVALID:
						assert(false);
				}
			}
			else changedLastTime = false;
		}
		else changedLastTime = false;
		if (changedLastTime) success = true;
	}
	return success;
}

int main(int argc, char **argv) {
	Flags flags;
	argc -= 1;
	argv++;
	if (argc % 2) { //options flag
		flags = parseFlags(argv[0]);
		if (flags.meta == INVALID) flags.meta = NEXT;
		if (flags.rule == INVALID) flags.rule = NEXT;
		argv++;
	}
	else flags.meta = flags.rule = NEXT; //no options flag
	ReplacementRule **rules = malloc(sizeof(*rules) * (argc / 2));
	for (unsigned int i = 0; argv[i * 2]; i++) {
		rules[i] = parseRule(argv[i * 2], argv[i * 2 + 1]);
		printf("%s, %s\n", rules[i]->from, rules[i]->to);
	}
}