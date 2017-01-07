module Parsing where

import Data.Char (isDigit)

-- Consider a "Parser" data type. The data type contains one
-- thing inside it: a function that "parses" a string.
-- Different parsers parse the string into different kinds of
-- data. For example, a `Parser Int` recognizes Ints, and
-- a `Parser AST` parses into an AST.
data Parser a = Parser (String -> Maybe (a, String))

parse :: Parser a -> String -> Maybe (a, String)
parse (Parser f) = f

-- Let's take a closer look at the type signature of that
-- function: String -> Maybe (a, String)
-- It takes in a string to parse, and returns a `Maybe`.
-- If the parse fails -- for example, if you are trying to
-- parse "zzz" into an Int -- the function returns Nothing.
-- Otherwise, the parser finds what it's looking for at
-- the beginning of the string, chops of the part of the
-- string it recognizes, and returns the parsed value and
-- the remainder of the string.

-- For example, consider this basicIntParser (you don't need
-- to understand the code):
basicIntParser :: Parser Int
basicIntParser =
  Parser $ \s ->
    case s of
      [] ->
        Nothing
      x : _ ->
        if isDigit x then
          Just (read $ takeWhile isDigit s, dropWhile isDigit s)
        else
          Nothing

-- Try these examples of using it:
parse1 = parse basicIntParser "123" -- Just (123, "")
parse2 = parse basicIntParser "3 bananas" -- Just (3, " bananas")
parse3 = parse basicIntParser "zzz4" -- Nothing
parse4 = parse basicIntParser "1 2 3" -- Just (1, " 2 3")


-- Please write the following parsers and functions:

-- satisfies: if first character of string satisfies
-- some condition, recognize it and parse successfully;
-- otherwise fail
satisfies :: (Char -> Bool) -> Parser Char
satisfies charMatch =
  Parser (\string ->
    case string of
      [] ->
        Nothing
      char : remainder ->
        if charMatch char then
          Just (char, remainder)
        else
          Nothing)

-- Example:
parseDigit :: Parser Char
parseDigit = satisfies isDigit
parseDigitEx1 = parse parseDigit "3 bananas" -- Just ('3', " bananas")
parseDigitEx2 = parse parseDigit "aleph bet" -- Nothing
parseDigitEx3 = parse parseDigit "100" -- Just ('1', "00")
parseDigitEx4 = parse parseDigit "4" -- Just('4', "")

-- keyword: if string begins with a given keyword, succeed
-- and parse into unit (). Otherwise fail.
keyword :: String -> Parser ()
keyword "" = parseReturn ()
keyword (char : remaining) =
  satisfies ((==) char) `parseAndThen` \_ ->
    keyword remaining

-- Example:
parseMalloc :: Parser ()
parseMalloc = keyword "malloc"
parseMallocEx1 = parse parseMalloc "malloc(4*sizeof(int))" -- Just ((), "(4*sizeof(int))"
parseMallocEx2 = parse parseMalloc "realloc()" -- Nothing


-- Write parseMap, the map function for parsers.
parseMap :: (a -> b) -> Parser a -> Parser b
parseMap aToB aParser =
  aParser `parseAndThen` \aValue ->
    parseReturn (aToB aValue)
-- Write parseReturn, the "succeed" or "return" function for
-- parsers, which always succeeds and returns a given value
-- without consuming any input.
parseReturn :: a -> Parser a
parseReturn result =
  Parser (\string -> Just (result, string))

-- Write parseAndThen, the andThen
-- for parsers.
parseAndThen :: Parser a -> (a -> Parser b) -> Parser b
parseAndThen aParser aToBParser =
  Parser (\string ->
    case parse aParser string of
      Nothing ->
        Nothing
      Just (aValue, remaining) ->
        parse (aToBParser aValue) remaining)

-- Write parseMap2, the map2 for parsers, using the helpers you wrote above.
parseMap2 :: (a -> b -> c) -> Parser a -> Parser b -> Parser c
parseMap2 aToBToC aParser bParser =
  aParser `parseAndThen` \aValue ->
    bParser `parseAndThen` \bValue ->
      parseReturn (aToBToC aValue bValue)

parseReverse :: Parser String
parseReverse = parseMap2 (\a -> \b -> [b, a]) parseDigit parseDigit
parseReverseEx1 = parse parseReverse "12abc" -- Just ("21", "abc")
parseReverseEx2 = parse parseReverse "09" -- Just ("90", "")
parseReverseEx3 = parse parseReverse "3" -- Nothing
parseReverseEx4 = parse parseReverse "abc" -- Nothing

parseDouble :: Parser Int
parseDouble = parseMap ((*) 2) basicIntParser
parseDoubleEx1 = parse parseDouble "abc" -- Nothing
parseDoubleEx2 = parse parseDouble "123 456" -- Just (246, " 456")