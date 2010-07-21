package visualization;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import triangulation.Edge;
import triangulation.Face;
import triangulation.StdFace;
import triangulation.Triangulation;
import triangulation.Vertex;

import Geoquant.Angle;
import Geoquant.Curvature2D;
import Geoquant.Length;

public class PlanarDevelopment {

  private Hashtable<Vertex, Point> points;
  private Hashtable<Edge, Line> lines;

  public PlanarDevelopment() {
    points = new Hashtable<Vertex, Point>();
    lines = new Hashtable<Edge, Line>();
  }

  public void clearSystem() {
    points.clear();
    lines.clear();
  }

  public Point getPoint(Vertex vertex) {
    return points.get(vertex);
  }

  public Line getLine(Edge edge) {
    return lines.get(edge);
  }

  public void generatePlane() {
    if (Triangulation.faceTable.isEmpty()) {
      return;
    } else {
      StdFace.generateOrientation();

      Face currFace = null;
      // first face will be handled in a special way cause there aren't
      // any faces already in the triangulation
      currFace = Triangulation.faceTable.values().iterator().next();
      StdFace stdFace = StdFace.getOrientedFace(currFace);
      Point p1, p2, p3;
      p1 = new Point(0, 0);
      p2 = new Point(Length.valueAt(stdFace.e12), 0);
      p3 = (new Point(Length.valueAt(stdFace.e13), 0)).rotate(Angle.valueAt(
          stdFace.v1, currFace));

      Line l12, l23, l13;
      l12 = new Line(p1, p2);
      l23 = new Line(p2, p3);
      l13 = new Line(p1, p3);

      points.put(stdFace.v1, p1);
      points.put(stdFace.v2, p2);
      points.put(stdFace.v3, p3);

      lines.put(stdFace.e12, l12);
      lines.put(stdFace.e23, l23);
      lines.put(stdFace.e13, l13);
      
      //System.err.println("Put Vertex " + stdFace.v1.getIndex() + " at point " + p1.toString());
      //System.err.println("Put Vertex " + stdFace.v2.getIndex() + " at point " + p2.toString());
      //System.err.println("Put Vertex " + stdFace.v3.getIndex() + " at point " + p3.toString());

      HashSet<Face> alreadyMapped = new HashSet<Face>();
      LinkedList<Face> facesToMap = new LinkedList<Face>();
      
      alreadyMapped.add(currFace);
      for (Face f : currFace.getLocalFaces()) {
        if (!alreadyMapped.contains(f)) {
          facesToMap.addLast(f);
        }
      }
      
      while (!facesToMap.isEmpty()) {
        currFace = facesToMap.removeFirst();
        //System.err.println("Working on face " + currFace.getIndex());
        //might as well prep for the next iteration here so we don't forget
        alreadyMapped.add(currFace);
        for (Face f : currFace.getLocalFaces()) {
          if (!alreadyMapped.contains(f)) {
            facesToMap.addLast(f);
          }
        }
        
        stdFace = StdFace.getOrientedFace(currFace);

        p1 = new Point(0, 0);
        p2 = new Point(Length.valueAt(stdFace.e12), 0);
        p3 = (new Point(Length.valueAt(stdFace.e13), 0)).rotate(Angle.valueAt(stdFace.v1, currFace));

        // find an edge that has already been mapped
        Edge mappedEdge = null;
        for (Edge e : currFace.getLocalEdges()) {
          if (lines.containsKey(e)) {
            mappedEdge = e;
            break;
          }
        }
        if (mappedEdge == null) {
          System.err.println("None of this face's edges have been mapped");
        }

        if (stdFace.e12.equals(mappedEdge)) { // matches the edge along the
                                              // x-axis
          Point mappedP1, mappedP2;
          mappedP1 = points.get(stdFace.v1);
          mappedP2 = points.get(stdFace.v2);
          double angleDiff = p2.subtract(p1).angle() - mappedP2.subtract(mappedP1).angle();

          Point thirdPoint = null;
          if (points.containsKey(stdFace.v3)) {
            thirdPoint = points.get(stdFace.v3);
          } else {
            thirdPoint = p3.rotate(angleDiff).add(points.get(stdFace.v1));
            points.put(stdFace.v3, thirdPoint);
            //System.err.println("Put Vertex " + stdFace.v3.getIndex() + " at point " + thirdPoint.toString());
          }
          if (!lines.containsKey(stdFace.e13)) {
            lines.put(stdFace.e13, new Line(mappedP1, thirdPoint));
          }
          if (!lines.containsKey(stdFace.e23)) {
            lines.put(stdFace.e23, new Line(mappedP2, thirdPoint));
          }
        } else if (stdFace.e23.equals(mappedEdge)) { // matches the edge far
                                                     // from the origin
          Point mappedP2, mappedP3;
          mappedP2 = points.get(stdFace.v2);
          mappedP3 = points.get(stdFace.v3);
          double angleDiff = p3.subtract(p2).angle() - mappedP3.subtract(mappedP2).angle();

          Point oddPoint = null;
          if (points.containsKey(stdFace.v1)) {
            oddPoint = points.get(stdFace.v1);
          } else {
            // this is tricky
            oddPoint = p1.subtract(p2).rotate(angleDiff).add(points.get(stdFace.v2));
            points.put(stdFace.v1, oddPoint);
            //System.err.println("Put Vertex " + stdFace.v1.getIndex() + " at point " + oddPoint.toString());
          }
          if (!lines.containsKey(stdFace.e13)) {
            lines.put(stdFace.e13, new Line(oddPoint, mappedP3));
          }
          if (!lines.containsKey(stdFace.e12)) {
            lines.put(stdFace.e12, new Line(oddPoint, mappedP2));
          }
        } else if (stdFace.e13.equals(mappedEdge)) { // matches the edge off the
                                                     // x-axis
          Point mappedP1, mappedP3;
          mappedP1 = points.get(stdFace.v1);
          mappedP3 = points.get(stdFace.v3);
          double angleDiff = p3.subtract(p1).angle() - mappedP3.subtract(mappedP1).angle();

          Point oddPoint = null;
          if (points.containsKey(stdFace.v2)) {
            oddPoint = points.get(stdFace.v2);
          } else {
            // this is tricky
            oddPoint = p2.rotate(angleDiff).add(points.get(stdFace.v1));
            points.put(stdFace.v2, oddPoint);
            //System.err.println("Put Vertex " + stdFace.v1.getIndex() + " at point " + oddPoint.toString());
          }
          if (!lines.containsKey(stdFace.e23)) {
            lines.put(stdFace.e23, new Line(oddPoint, mappedP3));
          }
          if (!lines.containsKey(stdFace.e12)) {
            lines.put(stdFace.e12, new Line(mappedP1, oddPoint));
          }
        }
        //done
      }//end of while loop
    }
  }

  public void printDevelopment() {
    for (Vertex v : points.keySet()) {
      System.out.println("Vertex " + v.getIndex() + " at " + points.get(v));
    }
    for (Edge e : lines.keySet()) {
      System.out.println("Edge " + e.getIndex() + " at " + lines.get(e));
    }
  }

  public void printForPython() {
    for (Edge e : lines.keySet()) {
      System.out.println("l.append(" + lines.get(e) + ")");
    }
  }

  // public void generatePlaneHelper(LinkedList<Face> facesToMap, HashSet<Face>
  // facesAlreadyMapped) {
  //  
  // }

}
