#include <stdio.h>
#include <stdlib.h>
#include "graph.h"

int main() {
	Graph *graph = makeGraph();
	Vertex *s = addVertex(graph, 's');
	Vertex *a = addVertex(graph, 'a');
	Vertex *b = addVertex(graph, 'b');
	Vertex *c = addVertex(graph, 'c');
	Vertex *d = addVertex(graph, 'd');
	Vertex *t = addVertex(graph, 't');
	free(addEdge(graph, s, a, 5));
	free(addEdge(graph, s, c, -2));
	free(addEdge(graph, a, b, 1));
	free(addEdge(graph, b, d, -1));
	free(addEdge(graph, b, t, 3));
	free(addEdge(graph, c, a, 2));
	free(addEdge(graph, c, b, 4));
	free(addEdge(graph, c, d, 4));
	free(addEdge(graph, d, t, 1));
	printGraph(graph);
	printf("%d\n", shortestPath(graph, s, t));
	freeGraph(graph);
}