#include <assert.h>
#include <stdio.h>
#include "fuzzySet.h"

int main() {
	FuzzySet *set1 = makeEmptyFuzzySet();
	for (unsigned int i = 0; i < 100; i++) {
		if (i % 2) addElement(set1, i, (double)i / 100.0);
	}
	for (unsigned int i = 0; i < 100; i++) {
		if (i % 2) assert(getIn(set1, i) == (double)i / 100.0);
		else assert(getIn(set1, i) == 0.0);
	}
	FuzzySet *set2 = complementSet(set1);
	for (unsigned int i = 0; i < 100; i++) {
		if (i % 2) assert(getIn(set2, i) == 1.0 - (double)i / 100.0);
		else assert(getIn(set2, i) == 1.0);
	}
	freeSet(set2);
}