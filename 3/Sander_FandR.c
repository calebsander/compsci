/*
	Caleb Sander
	11/03/2015
	Sander_FandR.c
	Does various replacement algorithms specified on command line to stdin
*/

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include "getLine.h"

#define DEFAULT_START_LENGTH 16 //allocation size for new GrowableString
typedef unsigned int uint;

//Stores a string that can easily be resized
typedef struct {
	char *string; //'\0'-terminated
	uint length; //doesn't include '\0'
	uint allocatedLength; //number of chars to allocate for string
} GrowableString;

//Allocate the necessary memory to store the desired length
void allocateCharacters(GrowableString *string) {
	uint allocSize = sizeof(*(string->string)) * string->allocatedLength;
	if (string->string) string->string = realloc(string->string, allocSize);
	else string->string = malloc(allocSize);
}
//If memory needs to be expanded, allocate twice as much as necessary
void allocateExtra(GrowableString *string) {
	uint necessarySize = string->length + 1; //need to store '\0' too
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
	uint length = strlen(mallocdString);
	string->length = length;
	string->allocatedLength = length + 1;
	return string;
}

//Adds a character onto a GrowableString
void concat(GrowableString *onto, char from) {
	char *insertionIndex = onto->string + onto->length;
	onto->length++;
	allocateExtra(onto);
	*insertionIndex = from; //replace null byte with new byte
	*(insertionIndex + 1) = '\0'; //add new null byte
}
/*Replaces a section of a string with a new string
	We have "aaaxxbbb\0" where xx is the part to replace. We want:
	        "aaayyybbb\0"
	1. Move bbb\0 to the new position:
	        "aaaxx?bbb\0"
	2. Insert replacement at the position (without '\0')
*/
void insertAt(GrowableString *string, uint index, uint deleteLength,
 char *insertString) {
	const uint insertLength = strlen(insertString);
	string->length += insertLength;
	string->length -= deleteLength;
	allocateExtra(string);
	const uint indexAfterInsertion = index + insertLength;
	memmove(
		string->string + indexAfterInsertion,
		string->string + index + deleteLength,
		(string->length + 1 - indexAfterInsertion) * sizeof(char) //copy null byte too
	);
	memcpy(string->string + index, insertString, insertLength * sizeof(char));
}

//Stores a flag that indicates how to scan the strings or switch between rules
typedef enum {
	QUIT, NEXT, RESCAN, START, INVALID
} Flag;
//Stores a string to replace with another string and where it is anchored
typedef struct {
	char *from, *to;
	bool atStart, atEnd;
} ReplacementRule;
//Stores each type of flag
typedef struct {
	Flag rule, meta; //for a single rule, between the rules
} Flags;

