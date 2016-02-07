#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#define DEFAULT_CAPACITY 10
typedef struct {
	int *elements;
	unsigned int size;
	unsigned int maxSize;
} GrowableArray;

void allocateElements(GrowableArray *array) {
	array->elements = realloc(array->elements, sizeof(*(array->elements)) * array->maxSize);
}
GrowableArray *newGrowableArray() {
	GrowableArray *newArray = malloc(sizeof(*newArray));
	newArray->maxSize = DEFAULT_CAPACITY;
	newArray->elements = NULL;
	allocateElements(newArray);
	newArray->size = 0;
	return newArray;
}
void add(GrowableArray *array, int element) {
	if (array->size == array->maxSize) {
		array->maxSize *= 2;
		allocateElements(array);
	}
	array->elements[array->size] = element;
	array->size++;
}
bool arrayEquals(GrowableArray *array1, GrowableArray *array2) {
	if (array1->size != array2->size) return false;
	else {
		unsigned int count = array1->size;
		int *one = array1->elements;
		int *two = array2->elements;
		for (unsigned int i = 0; i < count; i++) {
			if (one[i] != two[i]) return false;
		}
		return true;
	}
}
void freeGrowableArray(GrowableArray *array) {
	free(array->elements);
	free(array);
}

typedef struct node Node;
struct node {
	int value;
	Node *left, *right;
};

Node *newNode(int value, Node *left, Node *right) {
	Node *node = malloc(sizeof(*node));
	node->value = value;
	node->left = left;
	node->right = right;
	return node;
}
void preorderAddToArray(Node *tree, GrowableArray *array) {
	add(array, tree->value);
	if (tree->left) preorderAddToArray(tree->left, array);
	if (tree->right) preorderAddToArray(tree->right, array);
}
GrowableArray *preorder(Node *tree) {
	GrowableArray *array = newGrowableArray();
	preorderAddToArray(tree, array);
	return array;
}
void inorderAddToArray(Node *tree, GrowableArray *array) {
	if (tree->left) inorderAddToArray(tree->left, array);
	add(array, tree->value);
	if (tree->right) inorderAddToArray(tree->right, array);
}
GrowableArray *inorder(Node *tree) {
	GrowableArray *array = newGrowableArray();
	inorderAddToArray(tree, array);
	return array;
}

int indexOf(int *array, unsigned int start, unsigned int stop, int value) {
	for (unsigned int i = start; i < stop; i++) {
		if (array[i] == value) return i;
	}
	assert(false);
	return -1;
}
/*Strategy: given a certain segment of the inorder array (representing some subtree's values), look for the element that occurs first in the preorder array
This element is the root node's value, so split the inorder segment around that element to get the left and right trees
If there are no elements in the segment preceding this element, it has no left children (and similarly for the right side)
Call the function recursively on each child segment in the inordered array (using some indices tricks to reduce computation time)*/
Node *createTreeFromArrays(int *preordered, int *inordered, unsigned int preorderStart, unsigned int preorderStop, unsigned int inorderStart, unsigned int inorderStop) {
	assert(inorderStart != inorderStop);
	assert(preorderStart != preorderStop);
	unsigned int preorderMin; //will eventually contain the index of the first element in the preorder segment that lies in the inorder segment (the root node)
	unsigned int inorderIndex; //will eventually contain the index in the inorder array of the same element (splits the inorder segment into left and right trees)
	for (unsigned int i = inorderStart; i < inorderStop; i++) {
		int preorderIndex = indexOf(preordered, preorderStart, preorderStop, inordered[i]);
		if (i == inorderStart || preorderIndex < preorderMin) {
			preorderMin = preorderIndex;
			inorderIndex = i;
		}
	}
	Node *left;
	if (inorderStart == inorderIndex) left = NULL; //no left subtree
	else left = createTreeFromArrays(preordered, inordered, preorderMin + 1, preorderStop, inorderStart, inorderIndex);
	Node *right;
	if (inorderIndex == inorderStop - 1) right = NULL; //no right subtree
	else right = createTreeFromArrays(preordered, inordered, preorderMin + 1, preorderStop, inorderIndex + 1, inorderStop);
	return newNode(inordered[inorderIndex], left, right);
}
Node *reconstructTree(int *preordered, int *inordered, unsigned int length) {
	return createTreeFromArrays(preordered, inordered, 0, length, 0, length);
}
void printTree(Node *tree) {
	if (tree->left || tree->right) {
		printf("%c -> ", tree->value);
		if (tree->left) printf("%c ", tree->left->value);
		if (tree->right) printf("%c", tree->right->value);
		putchar('\n');
		if (tree->left) printTree(tree->left);
		if (tree->right) printTree(tree->right);
	}
}
bool treeEquals(Node *tree1, Node *tree2) {
	if (tree1->value != tree2->value) return false;
	if (((bool)tree1->left ^ (bool)tree2->left) || ((bool)tree1->right ^ (bool)tree2->right)) return false; //they must have the same children
	if (tree1->left && !treeEquals(tree1->left, tree2->left)) return false;
	if (tree1->right && !treeEquals(tree1->right, tree2->right)) return false;
	return true;
}
void freeTree(Node *tree) {
	if (tree->left) freeTree(tree->left);
	if (tree->right) freeTree(tree->right);
	free(tree);
}

int main() {
	Node *c = newNode('C', NULL, NULL);
	Node *e = newNode('E', NULL, NULL);
	Node *d = newNode('D', c, e);
	Node *a = newNode('A', NULL, NULL);
	Node *b = newNode('B', a, d);
	Node *h = newNode('H', NULL, NULL);
	Node *i = newNode('I', h, NULL);
	Node *g = newNode('G', NULL, i);
	Node *tree = newNode('F', b, g);
	GrowableArray *desiredPreorder = newGrowableArray();
	add(desiredPreorder, 'F'); add(desiredPreorder, 'B'); add(desiredPreorder, 'A'); add(desiredPreorder, 'D'); add(desiredPreorder, 'C'); add(desiredPreorder, 'E'); add(desiredPreorder, 'G'); add(desiredPreorder, 'I'); add(desiredPreorder, 'H');
	GrowableArray *preordered = preorder(tree);
	assert(arrayEquals(desiredPreorder, preordered));
	freeGrowableArray(desiredPreorder);
	GrowableArray *desiredInorder = newGrowableArray();
	for (char letter = 'A'; letter <= 'I'; letter++) add(desiredInorder, letter);
	GrowableArray *inordered = inorder(tree);
	assert(arrayEquals(desiredInorder, inordered));
	freeGrowableArray(desiredInorder);
	Node *reconstructed = reconstructTree(preordered->elements, inordered->elements, inordered->size);
	printTree(tree);
	putchar('\n');
	printTree(reconstructed);
	assert(treeEquals(tree, reconstructed));
	GrowableArray *preorderReconstructed = preorder(reconstructed);
	GrowableArray *inorderReconstructed = inorder(reconstructed);
	assert(arrayEquals(preordered, preorderReconstructed));
	assert(arrayEquals(inordered, inorderReconstructed));
	freeGrowableArray(preorderReconstructed);
	freeGrowableArray(inorderReconstructed);
	freeGrowableArray(preordered);
	freeGrowableArray(inordered);
	freeTree(tree);
	freeTree(reconstructed);
}