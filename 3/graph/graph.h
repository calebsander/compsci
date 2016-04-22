#ifndef GRAPH_INCLUDED
	#define GRAPH_INCLUDED
	#include "edge.h"
	#include "vertex.h"

	typedef struct graph Graph;

	Graph *makeGraph();
	VertexHashSet *vertices(Graph *graph);
	EdgeHashSet *edges(Graph *graph);
	VertexHashSet *neighbors(Graph *graph, Vertex *vertex);
	Edge *addEdge(Graph *graph, Vertex *vertex1, Vertex *vertex2);
	Vertex *addVertex(Graph *graph, VertexData data);
	void deleteEdge(Graph *graph, Edge *edge);
	void deleteVertex(Graph *graph, Vertex *vertex);
	void freeGraph(Graph *graph);
#endif