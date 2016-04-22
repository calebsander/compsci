#include <stdlib.h>
#include <stdio.h>
#include "graph.h"

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
void freeGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) deleteVertex(graph, nextVertex(vertices));
	free(vertices);
	freeSetVertex(graph->vertices);
	free(graph);
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
/*int main() {
	Graph *graph = makeGraph();
	Vertex *a = addVertex(graph, 'a');
	Vertex *b = addVertex(graph, 'b');
	Vertex *c = addVertex(graph, 'c');
	Vertex *d = addVertex(graph, 'd');
	Vertex *e = addVertex(graph, 'e');
	printGraph(graph); //a b c d e
	deleteVertex(graph, c);
	printGraph(graph); //a b d e
	Edge *edge1 = addEdge(graph, a, b);
	Edge *edge2 = addEdge(graph, d, b);
	Edge *edge4 = addEdge(graph, e, d);
	Edge *edge5 = addEdge(graph, d, a);
	free(edge2); free(edge4); free(edge5);
	printGraph(graph); //a <-> b, b <-> d, d <-> e, a <-> d
	putchar('\n');
	VertexSetIterator *adjacent = iteratorVertex(neighbors(graph, d));
	while (hasNextVertex(adjacent)) printf("%c ", nextVertex(adjacent)->data); //a b e
	free(adjacent);
	puts("\n");
	deleteEdge(graph, edge1);
	printGraph(graph); //b <-> d, d <-> e, a <-> d
	deleteVertex(graph, e);
	deleteVertex(graph, b);
	printGraph(graph); //a <-> d
	freeGraph(graph);
}*/