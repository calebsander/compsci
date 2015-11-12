/* getLine.c
 * Implements the getLine function for CS 3 Lab 2
 * Alex Lew
 * 11/8/15
 */

#include <stdlib.h>
#include <stdio.h>

#define DEFAULT_ALLOC_SIZE 10

/* Read the next line of the standard input into a newly allocated
 * buffer. Returns a pointer to that buffer if successful. If unable
 * to allocate memory, or if STDIN has ended (getchar() == EOF),
 * getLine will return NULL. It is the caller's responsiblity to free
 * the allocated memory when it is no longer needed. */
char *getLine() {
	int size = DEFAULT_ALLOC_SIZE; 
	char *str = malloc(size);
	if (!str) return NULL;

	int i = 0;
	char c;
	while ((c = getchar()) != '\n' && c != EOF) {
		if (i == size - 2) {
			size = size * 2;
			char *temp = realloc(str, size);
			if (!temp) {
				str[i] = '\0';
				return str;
			}
			else {
				str = temp;
			}
		}
		str[i] = c;
		i++;
	}
	if (c == '\n') { str[i++] = '\n'; }
	str[i] = '\0';
	if (i == 0) {
		free(str);
		str = NULL;
	}
	return str;
}
