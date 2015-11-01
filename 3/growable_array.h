#include <stdbool.h>

typedef struct {
	int *elements;
	unsigned int size;
	unsigned int allocatedSize;
} ArrayList;
ArrayList *newArrayList(void);
ArrayList *newArrayListWithCapacity(unsigned int initialCapacity);
void add(ArrayList *list, int value);
void addAll(ArrayList *list, ArrayList *otherList);
void addAt(ArrayList *list, unsigned int index, int value);
void addAllAt(ArrayList *list, unsigned int index, ArrayList *otherList);
void *get(ArrayList *list, unsigned int index);
int indexOf(ArrayList *list, int value);
int removeAt(ArrayList *list, unsigned int index); //returns removed value
void removeBetween(ArrayList *list, unsigned int start, unsigned int end);
void set(ArrayList *list, unsigned int index, int value);
ArrayList *subList(ArrayList *list, unsigned int start, unsigned int end);
void trimToSize(ArrayList *list);
void freeArrayList(ArrayList *list);

typedef struct {
	ArrayList *list;
	unsigned int index;
} ListIterator;
ListIterator *iterator(ArrayList *list);
bool hasNext(ListIterator *iterator);
void *next(ListIterator *iterator);
void remove(ListIterator *iterator);
void freeIterator(ListIterator *iterator);