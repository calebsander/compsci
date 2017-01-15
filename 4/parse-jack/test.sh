#!/bin/bash
for jackClass in ../../../CS4/project9/TextEditor/*.jack; do
	echo Parsing $jackClass
	./Main $jackClass > /dev/null
done