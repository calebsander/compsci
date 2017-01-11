module JackParser where

import Data.Char (isDigit, isAlpha, isSpace)
import Control.Monad (liftM, ap)

data Class
  = Class String [ClassVar] [Subroutine]
  deriving Show

data Type
  = JackInt
  | JackChar
  | JackBool
  | JackClass String
  deriving Show

data ClassVarScope
  = Static
  | Field
  deriving Show

data ClassVar
  = ClassVar ClassVarScope VarDec
  deriving Show

data SubroutineType
  = Method
  | Constructor
  | Function
  deriving Show

data Subroutine
  = Subroutine SubroutineType (Maybe Type) String [Parameter] [VarDec] [Statement]
  deriving Show

data Parameter =
  Parameter Type String
  deriving Show

data VarDec =
  VarDec Type [String]
  deriving Show

data Statement
  = Let VarAccess Expression
  | If Expression [Statement] [Statement]
  | While Expression [Statement]
  | Do SubCall
  | Return (Maybe Expression)
  deriving Show

data VarAccess
  = Var String
  | Subscript String Expression
  deriving Show

data Expression
  = Expression Term [(Op, Term)]
  deriving Show

data Op
  = Plus
  | Minus
  | Times
  | Div
  | And
  | Or
  | LessThan
  | GreaterThan
  | EqualTo
  deriving Show

data Term
  = IntConst Int
  | StringConst String
  | Parenthesized Expression
  | BoolConst Bool
  | This
  | Null
  | Access VarAccess
  | SubroutineCall SubCall
  | Unary UnaryOp Term
  deriving Show

data SubCall
  = Unqualified String [Expression]
  | Qualified String String [Expression]
  deriving Show

data UnaryOp
  = LogicalNot
  | IntegerNegate
  deriving Show

newtype Parser a = Parser (String -> Maybe (a, String))

parse :: Parser a -> String -> Maybe (a, String)
parse (Parser f) = f


-- THE FOLLOWING ALLOWS YOU TO USE DO NOTATION.
instance Monad Parser where
  (>>=) = parseAndThen
  return = parseReturn
instance Applicative Parser where
  (<*>) = ap
  pure = return
instance Functor Parser where
  fmap = liftM

-- PARSING FUNCTIONS WRITTEN FOR LAST HOMEWORK.

parseReturn :: a -> Parser a
parseReturn x =
  Parser $ \s -> Just (x, s)

