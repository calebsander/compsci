/* getLine.c
 * Declares the getLine function for CS 3 Lab 2
 * Alex Lew
 * 11/8/15
 */

/* getLine: Reads the next line of the standard input (terminated by 
 * '\n' or EOF) into a newly allocated buffer. Returns a pointer to that 
 * buffer if successful. It is the caller's responsiblity to free the
 * allocated memory when it is no longer needed.
 *
 * If unable to allocate memory, or if STDIN has ended (getchar() == EOF), 
 * getLine will return NULL. 
 *   
 * getLine returns a null-terminated string. If the line of input ended in
 * '\n' (instead of EOF), the returned string will also contain '\n' as its
 * last character (before '\0').
 */
char *getLine();

