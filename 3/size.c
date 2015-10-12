#include <stdio.h>

int main() {
	printf("char: %d\n", (int)sizeof(char));
	printf("short: %d\n", (int)sizeof(short));
	printf("int: %d\n", (int)sizeof(int));
	printf("long: %d\n", (int)sizeof(long));
	printf("long long: %d\n", (int)sizeof(long long));
	printf("pointer: %d\n", (int)sizeof(char*));
	printf("float: %d\n", (int)sizeof(float));
	printf("double: %d\n", (int)sizeof(double));
	printf("size_t: %d\n", (int)sizeof(size_t));
}