#include <stdio.h>
#include <stdlib.h>
#include "graph.h"

void print(Vertex *vertex) {
	putchar(vertex->data);
	putchar('\n');
}
void edge(Graph *g, Vertex *v1, Vertex *v2, int w) {
	free(addEdge(g, v1, v2, w));
}
int main() {
	Graph *graph = makeGraph();
	Vertex *a = addVertex(graph, 'a');
	Vertex *b = addVertex(graph, 'b');
	Vertex *c = addVertex(graph, 'c');
	Vertex *d = addVertex(graph, 'd');
	Vertex *e = addVertex(graph, 'e');
	Vertex *f = addVertex(graph, 'f');
	Vertex *g = addVertex(graph, 'g');
	edge(graph, a, b, 1);
	edge(graph, a, c, 2);
	edge(graph, a, d, 1);
	edge(graph, b, c, 3);
	edge(graph, c, d, 2);
	edge(graph, c, e, 4);
	edge(graph, c, f, 10);
	edge(graph, e, f, 3);
	printGraph(graph);
	printf("%d\n", shortestPath(graph, a, f));
	printGraph(graph);
	freeGraph(graph);
}