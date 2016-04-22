#ifndef EDGE_INCLUDED
	#define EDGE_INCLUDED
	#include "vertex.h"
	typedef struct {
		Vertex *vertex1, *vertex2;
	} Edge;
	typedef struct edgeHashSet EdgeHashSet;

	EdgeHashSet *makeEmptySetEdge();
	bool containsEdge(EdgeHashSet *set, Edge *value);
	void addElementEdge(EdgeHashSet *set, Edge *value);
	void removeElementEdge(EdgeHashSet *set, Edge *value);
	void freeSetEdge(EdgeHashSet *set);
	typedef struct edgeSetIterator EdgeSetIterator;
	EdgeSetIterator *iteratorEdge(EdgeHashSet *set);
	bool hasNextEdge(EdgeSetIterator *iterator);
	Edge *nextEdge(EdgeSetIterator *iterator);
#endif