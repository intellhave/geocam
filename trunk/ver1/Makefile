CC= g++ -ansi -c 

geotest: all
	g++ -ansi *.o -o geotest

all: topology geometry misc

topology: simplex.o vertex.o edge.o face.o tetra.o triangulation.o

geometry: area.o length.o  triposition.o geoquant.h # gqindex.o eucangle.o

misc: miscmath.o triangulationIO.o main.o

# Miscellaneous Code
main.o : main.cpp
	$(CC) main.cpp 

mismath.o : miscmath.*[cpp,h]
	$(CC) miscmath.cpp 

triangulationIO.o : triangulationIO.*[cpp,h] triangulation.*[cpp,h]
	$(CC) triangulationIO.cpp 

# Topology Code
simplex.o : simplex.*[cpp,h]
	$(CC) simplex.cpp 

vertex.o: vertex.*[cpp,h] simplex.h
	$(CC) vertex.cpp 

edge.o: edge.*[cpp,h] simplex.h
	$(CC) edge.cpp  

face.o: face.*[cpp,h] simplex.h edge.h
	$(CC) face.cpp 

tetra.o: tetra.*[cpp,h] simplex.h
	$(CC) tetra.cpp 

triangulation.o: triangulation.*[cpp,h] simplex.h vertex.h edge.h face.h tetra.h
	$(CC) triangulation.cpp 

# Geometry Code
triposition.o: triposition.*[cpp,h] simplex.*[cpp,h]
	$(CC) triposition.cpp 

gqindex.o: gqindex.*[cpp,h] geoquant.h triposition.h
	$(CC) gqindex.cpp 

#eucangle.o : eucangle.*[cpp,h] geoquant.h triposition.h miscmath.h
#	$(CC) eucangle.cpp 

length.o : length.*[cpp,h] geoquant.h triposition.h
	$(CC) length.cpp 

area.o : area.*[cpp,h] geoquant.h triposition.h
	$(CC) area.cpp 

tidy:
	mv *.o obj/.

clean:
	/bin/rm -rf *~ *.cpp~ *.h~ *.o obj/*