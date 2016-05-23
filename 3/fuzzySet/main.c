#include <assert.h>
#include <math.h>
#include <stdio.h>
#include "fuzzySet.h"

unsigned int hashKey(K key, unsigned int bucketCount) {
	return (unsigned int)((unsigned long)key % bucketCount);
}
bool equals(K key1, K key2) { //we say two keys are equal iff they point to the same thing
	return key1 == key2;
}

#define MAX_INT 100
unsigned int CACHED_POINTERS[MAX_INT];

bool isPrime(unsigned int num) {
	if (num <= 1) return false;
	const unsigned int maxTest = (unsigned int)sqrt((double)num);
	for (unsigned int test = 2; test <= maxTest; test++) {
		if (!(num % test)) return false;
	}
	return true;
}

int main() {
	for (unsigned int i = 0; i < MAX_INT; i++) CACHED_POINTERS[i] = i;
	FuzzySet *set1 = makeEmptyFuzzySet();
	for (unsigned int i = 0; i < MAX_INT; i++) {
		if (i % 2) addElement(set1, CACHED_POINTERS + i, (double)i / (double)MAX_INT);
	}
	for (unsigned int i = 0; i < MAX_INT; i++) {
		if (i % 2) assert(getIn(set1, CACHED_POINTERS + i) == (double)i / (double)MAX_INT);
		else assert(getIn(set1, CACHED_POINTERS + i) == 0.0);
	}
	FuzzySet *set2 = complementSet(set1);
	//printCachedValues(set2);
	for (unsigned int i = 0; i < MAX_INT; i++) {
		if (i % 2) assert(getIn(set2, CACHED_POINTERS + i) == 1.0 - (double)i / (double)MAX_INT);
		else assert(getIn(set2, CACHED_POINTERS + i) == 1.0);
	}
	//printCachedValues(set2); //make sure values have successfully been cached
	FuzzySet *primeSet = makeEmptyFuzzySet();
	for (unsigned int i = 1; i < MAX_INT; i++) {
		if (isPrime(i)) addElement(primeSet, CACHED_POINTERS + i, 0.5);
		else addElement(primeSet, CACHED_POINTERS + i, 0.0);
	}
	FuzzySet *modSet = makeEmptyFuzzySet();
	for (unsigned int i = 0; i < MAX_INT; i++) addElement(modSet, CACHED_POINTERS + i, (double)(i % 3) / 3.0);
	FuzzySet *unionOfSets = unionSet(modSet, primeSet);
	//for (unsigned int i = 0; i < MAX_INT; i++) printf("%d: %f\n", i, getIn(unionOfSets, CACHED_POINTERS + i));
	FuzzySet *evenSet = makeEmptyFuzzySet();
	for (unsigned int i = 0; i < MAX_INT; i++) {
		if (!(i % 2)) addElement(evenSet, CACHED_POINTERS + i, 1.0);
	}
	FuzzySet *intersection = intersectionSet(evenSet, modSet);
	for (unsigned int i = 0; i < MAX_INT; i++) printf("%d: %f\n", i, getIn(intersection, CACHED_POINTERS + i));
	freeSet(unionOfSets);
	freeSet(evenSet);
}