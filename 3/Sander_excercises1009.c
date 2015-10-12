#include <stdbool.h>
#include <stdio.h>

char *strcat(char *s, char *add) {
	char *sOrig = s;
	for (; *s; s++);
	while ((*(s++) = *(add++)));
	return sOrig;
}
bool strend(char *s, char *end) {
	const char *sOrig = s;
	const char *endOrig = end;
	for (; *s; s++);
	for (; *end; end++);
	for (; s >= sOrig && *s == *end; s--, end--) {
		if (end == endOrig) return true;
	}
	return false;
}
void reverse(char *start) {
	char *end = start;
	for (; *end; end++);
	end--;
	char swapTemp;
	for (; end > start; start++, end--) { //move both pointers to the middle, swapping values as we go
		swapTemp = *start;
		*start = *end;
		*end = swapTemp;
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
	printf("0\t%d\n", strend("ab", "abc"));
	printf("reverse:\n");
	char cba[] = "abc";
	reverse(cba);
	printf("cba\t%s\n", cba);
	char ba[] = "ab";
	reverse(ba);
	printf("ba\t%s\n", ba);
	char longString[] = "0123456789abcdefg";
	reverse(longString);
	printf("gfedcba9876543210\t%s\n", longString);
	reverse(empty);
	printf("\t%s\n", empty);
	char fourString[] = "1234";
	reverse(fourString);
	printf("4321\t%s\n", fourString);
	char singleton[] = "1";
	reverse(singleton);
	printf("1\t%s\n", singleton);
}