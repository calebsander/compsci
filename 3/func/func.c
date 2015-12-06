#include <stdlib.h>
#include "func.h"

typedef enum {
	CONSTANT,
	INPUT,
	COMPOSITION
} FuncType;
struct func {
	FuncType type;
};

typedef struct {
	Func func;
	int constant;
} ConstantFunction;
Func *newConstantFunction(int constant) {
	ConstantFunction *newFunc = malloc(sizeof(*newFunc));
	newFunc->constant = constant;
	newFunc->func.type = CONSTANT;
	return &(newFunc->func);
}

typedef struct {
	Func func;
	int *inputs, *outputs;
	unsigned int length;
} InputFunction;
Func *newFunction(int *inputs, int *outputs, unsigned int length) {
	InputFunction *newFunc = malloc(sizeof(*newFunc));
	newFunc->inputs = inputs;
	newFunc->outputs = outputs;
	newFunc->length = length;
	newFunc->func.type = INPUT;
	return &(newFunc->func);
}

typedef struct {
	Func func;
	Func *outer, *inner;
} CompositionFunction;
Func *newComposition(Func *outer, Func *inner) {
	CompositionFunction *newFunc = malloc(sizeof(*newFunc));
	newFunc->outer = outer;
	newFunc->inner = inner;
	newFunc->func.type = COMPOSITION;
	return &(newFunc->func);
}

int evaluate(Func *func, int value) {
	switch (func->type) {
		case CONSTANT:
			return ((ConstantFunction*)func)->constant;
		case INPUT: {
			int *inputs = ((InputFunction*)func)->inputs;
			int *outputs = ((InputFunction*)func)->inputs;
			unsigned int length = ((InputFunction*)func)->length;
			unsigned int i;
			for (i = 0; i < length && inputs[i] < value; i++);
			return outputs[i - 1];
			break;
		}
		case COMPOSITION: {
			CompositionFunction *realFunc = (CompositionFunction*)func;
			return evaluate(realFunc->outer, evaluate(realFunc->inner, value));
		}
	}
	return 0;
}