#include <stdbool.h>

typedef struct node Node;
struct node {
	Node *left, *right;
};

bool isNode(Node *node) {
	return node->left || node->right;
}
bool isLeaf(Node *node) {
	return !isNode(node);
}

/*
Suppose, for a contradiction, that there is some letter x whose encoding is a proper initial segment of a letter y. Then tracing the path to y requires going through x, violating the fact that x has no children. Therefore no letter's encoding is a proper initial segment of another letter's.
*/