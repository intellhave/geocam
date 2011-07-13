package triangulation;

public class Vertex extends Simplex {

  public Vertex(int index) {
    super(index);
  }

  public int getDegree() {
    return localEdges.size();
  }
  
  public Edge getEdge(Vertex v){
    if(this.equals(v)){
      return null;
    }
    Edge e = null;
      for(Edge e1 : this.getLocalEdges()){
        for(Edge e2 : v.getLocalEdges()){
          if(e1.equals(e2)){
            e = e1;
          }
        }
      }
    return e;
  }
  public void remove(){

    
    for (Tetra t: this.getLocalTetras()){
      for (Vertex neighborV: t.getLocalVertices())
        if (!neighborV.equals(this)) neighborV.removeTetra(t);
      for (Edge neighborE :t.getLocalEdges())
        neighborE.removeTetra(t);
      for (Face neighborF :t.getLocalFaces())
        neighborF.removeTetra(t);
      for (Tetra neighborT :t.getLocalTetras())
        neighborT.removeTetra(t);
      Triangulation.removeTetra(t);
    }

    for (Face f: this.getLocalFaces()){
      for (Vertex neighborV: f.getLocalVertices())
        if (!neighborV.equals(this)) neighborV.removeFace(f);
      for (Edge neighborE :f.getLocalEdges())
        neighborE.removeFace(f);
      for (Face neighborF :f.getLocalFaces())
        neighborF.removeFace(f);
      for (Tetra neighborT :f.getLocalTetras())
        neighborT.removeFace(f);     
      Triangulation.removeFace(f);
    }

    for (Edge e: this.getLocalEdges()){
      //     System.err.println(e);
           for (Vertex neighborV: e.getLocalVertices()){
             if (!neighborV.equals(this)) neighborV.removeEdge(e);
           }
           for (Edge neighborE :e.getLocalEdges()){
             neighborE.removeEdge(e);
           }
             
           for (Face neighborF :e.getLocalFaces()){
             neighborF.removeEdge(e);
           }
           for (Tetra neighborT :e.getLocalTetras()){
             neighborT.removeEdge(e);       
           }
           Triangulation.removeEdge(e);
    }  
  
    for (Vertex v: this.getLocalVertices()){
      v.removeVertex(this);
    }
    Triangulation.removeVertex(this);     
  }
}
