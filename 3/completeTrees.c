#include <assert.h>
#include <stdbool.h>
#include <stdlib.h>

typedef struct {
	int *values;
	unsigned int length;
} CompleteTree;

unsigned int getRoot(CompleteTree *tree) {
	return 0;
}
unsigned int getRightChild(CompleteTree *tree, unsigned int i);
bool isLeaf(CompleteTree *tree, unsigned int i) {
	return tree->length < getRightChild(tree, i);
}
unsigned int getLeftChild(CompleteTree *tree, unsigned int i) {
	return (i << 1) + 1;
}
unsigned int getRightChild(CompleteTree *tree, unsigned int i) {
	return (i << 1) + 2;
}
unsigned int getLeftSibling(CompleteTree *tree, unsigned int i) {
	return i - 1;
}
unsigned int getRightSibling(CompleteTree *tree, unsigned int i) {
	return i + 1;
}
unsigned int getAncestor(CompleteTree *tree, unsigned int i, unsigned int n) {
	return ((i + 1) >> n) - 1;
}
unsigned int getParent(CompleteTree *tree, unsigned int i) {
	return getAncestor(tree, i, 1);
}
unsigned int getDepth(CompleteTree *tree, unsigned int i) {
	i = getParent(tree, i) + 1; //overflow is no problem
	unsigned int depth = 0;
	while (i) {
		i >>= 1;
		depth++;
	}
	return depth;
}
unsigned int getHeight(CompleteTree *tree, unsigned int i) {
	const unsigned int lastElement = tree->length - 1;
	const unsigned int depthDifference = getDepth(tree, lastElement) - getDepth(tree, i);
	const unsigned int thisHeightEquivalent = getAncestor(tree, lastElement, depthDifference);
	if (thisHeightEquivalent < i) return depthDifference; //this node has no descendants on the last row
	else return depthDifference + 1;
}

int main() {
	CompleteTree testTree = {NULL, 12};
	/*
	        0
	   1         2
	 3   4     5    6
	7 8 9 10 11
	*/
	CompleteTree *tree = &testTree;
	for (unsigned int i = 0; i < 12; i++) {
		if (i < 6) assert(!isLeaf(tree, i));
		else assert(isLeaf(tree, i));
	}
	assert(getLeftChild(tree, 0) == 1);
	assert(getLeftChild(tree, 2) == 5);
	assert(getLeftChild(tree, 4) == 9);
	assert(getLeftChild(tree, 5) == 11);
	for (unsigned int i = 4; i < 7; i++) assert(getLeftSibling(tree, i) == (i - 1));
	for (unsigned int i = 3; i < 6; i++) assert(getRightSibling(tree, i) == (i + 1));
	for (unsigned int i = 1; i < 3; i++) assert(getParent(tree, i) == 0);
	for (unsigned int i = 3; i < 5; i++) assert(getParent(tree, i) == 1);
	for (unsigned int i = 5; i < 7; i++) assert(getParent(tree, i) == 2);
	for (unsigned int i = 7; i < 9; i++) assert(getParent(tree, i) == 3);
	for (unsigned int i = 9; i < 11; i++) assert(getParent(tree, i) == 4);
	assert(getParent(tree, 11) == 5);
	assert(!getDepth(tree, 0));
	for (unsigned int i = 1; i < 3; i++) assert(getDepth(tree, i) == 1);
	for (unsigned int i = 3; i < 7; i++) assert(getDepth(tree, i) == 2);
	for (unsigned int i = 7; i < testTree.length; i++) assert(getDepth(tree, i) == 3);
	assert(getHeight(tree, 0) == 4);
	for (unsigned int i = 1; i < 3; i++) assert(getHeight(tree, i) == 3);
	for (unsigned int i = 3; i < 6; i++) assert(getHeight(tree, i) == 2);
	for (unsigned int i = 6; i < testTree.length; i++) assert(getHeight(tree, i) == 1);
}