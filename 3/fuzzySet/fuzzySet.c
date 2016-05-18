#include <assert.h>
#include <stdlib.h>
#include "fuzzySet.h"

#define LOAD_FACTOR 0.75
#define DEFAULT_BUCKETS 10
typedef struct bucketNode BucketNode;
struct bucketNode {
	K key;
	double in;
	BucketNode *next;
};
typedef enum {
	NONE,
	COMPLEMENT,
	UNION,
	INTERSECTION
} Operation;
typedef struct {
	Operation operation;
	FuzzySet *set1; //must be set if operation != NONE
	FuzzySet *set2; //must be set if operation == UNION || operation == INTERSECTION
} Operands;
struct fuzzySet {
	unsigned int bucketCount;
	unsigned int elementCount;
	BucketNode **buckets;
	Operands operands;
};

unsigned int hashKey(K key, unsigned int bucketCount) {
	return (unsigned int)key % bucketCount;
}
void addElementResize(FuzzySet *set, K key, double in, bool external);
void allocateBuckets(FuzzySet *set, unsigned int count) {
	const unsigned int oldCount = set->bucketCount;
	BucketNode **oldBuckets = set->buckets;
	set->bucketCount = count;
	set->buckets = calloc(count, sizeof(*(set->buckets)));
	if (oldBuckets) {
		for (unsigned int oldBucket = 0; oldBucket < oldCount; oldBucket++) {
			BucketNode *node = oldBuckets[oldBucket];
			while (node) {
				addElementResize(set, node->key, node->in, false);
				BucketNode *lastNode = node;
				node = node->next;
				free(lastNode);
			}
		}
		free(oldBuckets);
	}
}
BucketNode *makeNode(K key, double in, BucketNode *next) {
	BucketNode *node = malloc(sizeof(*node));
	node->key = key;
	node->in = in;
	node->next = next;
	return node;
}

FuzzySet *noOperatorEmptySet() {
	FuzzySet *set = malloc(sizeof(*set));
	set->buckets = NULL;
	allocateBuckets(set, DEFAULT_BUCKETS);
	set->elementCount = 0;
	return set;
}
FuzzySet *makeEmptyFuzzySet() {
	FuzzySet *set = noOperatorEmptySet();
	set->operands.operation = NONE;
	return set;
}

#define max(a, b) (((a) > (b)) ? (a) : (b))
#define min(a, b) (((a) < (b)) ? (a) : (b))
double getInNoCache(FuzzySet *set, K key) {
	//Look through cache
	for (BucketNode *node = set->buckets[hashKey(key, set->bucketCount)]; node; node = node->next) {
		if (node->key == key) return node->in;
	}
	//if not in cache
	switch (set->operands.operation) {
		case NONE:
			return 0.0; //value simply didn't exist
		case COMPLEMENT:
			return 1.0 - getInNoCache(set->operands.set1, key);
		case UNION:
			return max(getInNoCache(set->operands.set1, key), getInNoCache(set->operands.set2, key));
		case INTERSECTION:
			return min(getInNoCache(set->operands.set1, key), getInNoCache(set->operands.set2, key));
		default:
			assert(false);
	}
}
double getIn(FuzzySet *set, K key) {
	const double result = getInNoCache(set, key);
	addElementResize(set, key, result, true); //avoid checking value and whether key was already there
	return result;
}
bool contains(FuzzySet *set, K key) {
	return getIn(set, key) != 0.0;
}
void addElement(FuzzySet *set, K key, double in) {
	removeElement(set, key);
	assert(0.0 <= in && in <= 1.0);
	addElementResize(set, key, in, true);
}
void addElementResize(FuzzySet *set, K key, double in, bool external) {
	if (external && set->elementCount + 1 > (int)((double)set->bucketCount * LOAD_FACTOR)) allocateBuckets(set, set->bucketCount << 1); //there is a possibility of exceeding the load factor
	BucketNode **node = set->buckets + hashKey(key, set->bucketCount);
	while (*node) {
		if ((*node)->key == key) return; //key already exists
		node = &((*node)->next);
	}
	if (external) set->elementCount++;
	*node = makeNode(key, in, *node);
}
void removeElement(FuzzySet *set, K key) {
	BucketNode **node = set->buckets + hashKey(key, set->bucketCount);
	while (*node) {
		if ((*node)->key == key) {
			BucketNode *removeNode = *node;
			*node = (*node)->next;
			free(removeNode);
			set->elementCount--;
			break;
		}
		node = &((*node)->next);
	}
}
unsigned int size(FuzzySet *set) {
	return set->elementCount;
}
void freeSet(FuzzySet *set) {
	for (unsigned int bucket = 0; bucket < set->bucketCount; bucket++) {
		BucketNode *node = set->buckets[bucket];
		while (node) {
			BucketNode *lastNode = node;
			node = node->next;
			free(lastNode);
		}
	}
	free(set->buckets);
	free(set);
}

FuzzySet *complementSet(FuzzySet *set) {
	FuzzySet *newSet = noOperatorEmptySet();
	newSet->operands.operation = COMPLEMENT;
	newSet->operands.set1 = set;
	return newSet;
}
FuzzySet *unionSet(FuzzySet *set1, FuzzySet *set2) {
	FuzzySet *newSet = noOperatorEmptySet();
	newSet->operands.operation = UNION;
	newSet->operands.set1 = set1;
	newSet->operands.set2 = set2;
	return newSet;
}
FuzzySet *intersectionSet(FuzzySet *set1, FuzzySet *set2) {
	FuzzySet *newSet = noOperatorEmptySet();
	newSet->operands.operation = INTERSECTION;
	newSet->operands.set1 = set1;
	newSet->operands.set2 = set2;
	return newSet;
}

struct fuzzySetIterator {
	FuzzySet *set;
	unsigned int bucket;
	BucketNode *nextNode;
};
void advanceIterator(FuzzySetIterator *iterator) { //if the next node is null, go to the next bucket until finding one that isn't null
	while (!iterator->nextNode) {
		if (hasNext(iterator)) {
			iterator->bucket++;
			//Make sure end of set hasn't yet been removed
			if (hasNext(iterator)) iterator->nextNode = iterator->set->buckets[iterator->bucket];
		}
		else break;
	}
}
FuzzySetIterator *iterator(FuzzySet *set) {
	FuzzySetIterator *iterator = malloc(sizeof(*iterator));
	iterator->set = set;
	iterator->bucket = 0;
	iterator->nextNode = set->buckets[iterator->bucket];
	advanceIterator(iterator);
	return iterator;
}
bool hasNext(FuzzySetIterator *iterator) {
	return iterator->bucket != iterator->set->bucketCount;
}
Element next(FuzzySetIterator *iterator) {
	Element result = *((Element *)iterator->nextNode);
	iterator->nextNode = iterator->nextNode->next;
	advanceIterator(iterator);
	return result;
}