//Reads a replacement string into search string and anchors
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
				//if '@' is at end of string, it is just a normal character
				if (nextChar) {
					concat(normalizedFromString, nextChar);
					fromString++;
				}
				else concat(normalizedFromString, *fromString);
				break;
			case '#':
				nextChar = *(fromString + 1);
				//if '#' is not at end of string, treat it as normal
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
//Frees rule and the malloc'd from string
void freeRule(ReplacementRule *rule) {
	free(rule->from);
	free(rule);
}
//Alert that there was an error in the arguments and exit the program
void argumentError() {
	fputs("Syntax:\tFandR [-SRNQsrnq] [FROM TO]*\n", stderr);
	exit(EXIT_FAILURE);
}
//Returns whether c is a lower-case character
bool isUpperCase(char c) {
	return 'A' <= c && c <= 'Z';
}
//Returns whether c is an upper-case character
bool isLowerCase(char c) {
	return 'a' <= c && c <= 'z';
}
//Parse a flag string - doesn't return a pointer since Flags has same size
Flags parseFlags(char *string) {
	if (*string != '-') argumentError();
	Flags flags = {INVALID, INVALID};
	char lowerChar; //current flag character in lower case
	Flag processedFlag; //Flag corresponding to current character
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
				argumentError();
		}
		if (isUpperCase(*string)) flags.meta = processedFlag;
		else if (isLowerCase(*string)) flags.rule = processedFlag;
	}
	return flags;
}
//Calculates the next index to search at/rule to use based on the flag
uint getNextIndex(Flag flag, uint insertionIndex, uint insertionLength) {
	switch (flag) {
		case NEXT:
			return insertionIndex + insertionLength;
		case RESCAN:
			return insertionIndex;
		default:
			return 0; //go back to beginning for START, doesn't matter for QUIT
	}
}
//Takes in a malloc'd line and runs the replacement with a certain flag
//returns success of replacement
bool runReplacement(char *line, ReplacementRule *rule, Flag flag) {
	if ((flag == RESCAN || flag == START) && !strcmp(rule->from, rule->to)) {
		return false; //no replacement would be made
	}
	uint index = 0; //index to start replacing at
	bool reapplyRule = true; //whether the rule needs to be reapplied
	bool anchorConditionsMet; //whether each present anchor is satisfied by line
	bool success = false; //whether any replacement ever happened
	const uint fromLength = strlen(rule->from);
	const uint insertionLength = strlen(rule->to);
	//Wrap the line; when wrappedLine is changed, it will affect line
	GrowableString *wrappedLine = newStringFromMallocd(line);
	while (reapplyRule) { //keep going while a change was made
		anchorConditionsMet = true;
		//Anchor conditions are not met if anchoring to start and starts don't match
		//or anchoring to end and ends don't match
		if (rule->atStart && strncmp(rule->from, wrappedLine->string, fromLength)) {
			anchorConditionsMet = false;
		}
		if (rule->atEnd) {
			index = wrappedLine->length - fromLength; //make sure to only match at end
			if (strncmp(rule->from, wrappedLine->string + index, fromLength)) {
				anchorConditionsMet = false;
			}
		}
		printf("Anchor: %d\n", anchorConditionsMet); //DEBUG
		if (anchorConditionsMet) {
			const char *foundIndex = strstr(wrappedLine->string + index, rule->from);
			printf("Index: %d\n", (int)(foundIndex ? foundIndex - wrappedLine->string : -1)); //DEBUG
			if (foundIndex) { //if a match was found at or after the current index
				const uint insertionIndex = foundIndex - wrappedLine->string;
				insertAt(wrappedLine, insertionIndex, fromLength, rule->to);
				reapplyRule = flag != QUIT; //don't keep going
				success = true; //at least one replacement was made
				index = getNextIndex(flag, insertionIndex, insertionLength);
			}
			else reapplyRule = false;
		}
		else reapplyRule = false;
	}
	free(wrappedLine); //just free wrapper, not line itself
	return success;
}

int main(int argc, char **argv) {
	argv++; argc -= 1; //don't count argument to run the program
	Flags flags = {NEXT, NEXT};
	if (argc % 2) { //options flag exists
		flags = parseFlags(*argv);
		if (flags.meta == INVALID) flags.meta = NEXT; //set defaults
		if (flags.rule == INVALID) flags.rule = NEXT;
		argv++; //make sure argv points to first FROM
	}
	const uint numRules = (argc / 2); //the number of pairs of strings
	ReplacementRule **rules = malloc(sizeof(*rules) * numRules);
	for (uint i = 0; argv[i * 2]; i++) { //go through arguments 2 at a time
		rules[i] = parseRule(argv[i * 2], argv[i * 2 + 1]);
		printf("Rule: %s, %s, %d, %d\n", rules[i]->from, rules[i]->to, rules[i]->atStart, rules[i]->atEnd); //DEBUG
	}
	char *origLine = "aaabbb";
	char *line = malloc(strlen(origLine) + 1);
	strcpy(line, origLine);
	//while ((line = getLine())) {
		uint index = 0;
		bool changedLastTime = false;
		//If we get to what would be the rule following the last
		//or flag was 'Q' and
		while (index != numRules && !(flags.meta == QUIT && changedLastTime)) {
			printf("\nRule Index: %u\n", index); //DEBUG
			changedLastTime = runReplacement(line, rules[index], flags.rule);
			if (changedLastTime) index = getNextIndex(flags.meta, index, 1);
			else index++;
			printf("Changed: %d ", changedLastTime); //DEBUG
			printf("Replaced: %s\n", line); //DEBUG
		}
		//printf("%s", line);
		free(line);
	//}
	for (uint i = 0; i < numRules; i++) freeRule(rules[i]);
	free(rules);
}