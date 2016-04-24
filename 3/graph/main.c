#include <stdio.h>
#include <stdlib.h>
#include "graph.h"

void print(Vertex *vertex) {
	putchar(vertex->data);
	putchar('\n');
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
	Vertex *h = addVertex(graph, 'h');
	Vertex *i = addVertex(graph, 'i');
	Vertex *j = addVertex(graph, 'j');
	free(addEdge(graph, a, b));
	free(addEdge(graph, a, e));
	free(addEdge(graph, b, c));
	free(addEdge(graph, b, d));
	free(addEdge(graph, c, d));
	free(addEdge(graph, c, e));
	free(addEdge(graph, e, f));
	free(addEdge(graph, f, g));
	free(addEdge(graph, g, h));
	free(addEdge(graph, i, j));
	traverseDepthFirst(graph, e, &print);
	freeGraph(graph);
}