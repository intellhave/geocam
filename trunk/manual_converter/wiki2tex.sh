#!/bin/bash

# Bring in the wiki to TeX script...
PATH=$PATH:$HOME/manual_converter

# Iterate over all of the files given at the command line.
# for each file of wiki code, convert it to a .tex file.
for file in $@
do
    name=${file%\.wiki}
    echo "Converting ${name}.wiki to ${name}.tex"
    bash convert.sh ${file} > ${name}.tex
    pdflatex ${name}.tex
done