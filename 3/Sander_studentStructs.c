#include <assert.h>
#include <stdint.h>
#include <string.h>

typedef struct {
	char *name;
	int8_t grade;
} Student;

int8_t lookup(Student *students, char *name, unsigned int count) {
	for (int i = 0; i < count; i++) {
		if (!strcmp(students[i].name, name)) return students[i].grade;
	}
	return -1;
}

int main() {
	char *nik = "Nik Castro",
	     *hosking = "Matt Hosking",
	     *zach = "Zach Perlo",
	     *caleb = "Caleb Sander",
	     *randy = "Randy Zhou";
	Student students[] = {
		{nik, 12},
		{hosking, 12},
		{zach, 12},
		{caleb, 11},
		{randy, 11}
	};
	const unsigned int numStudents = sizeof(students) / sizeof(Student);
	assert(lookup(students, "Mr. Lew", numStudents) == -1);
	assert(lookup(students, nik, numStudents) == 12);
	assert(lookup(students, randy, numStudents) == 11);
	assert(lookup(students, hosking, numStudents) == 12);
	assert(lookup(students, caleb, numStudents) == 11);
	assert(lookup(students, zach, numStudents) == 12);
	assert(lookup(students, "Reid Yesson RIP", numStudents) == -1);
}