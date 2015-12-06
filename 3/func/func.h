typedef struct func Func;

Func *newConstantFunction(int constant);
Func *newFunction(int *inputs, int *outputs, unsigned int length);
Func *newComposition(Func *func1, Func *func2);

int evaluate(Func *func, int value);