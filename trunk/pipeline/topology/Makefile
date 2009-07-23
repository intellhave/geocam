CC= g++ -ansi -c -g
	
archive: simplex.o vertex.o edge.o face.o tetra.o triangulation.o triangulationIO.o 
	ar rcs topology.a *.o	

triangulationIO.o : triangulationIO.*[cpp,h] triangulation.*[cpp,h]

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

tidy:
	/bin/rm -rf *.o *~

clean:
	/bin/rm -rf *~ *.o *.a