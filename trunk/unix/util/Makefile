BASE= /home/jthomas/pipeline_src
TOPO= $(BASE)/topology
CC= g++ -ansi -g -pg -fno-omit-frame-pointer -c -iquote$(TOPO)

OBJS= 3DInputOutput.o triangulationInputOutput.o triangulationIO.o miscmath.o

####################################################################

archive: $(OBJS)
	ar rcs util.a $(OBJS)

%.o: %.cpp
	$(CC) $<

#####################################################################

clean:
	/bin/rm -rf *~ *.o *.a