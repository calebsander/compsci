#include <stdio.h>
#include <stdlib.h>
#include "graph.h"
#include "priorityQueue.h"

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
	edge(graph, b, c, 6);
	edge(graph, b, d, 2);
	edge(graph, b, e, 1);
	edge(graph, c, e, 4);
	edge(graph, d, e, 2);
	edge(graph, d, g, 1);
	edge(graph, e, f, 3);
	printGraph(graph);
	printf("%d\n", shortestPath(graph, a, c));
	printGraph(graph);
	freeGraph(graph);
}