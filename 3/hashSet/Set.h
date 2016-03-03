#include <stdbool.h>
typedef struct hashSet HashSet;

HashSet *makeEmptySet();
unsigned int size(HashSet *set);
bool contains(HashSet *set, int value);
void addElement(HashSet *set, int value);
void removeElement(HashSet *set, int value);
void printSet(HashSet *set);
void freeSet(HashSet *set);