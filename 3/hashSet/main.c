#include <assert.h>
#include <math.h>
#include "Set.h"

int main() {
	HashSet *testSet = makeEmptySet();
	assert(!size(testSet));
	//Test adding some elements
	for (unsigned int i = 1; i <= 10; i++) addElement(testSet, i * i);
	printSet(testSet);
	//Ensure adding duplicates is handled correctly
	addElement(testSet, 25); //duplicate
	addElement(testSet, 50); //not duplicate
	printSet(testSet);
	removeElement(testSet, 50);
	//Test rebucketing
	for (unsigned int i = 1; i < 100; i++) addElement(testSet, i * 1000);
	//Test removal
	for (unsigned int i = 1; i < 100; i++) removeElement(testSet, i * 1000);
	for (unsigned int i = 1; i < 100; i++) removeElement(testSet, -i);
	//Test contains
	for (unsigned int i = 1; i <= 100; i++) {
		const double sqrted = sqrt(i);
		assert((sqrted != (double)(int)sqrted) ^ contains(testSet, i));
	}
	printSet(testSet);
	freeSet(testSet);
}