package Triangulation;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Simplex {
  protected int serialNumber;
  protected int index;
  private static int simplexCounter = 0;
  protected HashSet<Vertex> localVertices;
  protected HashSet<Edge> localEdges;
  protected HashSet<Face> localFaces;
  protected HashSet<Tetra> localTetras;
  
  public Simplex(int index) {
    this.index = index;
    serialNumber = simplexCounter;
    simplexCounter++;
  }
  
  public int getIndex() {
    return index;
  }
  
  public int getSerialNumber() {
    return serialNumber;
  }
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  // Local simplices retrieval methods
  public HashSet<Vertex> getLocalVertices() {
    return localVertices;
  }
  public HashSet<Edge> getLocalEdges() {
    return localEdges;
  }
  public HashSet<Face> getLocalFaces() {
    return localFaces;
  }
  public HashSet<Tetra> getLocalTetras() {
    return localTetras;
  }
  
  // Adding local simplices
  public void addVertex(Vertex v) {
    localVertices.add(v);
  }
  public void addEdge(Edge e) {
    localEdges.add(e);
  }
  public void addFace(Face f) {
    localFaces.add(f);
  }
  public void addTetra(Tetra t) {
    localTetras.add(t);
  }
  
  
  // Removing local simplices
  public void removeVertex(Vertex v) {
    localVertices.remove(v);
  }
  public void removeEdge(Edge e) {
    localEdges.remove(e);
  }
  public void removeFace(Face f) {
    localFaces.remove(f);
  }
  public void removeTetra(Tetra t) {
    localTetras.remove(t);
  }
  public void clearLocals() {
    localVertices.clear();
    localEdges.clear();
    localFaces.clear();
    localTetras.clear(); 
  }
  
  // Adjacency checks
  public boolean isAdjVertex(Vertex v) {
    return localVertices.contains(v);
  }
  public boolean isAdjEdge(Edge e) {
    return localEdges.contains(e);
  }
  public boolean isAdjFace(Face f) {
    return localFaces.contains(f);
  }
  public boolean isAdjTetra(Tetra t) {
    return localTetras.contains(t);
  }
}
