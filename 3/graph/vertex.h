#ifndef VERTEX_INCLUDED
	#define VERTEX_INCLUDED
	#include <stdbool.h>
	typedef int VertexData;
	typedef struct vertexHashSet VertexHashSet;
	typedef struct {
		VertexData data;
		void *decoration;
		VertexHashSet *adjacent;
	} Vertex;

	VertexHashSet *makeEmptySetVertex();
	bool containsVertex(VertexHashSet *set, Vertex *value);
	int weightToVertex(VertexHashSet *set, Vertex *vertex);
	void addElementVertex(VertexHashSet *set, Vertex *value, int weight);
	void removeElementVertex(VertexHashSet *set, Vertex *value);
	unsigned int size(VertexHashSet *set);
	void freeSetVertex(VertexHashSet *set);
	typedef struct vertexSetIterator VertexSetIterator;
	VertexSetIterator *iteratorVertex(VertexHashSet *set);
	bool hasNextVertex(VertexSetIterator *iterator);
	Vertex *nextVertex(VertexSetIterator *iterator);
#endif