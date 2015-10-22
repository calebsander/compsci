/*
	studentStructs
	Caleb Sander
	10/20/2015
	Defines a Student struct and a lookup function to use on arrays of it
*/

#include <assert.h>
#include <stdint.h>
#include <string.h>

typedef struct {
	char *name;
	int8_t grade; //e.g. 9 for 9th grader
} Student;

/*
	students is a pointer to the start of an array of students
	name is a string containing the name to be looked up
	count is the length of students
	returns the grade number of the student with the specified name, or -1 if no student has that name
*/
int8_t lookup(Student *students, char *name, unsigned int count) {
	for (unsigned int i = 0; i < count; i++) {
		if (!strcmp(students[i].name, name)) return students[i].grade;
	}
	return -1;
}

int main() {
	//Constants for names of students in class
	char *nik = "Nik Castro",
	     *hosking = "Matt Hosking",
	     *zach = "Zach Perlo",
	     *caleb = "Caleb Sander",
	     *randy = "Randy Zhou";
	//Initialize Student structs
	Student students[] = {
		{nik, 12},
		{hosking, 12},
		{zach, 12},
		{caleb, 11},
		{randy, 11}
	};
	const unsigned int numStudents = sizeof(students) / sizeof(Student);
	//Check that lookup returns the correct values
	assert(lookup(students, "Mr. Lew", numStudents) == -1);
	assert(lookup(students, nik, numStudents) == 12);
	assert(lookup(students, randy, numStudents) == 11);
	assert(lookup(students, hosking, numStudents) == 12);
	assert(lookup(students, caleb, numStudents) == 11);
	assert(lookup(students, zach, numStudents) == 12);
	assert(lookup(students, "Reid Yesson RIP", numStudents) == -1);
}