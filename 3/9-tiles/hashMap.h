#include <stdbool.h>
#include "position.h"

typedef struct hashMap HashMap;

HashMap *makeEmptyMap();
unsigned int size(HashMap *map);
bool contains(HashMap *map, Position *key);
void put(HashMap *map, Position *key, Position *from, unsigned short length);
Position *getFrom(HashMap *map, Position *key);
unsigned short getLength(HashMap *map, Position *key);
void freeEntries(HashMap *map, Position *initial, Position *goal); //frees the positions and their strings, skipping the specified non-malloced ones
void freeMap(HashMap *map);
