#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "Set.h"

#define LOAD_FACTOR 0.75
#define DEFAULT_BUCKETS 10
typedef struct bucketNode BucketNode;
struct bucketNode {
	int value;
	BucketNode *next;
};
struct hashSet {
	unsigned int bucketCount;
	unsigned int elementCount;
	BucketNode **buckets;
};

unsigned int hash(int value, unsigned int bucketCount) {
	return (unsigned int)value % bucketCount;
}
void addElementResize(HashSet *set, int value, bool external);
void allocateBuckets(HashSet *set, unsigned int count) {
	const unsigned int oldCount = set->bucketCount;
	BucketNode **oldBuckets = set->buckets;
	set->bucketCount = count;
	set->buckets = calloc(count, sizeof(*(set->buckets)));
	if (oldBuckets) {
		for (unsigned int oldBucket = 0; oldBucket < oldCount; oldBucket++) {
			BucketNode *node = oldBuckets[oldBucket];
			while (node) {
				addElementResize(set, node->value, false);
				BucketNode *lastNode = node;
				node = node->next;
				free(lastNode);
			}
		}
		free(oldBuckets);
	}
}
BucketNode *makeNode(int value, BucketNode *next) {
	BucketNode *node = malloc(sizeof(*node));
	node->value = value;
	node->next = next;
	return node;
}

HashSet *makeEmptySet() {
	HashSet *set = malloc(sizeof(*set));
	set->buckets = NULL;
	allocateBuckets(set, DEFAULT_BUCKETS);
	set->elementCount = 0;
	return set;
}

unsigned int size(HashSet *set) {
	unsigned int result = 0;
	for (unsigned int bucket = 0; bucket < set->bucketCount; bucket++) {
		for (BucketNode *node = set->buckets[bucket]; node; node = node->next) result++;
	}
	return result;
}
bool contains(HashSet *set, int value) {
	for (BucketNode *node = set->buckets[hash(value, set->bucketCount)]; node; node = node->next) {
		if (node->value == value) return true;
	}
	return false;
}
void addElement(HashSet *set, int value) {
	addElementResize(set, value, true);
}
void addElementResize(HashSet *set, int value, bool external) {
	if (external && set->elementCount + 1 > (int)((double)set->bucketCount * LOAD_FACTOR)) allocateBuckets(set, set->bucketCount << 1); //there is a possibility of exceeding the load factor
	BucketNode **node = set->buckets + hash(value, set->bucketCount);
	while (*node) {
		if ((*node)->value == value) return; //value already exists
		node = &((*node)->next);
	}
	if (external) set->elementCount++;
	*node = makeNode(value, *node);
	assert(!(external && set->elementCount != size(set)));
}
void removeElement(HashSet *set, int value) {
	BucketNode **node = set->buckets + hash(value, set->bucketCount);
	while (*node) {
		if ((*node)->value == value) {
			BucketNode *removeNode = *node;
			*node = (*node)->next;
			free(removeNode);
			set->elementCount--;
			break;
		}
		node = &((*node)->next);
	}
}
void printSet(HashSet *set) {
	for (unsigned int bucket = 0; bucket < set->bucketCount; bucket++) {
		for (BucketNode *node = set->buckets[bucket]; node; node = node->next) printf("%d, ", node->value);
	}
	putchar('\n');
}
void freeSet(HashSet *set) {
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