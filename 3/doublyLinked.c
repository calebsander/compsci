#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

//A node in a list of strings
struct node {
	struct node *next;
	struct node *prev;
	char *value; //each node stores a string
};

/* A headed, circular doubly linked list is a linked list where
 * each node contains a next pointer and a prev pointer. The empty
 * list is represented by a pointer to a single node, the "head" node,
 * with next and prev set to *itself*. The value of the head node is NULL.
 * A non-empty list consists of a head whose next pointer points to the
 * first node of the list, and whose prev pointer points to the last node
 * of the list. The reason for this is that we'd like to preserve the axiom
 *     n->next->prev == n->prev->next == n
 * for any node *n. This is why we call the list circular: there are no NULL
 * pointers.
 *
 * The nice thing about circular doubly linked lists is that you can easily
 * remove or insert a node without having a pointer to the head of the list.
 * We might use this data structure to represent, for example, the players
 * in a board game, keeping a pointer to the current player node and removing
 * players when they lose. We could also use it for any other naturally circular
 * data, like the spaces on the board in a board game like Monopoly.
 */

//Malloc space for a new node with given properties
struct node *makeNode(char *value, struct node *next, struct node *prev) {
	struct node *node = malloc(sizeof(*node));
	node->value = value;
	node->next = next;
	node->prev = prev;
	return node;
}

//Malloc space for (and return) the head of an empty list.
struct node *makeEmptyList() {
	struct node *list = makeNode(NULL, NULL, NULL);
	list->next = list;
	list->prev = list;
	return list;
}

bool isEmpty(struct node *node) {
	return node->next == node;
}
bool isHeadNode(struct node *node) {
	return !node->value;
}

//Given a pointer to *any node* of a circular doubly linked list,
//return the list's length.
int length(struct node *list) {
	struct node *origNode = list;
	list = list->next;
	int i;
	for (i = 0; list != origNode; i++) list = list->next;
	return i;
}

//Insert a new node with the given (string) value after
//the given node.
void insertAfter(struct node *node, char *newValue) {
	struct node *newNode = makeNode(newValue, node->next, node);
	node->next->prev = newNode;
	node->next = newNode;
}

//Insert a new node with the given (string) value before
//the given node.
void insertBefore(struct node *node, char *newValue) {
	insertAfter(node->prev, newValue);
}

//Delete node from the list (and free the space associated with it).
//Assume node does not point to the header.
void deleteNode(struct node *node) {
	node->prev->next = node->next;
	node->next->prev = node->prev;
	free(node);
}

//If node is any node in a sorted list, insert
//a new node containing newValue into the sorted list,
//keeping the list sorted correctly. (Values are sorted in
//ascending lexicographic order, starting at the head.)
void insertOrdered(struct node *node, char *newValue) {
	if (isEmpty(node)) insertAfter(node, newValue); //if there is no element to compare with, just do an immediate insertion
	else {
		if (isHeadNode(node)) node = node->next; //if at the head, move to a node in the list
		int cmp = strcmp(node->value, newValue);
		if (cmp > 0) { //need to go to the previous ones
			do {
				node = node->prev;
			} while (!isHeadNode(node) && strcmp(node->value, newValue) > 0);
			insertAfter(node, newValue);
		}
		else if (cmp < 0) { //need to go to the next ones
			do {
				node = node->next;
			} while (!isHeadNode(node) && strcmp(node->value, newValue) < 0);
			insertBefore(node, newValue);
		}
		else insertAfter(node, newValue); //equal; can be inserted here
	}
}

//Get pointer to the element occurring n elements after the given node
//(not counting the head).
//getElement(head, 0) should return first element, not head.
//getElement(last, 1) should return first element, not head.
//getElement(first, 1) should return second element, or, if
//  the list has only one element, the first element. (By adding
//  one, we "wrap around.")
//If start is the head and the list is empty, return NULL.
struct node *getElement(struct node *start, int index) {
	if (isEmpty(start)) return NULL; //empty list
	if (isHeadNode(start)) index++; //head element, so we need to skip over it
	for (; index; index--) {
		start = start->next;
		if (isHeadNode(start)) index++; //head element, so we need to skip over it (do an extra advancement)
	}
	return start;
}

int main(int argc, char **argv) {
	struct node *l = makeEmptyList();
	assert(length(l) == 0);
	assert(getElement(l, 5) == NULL);
	char *two = "b";
	char *three = "c";
	char *four = "d";
	char *five = "e";
	char *seven = "g";
	insertOrdered(l, five); //5
	insertOrdered(l, two); //2, 5
	insertOrdered(l, seven); //2, 5, 7
	assert(getElement(l, 2)->value == seven);
	assert(getElement(l, 1)->value == five);
	assert(getElement(l, 0)->value == two);
	assert(getElement(l, 8)->value == seven);
	assert(getElement(getElement(l, 3), 4)->value == five);
	deleteNode(getElement(l, 2)); //2, 5
	assert(length(l) == 2);
	assert(length(l->next) == 2);
	assert(length(l->prev) == 2);
	insertAfter(l->next, four); //2, 4, 5
	insertBefore(l->next, three); //3, 2, 4, 5
	assert(length(l) == 4);
	assert(getElement(l, 1)->value == two);
}