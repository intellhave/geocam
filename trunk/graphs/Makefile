DOT = dot -Tpdf

modules.pdf: modules.dot
	$(DOT) modules.dot -o modules.pdf

ehr_geometry.pdf: ehr_geometry.dot
	$(DOT) ehr_geometry.dot -o ehr_geometry.pdf

clean:
	/bin/rm -rf *~ *.pdf