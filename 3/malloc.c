#include <assert.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>

typedef struct md BlockMetadata;
struct md {
	bool used;
	size_t size;
	BlockMetadata *next;
};

#define SIZE 1000000 //number of bytes to have available
char heap[SIZE];
char *heapPointer = heap;
BlockMetadata *lastBlock = NULL;

bool successfullyFound;
void lookDownHeap(bool (*process)(BlockMetadata *, void *ctx), void *ctx) {
	successfullyFound = false;
	BlockMetadata *currentBlock = lastBlock;
	while (currentBlock && !successfullyFound) {
		successfullyFound = (*process)(currentBlock, ctx);
		currentBlock = currentBlock->next;
	}
}

void *new_malloc(size_t size) {
	BlockMetadata *metadata = (BlockMetadata *)heapPointer;
	metadata->used = true;
	metadata->size = size;
	metadata->next = lastBlock;
	lastBlock = metadata;
	heapPointer += size + sizeof(BlockMetadata);
	return metadata + 1;
}
BlockMetadata *blockToUse;
bool reusable(BlockMetadata *block, void *ctx) {
	const size_t size = *((size_t *)ctx);
	if (!block->used && block->size >= size) {
		blockToUse = block;
		return true;
	}
	return false;
}
void *our_malloc(size_t size) {
	blockToUse = NULL;
	lookDownHeap(&reusable, &size);
	if (blockToUse) { //if there is a block that can be reused
		blockToUse->used = true;
		return blockToUse + 1;
	}
	else return new_malloc(size);
}

bool printBlock(BlockMetadata *block, void *ctx) {
	printf("Block %p, Size: %zu, Used: %s\n", (void *)(block + 1), block->size, block->used ? "true" : "false");
	return false;
}
void printMemoryInformation() {
	lookDownHeap(&printBlock, NULL);
	putchar('\n');
}

bool tryToFree(BlockMetadata *block, void *address) {
	if (block->used && block + 1 == address) {
		block->used = false;
		return true;
	}
	return false;
}
void our_free(void *address) {
	if (address) { //if address == NULL, nothing needs to be one
		lookDownHeap(&tryToFree, address);
		if (!successfullyFound) {
			fprintf(stderr, "%p is not a valid address\n", address);
			exit(1);
		}
	}
}

void *our_realloc(void *address, size_t size) {
	our_free(address); //ensure address exists
	BlockMetadata *metadata = (BlockMetadata *)address - 1;
	if (size > metadata->size) {
		void *newAddress = our_malloc(size);
		memmove(newAddress, address, metadata->size); //migrate all old data
		return newAddress;
	}
	else { //can reuse old block
		metadata->used = true; //since free() was called
		return address;
	}
}

void *our_calloc(size_t size) {
	void *address = our_malloc(size);
	memset(address, 0, size);
	return address;
}

int main() {
	memset(heap, 42, SIZE); //pretend that we have garbage in the heap memory
	int *array = our_malloc(sizeof(*array) * 5);
	for (int i = 0; i != 5; i++) array[i] = i;
	for (int i = 0; i != 5; i++) assert(i == array[i]);
	int *array2 = our_calloc(sizeof(*array2) * 1000);
	for (int i = 0; i != 1000; i++) assert(!array2[i]); //ensure array2 was properly cleared
	printMemoryInformation();
	our_free(NULL);
	our_free(array);
	//our_free(array); (correctly throws an error)
	printMemoryInformation();
	int *array3 = our_malloc(sizeof(*array3) * 5);
	for (int i = 0; i != 5; i++) array3[i] = i;
	printMemoryInformation();
	array3 = our_realloc(array3, sizeof(*array3) * 10);
	for (int i = 0; i != 5; i++) assert(i == array3[i]);
	printMemoryInformation();
	our_free(array3);
	printMemoryInformation();
	int *array4 = our_malloc(sizeof(*array4) * 9);
	assert(array4 == array3);
	printMemoryInformation();
	int *array5 = our_malloc(sizeof(*array5) * 5);
	assert(array5 == array);
	printMemoryInformation();
	our_free(array2);
	our_free(array4);
	our_free(array5);
	printMemoryInformation();
}