parseAndThen :: Parser a -> (a -> Parser b) -> Parser b
parseAndThen pa f =
  Parser $ \s ->
    case parse pa s of
      Nothing ->
        Nothing
      Just (a, s') ->
        parse (f a) s'

satisfies :: (Char -> Bool) -> Parser Char
satisfies c =
  Parser $ \s ->
    case s of
      [] -> Nothing
      first : rest ->
        if c first then
          Just (first, rest)
        else
          Nothing

keyword :: String -> Parser ()
keyword [] = return ()
keyword (c:cs) = do
  satisfies (c ==)
  keyword cs

parseMap :: (a -> b) -> Parser a -> Parser b
parseMap aToB aParser = do
  a <- aParser
  return (aToB a)

parseMap2 :: (a -> b -> c) -> Parser a -> Parser b -> Parser c
parseMap2 f pa pb = do
  a <- pa
  b <- pb
  return (f a b)

-- PARSER FUNCTIONS WRITTEN IN CLASS

parseKeywordValue :: [(String, a)] -> Parser a
parseKeywordValue nameValues =
  let
    getParser (name, value) =
      parseMap (\_ -> value) (keyword name)
  in
    choice (map getParser nameValues)

parseClassVarScope :: Parser ClassVarScope
parseClassVarScope =
  parseKeywordValue
    [ ("static", Static)
    , ("field", Field)
    ]

zeroOrMore :: Parser a -> Parser [a]
zeroOrMore parser =
  Parser $ \string ->
    case parse parser string of
      Nothing ->
        Just ([], string)
      Just (aValue, remaining) ->
        parse (parseMap (aValue :) (zeroOrMore parser)) remaining


identifier :: Parser String
identifier =
  let
    isValidFirstChar c =
      isAlpha c || c == '_'
    isValidLaterChar c =
      isValidFirstChar c || isDigit c
  in
    parseMap2 (:) (satisfies isValidFirstChar) (zeroOrMore (satisfies isValidLaterChar))


jackTypeParser :: Parser Type
jackTypeParser =
  choice
    [ parseKeywordValue
      [ ("int", JackInt)
      , ("char", JackChar)
      , ("boolean", JackBool)
      ]
    , parseMap JackClass identifier
    ]

choice :: [Parser a] -> Parser a
choice [] = parseFail
choice (firstChoice : remainingChoices) =
  Parser $ \string ->
    case parse firstChoice string of
      Nothing ->
        parse (choice remainingChoices) string
      success ->
        success

requiredSpaceParser :: Parser ()
requiredSpaceParser =
  Parser $ \string ->
    let
      dropped = dropWhile isSpace string
    in
      if string == dropped then Nothing -- some whitespace required
      else Just ((), dropped)

optionalSpaceParser :: Parser ()
optionalSpaceParser =
  choice
    [ requiredSpaceParser
    , return ()
    ]

parseCommaSeparated :: Parser a -> Parser [a]
parseCommaSeparated parser = do
  list <- parseOptional $ do
    first <- parser
    remaining <- zeroOrMore $ do
      optionalSpaceParser
      keyword ","
      optionalSpaceParser
      parser
    return (first : remaining)
  return (resolveMaybeList list)

parseFail :: Parser a
parseFail = Parser (\_ -> Nothing)

parseVarDec :: Parser VarDec
parseVarDec = do
  jackType <- jackTypeParser
  requiredSpaceParser
  vars <- parseCommaSeparated identifier
  case vars of
    [] ->
      parseFail
    _ -> do
      optionalSpaceParser
      keyword ";"
      return (VarDec jackType vars)

parseClassVar :: Parser ClassVar
parseClassVar = do
  scope <- parseClassVarScope
  requiredSpaceParser
  dec <- parseVarDec
  return (ClassVar scope dec)

parseOptional :: Parser a -> Parser (Maybe a)
parseOptional parser =
  Parser $ \string ->
    case parse parser string of
      Nothing ->
        Just (Nothing, string)
      Just (value, remaining) ->
        Just (Just value, remaining)

parseIntConstant :: Parser Int
parseIntConstant =
  Parser $ \string ->
    case string of
      "" ->
        Nothing
      firstChar : _ ->
        if isDigit firstChar then
          let readValue = read (takeWhile isDigit string)
          in
            if readValue <= 32767 then
              Just (readValue, dropWhile isDigit string)
            else Nothing
        else Nothing

parseStringConstant :: Parser String
parseStringConstant = do
  keyword "\""
  string <- zeroOrMore $
    satisfies $ \c ->
      not (c == '\r' || c == '\n' || c == '"')
  keyword "\""
  return string

parseAccess :: Parser VarAccess
parseAccess =
  let
    parseArrayAccess = do
      varName <- identifier
      optionalSpaceParser
      keyword "["
      optionalSpaceParser
      index <- parseExpression
      optionalSpaceParser
      keyword "]"
      return (Subscript varName index)
  in
    choice
      [ parseArrayAccess
      , parseMap Var identifier -- this must come after array access because it can start that expression
      ]

parseParenthesized :: Parser Term
parseParenthesized = do
  keyword "("
  optionalSpaceParser
  term <- parseExpression
  keyword ")"
  return (Parenthesized term)

parseUnqualifiedSubCall :: Parser SubCall
parseUnqualifiedSubCall = do
  name <- identifier
  optionalSpaceParser
  keyword "("
  optionalSpaceParser
  arguments <- parseCommaSeparated parseExpression
  optionalSpaceParser
  keyword ")"
  return (Unqualified name arguments)

parseQualifiedSubCall :: Parser SubCall
parseQualifiedSubCall = do
  classOrVarName <- identifier
  optionalSpaceParser
  keyword "."
  optionalSpaceParser
  Unqualified callName arguments <- parseUnqualifiedSubCall
  return (Qualified classOrVarName callName arguments)

parseSubCall :: Parser SubCall
parseSubCall =
  choice
    [ parseUnqualifiedSubCall
    , parseQualifiedSubCall
    ]

parseUnaryOp :: Parser UnaryOp
parseUnaryOp =
  choice
   [ parseMap (\_ -> LogicalNot) (keyword "~")
   , parseMap (\_ -> IntegerNegate) (keyword "-")
   ]

parseUnaryOperation :: Parser Term
parseUnaryOperation = do
  op <- parseUnaryOp
  optionalSpaceParser
  term <- parseTerm
  return (Unary op term)

parseTerm :: Parser Term
parseTerm =
  choice
    [ parseMap IntConst parseIntConstant
    , parseMap StringConst parseStringConstant
    , parseKeywordValue
      [ ("true", BoolConst True)
      , ("false", BoolConst False)
      , ("null", Null)
      , ("this", This)
      ]
    , parseMap SubroutineCall parseSubCall
    , parseMap Access parseAccess -- this must come after array subroutine call because a variable access can start that expression
    , parseParenthesized
    , parseUnaryOperation
    ]

parseOp :: Parser Op
parseOp =
  parseKeywordValue
    [ ("+", Plus)
    , ("-", Minus)
    , ("*", Times)
    , ("/", Div)
    , ("&", And)
    , ("|", Or)
    , ("<", LessThan)
    , (">", GreaterThan)
    , ("=", EqualTo)
    ]

parseExpression :: Parser Expression
parseExpression = do
  firstTerm <- parseTerm
  operations <- zeroOrMore $ do
    optionalSpaceParser
    op <- parseOp
    optionalSpaceParser
    term <- parseTerm
    return (op, term)
  return (Expression firstTerm operations)

parseLet :: Parser Statement
parseLet = do
  keyword "let"
  requiredSpaceParser
  access <- parseAccess
  optionalSpaceParser
  keyword "="
  optionalSpaceParser
  expression <- parseExpression
  optionalSpaceParser
  keyword ";"
  return (Let access expression)

-- Parses a list of statements, including surrounding whitespace
parseBlock :: Parser [Statement]
parseBlock = do
  optionalSpaceParser
  zeroOrMore $ do
    statement <- parseStatement
    optionalSpaceParser
    return statement

parseConditionAndBlock :: String -> Parser (Expression, [Statement])
parseConditionAndBlock controlKeyword = do
  keyword controlKeyword
  optionalSpaceParser
  keyword "("
  optionalSpaceParser
  expression <- parseExpression
  optionalSpaceParser
  keyword ")"
  optionalSpaceParser
  keyword "{"
  block <- parseBlock
  keyword "}"
  return (expression, block)

parseElse :: Parser [Statement]
parseElse = do
  keyword "else"
  optionalSpaceParser
  keyword "{"
  block <- parseBlock
  keyword "}"
  return block

resolveMaybeList :: Maybe ([a]) -> [a]
resolveMaybeList Nothing = []
resolveMaybeList (Just as) = as

parseIf :: Parser Statement
parseIf = do
  (expression, block) <- parseConditionAndBlock "if"
  optionalSpaceParser
  elseBlock <- parseOptional parseElse
  return (If expression block (resolveMaybeList elseBlock))

parseWhile :: Parser Statement
parseWhile = do
  (expression, block) <- parseConditionAndBlock "while"
  return (While expression block)

parseDo :: Parser Statement
parseDo = do
  keyword "do"
  requiredSpaceParser
  subCall <- parseSubCall
  optionalSpaceParser
  keyword ";"
  return (Do subCall)

parseReturnStatement :: Parser Statement
parseReturnStatement =
  let
    spaceAndValueParser = do
      requiredSpaceParser
      expression <- parseExpression
      optionalSpaceParser
      return (Just expression)
  in
    do
      keyword "return"
      returnValue <- choice
        [ spaceAndValueParser
        , parseMap (\_ -> Nothing) optionalSpaceParser -- this must follow the return value parser since "return" is at the start of "return value"
        ]
      keyword ";"
      return (Return returnValue)

parseStatement :: Parser Statement
parseStatement =
  choice
    [ parseLet
    , parseIf
    , parseWhile
    , parseDo
    , parseReturnStatement
    ]

parseSubroutineType :: Parser SubroutineType
parseSubroutineType =
  parseKeywordValue
    [ ("method", Method)
    , ("constructor", Constructor)
    , ("function", Function)
    ]

parseMaybeVoidType :: Parser (Maybe Type)
parseMaybeVoidType =
  choice
    [ parseKeywordValue [("void", Nothing)]
    , parseMap Just jackTypeParser
    ]

parseParameter :: Parser Parameter
parseParameter = do
  jackType <- jackTypeParser
  requiredSpaceParser
  name <- identifier
  return (Parameter jackType name)

parseSubroutine :: Parser Subroutine
parseSubroutine = do
  methodType <- parseSubroutineType
  requiredSpaceParser
  returnType <- parseMaybeVoidType
  requiredSpaceParser
  name <- identifier
  optionalSpaceParser
  keyword "("
  optionalSpaceParser
  parameters <- parseCommaSeparated parseParameter
  keyword ")"
  optionalSpaceParser
  keyword "{"
  variables <- zeroOrMore $ do
    optionalSpaceParser
    keyword "var"
    requiredSpaceParser
    parseVarDec
  statements <- parseBlock
  keyword "}"
  return (Subroutine methodType returnType name parameters variables statements)

parseClass :: Parser Class
parseClass = do
  optionalSpaceParser -- must include surrounding whitespace because this is the root parser
  keyword "class"
  requiredSpaceParser
  name <- identifier
  optionalSpaceParser
  keyword "{"
  varDecs <- zeroOrMore $ do
    optionalSpaceParser
    parseClassVar
  subroutines <- zeroOrMore $ do
    optionalSpaceParser
    parseSubroutine
  optionalSpaceParser
  keyword "}"
  return (Class name varDecs subroutines)