#!/bin/bash

dir=/home/jthomas/manual_converter

# Remove leading whitespace from header lines.  For an unknown reason,
# a line like "  ===My Title===" confuses the wiki syntax parser.
sed 's/^[ \t]*=/=/g' $1 > temp.wiki
python ${dir}/wikify.py temp.wiki > temp.html

# Remove tags that html2tex cannot parse correctly.
# These are <\div> and <br/>.
cat temp.html | sed 's/<\/div>//g
                     s/<br\/>/<br>/g' > clean.html

html2tex clean.html -o temp.tex

# Tweak the formatting of html2tex currently we want:
# 0) Convert incorrect quotation marks (in titles) to
#    LaTex-Style quotes.
# 1) To get the code-font in titles correct. 
# 2) To turn \section entries into \subsection* entries
# 3) To turn \chapter entries into \section* entries
# 4) To turn \newline entries into actual newlines.
#
# Someone cleverer than I may know how to do these replacements without
# having to use several instances of sed.

cat temp.tex | sed 's/\\\"{}\(.*\)\\\"{}/``\1\@@/g'  \
             | sed "s/``\(.*\)@@/``\1''/g"  \
             | sed 's/\\char18\(.*\)\\char18/\\texttt{\1}/g
                     s/\\section/\\subsection*/g
                     s/\\chapter/\\section*/g
		     s/\\newline/\n/g' > clean.tex

cat ${dir}/header.tex
cat clean.tex
cat ${dir}/footer.tex

rm -f clean.tex temp.html clean.html temp.wiki