#ifndef __FUZZY_INCLUDED__
	#define __FUZZY_INCLUDED__
	#include <stdbool.h>

	typedef int K;
	typedef struct fuzzySet FuzzySet;
	typedef struct fuzzySetIterator FuzzySetIterator;
	typedef struct {
		K key;
		double in;
	} Element;

	unsigned int hashKey(K key, unsigned int bucketCount);

	FuzzySet *makeEmptyFuzzySet();
	double getIn(FuzzySet *set, K key);
	bool contains(FuzzySet *set, K key);
	void addElement(FuzzySet *set, K key, double in);
	void removeElement(FuzzySet *set, K key);
	unsigned int size(FuzzySet *set);
	void freeSet(FuzzySet *set);

	FuzzySet *complementSet(FuzzySet *set);
	FuzzySet *unionSet(FuzzySet *set1, FuzzySet *set2);
	FuzzySet *intersectionSet(FuzzySet *set1, FuzzySet *set2);

	FuzzySetIterator *iterator(FuzzySet *set);
	bool hasNext(FuzzySetIterator *iterator);
	Element next(FuzzySetIterator *iterator);
#endif