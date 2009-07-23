BASE= /home/jthomas/geocam/pipeline
GEOM= $(BASE)/geometry
TOPO= $(BASE)/topology
CC= g++ -ansi -g -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(BASE)

IO= 3DInputOutput.o triangulationInputOutput.o
OBJS= main.o miscmath.o Pipelined_Newtons_Method.o 

####################################################################

newtons: $(OBJS) $(IO) $(GEOM)/geometry.a $(TOPO)/topology.a
	g++ -g $(OBJS) $(IO) $(GEOM)/geometry.a $(TOPO)/topology.a -o newtons

%.o: %.cpp
	$(CC) $<

#####################################################################

log_clean:
	/bin/rm *.log
clean:
	/bin/rm -rf *~ *.o *.a