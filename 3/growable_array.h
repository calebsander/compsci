#include <stdbool.h>

typedef struct {
	int *elements;
	unsigned int size; //number of elements currently being stored
	unsigned int allocatedSize; //number of elements that can be stored without realloc'ing
} ArrayList;
ArrayList *newArrayList(void); //creates an ArrayList with a default capcity
ArrayList *newArrayListWithCapacity(unsigned int initialCapacity); //creates an ArrayList with the specified initial capacity
void add(ArrayList *list, int value);
void addAll(ArrayList *list, ArrayList *otherList); //adds each element of the other list to the end of list
void addAt(ArrayList *list, unsigned int index, int value);
void addAllAt(ArrayList *list, unsigned int index, ArrayList *otherList); //adds each element of the other list at the specified index
void ensureCapacity(ArrayList *list, unsigned int capacity); //allocates enough space to store the desired capacity
int get(ArrayList *list, unsigned int index);
int indexOf(ArrayList *list, int value);
int lastIndexOf(ArrayList *list, int value);
int removeAt(ArrayList *list, unsigned int index); //returns removed value
void removeBetween(ArrayList *list, unsigned int start, unsigned int end); //removes all values with start ≤ index < end
void set(ArrayList *list, unsigned int index, int value);
unsigned int size(ArrayList *list);
ArrayList *subList(ArrayList *list, unsigned int start, unsigned int end);
void trim(ArrayList *list); //frees unnecessary memory
void freeArrayList(ArrayList *list); //deallocates list and its components

typedef struct {
	ArrayList *list;
	unsigned int index; //the current index being iterated at
} ListIterator;
ListIterator *iterator(ArrayList *list); //creates an iterator for the list
bool hasNext(ListIterator *iterator);
int next(ListIterator *iterator);
void remove(ListIterator *iterator); //removes from the list the last element returned by next()