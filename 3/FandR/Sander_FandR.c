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
#include "getLine.h"

#define DEFAULT_START_LENGTH 16 //allocation size for new GrowableString
typedef unsigned int uint;

//Stores a string that can easily be resized
typedef struct {
	char *string; //'\0'-terminated
	uint length; //doesn't include '\0'
	uint allocatedLength; //number of chars to allocate for string
} GrowableString;

//Allocate the necessary memory to store the desired length
void allocateCharacters(GrowableString *string, uint allocatedLength) {
	string->allocatedLength = allocatedLength;
	uint allocSize = sizeof(*(string->string)) * allocatedLength;
	//If string has already been allocated, realloc; otherwise, just malloc
	if (string->string) string->string = realloc(string->string, allocSize);
	else string->string = malloc(allocSize);
}
//If memory needs to be expanded, allocate twice as much as necessary
void allocateExtra(GrowableString *string, uint length) {
	string->length = length;
	uint necessarySize = length + 1; //need to store '\0' too
	if (necessarySize > string->allocatedLength) {
		//I don't always realloc, but when I do, I prefer dos times as much
		allocateCharacters(string, necessarySize * 2);
	}
}
//Make an empty GrowableString
GrowableString *emptyString() {
	GrowableString *string = malloc(sizeof(*string));
	string->string = NULL; //signal that it needs to be allocated
	string->length = 0;
	allocateCharacters(string, DEFAULT_START_LENGTH);
	*(string->string) = '\0';
	return string;
}
//Make a GrowableString to wrap an already malloc'd string
GrowableString *newStringFromMallocd(char *mallocdString) {
	GrowableString *string = malloc(sizeof(*string));
	string->string = mallocdString;
	string->length = strlen(mallocdString);
	string->allocatedLength = string->length + 1; //'\0' was allocated too
	return string;
}

//Adds a character onto a GrowableString
void concat(GrowableString *onto, char from) {
	char *insertionIndex = onto->string + onto->length;
	allocateExtra(onto, onto->length + 1);
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
	allocateExtra(string, string->length + insertLength - deleteLength);
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
	QUIT, NEXT, RESCAN, START
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
	Flags flags = {NEXT, NEXT};
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
/*Calculates the next index to search at/rule to use based on the flag
	lastIndex is the index of the start of the last thing done,
	lastLength is the number of indices that the last thing applied to*/
uint getNextIndex(Flag flag, uint lastIndex, uint lastLength) {
	switch (flag) {
		case NEXT:
			return lastIndex + lastLength;
		case RESCAN:
			return lastIndex;
		default:
			return 0; //go back to beginning for START, doesn't matter for QUIT
	}
}
//Takes in a pointer to a malloc'd line and runs the replacement with the flag
//returns success of replacement
bool runReplacement(char **line, ReplacementRule *rule, Flag flag) {
	if ((flag == RESCAN || flag == START) && !strcmp(rule->from, rule->to)) {
		return false; //no replacement would be made
	}
	uint index = 0; //index to start replacing at
	bool reapplyRule = true; //whether the rule needs to be reapplied
	bool anchorConditionsMet; //whether each present anchor is satisfied by line
	bool success = false; //whether any replacement ever happened
	uint lengthOfLine; //the number of characters in the line without '\n'
	const uint fromLength = strlen(rule->from);
	const uint insertionLength = strlen(rule->to);
	//Wrap the line for easier modification
	GrowableString *wrappedLine = newStringFromMallocd(*line);
	while (reapplyRule) { //keep going while a change was made
		anchorConditionsMet = true;
		//Anchor conditions are not met if anchoring to start and starts don't match
		//or anchoring to end and ends don't match
		if (rule->atStart && strncmp(rule->from, wrappedLine->string, fromLength)) {
			anchorConditionsMet = false;
		}
		if (rule->atEnd) {
			lengthOfLine = wrappedLine->length;
			if (lengthOfLine && wrappedLine->string[lengthOfLine - 1] == '\n') {
				lengthOfLine--;
			}
			index = lengthOfLine - fromLength; //make sure to only match at end
			if (strncmp(rule->from, wrappedLine->string + index, fromLength)) {
				anchorConditionsMet = false;
			}
		}
		if (anchorConditionsMet) {
			const char *foundIndex = strstr(wrappedLine->string + index, rule->from);
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
	*line = wrappedLine->string;
	return success;
}

int main(int argc, char **argv) {
	argv++; argc -= 1; //don't count argument to run the program
	Flags flags;
	if (argc % 2) { //options flag exists
		flags = parseFlags(*argv);
		argv++; //make sure argv points to first FROM
	}
	else {
		flags.rule = NEXT;
		flags.meta = NEXT;
	}
	const uint numRules = argc / 2; //the number of pairs of strings
	ReplacementRule **rules = malloc(sizeof(*rules) * numRules);
	for (uint i = 0; argv[i * 2]; i++) { //go through arguments 2 at a time
		rules[i] = parseRule(argv[i * 2], argv[i * 2 + 1]);
	}
	char *line;
	while ((line = getLine())) {
		uint index = 0;
		bool changedLastTime = false;
		//If we get to what would be the rule following the last
		//or flag was 'Q' and
		while (index != numRules && !(flags.meta == QUIT && changedLastTime)) {
			changedLastTime = runReplacement(&line, rules[index], flags.rule);
			if (changedLastTime) index = getNextIndex(flags.meta, index, 1);
			else index++;
		}
		printf("%s", line);
		free(line);
	}
	for (uint i = 0; i < numRules; i++) freeRule(rules[i]);
	free(rules);
}