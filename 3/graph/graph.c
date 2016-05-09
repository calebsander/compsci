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
			addElementEdge(edgeSet, makeEdge(vertex, neighbor, weightToVertex(vertex->adjacent, neighbor))); //guarantee that each edge is only printed once
		}
		free(neighbors);
	}
	free(vertexIterator);
	return edgeSet;
}
VertexHashSet *neighbors(Graph *graph, Vertex *vertex) {
	return vertex->adjacent;
}

Edge *addEdge(Graph *graph, Vertex *vertex1, Vertex *vertex2, int weight) { //the returned edge is newly malloc'd and must be freed when done being used
	if (vertex1 == vertex2) return NULL; //can't have a vertex connected to itself
	else {
		addElementVertex(vertex1->adjacent, vertex2, weight);
		return makeEdge(vertex1, vertex2, weight);
	}
}
Vertex *addVertex(Graph *graph, VertexData data) { //the returned vertex is part of the graph, so it should NOT be freed
	Vertex *vertex = malloc(sizeof(*vertex));
	vertex->data = data;
	vertex->adjacent = makeEmptySetVertex();
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
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) removeElementVertex(nextVertex(vertices)->adjacent, vertex);
	free(vertices);
	freeVertex(vertex);
}
void printVertex(Vertex *vertex) {
	printf("%c\n", vertex->data);
}
void printGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	puts("Vertices:");
	while (hasNextVertex(vertices)) {
		Vertex *vertex = nextVertex(vertices);
		printVertex(vertex);
	}
	free(vertices);
	puts("Edges:");
	EdgeHashSet *edgeSet = edges(graph);
	EdgeSetIterator *edgeIterator = iteratorEdge(edgeSet);
	while (hasNextEdge(edgeIterator)) {
		const Edge *edge = nextEdge(edgeIterator);
		printf("%c -(%d)-> %c\n", edge->vertex1->data, weightToVertex(edge->vertex1->adjacent, edge->vertex2), edge->vertex2->data);
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
int shortestPath(Graph *graph, Vertex *start, Vertex *end) {
	//Store the shortest distances found from start to vertices (or INT_MAX if none exists)
	VertexHashSet *shortest = makeEmptySetVertex(); //stores the current shortest path to each vertex
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) {
		Vertex *vertex = nextVertex(vertices);
		if (vertex == start) addElementVertex(shortest, vertex, 0); //only known one
		else addElementVertex(shortest, vertex, INT_MAX);
	}
	free(vertices);
	//Calculate shortest distances
	unsigned int vertexCount = size(graph->vertices);
	for (unsigned int i = 1; i != vertexCount; i++) { //if no negative loops exist, longest possible paths traverses V-1 vertices
		VertexSetIterator *vertices = iteratorVertex(graph->vertices);
		while (hasNextVertex(vertices)) {
			Vertex *examinedVertex = nextVertex(vertices);
			VertexSetIterator *possiblyAdjacent = iteratorVertex(graph->vertices);
			while (hasNextVertex(possiblyAdjacent)) {
				Vertex *adjacentCandidate = nextVertex(possiblyAdjacent);
				if (containsVertex(adjacentCandidate->adjacent, examinedVertex)) { //look for a vertex that can reach this one
					const int otherDistance = weightToVertex(shortest, adjacentCandidate);
					if (otherDistance != INT_MAX) { //make sure the other vertex has been reached from start
						const int currentWeight = weightToVertex(shortest, examinedVertex); //old shortest path
						const int newWeight = otherDistance + weightToVertex(adjacentCandidate->adjacent, examinedVertex); //new shortest path
						if (newWeight < currentWeight) { //update shortest value in the set
							removeElementVertex(shortest, examinedVertex);
							addElementVertex(shortest, examinedVertex, newWeight);
						}
					}
				}
			}
			free(possiblyAdjacent);
		}
		free(vertices);
	}
	//Find path from resulting distances
	const int result = weightToVertex(shortest, end);
	if (result == INT_MAX) { //could not get to end
		freeSetVertex(shortest);
		return -1;
	}
	else {
		Vertex *pathVertex = end;
		Deque *visitedStack = makeEmptyDeque();
		while (pathVertex != start) { //go backward from end to start
			pushFront(pathVertex, visitedStack);
			const int minDistance = weightToVertex(shortest, pathVertex);
			VertexSetIterator *possiblyAdjacent = iteratorVertex(graph->vertices);
			while (hasNextVertex(possiblyAdjacent)) {
				Vertex *adjacentCandidate = nextVertex(possiblyAdjacent);
				//Look for a vertex that can reach pathVertex and matches the minimum path length calculated before
				if (containsVertex(adjacentCandidate->adjacent, pathVertex)) {
					const int otherDistance = weightToVertex(shortest, adjacentCandidate);
					if (otherDistance != INT_MAX && otherDistance + weightToVertex(adjacentCandidate->adjacent, pathVertex) == minDistance) {
						pathVertex = adjacentCandidate;
						break; //no need to look for any other path
					}
				}
			}
			free(possiblyAdjacent);
		}
		freeSetVertex(shortest);
		printVertex(start); //print the final vertex too (since loop ends when pathVertex == start)
		while (!isEmptyDeque(visitedStack)) printVertex(popFront(visitedStack)); //print in reverse order
		freeDeque(visitedStack);
		return result;
	}
}
void freeGraph(Graph *graph) {
	VertexSetIterator *vertices = iteratorVertex(graph->vertices);
	while (hasNextVertex(vertices)) deleteVertex(graph, nextVertex(vertices));
	free(vertices);
	freeSetVertex(graph->vertices);
	free(graph);
}