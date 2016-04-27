#include <limits.h>
#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include "graph.h"
#include "deque.h"

struct graph {
	VertexHashSet *vertices; //used as just a set rather than a map of vertices to weights
};

Graph *makeGraph() {
	Graph *graph = malloc(sizeof(*graph));
	graph->vertices = makeEmptySetVertex();
	return graph;
}

VertexHashSet *vertices(Graph *graph) {
	return graph->vertices;
}
Edge *makeEdge(Vertex *vertex1, Vertex *vertex2, int weight) {
	Edge *edge = malloc(sizeof(*edge));
	edge->vertex1 = vertex1;
	edge->vertex2 = vertex2;
	edge->weight = weight;
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
			if (vertex < neighbor) addElementEdge(edgeSet, makeEdge(vertex, neighbor, weightToVertex(vertex->adjacent, neighbor))); //guarantee that each edge is only printed once
		}
		free(neighbors);
	}
	free(vertexIterator);
	return edgeSet;
}
VertexHashSet *neighbors(Graph *graph, Vertex *vertex) {
	return vertex->adjacent;
}

Edge *addEdge(Graph *graph, Vertex *vertex1, Vertex *vertex2, int weight) { //the returned edge is newly malloc'd and must be freed when doen being used
	if (vertex1 == vertex2) return NULL; //can't have a vertex connected to itself
	else {
		addElementVertex(vertex1->adjacent, vertex2, weight);
		addElementVertex(vertex2->adjacent, vertex1, weight);
		return makeEdge(vertex1, vertex2, weight);
	}
}
Vertex *addVertex(Graph *graph, VertexData data) { //the returned vertex is part of the graph, so it should NOT be freed
	Vertex *vertex = malloc(sizeof(*vertex));
	vertex->data = data;
	vertex->adjacent = makeEmptySetVertex();
	vertex->distanceFromStart = -1;
	addElementVertex(graph->vertices, vertex, 0);
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
	printf("Vertices:\n");
	while (hasNextVertex(vertices)) {
		Vertex *vertex = nextVertex(vertices);
		printf("%c - %d\n", vertex->data, vertex->distanceFromStart);
	}
	free(vertices);
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
	VertexHashSet *visited = makeEmptySetVertex(); //used as just a set rather than a map of vertices to weights
	addElementVertex(visited, start, 0);
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
				addElementVertex(visited, next, 0);
				pushFront(next, stack);
			}
		}
	}
	freeSetVertex(visited);
	freeDeque(stack);
}
bool labelNextVertex(Graph *graph) { //returns whether it filled one in
	Vertex *minVertex = NULL;
	int minDistance = INT_MAX;
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) {
		Vertex *hostVertex = nextVertex(vertices);
		const int distanceToHost = hostVertex->distanceFromStart;
		if (distanceToHost != -1) { //if hostVertex is in the calculated set
			VertexSetIterator *neighbors = iteratorVertex(hostVertex->adjacent);
			while (hasNextVertex(neighbors)) { //look at uncalculated neighbors
				Vertex *neighbor = nextVertex(neighbors);
				const int nextDistance = weightToVertex(hostVertex->adjacent, neighbor);
				if (neighbor->distanceFromStart == -1) {
					const int totalDistance = distanceToHost + nextDistance;
					if (totalDistance < minDistance) {
						minVertex = neighbor;
						minDistance = totalDistance;
					}
				}
			}
			free(neighbors);
		}
	}
	free(vertices);
	if (minVertex) {
		minVertex->distanceFromStart = minDistance;
		return true;
	}
	else return false;
}
int shortestPath(Graph *graph, Vertex *start, Vertex *end) {
	start->distanceFromStart = 0;
	while (labelNextVertex(graph)) {
		if (end->distanceFromStart != -1) return end->distanceFromStart;
	}
	return -1;
}
void freeGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) deleteVertex(graph, nextVertex(vertices));
	free(vertices);
	freeSetVertex(graph->vertices);
	free(graph);
}