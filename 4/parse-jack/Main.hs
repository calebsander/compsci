module Main where

import JackParser (parse, parseClass)
import System.Environment
import System.Exit
import System.IO

main :: IO ()
main = do
  args <- getArgs
  case args of
    [fileName] -> do
      fileHandle <- openFile fileName ReadMode
      contents <- hGetContents fileHandle
      case parse parseClass contents of
        Nothing -> do
          hPutStrLn stderr ("Could not parse " ++ fileName)
          exitFailure
        Just (jackClass, _) ->
          putStrLn (show jackClass)
      hClose fileHandle
    _ -> do
      hPutStrLn stderr "Syntax: ./Main JackClass.jack"
      exitFailure