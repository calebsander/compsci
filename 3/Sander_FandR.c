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
	GrowableString *string = malloc(sizeof(*string) * 1);
	string->string = NULL; //signal that it needs to be allocated
	string->length = 0;
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
void insertAt(GrowableString *string, unsigned int index, unsigned int deleteLength, char *insertString) {
	const unsigned int insertLength = strlen(insertString);
	string->length += insertLength;
	string->length -= deleteLength;
	allocateExtra(string);
	const unsigned int indexAfterInsertion = index + insertLength;
	//Correct these to account for actual sizeof(char)
	memmove(string->string + indexAfterInsertion, string->string + index + deleteLength, (string->length + 1 - indexAfterInsertion) * sizeof(char)); //copy null byte too
	memcpy(string->string + index, insertString, insertLength * sizeof(char));
}

typedef enum {
	QUIT, NEXT, RESCAN, START, INVALID
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
unsigned int getNextSearchIndex(Flag flag, unsigned int insertionIndex, unsigned int insertionLength) {
	switch (flag) {
		case NEXT:
			return insertionIndex + insertionLength;
		case RESCAN:
			return insertionIndex;
		case START:
			return 0;
		default:
			return 0; //doesn't matter for QUIT or INVALID
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
				index = getNextSearchIndex(flag, insertionIndex, insertionLength);
			}
			else changedLastTime = false;
		}
		else changedLastTime = false;
	}
	free(wrappedLine);
	return success;
}
unsigned int getNextRuleIndex(Flag flag, unsigned int index, bool success) {
	switch (flag) {
		case NEXT:
			if (success) return index + 1;
			else return index;
		case RESCAN:
			if (success) return index;
			else return index + 1;
		case START:
			if (success) return 0;
			else return index + 1;
		default:
			return 0; //doesn't matter for QUIT or INVALID
	}
}

int main(int argc, char **argv) {
	Flags flags = {NEXT, NEXT};
	argc -= 1;
	argv++;
	if (argc % 2) { //options flag
		flags = parseFlags(argv[0]);
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
	char *origLine = "aabbab";
	char *line = malloc(strlen(origLine) + 1);
	strcpy(line, origLine);
	unsigned int index = 0;
	bool changedLastTime = false;
	while (index != numRules && !(changedLastTime && flags.meta == QUIT)) {
		printf("\nRule Index: %u\n", index);
		changedLastTime = runReplacement(line, rules[index], flags.rule);
		index = getNextRuleIndex(flags.meta, index, changedLastTime);
		printf("Changed: %d ", changedLastTime);
		printf("Replaced: %s\n", line);
	}
	free(line);
	for (unsigned int i = 0; i < numRules; i++) freeRule(rules[i]);
	free(rules);
}