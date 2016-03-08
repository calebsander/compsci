#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "hashMap.h"

#define LOAD_FACTOR 5
#define DEFAULT_BUCKETS 1024
typedef struct bucketNode BucketNode;
struct bucketNode {
	Position *key;
	Position *from;
	unsigned short length;
	BucketNode *next;
};
struct hashMap {
	unsigned int bucketCount;
	unsigned int elementCount;
	BucketNode **buckets;
};

unsigned int hash(Position *value, unsigned int bucketCount) {
	return hashCode(value) % bucketCount;
}
void addElementResize(HashMap *map, Position *key, Position *from, unsigned short length, bool external);
void allocateBuckets(HashMap *map, unsigned int count) {
	const unsigned int oldCount = map->bucketCount;
	BucketNode **oldBuckets = map->buckets;
	map->bucketCount = count;
	map->buckets = calloc(count, sizeof(*(map->buckets)));
	if (oldBuckets) { //if the map hadn't yet been created
		for (unsigned int oldBucket = 0; oldBucket < oldCount; oldBucket++) {
			BucketNode *node = oldBuckets[oldBucket];
			while (node) {
				addElementResize(map, node->key, node->from, node->length, false);
				BucketNode *lastNode = node;
				node = node->next;
				free(lastNode);
			}
		}
		free(oldBuckets);
	}
}
BucketNode *makeNode(Position *key, Position *from, unsigned short length, BucketNode *next) {
	BucketNode *node = malloc(sizeof(*node));
	node->key = key;
	node->from = from;
	node->length = length;
	node->next = next;
	return node;
}

HashMap *makeEmptyMap() {
	HashMap *map = malloc(sizeof(*map));
	map->buckets = NULL;
	allocateBuckets(map, DEFAULT_BUCKETS);
	map->elementCount = 0;
	return map;
}

unsigned int size(HashMap *map) {
	unsigned int result = 0;
	for (unsigned int bucket = 0; bucket < map->bucketCount; bucket++) {
		for (BucketNode *node = map->buckets[bucket]; node; node = node->next) result++;
	}
	return result;
}
bool contains(HashMap *map, Position *key) {
	for (BucketNode *node = map->buckets[hash(key, map->bucketCount)]; node; node = node->next) {
		if (equals(node->key, key)) return true;
	}
	return false;
}
void put(HashMap *map, Position *key, Position *from, unsigned short length) { //no checking is done to ensure that the key is not alreay in the map
	addElementResize(map, key, from, length, true);
}
void addElementResize(HashMap *map, Position *key, Position *from, unsigned short length, bool external) {
	if (external && map->elementCount + 1 > map->bucketCount * LOAD_FACTOR) allocateBuckets(map, map->bucketCount << 1); //there is a possibility of exceeding the load factor
	BucketNode **node = map->buckets + hash(key, map->bucketCount);
	if (external) map->elementCount++;
	*node = makeNode(key, from, length, *node);
	assert(!(external && map->elementCount != size(map)));
}
Position *getFrom(HashMap *map, Position *key) {
	for (BucketNode *node = map->buckets[hash(key, map->bucketCount)]; node; node = node->next) {
		if (equals(node->key, key)) return node->from;
	}
	assert(false);
	return NULL;
}
unsigned short getLength(HashMap *map, Position *key) {
	for (BucketNode *node = map->buckets[hash(key, map->bucketCount)]; node; node = node->next) {
		if (equals(node->key, key)) return node->length;
	}
	assert(false);
	return 0;
}
void freeEntries(HashMap *map, Position *initial, Position *goal) {
	for (unsigned int bucket = 0; bucket < map->bucketCount; bucket++) {
		for (BucketNode *node = map->buckets[bucket]; node; node = node->next) {
			if (!(equals(node->key, initial) || equals(node->key, goal))) freePosition(node->key);
		}
	}
}
void freeMap(HashMap *map) {
	for (unsigned int bucket = 0; bucket < map->bucketCount; bucket++) {
		BucketNode *node = map->buckets[bucket];
		while (node) {
			BucketNode *lastNode = node;
			node = node->next;
			free(lastNode);
		}
	}
	free(map->buckets);
	free(map);
}
