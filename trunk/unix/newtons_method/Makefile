BASE= /home/jthomas/pipeline_src
GEOM= $(BASE)/ehr_geometry
TOPO= $(BASE)/topology
UTIL= $(BASE)/util
CC= g++ -ansi -g -pg -fno-omit-frame-pointer -c -iquote$(GEOM) -iquote$(TOPO) -iquote$(UTIL)

OBJS= main.o Pipelined_Newtons_Method.o 

####################################################################

newtons: $(OBJS) $(IO) $(GEOM)/geometry.a $(TOPO)/topology.a $(UTIL)/util.a
	g++ -g -pg $(OBJS) $(GEOM)/geometry.a $(TOPO)/topology.a $(UTIL)/util.a -o newtons

%.o: %.cpp
	$(CC) $<

#####################################################################

log_clean:
	/bin/rm *.log
clean:
	/bin/rm -rf *~ *.o *.a newtons