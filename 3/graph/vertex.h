#ifndef VERTEX_INCLUDED
	#define VERTEX_INCLUDED
	#include <stdbool.h>
	typedef int VertexData;
	typedef struct vertexHashSet VertexHashSet;
	typedef struct {
		VertexData data;
		VertexHashSet *adjacent;
	} Vertex;

	VertexHashSet *makeEmptySetVertex();
	bool containsVertex(VertexHashSet *set, Vertex *value);
	void addElementVertex(VertexHashSet *set, Vertex *value);
	void removeElementVertex(VertexHashSet *set, Vertex *value);
	void freeSetVertex(VertexHashSet *set);
	typedef struct vertexSetIterator VertexSetIterator;
	VertexSetIterator *iteratorVertex(VertexHashSet *set);
	bool hasNextVertex(VertexSetIterator *iterator);
	Vertex *nextVertex(VertexSetIterator *iterator);
#endif