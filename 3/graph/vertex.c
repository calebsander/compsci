#include <stdlib.h>
#include "vertex.h"

#define LOAD_FACTOR 0.75
#define DEFAULT_BUCKETS 10
typedef struct bucketNode BucketNode;
struct bucketNode {
	Vertex *value;
	BucketNode *next;
};
struct vertexHashSet {
	unsigned int bucketCount;
	unsigned int elementCount;
	BucketNode **buckets;
};

unsigned int hashVertex(Vertex *value, unsigned int bucketCount) {
	return (unsigned int)(long)value % bucketCount;
}
void addElementResizeVertex(VertexHashSet *set, Vertex *value, bool external);
void allocateBucketsVertex(VertexHashSet *set, unsigned int count) {
	const unsigned int oldCount = set->bucketCount;
	BucketNode **oldBuckets = set->buckets;
	set->bucketCount = count;
	set->buckets = calloc(count, sizeof(*(set->buckets)));
	if (oldBuckets) {
		for (unsigned int oldBucket = 0; oldBucket < oldCount; oldBucket++) {
			BucketNode *node = oldBuckets[oldBucket];
			while (node) {
				addElementResizeVertex(set, node->value, false);
				BucketNode *lastNode = node;
				node = node->next;
				free(lastNode);
			}
		}
		free(oldBuckets);
	}
}
BucketNode *makeNodeVertex(Vertex *value, BucketNode *next) {
	BucketNode *node = malloc(sizeof(*node));
	node->value = value;
	node->next = next;
	return node;
}

VertexHashSet *makeEmptySetVertex() {
	VertexHashSet *set = malloc(sizeof(*set));
	set->buckets = NULL;
	allocateBucketsVertex(set, DEFAULT_BUCKETS);
	set->elementCount = 0;
	return set;
}

bool containsVertex(VertexHashSet *set, Vertex *value) {
	for (BucketNode *node = set->buckets[hashVertex(value, set->bucketCount)]; node; node = node->next) {
		if (node->value == value) return true;
	}
	return false;
}
void addElementVertex(VertexHashSet *set, Vertex *value) {
	addElementResizeVertex(set, value, true);
}
void addElementResizeVertex(VertexHashSet *set, Vertex *value, bool external) {
	if (external && set->elementCount + 1 > (int)((double)set->bucketCount * LOAD_FACTOR)) allocateBucketsVertex(set, set->bucketCount << 1); //there is a possibility of exceeding the load factor
	BucketNode **node = set->buckets + hashVertex(value, set->bucketCount);
	while (*node) {
		if ((*node)->value == value) return; //value already exists
		node = &((*node)->next);
	}
	if (external) set->elementCount++;
	*node = makeNodeVertex(value, *node);
}
void removeElementVertex(VertexHashSet *set, Vertex *value) {
	BucketNode **node = set->buckets + hashVertex(value, set->bucketCount);
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
void freeSetVertex(VertexHashSet *set) {
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

struct vertexSetIterator {
	VertexHashSet *set;
	unsigned int bucket;
	BucketNode *nextNode;
};
void advanceVertex(VertexSetIterator *iterator) { //if the next node is null, go to the next bucket until finding one that isn't null
	while (!iterator->nextNode) {
		if (hasNextVertex(iterator)) {
			iterator->bucket++;
			//Make sure end of set hasn't yet been removed
			if (hasNextVertex(iterator)) iterator->nextNode = iterator->set->buckets[iterator->bucket];
		}
		else break;
	}
}
VertexSetIterator *iteratorVertex(VertexHashSet *set) {
	VertexSetIterator *iterator = malloc(sizeof(*iterator));
	iterator->set = set;
	iterator->bucket = 0;
	iterator->nextNode = set->buckets[iterator->bucket];
	advanceVertex(iterator);
	return iterator;
}
bool hasNextVertex(VertexSetIterator *iterator) {
	return iterator->bucket != iterator->set->bucketCount;
}
Vertex *nextVertex(VertexSetIterator *iterator) {
	Vertex *result = iterator->nextNode->value;
	iterator->nextNode = iterator->nextNode->next;
	advanceVertex(iterator);
	return result;
}