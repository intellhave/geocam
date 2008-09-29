/********************************Add************************************/

#include "triangulation/triangulation.h"
#include <vector>

void add(Vertex, Vertex);
void add(Vertex, Edge);
void add(Vertex, Face);
void add(Vertex, Tetra);
void add(Edge, Vertex);
void add(Edge, Edge);
void add(Edge, Face);
void add(Edge, Tetra);
void add(Face, Vertex);
void add(Face, Edge);
void add(Face, Face);
void add(Face, Tetra);
void add(Tetra, Vertex);
void add(Tetra, Edge);
void add(Tetra, Face);
void add(Tetra, Tetra);

void add(Vertex, vector<Vertex>);
void add(Vertex, vector<Edge>);
void add(Vertex, vector<Face>);
void add(Vertex, vector<Tetra>);
void add(Edge, vector<Vertex>);
void add(Edge, vector<Edge>);
void add(Edge, vector<Face>);
void add(Edge, vector<Tetra>);
void add(Face, vector<Vertex>);
void add(Face, vector<Edge>);
void add(Face, vector<Face>);
void add(Face, vector<Tetra>);
void add(Tetra, vector<Vertex>);
void add(Tetra, vector<Edge>);
void add(Tetra, vector<Face>);
void add(Tetra, vector<Tetra>);

void add(vector<Vertex>, Vertex);
void add(vector<Vertex>, Edge);
void add(vector<Vertex>, Face);
void add(vector<Vertex>, Tetra);
void add(vector<Edge>, Vertex);
void add(vector<Edge>, Edge);
void add(vector<Edge>, Face);
void add(vector<Edge>, Tetra);
void add(vector<Face>, Vertex);
void add(vector<Face>, Edge);
void add(vector<Face>, Face);
void add(vector<Face>, Tetra);
void add(vector<Tetra>, Vertex);
void add(vector<Tetra>, Edge);
void add(vector<Tetra>, Face);
void add(vector<Tetra>, Tetra);

void add(vector<Vertex>, vector<Vertex>);
void add(vector<Vertex>, vector<Edge>);
void add(vector<Vertex>, vector<Face>);
void add(vector<Vertex>, vector<Tetra>);
void add(vector<Edge>, vector<Vertex>);
void add(vector<Edge>, vector<Edge>);
void add(vector<Edge>, vector<Face>);
void add(vector<Edge>, vector<Tetra>);
void add(vector<Face>, vector<Vertex>);
void add(vector<Face>, vector<Edge>);
void add(vector<Face>, vector<Face>);
void add(vector<Face>, vector<Tetra>);
void add(vector<Tetra>, vector<Vertex>);
void add(vector<Tetra>, vector<Edge>);
void add(vector<Tetra>, vector<Face>);
void add(vector<Tetra>, vector<Tetra>);

/******************************Remove***********************************/

void remove(Vertex, Vertex);
void remove(Vertex, Edge);
void remove(Vertex, Face);
void remove(Vertex, Tetra);
void remove(Edge, Vertex);
void remove(Edge, Edge);
void remove(Edge, Face);
void remove(Edge, Tetra);
void remove(Face, Vertex);
void remove(Face, Edge);
void remove(Face, Face);
void remove(Face, Tetra);
void remove(Tetra, Vertex);
void remove(Tetra, Edge);
void remove(Tetra, Face);
void remove(Tetra, Tetra);

void remove(Vertex, vector<Vertex>);
void remove(Vertex, vector<Edge>);
void remove(Vertex, vector<Face>);
void remove(Vertex, vector<Tetra>);
void remove(Edge, vector<Vertex>);
void remove(Edge, vector<Edge>);
void remove(Edge, vector<Face>);
void remove(Edge, vector<Tetra>);
void remove(Face, vector<Vertex>);
void remove(Face, vector<Edge>);
void remove(Face, vector<Face>);
void remove(Face, vector<Tetra>);
void remove(Tetra, vector<Vertex>);
void remove(Tetra, vector<Edge>);
void remove(Tetra, vector<Face>);
void remove(Tetra, vector<Tetra>);

void remove(vector<Vertex>, Vertex);
void remove(vector<Vertex>, Edge);
void remove(vector<Vertex>, Face);
void remove(vector<Vertex>, Tetra);
void remove(vector<Edge>, Vertex);
void remove(vector<Edge>, Edge);
void remove(vector<Edge>, Face);
void remove(vector<Edge>, Tetra);
void remove(vector<Face>, Vertex);
void remove(vector<Face>, Edge);
void remove(vector<Face>, Face);
void remove(vector<Face>, Tetra);
void remove(vector<Tetra>, Vertex);
void remove(vector<Tetra>, Edge);
void remove(vector<Tetra>, Face);
void remove(vector<Tetra>, Tetra);

void remove(vector<Vertex>, vector<Vertex>);
void remove(vector<Vertex>, vector<Edge>);
void remove(vector<Vertex>, vector<Face>);
void remove(vector<Vertex>, vector<Tetra>);
void remove(vector<Edge>, vector<Vertex>);
void remove(vector<Edge>, vector<Edge>);
void remove(vector<Edge>, vector<Face>);
void remove(vector<Edge>, vector<Tetra>);
void remove(vector<Face>, vector<Vertex>);
void remove(vector<Face>, vector<Edge>);
void remove(vector<Face>, vector<Face>);
void remove(vector<Face>, vector<Tetra>);
void remove(vector<Tetra>, vector<Vertex>);
void remove(vector<Tetra>, vector<Edge>);
void remove(vector<Tetra>, vector<Face>);
void remove(vector<Tetra>, vector<Tetra>);
