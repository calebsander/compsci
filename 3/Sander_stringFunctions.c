/*
	Caleb Sander
	10/06/2015
	stringFunctions.c
*/

#include <stdbool.h>
#include <stdio.h>

char toLowerCase(char c) {
	//Uppercase letters are the only ones that need to be changed
	if ('A' <= c && c <= 'Z') return c - 'A' + 'a';
	else return c;
}
bool stringEquals(char *string1, char *string2, bool caseSensitive) {
	//Go until hitting the end of one of the strings
	while (*string1 && *string2) {
		//If characters are unequal, then the strings can't be equal
		if (caseSensitive) {
			if (*string1 != *string2) return false;
		}
		else {
			if (toLowerCase(*string1) != toLowerCase(*string2)) return false;
		}
		//Go on to the next character
		string1++;
		string2++;
	}
	return *string1 == *string2; //make sure strings had the same length
}
bool contains(char *str, char c) {
	for (; *str; str++) {
		if (*str == c) return true;
	}
	return false;
}
int stringSpan(char *string1, char *string2) {
	int i; //keeps track of the result (which is also the current index in string1)
	//Iterate over the characters until hitting the end or one that is in string2
	for (i = 0; string1[i] && !contains(string2, string1[i]); i++);
	return i;
}
int indexOfLast(char *str, char c) {
	int lastIndex = -1;
	for (int i = 0; str[i]; i++) {
		if (str[i] == c) lastIndex = i;
	}
	return lastIndex;
}

int main() {
	printf("stringEquals:\n");
	printf("Equal\t%d\n", stringEquals("abc", "abc", true));
	printf("Equal\t%d\n", stringEquals("abc", "AbC", false));
	printf("Equal\t%d\n", stringEquals("", "", true));
	printf("Unequal\t%d\n", stringEquals("abc", "AbC", true));
	printf("Unequal\t%d\n", stringEquals("abc", "def", false));
	printf("Unequal\t%d\n", stringEquals("abc", "abcd", false));
	printf("Unequal\t%d\n", stringEquals("abc", "", false));
	printf("Unequal\t%d\n", stringEquals("abcd", "efg", false));
	printf("Unequal\t%d\n", stringEquals("", "test", true));
	printf("stringSpan:\n");
	printf("0\t%d\n", stringSpan("cat", "abcdef"));
	printf("2\t%d\n", stringSpan("friend", "eioua"));
	printf("3\t%d\n", stringSpan("reaction", "bcd"));
	printf("4\t%d\n", stringSpan("inky", "car"));
	printf("0\t%d\n", stringSpan("", "apple"));
	printf("5\t%d\n", stringSpan("apple", ""));
	printf("indexOfLast:\n");
	printf("0\t%d\n", indexOfLast("abc", 'a'));
	printf("2\t%d\n", indexOfLast("-+-", '-'));
	printf("-1\t%d\n", indexOfLast("aaaaaaaaaaa", 'b'));
	printf("-1\t%d\n", indexOfLast("", 'x'));
	printf("4\t%d\n", indexOfLast("     ", ' '));
}
