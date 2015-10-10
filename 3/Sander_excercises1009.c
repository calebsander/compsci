#include <stdbool.h>
#include <stdio.h>

char *strcat(char *s, char *add) {
	char *sOrig = s;
	while (*(s++));
	while ((*(s++) = *(add++)));
	return sOrig;
}
bool strend(char *s, char *end) {
	const char *sOrig = s;
	const char *endOrig = end;
	while (*s) s++;
	while (*end) end++;
	while (s >= sOrig && end >= endOrig && *s == *end) {
		s--;
		end--;
	}
	return end + 1 == endOrig; //if we have gotten all the way to the character before the start of end, it was all at the end of s
}
void reverse(char *start) {
	char *end = start;
	while (*end) end++;
	char swapTemp;
	while (end-- > start) { //move both pointers to the middle, swapping values as we go
		swapTemp = *start;
		*start = *end;
		*end = swapTemp;
		start++;
	}
}

int main() {
	//A lot of tests
	printf("strcat:\n");
	char abc[10] = "abc";
	printf("abcdef\t%s\n", strcat(abc, "def"));
	char empty[10] = "";
	printf("\t%s\n", strcat(empty, ""));
	char noCat[] = "abc";
	printf("abc\t%s\n", strcat(noCat, ""));
	char noStart[10] = "";
	printf("def\t%s\n", strcat(noStart, "def"));
	printf("strend:\n");
	printf("1\t%d\n", strend("abcd", "bcd"));
	printf("1\t%d\n", strend("abc", "abc"));
	printf("0\t%d\n", strend("abc", "efghi"));
	printf("0\t%d\n", strend("abcdefghij", "o"));
	printf("1\t%d\n", strend("abc", ""));
	printf("1\t%d\n", strend("", ""));
	printf("reverse:\n");
	char cba[] = "abc";
	reverse(cba);
	printf("cba\t%s\n", cba);
	char ba[] = "ab";
	reverse(ba);
	printf("ba\t%s\n", ba);
	char longString[] = "0123456789abcdef";
	reverse(longString);
	printf("fedcba9876543210\t%s\n", longString);
	reverse(empty);
	printf("\t%s\n", empty);
	char fourString[] = "1234";
	reverse(fourString);
	printf("4321\t%s\n", fourString);
	char singleton[] = "1";
	reverse(singleton);
	printf("1\t%s\n", singleton);
}