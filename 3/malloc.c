#include <assert.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

typedef struct md BlockMetadata;
struct md {
	size_t size;
	BlockMetadata *next; //only needs to be set if on the free list
};

#define SIZE 1000000 //number of bytes to have available
char heap[SIZE];
char *heapPointer = heap;
BlockMetadata *freeBlocks = NULL;

bool successfullyFound;
void lookDownHeap(bool (*process)(BlockMetadata *, void *), void *ctx) {
	successfullyFound = false;
	BlockMetadata *currentBlock = freeBlocks;
	while (currentBlock && !successfullyFound) {
		successfullyFound = (*process)(currentBlock, ctx);
		currentBlock = currentBlock->next;
	}
}
void addToFreeList(BlockMetadata *block) {
	const size_t size = block->size;
	BlockMetadata **currentBlock = &freeBlocks;
	while (*currentBlock && (*currentBlock)->size < size) currentBlock = &(*currentBlock)->next;
	block->next = *currentBlock;
	*currentBlock = block;
}
void removeFromFreeList(BlockMetadata *block) {
	BlockMetadata **currentBlock = &freeBlocks;
	while (*currentBlock) {
		if (*currentBlock == block) {
			*currentBlock = block->next;
			break;
		}
		currentBlock = &(*currentBlock)->next;
	}
}
bool isFree(BlockMetadata *block) {
	for (BlockMetadata *currentBlock = freeBlocks; currentBlock; currentBlock = currentBlock->next) {
		if (currentBlock == block) return true;
	}
	return false;
}
//If a sufficient space can be split off the block, resize the block and return the new block's metadata
//Else, un-free the block and return it
BlockMetadata *split(BlockMetadata *block, size_t size) {
	removeFromFreeList(block);
	const size_t otherSize = size + sizeof(*block); //size of next block and its metadata
	if (block->size > otherSize) { //if there would be at least 1 byte left over in this block after the split
		const size_t newSize = block->size - otherSize; //new size for this block (after splitting off the other block)
		block->size = newSize;
		addToFreeList(block);
		BlockMetadata *newBlock = block + newSize; //split off the later part of the block so freed blocks don't need to be updated
		newBlock->size = size;
		return newBlock;
	}
	else return block;
}

void *new_malloc(size_t size) {
	BlockMetadata *metadata = (BlockMetadata *)heapPointer;
	metadata->size = size;
	heapPointer += sizeof(*metadata) + size;
	return metadata + 1;
}
BlockMetadata *blockToUse;
bool reusable(BlockMetadata *block, void *ctx) {
	const size_t size = *((size_t *)ctx);
	if (block->size >= size) {
		blockToUse = block;
		return true;
	}
	else return false;
}
void *our_malloc(size_t size) {
	blockToUse = NULL;
	lookDownHeap(&reusable, &size);
	if (blockToUse) { //if there is a block that can be reused
		blockToUse = split(blockToUse, size);
		return blockToUse + 1;
	}
	else return new_malloc(size);
}

bool printBlock(BlockMetadata *block, void *ctx) {
	printf("Block %p, Size: %zu\n", (void *)(block + 1), block->size);
	return false;
}
void printMemoryInformation() {
	puts("Free blocks:");
	lookDownHeap(&printBlock, NULL);
	putchar('\n');
}

BlockMetadata *getNextBlock(BlockMetadata *block) {
	return (BlockMetadata *)((char *)block + sizeof(*block) + block->size);
}
void joinBlocks(BlockMetadata *block) {
	BlockMetadata *nextBlock = getNextBlock(block);
	removeFromFreeList(nextBlock);
	block->size += sizeof(*nextBlock) + nextBlock->size;
	fprintf(stderr, "Joining %p with %p\n", (void *)(block + 1), (void *)(nextBlock + 1));
}
bool lookForPreviousBlock(BlockMetadata *block, void *ctx) {
	if (getNextBlock(block) == (BlockMetadata *)ctx) {
		joinBlocks(block);
		return true;
	}
	else return false;
}
void our_free(void *address) {
	if (address) { //if address == NULL, nothing needs to be one
		BlockMetadata *metadata = (BlockMetadata *)address - 1;
		BlockMetadata *nextBlock = getNextBlock(metadata);
		if ((char *)nextBlock != heapPointer) {
			if (isFree(nextBlock)) joinBlocks(metadata); //if the next block exists and is free, join the two blocks
		}
		addToFreeList(metadata);
		lookDownHeap(&lookForPreviousBlock, metadata); //if the previous block is free, join it with this one
	}
}

int main() {
	memset(heap, 42, SIZE); //pretend that we have garbage in the heap memory
	uint32_t *a = our_malloc(sizeof(*a) * 10);
	printf("a stored in ");
	printBlock((BlockMetadata *)a - 1, NULL);
	printMemoryInformation();
	our_free(a);
	printMemoryInformation();
	uint32_t *b = our_malloc(sizeof(*b) * 20); //should require a new block
	printf("b stored in ");
	printBlock((BlockMetadata *)b - 1, NULL);
	printMemoryInformation();
	uint32_t *c = our_malloc(sizeof(*c) * 9); //should go in a's old block
	printf("c stored in ");
	printBlock((BlockMetadata *)c - 1, NULL);
	printMemoryInformation(); //should be no free blocks left
	assert(c == a);
	uint32_t *smallD = our_malloc(sizeof(*smallD) * 100);
	uint32_t *bigD = our_malloc(sizeof(*bigD) * 1000);
	printf("small stored in ");
	printBlock((BlockMetadata *)smallD - 1, NULL);
	printf("big stored in ");
	printBlock((BlockMetadata *)bigD - 1, NULL);
	our_free(smallD);
	our_free(bigD);
	printMemoryInformation(); //should have one block of 400 and one block of 4000 free
	uint32_t *e = our_malloc(100);
	printf("e stored in ");
	printBlock((BlockMetadata *)e - 1, NULL);
	printMemoryInformation();
}