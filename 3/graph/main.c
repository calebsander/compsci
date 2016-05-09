#include <stdio.h>
#include <stdlib.h>
#include "graph.h"

int main() {
	Graph *graph = makeGraph();
	Vertex *b = addVertex(graph, 'b');
	Vertex *c = addVertex(graph, 'c');
	Vertex *d = addVertex(graph, 'd');
	Vertex *l = addVertex(graph, 'l');
	Vertex *m = addVertex(graph, 'm');
	Vertex *p = addVertex(graph, 'p');
	free(addEdge(graph, b, m, 5));
	free(addEdge(graph, b, p, 10));
	free(addEdge(graph, c, b, 3));
	free(addEdge(graph, c, d, 4));
	free(addEdge(graph, c, m, 9));
	free(addEdge(graph, d, b, 10));
	free(addEdge(graph, d, c, 4));
	free(addEdge(graph, d, l, 10));
	free(addEdge(graph, l, d, 10));
	free(addEdge(graph, l, m, 10));
	free(addEdge(graph, m, b, 5));
	free(addEdge(graph, m, c, 9));
	free(addEdge(graph, m, p, 4));
	free(addEdge(graph, p, m, 4));
	printGraph(graph);
	putchar('\n');
	printf("%d\n\n", shortestPath(graph, d, m));
	printf("%d\n\n", shortestPath(graph, b, c));
	printf("%d\n\n", shortestPath(graph, m, c));
	printf("%d\n\n", shortestPath(graph, l, l));
	printf("%d\n\n", shortestPath(graph, b, addVertex(graph, 'q')));
	freeGraph(graph);
}