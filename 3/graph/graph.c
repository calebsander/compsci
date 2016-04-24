#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include "graph.h"
#include "deque.h"

struct graph {
	VertexHashSet *vertices;
};

Graph *makeGraph() {
	Graph *graph = malloc(sizeof(*graph));
	graph->vertices = makeEmptySetVertex();
	return graph;
}

VertexHashSet *vertices(Graph *graph) {
	return graph->vertices;
}
Edge *makeEdge(Vertex *vertex1, Vertex *vertex2) {
	Edge *edge = malloc(sizeof(*edge));
	edge->vertex1 = vertex1;
	edge->vertex2 = vertex2;
	return edge;
}
EdgeHashSet *edges(Graph *graph) {
	VertexSetIterator *vertexIterator = iteratorVertex(graph->vertices);
	EdgeHashSet *edgeSet = makeEmptySetEdge();
	while (hasNextVertex(vertexIterator)) {
		Vertex *vertex = nextVertex(vertexIterator);
		VertexSetIterator *neighbors = iteratorVertex(vertex->adjacent);
		while (hasNextVertex(neighbors)) {
			Vertex *neighbor = nextVertex(neighbors);
			if (vertex < neighbor) addElementEdge(edgeSet, makeEdge(vertex, neighbor)); //guarantee that each edge is only printed once
		}
		free(neighbors);
	}
	free(vertexIterator);
	return edgeSet;
}
VertexHashSet *neighbors(Graph *graph, Vertex *vertex) {
	return vertex->adjacent;
}

Edge *addEdge(Graph *graph, Vertex *vertex1, Vertex *vertex2) { //the returned edge is newly malloc'd and must be freed when doen being used
	if (vertex1 == vertex2) return NULL; //can't have a vertex connected to itself
	else {
		addElementVertex(vertex1->adjacent, vertex2);
		addElementVertex(vertex2->adjacent, vertex1);
		return makeEdge(vertex1, vertex2);
	}
}
Vertex *addVertex(Graph *graph, VertexData data) { //the returned vertex is part of the graph, so it should NOT be freed
	Vertex *vertex = malloc(sizeof(*vertex));
	vertex->data = data;
	vertex->adjacent = makeEmptySetVertex();
	addElementVertex(graph->vertices, vertex);
	return vertex;
}
void deleteEdge(Graph *graph, Edge *edge) { //will free the edge passed in
	removeElementVertex(edge->vertex1->adjacent, edge->vertex2);
	removeElementVertex(edge->vertex2->adjacent, edge->vertex1);
	free(edge);
}
void freeVertex(Vertex *vertex) {
	freeSetVertex(vertex->adjacent);
	free(vertex);
}
void deleteVertex(Graph *graph, Vertex *vertex) { //takes care of freeing the vertex as it is no longer needed
	removeElementVertex(graph->vertices, vertex);
	VertexSetIterator *neighbors = iteratorVertex(vertex->adjacent);
	while (hasNextVertex(neighbors)) removeElementVertex(nextVertex(neighbors)->adjacent, vertex);
	free(neighbors);
	freeVertex(vertex);
}
void printGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	printf("Vertices: ");
	while (hasNextVertex(vertices)) printf("%c ", nextVertex(vertices)->data);
	free(vertices);
	putchar('\n');
	EdgeHashSet *edgeSet = edges(graph);
	EdgeSetIterator *edgeIterator = iteratorEdge(edgeSet);
	while (hasNextEdge(edgeIterator)) {
		const Edge *edge = nextEdge(edgeIterator);
		printf("%c <-> %c\n", edge->vertex1->data, edge->vertex2->data);
	}
	free(edgeIterator);
	freeSetEdge(edgeSet);
}
void traverseDepthFirst(Graph *graph, Vertex *start, void (*visit)(Vertex *)) {
	VertexHashSet *visited = makeEmptySetVertex();
	addElementVertex(visited, start);
	Deque *stack = makeEmptyDeque();
	pushFront(start, stack);
	while (!isEmptyDeque(stack)) { //while some connected vertices haven't yet been looked at
		Vertex *current = popFront(stack); //look at the current vertex
		(*visit)(current);
		VertexHashSet *adjacent = neighbors(graph, current);
		VertexSetIterator *neighborIterator = iteratorVertex(adjacent);
		while (hasNextVertex(neighborIterator)) { //add any unadded neighbors to the queue
			Vertex *next = nextVertex(neighborIterator);
			if (!containsVertex(visited, next)) {
				addElementVertex(visited, next);
				pushFront(next, stack);
			}
		}
	}
	freeSetVertex(visited);
	freeDeque(stack);
}
void freeGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) deleteVertex(graph, nextVertex(vertices));
	free(vertices);
	freeSetVertex(graph->vertices);
	free(graph);
}