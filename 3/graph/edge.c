#include <stdlib.h>
#include "edge.h"

#define LOAD_FACTOR 0.75
#define DEFAULT_BUCKETS 10
typedef struct bucketNode BucketNode;
struct bucketNode {
	Edge *value;
	BucketNode *next;
};
struct edgeHashSet {
	unsigned int bucketCount;
	unsigned int elementCount;
	BucketNode **buckets;
};

unsigned int hashEdge(Edge *value, unsigned int bucketCount) {
	return (unsigned int)(long)value % bucketCount;
}
void addElementResizeEdge(EdgeHashSet *set, Edge *value, bool external);
void allocateBucketsEdge(EdgeHashSet *set, unsigned int count) {
	const unsigned int oldCount = set->bucketCount;
	BucketNode **oldBuckets = set->buckets;
	set->bucketCount = count;
	set->buckets = calloc(count, sizeof(*(set->buckets)));
	if (oldBuckets) {
		for (unsigned int oldBucket = 0; oldBucket < oldCount; oldBucket++) {
			BucketNode *node = oldBuckets[oldBucket];
			while (node) {
				addElementResizeEdge(set, node->value, false);
				BucketNode *lastNode = node;
				node = node->next;
				free(lastNode);
			}
		}
		free(oldBuckets);
	}
}
BucketNode *makeNodeEdge(Edge *value, BucketNode *next) {
	BucketNode *node = malloc(sizeof(*node));
	node->value = value;
	node->next = next;
	return node;
}

EdgeHashSet *makeEmptySetEdge() {
	EdgeHashSet *set = malloc(sizeof(*set));
	set->buckets = NULL;
	allocateBucketsEdge(set, DEFAULT_BUCKETS);
	set->elementCount = 0;
	return set;
}

bool containsEdge(EdgeHashSet *set, Edge *value) {
	for (BucketNode *node = set->buckets[hashEdge(value, set->bucketCount)]; node; node = node->next) {
		if (node->value == value) return true;
	}
	return false;
}
void addElementEdge(EdgeHashSet *set, Edge *value) {
	addElementResizeEdge(set, value, true);
}
void addElementResizeEdge(EdgeHashSet *set, Edge *value, bool external) {
	if (external && set->elementCount + 1 > (int)((double)set->bucketCount * LOAD_FACTOR)) allocateBucketsEdge(set, set->bucketCount << 1); //there is a possibility of exceeding the load factor
	BucketNode **node = set->buckets + hashEdge(value, set->bucketCount);
	while (*node) {
		if ((*node)->value == value) return; //value already exists
		node = &((*node)->next);
	}
	if (external) set->elementCount++;
	*node = makeNodeEdge(value, *node);
}
void removeElementEdge(EdgeHashSet *set, Edge *value) {
	BucketNode **node = set->buckets + hashEdge(value, set->bucketCount);
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
void freeSetEdge(EdgeHashSet *set) {
	for (unsigned int bucket = 0; bucket < set->bucketCount; bucket++) {
		BucketNode *node = set->buckets[bucket];
		while (node) {
			BucketNode *lastNode = node;
			node = node->next;
			free(lastNode->value); //each edge should be freed as well because they are probably not being used anywhere else (unlike vertices in a vertex set)
			free(lastNode);
		}
	}
	free(set->buckets);
	free(set);
}

struct edgeSetIterator {
	EdgeHashSet *set;
	unsigned int bucket;
	BucketNode *nextNode;
};
void advanceEdge(EdgeSetIterator *iterator) { //if the next node is null, go to the next bucket until finding one that isn't null
	while (!iterator->nextNode) {
		if (hasNextEdge(iterator)) {
			iterator->bucket++;
			if (hasNextEdge(iterator)) iterator->nextNode = iterator->set->buckets[iterator->bucket]; //we have reached the end, so stop trying to look for an element
			else break;
		}
		else break;
	}
}
EdgeSetIterator *iteratorEdge(EdgeHashSet *set) {
	EdgeSetIterator *iterator = malloc(sizeof(*iterator));
	iterator->set = set;
	iterator->bucket = 0;
	iterator->nextNode = set->buckets[iterator->bucket];
	advanceEdge(iterator);
	return iterator;
}
bool hasNextEdge(EdgeSetIterator *iterator) {
	return iterator->bucket != iterator->set->bucketCount;
}
Edge *nextEdge(EdgeSetIterator *iterator) {
	Edge *result = iterator->nextNode->value;
	iterator->nextNode = iterator->nextNode->next;
	advanceEdge(iterator);
	return result;
}