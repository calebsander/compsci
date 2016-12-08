data Op
  = Plus
  | Minus
  | Times
  | Div

type VariableName = String

data Expression
  = Const Int
  | Var VariableName
  | Binop Expression Op Expression
  | StmExp Statement Expression

data Statement
  = Assign VariableName Expression
  | Sequence Statement Statement

type Environment = [(VariableName, Int)]

resolve :: Environment -> VariableName -> Int
resolve [] variableName =
  error ("Variable not assigned: " ++ variableName)
resolve ((name, value) : remainingEnvironment) variableName =
  if name == variableName then
    value
  else
    resolve remainingEnvironment variableName

opFunction :: Op -> (Int -> Int -> Int)
opFunction Plus = (+)
opFunction Minus = (-)
opFunction Times = (*)
opFunction Div = div

interpretStatement :: Environment -> Statement -> Environment
interpretStatement env (Assign variableName expression) =
  let
    interpretedExpression = interpretExpression expression env
    value = fst interpretedExpression
    envAfterExpression = snd interpretedExpression
  in
    -- It is not a problem that the same variable can be in the environment twice; only the first instance matters
    (variableName, value) : envAfterExpression
interpretStatement env (Sequence statement1 statement2) =
  let
    envAfterFirst = interpretStatement env statement1
  in
    interpretStatement envAfterFirst statement2

interpretExpression :: Expression -> Environment -> (Int, Environment)
interpretExpression (Const val) env = (val, env)
interpretExpression (Var name) env = (resolve env name, env)
interpretExpression (Binop expression1 op expression2) env =
  let
    (interpreted1, env1) = interpretExpression expression1 env
    (interpreted2, env2) = interpretExpression expression2 env1
  in
    ((opFunction op) interpreted1 interpreted2, env2)
interpretExpression (StmExp statement expression) env =
  let
    envAfterStatement = interpretStatement env statement
  in
    interpretExpression expression envAfterStatement

testExpression =
  StmExp
    (Sequence
      (Assign "a"
        (Binop (Const 5) Plus (Const 3))
      )
      (Sequence
        (Assign "b"
          (StmExp
            (Assign "c" (Const 2))
            (Binop (Var "c") Minus (Const 7))
          )
        )
        (Assign "d" (Const 4))
      )
    )
    (Binop
      (Var "d")
      Minus
      (Binop
        (Var "c")
        Times
        (Var "a")
      )
    )
interpretedTestExpression = interpretExpression testExpression []