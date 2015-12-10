#include <stdlib.h>
#include <assert.h>

struct node {
	int value;
	struct node *next;
};

struct node *makeEmptyList() {
	return NULL;
}

struct node *makeNode(int value, struct node *next) {
	struct node *newNode = malloc(sizeof(*newNode));
	newNode->value = value;
	newNode->next = next;
	return newNode;
}

//Get the int value stored at index idx in list
//List must be at least idx elements long
int getValueAt(struct node *list, int idx) {
	for (; idx; idx--) list = list->next;
	return list->value;
}

//Get the length of a given list
int length(struct node *list) {
	int i;
	for (i = 0; list; i++) list = list->next;
	return i;
}

//Insert newValue into *list so that getValueAt(*list, idx) == newValue
void insertAt(struct node **list, int idx, int newValue) {
	for (; idx; idx--) list = &((*list)->next);
	*list = makeNode(newValue, *list);
}

//Delete the item at the given index (idx) from *list
void deleteAt(struct node **list, int idx) {
	for (; idx; idx--) list = &((*list)->next);
	struct node *nextElement = (*list)->next;
	free(*list);
	*list = nextElement;
}

//Delete the first occurrence of a given value in *list
void deleteValue(struct node **list, int value) {
	for (; (*list)->value != value;) list = &((*list)->next);
	struct node *nextElement = (*list)->next;
	free(*list);
	*list = nextElement;
}

//Increments every element's value in list
void incrementValues(struct node *list) {
	for (; list; list = list->next) list->value++;
}

//Assume *list is a sorted list (from lowest to highest).
//Insert newValue into *list, maintaining this property.
void insertOrdered(struct node **list, int newValue) {
	for (; *list && (*list)->value < newValue;) list = &((*list)->next); //keep going until reaching the pointer to the end of the list or newValue <= the current value
	*list = makeNode(newValue, *list);
}

/*
	Challenge problem:
	Go to the end of the list, and whatever value is stored there, 
	go back that many nodes, and return the value stored at that node.
	As an especially tricky challenge: can you do this by following only 
	n + m links, TOTAL,
	where n is the number of elements in list, and m is the number stored
	at the last index?*/
int challengeProblem(struct node *list) {
	struct node *previous, *next;
	for (; list; list = next) {
		next = list->next;
		list->next = previous;
		previous = list;
	}
	int backSteps = previous->value; //using previous because list == NULL
	for (; backSteps; backSteps--) previous = previous->next;
	return previous->value;
}

int main() {
	//Testing your code:
	struct node *l = makeEmptyList();
	insertOrdered(&l, 5); //5
	insertOrdered(&l, 2); //2, 5
	insertOrdered(&l, 7); //2, 5, 7
	assert(getValueAt(l, 2) == 7);
	assert(getValueAt(l, 1) == 5);
	assert(getValueAt(l, 0) == 2);
	deleteValue(&l, 7); //2, 5
	assert(length(l) == 2);
	insertAt(&l, 1, 4); //2, 4, 5
	assert(length(l) == 3);
	assert(getValueAt(l, 1) == 4);
	deleteAt(&l, 0); //4, 5
	incrementValues(l); //5, 6
	insertOrdered(&l, 5); //5, 5, 6
	insertOrdered(&l, 2);
	insertOrdered(&l, 2);
	insertOrdered(&l, 1); //1, 1, 2, 5, 5, 6
	insertAt(&l, 6, 4); //1, 1, 2, 5, 5, 6, 4
	assert(getValueAt(l, 4) == 5);
	assert(getValueAt(l, 5) == 6);
	assert(getValueAt(l, 6) == 4);
	assert(challengeProblem(l) == 2);
}
