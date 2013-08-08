package inputOutput;

import geoquant.Alpha;
import geoquant.Eta;
import geoquant.Length;
import geoquant.Radius;
import geoquant.TriPosition;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import triangulation.Edge;
import triangulation.Face;
import triangulation.FaceGrouping;
import triangulation.Simplex;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.AssetManager;
import development.DevelopmentComputations;
import development.TextureCoords;

public class TriangulationIO {
  private static Schema triangulationSchema = null;
  private static String namespace = "http://code.google.com/p/geocam";
  private static String schemaPath = AssetManager.getAssetPath("Triangulations/TriangulationSchema.xsd");

  public static void readTriangulation(String fileName) {
    if (fileName.endsWith(".txt")){
      readLutzFile(fileName);
    } else {
      Document triangulationDoc = XMLParser.parseDocument(fileName);
      if (triangulationDoc == null) {
        return;
      }

      try {
        if (triangulationSchema == null) {       
          SchemaFactory sf = SchemaFactory
              .newInstance("http://www.w3.org/2001/XMLSchema");
          triangulationSchema = sf.newSchema(new File(schemaPath));
        }
        triangulationSchema.newValidator().validate(
            new DOMSource(triangulationDoc));
      } catch (SAXException e) {
        System.err.println("Error: Document " + fileName + " did not validate against TriangulationSchema.");
        System.exit(1);        
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      readTriangulation(triangulationDoc);
    }
  }

  public static void readTriangulation(Document triangulationDoc) {
    Triangulation.reset();
    HashMap<TriPosition, Edge> edgeList = new HashMap<TriPosition, Edge>();
    HashMap<TriPosition, Face> faceList = new HashMap<TriPosition, Face>();
    TriPosition T;
    Vertex[] verts = new Vertex[0];
    Edge[] edges = new Edge[0];
    Face[] faces = new Face[0];
    Tetra[] tetras = new Tetra[0];

    NodeList simplexList = triangulationDoc.getElementsByTagName("Vertex");
    NodeList localList;
    Element simplexNode;
    Element localSimplices;
    Scanner localScanner;
    Vertex v;
    for (int i = 0; i < simplexList.getLength(); i++) {
      simplexNode = (Element) simplexList.item(i);
      v = getVertex(Integer.parseInt(simplexNode.getAttribute("index")));
      localList = simplexNode.getElementsByTagName("Vertices");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Vertex v2;
        while (localScanner.hasNextInt()) {
          v2 = getVertex(localScanner.nextInt());
          // v.addUniqueVertex(v2);
          v.addVertex(v2);
          // v2.addUniqueVertex(v);
          // v2.addVertex(v);
        }
      }
      localList = simplexNode.getElementsByTagName("Edges");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Edge e2;
        while (localScanner.hasNextInt()) {
          e2 = getEdge(localScanner.nextInt());
          v.addUniqueEdge(e2);
          // e2.addUniqueVertex(v);
        }
      }
      localList = simplexNode.getElementsByTagName("Faces");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Face f2;
        while (localScanner.hasNextInt()) {
          f2 = getFace(localScanner.nextInt());
          v.addUniqueFace(f2);
          // f2.addUniqueVertex(v);
        }
      }
      localList = simplexNode.getElementsByTagName("Tetras");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Tetra t2;
        while (localScanner.hasNextInt()) {
          t2 = getTetra(localScanner.nextInt());
          v.addUniqueTetra(t2);
          // t2.addUniqueVertex(v);
        }
      }
      
      localList = simplexNode.getElementsByTagName("metafaces");
      if(localList.getLength() == 1){
    	  localSimplices = (Element) localList.item(0);
    	  localScanner = new Scanner(localSimplices.getTextContent());
    	  while(localScanner.hasNextInt()){
    		  v.addMetaFace(localScanner.nextInt());
    	  }
      }
      String radius = simplexNode.getAttribute("radius");
      if (radius.length() != 0) {
        Radius.at(v).setValue(Double.parseDouble(radius));
      }
      String alpha = simplexNode.getAttribute("alpha");
      if (alpha.length() != 0) {
        Alpha.at(v).setValue(Double.parseDouble(alpha));
      }
      String multiplicity = simplexNode.getAttribute("multiplicity");
      if (multiplicity.length() != 0) {
        v.setMultiplicity(Integer.parseInt(multiplicity));
      }
      String boundary = simplexNode.getAttribute("boundary");
      if(boundary.length() !=0){
    	  v.setBoundary(Boolean.parseBoolean(boundary));
      }
      String xcoord = simplexNode.getAttribute("x-coordinate");
      double [] coords = {Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_NORMAL};
      if(xcoord.length() != 0){
    	  coords[0] = Double.parseDouble(xcoord);
      }
      String ycoord = simplexNode.getAttribute("y-coordinate");
      if(ycoord.length() != 0){
    	  coords[1] = Double.parseDouble(ycoord);
      }
      String zcoord = simplexNode.getAttribute("z-coordinate");
      if(zcoord.length() != 0){
    	 coords[2] = Double.parseDouble(zcoord);
      }
      v.setCoordinates(coords);
    }

    Edge e;
    simplexList = triangulationDoc.getElementsByTagName("Edge");
    for (int i = 0; i < simplexList.getLength(); i++) {
      simplexNode = (Element) simplexList.item(i);
      e = getEdge(Integer.parseInt(simplexNode.getAttribute("index")));
      localList = simplexNode.getElementsByTagName("Vertices");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Vertex v2;
        while (localScanner.hasNextInt()) {
          v2 = getVertex(localScanner.nextInt());
          e.addUniqueVertex(v2);
          // v2.addUniqueEdge(e);
        }
      }
      T = new TriPosition(e.getLocalVertices().get(0).getIndex(), e
          .getLocalVertices().get(1).getIndex());
      edgeList.put(T, e);
      localList = simplexNode.getElementsByTagName("Edges");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Edge e2;
        while (localScanner.hasNextInt()) {
          e2 = getEdge(localScanner.nextInt());
          e.addUniqueEdge(e2);
          // e2.addUniqueEdge(e2);
        }
      }
      localList = simplexNode.getElementsByTagName("Faces");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Face f2;
        while (localScanner.hasNextInt()) {
          f2 = getFace(localScanner.nextInt());
          e.addUniqueFace(f2);
          // f2.addUniqueEdge(e);
        }
      }
      localList = simplexNode.getElementsByTagName("Tetras");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Tetra t2;
        while (localScanner.hasNextInt()) {
          t2 = getTetra(localScanner.nextInt());
          e.addUniqueTetra(t2);
          // t2.addUniqueEdge(e);
        }
      }
      
      localList = simplexNode.getElementsByTagName("metafaces");
      if(localList.getLength() == 1){
    	  localSimplices = (Element) localList.item(0);
    	  localScanner = new Scanner(localSimplices.getTextContent());
    	  while(localScanner.hasNextInt()){
    		  e.addMetaFace(localScanner.nextInt());
    	  }
      }
      
      
      String eta = simplexNode.getAttribute("eta");
      if (eta.length() != 0) {
        Eta.at(e).setValue(Double.parseDouble(eta));
      }
      String length = simplexNode.getAttribute("length");
      if (length.length() != 0) {
        Length.at(e).setValue(Double.parseDouble(length));
      }
      String emultiplicity = simplexNode.getAttribute("multiplicity");
      if (emultiplicity.length() != 0) {
        e.setMultiplicity(Integer.parseInt(emultiplicity));
      }
      String boundary = simplexNode.getAttribute("boundary");
      if(boundary.length() !=0){
    	  e.setBoundary(Boolean.parseBoolean(boundary));
      }
    }

    Face f;
    simplexList = triangulationDoc.getElementsByTagName("Face");
    for (int i = 0; i < simplexList.getLength(); i++) {
      simplexNode = (Element) simplexList.item(i);
      f = getFace(Integer.parseInt(simplexNode.getAttribute("index")));
      localList = simplexNode.getElementsByTagName("Vertices");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Vertex v2;
        while (localScanner.hasNextInt()) {
          v2 = getVertex(localScanner.nextInt());
          f.addUniqueVertex(v2);
          // f.addVertex(v2);
          // v2.addUniqueFace(f);
        }
      }
      verts = f.getLocalVertices().toArray(new Vertex[0]);
      T = new TriPosition(verts[0].getIndex(), verts[1].getIndex(),
          verts[2].getIndex());
      faceList.put(T, f);

      localList = simplexNode.getElementsByTagName("Edges");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Edge e2;
        while (localScanner.hasNextInt()) {
          e2 = getEdge(localScanner.nextInt());
          f.addUniqueEdge(e2);
          // e2.addUniqueFace(f);
        }
      }
      Edge e2;
      for (int j = 0; j < verts.length; j++) {
        for (int k = j + 1; k < verts.length; k++) {
          T = new TriPosition(verts[j].getIndex(), verts[k].getIndex());
          e2 = edgeList.get(T);
          if (e2 == null) {
            e2 = getEdge(Triangulation.greatestEdge() + 1);
            edgeList.put(T, e2);
            e2.addUniqueVertex(verts[j]);
            e2.addUniqueVertex(verts[k]);
            verts[j].addUniqueEdge(e2);
            verts[k].addUniqueEdge(e2);
          }
          f.addUniqueEdge(e2);
          // e2.addUniqueFace(f);
        }
      }

      localList = simplexNode.getElementsByTagName("Faces");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Face f2;
        while (localScanner.hasNextInt()) {
          f2 = getFace(localScanner.nextInt());
          f.addUniqueFace(f2);
          // f2.addUniqueFace(f);
        }
      }
      localList = simplexNode.getElementsByTagName("Tetras");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Tetra t2;
        while (localScanner.hasNextInt()) {
          t2 = getTetra(localScanner.nextInt());
          f.addUniqueTetra(t2);
          // t2.addUniqueFace(f);
        }
      }
      
      localList = simplexNode.getElementsByTagName("Tetras");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Tetra t2;
        while (localScanner.hasNextInt()) {
          t2 = getTetra(localScanner.nextInt());
          f.addUniqueTetra(t2);
          // t2.addUniqueFace(f);
        }
      }
      
      localList = simplexNode.getElementsByTagName("metaface");
      if(localList.getLength() == 1){
    	  localSimplices = (Element) localList.item(0);
    	  localScanner = new Scanner(localSimplices.getTextContent());
    	  while(localScanner.hasNextInt()){
    		  f.addMetaFace(localScanner.nextInt());
    	  }
      }
      
      NodeList rgbComponents = simplexNode.getElementsByTagName("Color");
      if (rgbComponents.getLength() > 0) {
        Element rgbs = (Element) rgbComponents.item(0);
        localScanner = new Scanner(rgbs.getTextContent());
        ArrayList<Float> colors = new ArrayList<Float>();
        while (localScanner.hasNextFloat()) {
          float color = localScanner.nextFloat();
          colors.add(color);
        }
        Color faceColor = new Color(colors.get(0), colors.get(1), colors.get(2));
        f.setColor(faceColor);
      }
    
      String multiplicity = simplexNode.getAttribute("multiplicity");
      if (multiplicity.length() != 0) {
        f.setMultiplicity(Integer.parseInt(multiplicity));
      }
      String boundary = simplexNode.getAttribute("boundary");
      if(boundary.length() !=0){
    	  f.setBoundary(Boolean.parseBoolean(boundary));
      }
      
    }

    Tetra t;
    simplexList = triangulationDoc.getElementsByTagName("Tetra");
    for (int i = 0; i < simplexList.getLength(); i++) {
      simplexNode = (Element) simplexList.item(i);
      t = getTetra(Integer.parseInt(simplexNode.getAttribute("index")));
      localList = simplexNode.getElementsByTagName("Vertices");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Vertex v2;
        while (localScanner.hasNextInt()) {
          v2 = getVertex(localScanner.nextInt());
          t.addUniqueVertex(v2);
          // v2.addUniqueTetra(t);
        }
      }
      localList = simplexNode.getElementsByTagName("Edges");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Edge e2;
        while (localScanner.hasNextInt()) {
          e2 = getEdge(localScanner.nextInt());
          t.addUniqueEdge(e2);
          // e2.addUniqueTetra(t);
        }
      }
      verts = t.getLocalVertices().toArray(new Vertex[0]);
      Edge e2;
      for (int j = 0; j < verts.length; j++) {
        for (int k = j + 1; k < verts.length; k++) {
          T = new TriPosition(verts[j].getIndex(), verts[k].getIndex());
          e2 = edgeList.get(T);
          if (e2 == null) {
            e2 = getEdge(Triangulation.greatestEdge() + 1);
            edgeList.put(T, e2);
            e2.addUniqueVertex(verts[j]);
            e2.addUniqueVertex(verts[k]);
            verts[j].addUniqueEdge(e2);
            verts[k].addUniqueEdge(e2);
          }
          t.addUniqueEdge(e2);
          // e2.addUniqueTetra(t);
        }
      }

      localList = simplexNode.getElementsByTagName("Faces");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Face f2;
        while (localScanner.hasNextInt()) {
          f2 = getFace(localScanner.nextInt());
          t.addUniqueFace(f2);
          // f2.addUniqueTetra(t);
        }
      }
      /*
       * Face f2; Edge ejk, ejl, ekl; for(int j = 0; j < verts.length; j++) {
       * for(int k = j + 1; k < verts.length; k++) { for(int l = k + 1; l <
       * verts.length; l++) { T = new TriPosition(verts[j].getIndex(),
       * verts[k].getIndex(), verts[l].getIndex()); f2 = faceList.get(T); if(f2
       * == null) { f2 = getFace(Triangulation.greatestFace() + 1);
       * faceList.put(T, f2); f2.addUniqueVertex(verts[j]);
       * f2.addUniqueVertex(verts[k]); f2.addUniqueVertex(verts[l]);
       * verts[j].addUniqueFace(f2); verts[k].addUniqueFace(f2);
       * verts[l].addUniqueFace(f2); } ejk = edgeList.get(new
       * TriPosition(verts[j].getIndex(), verts[k].getIndex())); ejl =
       * edgeList.get(new TriPosition(verts[j].getIndex(),
       * verts[l].getIndex())); ekl = edgeList.get(new
       * TriPosition(verts[k].getIndex(), verts[l].getIndex()));
       * f2.addUniqueEdge(ejk); f2.addUniqueEdge(ejl); f2.addUniqueEdge(ekl);
       * ejk.addUniqueFace(f2); ejl.addUniqueFace(f2); ekl.addUniqueFace(f2);
       * 
       * t.addUniqueFace(f2); f2.addUniqueTetra(t); } } }
       */
      localList = simplexNode.getElementsByTagName("Tetras");
      if (localList.getLength() == 1) {
        localSimplices = (Element) localList.item(0);
        localScanner = new Scanner(localSimplices.getTextContent());
        Tetra t2;
        while (localScanner.hasNextInt()) {
          t2 = getTetra(localScanner.nextInt());
          t.addUniqueTetra(t2);
          // t2.addUniqueTetra(t);
        }
      }
      String multiplicity = simplexNode.getAttribute("multiplicity");
      if (multiplicity.length() != 0) {
        t.setMultiplicity(Integer.parseInt(multiplicity));
      }
      String boundary = simplexNode.getAttribute("boundary");
      if(boundary.length() !=0){
    	  t.setBoundary(Boolean.parseBoolean(boundary));
      }
      
    }

    for (Vertex v2 : Triangulation.vertexTable.values()) {
      edges = v2.getLocalEdges().toArray(new Edge[0]);
      for (int i = 0; i < edges.length; i++) {
        for (int j = i + 1; j < edges.length; j++) {
          edges[i].addUniqueEdge(edges[j]);
          try {
            edges[j].addUniqueEdge(edges[i]);
          } catch (NullPointerException exc) {
            System.err.println("Null Pointer Exception\n" + v2 + "\nEdges = "
                + edges.length);
          }
        }
      }
    }

    for (Edge e2 : Triangulation.edgeTable.values()) {
      faces = e2.getLocalFaces().toArray(new Face[0]);
      for (int i = 0; i < faces.length; i++) {
        for (int j = i + 1; j < faces.length; j++) {
          faces[i].addUniqueFace(faces[j]);
          faces[j].addUniqueFace(faces[i]);
        }
      }
    }
    for (Face f2 : Triangulation.faceTable.values()) {
      tetras = f2.getLocalTetras().toArray(new Tetra[0]);
      for (int i = 0; i < tetras.length; i++) {
        for (int j = i + 1; j < tetras.length; j++) {
          tetras[i].addUniqueTetra(tetras[j]);
          tetras[j].addUniqueTetra(tetras[i]);
        }
      }
    }
    for (Tetra t2 : Triangulation.tetraTable.values()) {
      verts = t2.getLocalVertices().toArray(new Vertex[0]);
      for (int i = 0; i < verts.length; i++) {
        for (int j = i + 1; j < verts.length; j++) {
          verts[i].addUniqueVertex(verts[j]);
          verts[j].addUniqueVertex(verts[i]);
        }
      }
    }

    //reset TextureCoords values and create the face groups
    
    /**
     * TODO: We need some equivalence relation between faces in .xml documents in order
     * to get consistent texturing.
     */
    
    TextureCoords.reset();
    
    //Check if the xml file had color info
    Face testFace = Triangulation.faceTable.get(1);
    if(testFace.hasMetaFace()){
    	for(Face face : Triangulation.faceTable.values()){
    		if(Triangulation.groupTable.values().isEmpty()){
    			FaceGrouping fg = new FaceGrouping(face);
    			Triangulation.putFaceGrouping(face, fg);
    		}
    		else{
    			boolean added = false;
    			for(FaceGrouping fg : Triangulation.groupTable.values()){
    				if(fg.getFaces().get(0).getMetaFace() == face.getMetaFace()){
    					fg.add(face);
    	    			Triangulation.putFaceGrouping(face, fg);
    					added = true;
    					break;
    				}	
    			}
    			if(added)continue;
    			FaceGrouping fg = new FaceGrouping(face);
    			Triangulation.putFaceGrouping(face, fg);
    		}
    	}
    }
    
    else if(testFace.getColor() != null){
      createGroupings();
    }
    else{
      //If not color, just give each face its own face group for the purposes of texturing
      for(Face face : Triangulation.faceTable.values()){
        FaceGrouping fg = new FaceGrouping(face);
        Triangulation.putFaceGrouping(face, fg);
      }
    }
  }

  /*********************************************************************************
   * createGroupings
   * 
   * This method is responsible for going recursively through the faces in the
   * triangulation and putting them together in their appropriate groups (i.e.
   * putting together all triangles that belong to the same surface of the
   * manifold). It then stores the faces and these FaceGroupings in a map from
   * Faces => FaceGroupings, allowing other classes to quickly access the
   * FaceGrouping for any given Face. The map can be found in Triangulation.
   * 
   * Note that right now this method determines if two faces should be grouped
   * based on whether they share an edge and have the same color. This may need
   * to be updated later.
   *********************************************************************************/
  
  private static void createGroupings() {
    for (Face f : Triangulation.faceTable.values()) {
      FaceGrouping fg = Triangulation.groupTable.get(f);

      if (fg != null)
        continue;

      fg = new FaceGrouping(f);
      Triangulation.groupTable.put(f, fg);
      checkNeighbors(f, fg);
    }
  }

  /*********************************************************************************
   * checkNeighbors
   * 
   * This is a recursive helper method for createGroupings.
   *********************************************************************************/
  
  private static void checkNeighbors(Face f, FaceGrouping fg) {
    for (Edge e : f.getLocalEdges()) {
      Face adjFace = DevelopmentComputations.getNewFace(f, e);
      if (adjFace == null)
        continue;
      if (fg.contains(adjFace))
        continue;
      // TODO: modify condition for grouping?
      if (f.getColor().equals(adjFace.getColor())) {
        fg.add(adjFace);
        Triangulation.putFaceGrouping(adjFace, fg);
        checkNeighbors(adjFace, fg);
      }
    }
  }

  public static void writeTriangulation(String filename) {
    Document triangulationDoc = XMLParser.createDocument(namespace,
        "Triangulation");
    Element triangulation = triangulationDoc.getDocumentElement();
    Element simplex;
    Element localSimplex;
    String local;

    // Vertices
    for (Vertex v : Triangulation.vertexTable.values()) {
      simplex = triangulationDoc.createElement("Vertex");
      simplex.setAttribute("index", "" + v.getIndex());
      simplex.setAttribute("radius", "" + Radius.valueAt(v));
      simplex.setAttribute("alpha", "" + Alpha.valueAt(v));
      simplex.setAttribute("multiplicity", "" + v.getMultiplicity());
      simplex.setAttribute("boundary", "" + v.getBoundary());
      double [] array = v.getCoordinates();
      if(array[0] != Double.MIN_NORMAL)
      	simplex.setAttribute("x-coordinate", ""+array[0]);
      if(array[1] != Double.MIN_NORMAL)
        simplex.setAttribute("y-coordinate", ""+array[1]);
      if(array[2] != Double.MIN_NORMAL)
    	  simplex.setAttribute("z-coordinate", ""+array[2]);

      // Local Vertices
      localSimplex = triangulationDoc.createElement("Vertices");
      local = "";
      for (Vertex v2 : v.getLocalVertices()) {
        local += "" + v2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Edges
      localSimplex = triangulationDoc.createElement("Edges");
      local = "";
      for (Edge e2 : v.getLocalEdges()) {
        local += "" + e2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Faces
      localSimplex = triangulationDoc.createElement("Faces");
      local = "";
      for (Face f2 : v.getLocalFaces()) {
        local += "" + f2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Tetras
      localSimplex = triangulationDoc.createElement("Tetras");
      local = "";
      for (Tetra t2 : v.getLocalTetras()) {
        local += "" + t2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      triangulation.appendChild(simplex);
    }

    // Edges
    for (Edge e : Triangulation.edgeTable.values()) {
      simplex = triangulationDoc.createElement("Edge");
      simplex.setAttribute("index", "" + e.getIndex());
      simplex.setAttribute("eta", "" + Eta.valueAt(e));
      simplex.setAttribute("length", "" + Length.valueAt(e));
      simplex.setAttribute("multiplicity", "" + e.getMultiplicity());
      simplex.setAttribute("boundary", "" + e.getBoundary());


      // Local Vertices
      localSimplex = triangulationDoc.createElement("Vertices");
      local = "";
      for (Vertex v2 : e.getLocalVertices()) {
        local += "" + v2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Edges
      localSimplex = triangulationDoc.createElement("Edges");
      local = "";
      for (Edge e2 : e.getLocalEdges()) {
        local += "" + e2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Faces
      localSimplex = triangulationDoc.createElement("Faces");
      local = "";
      for (Face f2 : e.getLocalFaces()) {
        local += "" + f2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Tetras
      localSimplex = triangulationDoc.createElement("Tetras");
      local = "";
      for (Tetra t2 : e.getLocalTetras()) {
        local += "" + t2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      triangulation.appendChild(simplex);
    }

    // Faces
    for (Face f : Triangulation.faceTable.values()) {
      simplex = triangulationDoc.createElement("Face");
      simplex.setAttribute("index", "" + f.getIndex());
      simplex.setAttribute("multiplicity", "" + f.getMultiplicity());
      simplex.setAttribute("boundary", "" + f.getBoundary());


      // Local Vertices
      localSimplex = triangulationDoc.createElement("Vertices");
      local = "";
      for (Vertex v2 : f.getLocalVertices()) {
        local += "" + v2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Edges
      localSimplex = triangulationDoc.createElement("Edges");
      local = "";
      for (Edge e2 : f.getLocalEdges()) {
        local += "" + e2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Faces
      localSimplex = triangulationDoc.createElement("Faces");
      local = "";
      for (Face f2 : f.getLocalFaces()) {
        local += "" + f2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Tetras
      localSimplex = triangulationDoc.createElement("Tetras");
      local = "";
      for (Tetra t2 : f.getLocalTetras()) {
        local += "" + t2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      triangulation.appendChild(simplex);
    }

    // Tetras
    for (Tetra t : Triangulation.tetraTable.values()) {
      simplex = triangulationDoc.createElement("Tetra");
      simplex.setAttribute("index", "" + t.getIndex());
      simplex.setAttribute("multiplicity", "" + t.getMultiplicity());
      simplex.setAttribute("boundary", "" + t.getBoundary());


      // Local Vertices
      localSimplex = triangulationDoc.createElement("Vertices");
      local = "";
      for (Vertex v2 : t.getLocalVertices()) {
        local += "" + v2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Edges
      localSimplex = triangulationDoc.createElement("Edges");
      local = "";
      for (Edge e2 : t.getLocalEdges()) {
        local += "" + e2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Faces
      localSimplex = triangulationDoc.createElement("Faces");
      local = "";
      for (Face f2 : t.getLocalFaces()) {
        local += "" + f2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      // Local Tetras
      localSimplex = triangulationDoc.createElement("Tetras");
      local = "";
      for (Tetra t2 : t.getLocalTetras()) {
        local += "" + t2.getIndex() + " ";
      }
      if (local.length() > 0) {
        localSimplex.setTextContent(local);
        simplex.appendChild(localSimplex);
      }

      triangulation.appendChild(simplex);
    }

    XMLParser.writeDocument(triangulationDoc, filename);
  }

  private static Vertex getVertex(int index) {
    Vertex v = Triangulation.vertexTable.get(index);
    if (v == null) {
      v = new Vertex(index);
      Triangulation.vertexTable.put(index, v);
    }
    return v;
  }

  private static Edge getEdge(int index) {
    Edge e = Triangulation.edgeTable.get(index);
    if (e == null) {
      e = new Edge(index);
      Triangulation.edgeTable.put(index, e);
    }
    return e;
  }

  private static Face getFace(int index) {
    Face f = Triangulation.faceTable.get(index);
    if (f == null) {
      f = new Face(index);
      Triangulation.faceTable.put(index, f);
    }
    return f;
  }

  private static Tetra getTetra(int index) {
    Tetra t = Triangulation.tetraTable.get(index);
    if (t == null) {
      t = new Tetra(index);
      Triangulation.tetraTable.put(index, t);
    }
    return t;
  }

  @Deprecated
  public static void read2DTriangulationFile(String filename) {
    read2DTriangulationFile(new File(filename));
  }

  @Deprecated
  public static void read2DTriangulationFile(File file) {
    Triangulation.reset();
    Scanner fileScanner;
    Scanner stringScanner;
    String type;
    Simplex simplex;
    int index;
    Vertex v;
    Edge e;
    Face f;

    try {
      fileScanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      System.err.print("File given by " + file + " could not be found\n");
      ex.printStackTrace();
      return;
    }

    while (fileScanner.hasNextLine()) {
      stringScanner = new Scanner(fileScanner.nextLine());
      type = stringScanner.next();
      index = stringScanner.nextInt();

      if (type.compareTo("Vertex:") == 0) {
        simplex = Triangulation.vertexTable.get(index);
        if (simplex == null) {
          simplex = new Vertex(index);
          Triangulation.putVertex((Vertex) simplex);
        }
      } else if (type.compareTo("Edge:") == 0) {
        simplex = Triangulation.edgeTable.get(index);
        if (simplex == null) {
          simplex = new Edge(index);
          Triangulation.putEdge((Edge) simplex);
        }
      } else if (type.compareTo("Face:") == 0) {
        simplex = Triangulation.faceTable.get(index);
        if (simplex == null) {
          simplex = new Face(index);
          Triangulation.putFace((Face) simplex);
        }
      } else {
        System.err.print("Invald simplex type " + type + "\n");
        Triangulation.reset();
        return;
      }

      // Local vertices
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        v = Triangulation.vertexTable.get(index);
        if (v == null) {
          v = new Vertex(index);
          Triangulation.putVertex(v);
        }
        simplex.addVertex(v);
      }

      // Local edges
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        e = Triangulation.edgeTable.get(index);
        if (e == null) {
          e = new Edge(index);
          Triangulation.putEdge(e);
        }
        simplex.addEdge(e);
      }

      // Local faces
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        f = Triangulation.faceTable.get(index);
        if (f == null) {
          f = new Face(index);
          Triangulation.putFace(f);
        }
        simplex.addFace(f);
      }

      // Check for radii / lengths
      if (fileScanner.hasNextDouble()) {
        stringScanner = new Scanner(fileScanner.nextLine());
        if (type.compareTo("Vertex:") == 0) {
          Radius.at((Vertex) simplex).setValue(stringScanner.nextDouble());
        } else if (type.compareTo("Edge:") == 0) {
          Length.at((Edge) simplex).setValue(stringScanner.nextDouble());
        }
      }
    }
  }

  @Deprecated
  public static void read3DTriangulationFile(String filename) {
    read3DTriangulationFile(new File(filename));
  }

  @Deprecated
  public static void read3DTriangulationFile(File file) {
    Triangulation.reset();
    Scanner fileScanner;
    Scanner stringScanner;
    String type;
    Simplex simplex;
    int index;
    Vertex v;
    Edge e;
    Face f;
    Tetra t;

    try {
      fileScanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      System.err.print("File given by " + file + " could not be found\n");
      ex.printStackTrace();
      return;
    }

    while (fileScanner.hasNextLine()) {
      stringScanner = new Scanner(fileScanner.nextLine());
      type = stringScanner.next();
      index = stringScanner.nextInt();

      if (type.compareTo("Vertex:") == 0) {
        simplex = Triangulation.vertexTable.get(index);
        if (simplex == null) {
          simplex = new Vertex(index);
          Triangulation.putVertex((Vertex) simplex);
        }
      } else if (type.compareTo("Edge:") == 0) {
        simplex = Triangulation.edgeTable.get(index);
        if (simplex == null) {
          simplex = new Edge(index);
          Triangulation.putEdge((Edge) simplex);
        }
      } else if (type.compareTo("Face:") == 0) {
        simplex = Triangulation.faceTable.get(index);
        if (simplex == null) {
          simplex = new Face(index);
          Triangulation.putFace((Face) simplex);
        }
      } else if (type.compareTo("Tetra:") == 0) {
        simplex = Triangulation.tetraTable.get(index);
        if (simplex == null) {
          simplex = new Tetra(index);
          Triangulation.putTetra((Tetra) simplex);
        }
      } else {
        System.err.print("Invald simplex type " + type + "\n");
        Triangulation.reset();
        return;
      }

      // Local vertices
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        v = Triangulation.vertexTable.get(index);
        if (v == null) {
          v = new Vertex(index);
          Triangulation.putVertex(v);
        }
        simplex.addVertex(v);
      }

      // Local edges
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        e = Triangulation.edgeTable.get(index);
        if (e == null) {
          e = new Edge(index);
          Triangulation.putEdge(e);
        }
        simplex.addEdge(e);
      }

      // Local faces
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        f = Triangulation.faceTable.get(index);
        if (f == null) {
          f = new Face(index);
          Triangulation.putFace(f);
        }
        simplex.addFace(f);
      }

      // Local tetra
      stringScanner = new Scanner(fileScanner.nextLine());
      while (stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        t = Triangulation.tetraTable.get(index);
        if (t == null) {
          t = new Tetra(index);
          Triangulation.putTetra(t);
        }
        simplex.addTetra(t);
      }

      // Check for radii / lengths
      if (fileScanner.hasNextDouble()) {
        if (type.compareTo("Vertex:") == 0) {
          Radius.at((Vertex) simplex).setValue(fileScanner.nextDouble());
        } else if (type.compareTo("Edge:") == 0) {
          Length.at((Edge) simplex).setValue(fileScanner.nextDouble());
        }
      }
    }
  }

  private static void readLutzFile(String filename) {
    File file = new File(filename);
    Scanner scanner = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    if (scanner.findInLine("\\[\\w*,\\w*,\\w*\\]") != null) {
      read2DLutzFile(filename);
    } else if (scanner.findInLine("\\[\\w*,\\w*,\\w*,\\w*\\]") != null) {
      read3DLutzFile(filename);
    }
  }

  private static void read2DLutzFile(String filename) {
    read2DLutzFile(new File(filename));
  }

  private static void read2DLutzFile(File file) {
    Triangulation.reset();
    String faces;
    Scanner scanner = null;
    Scanner line;
    HashMap<TriPosition, Edge> edgeList = new HashMap<TriPosition, Edge>();
    Vertex v;
    Edge e;
    Face f;
    Vertex[] verts = new Vertex[3];
    int index;
    TriPosition T;

    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }

    faces = "";
    while (scanner.hasNextLine()) {
      faces = faces.concat(scanner.nextLine());
    }

    faces = faces.substring(faces.lastIndexOf("=") + 1);
    faces = faces.replaceAll("[^0-9],[^0-9]", "\n");
    faces = faces.replaceAll(",", " ");
    faces = faces.replaceAll("[^0-9 \n]", "");

    scanner = new Scanner(faces);
    while (scanner.hasNextLine()) {
      line = new Scanner(scanner.nextLine());

      // Create face;
      f = new Face(Triangulation.greatestFace() + 1);
      Triangulation.putFace(f);

      // Fill out verts, create vertices, add to face
      for (int i = 0; i < verts.length; i++) {
        index = line.nextInt();
        v = Triangulation.vertexTable.get(index);
        if (v == null) {
          v = new Vertex(index);
          Triangulation.putVertex(v);
        }
        v.addFace(f);
        f.addVertex(v);
        verts[i] = v;
        // add to other vertices
        for (int j = 0; j < i; j++) {
          if (!verts[j].isAdjVertex(v)) {
            verts[j].addVertex(v);
            v.addVertex(verts[j]);
          }
        }
      }

      // Build edges
      for (int i = 0; i < verts.length; i++) {
        for (int j = 0; j < i; j++) {
          T = new TriPosition(verts[i].getIndex(), verts[j].getIndex());
          e = edgeList.get(T);
          if (e == null) {
            e = new Edge(Triangulation.greatestEdge() + 1);
            Triangulation.putEdge(e);
            edgeList.put(T, e);
            for (Edge e2 : verts[i].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            for (Edge e2 : verts[j].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            verts[i].addEdge(e);
            verts[j].addEdge(e);
            e.addVertex(verts[j]);
            e.addVertex(verts[i]);
          } else {
            for (Face f2 : e.getLocalFaces()) {
              f.addFace(f2);
              f2.addFace(f);
            }
          }
          e.addFace(f);
          f.addEdge(e);
        }
      }
    }
  }

  private static void read3DLutzFile(String filename) {
    read3DLutzFile(new File(filename));
  }

  private static void read3DLutzFile(File file) {
    Triangulation.reset();
    String tetras;
    Scanner scanner = null;
    Scanner line;
    HashMap<TriPosition, Edge> edgeList = new HashMap<TriPosition, Edge>();
    HashMap<TriPosition, Face> faceList = new HashMap<TriPosition, Face>();
    Vertex v;
    Edge e;
    Face f;
    Tetra t;
    Vertex[] verts = new Vertex[4];
    int index;
    TriPosition T;

    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }

    tetras = "";
    while (scanner.hasNextLine()) {
      tetras = tetras.concat(scanner.nextLine());
    }

    tetras = tetras.substring(tetras.lastIndexOf("=") + 1);
    tetras = tetras.replaceAll("[^0-9],[^0-9]", "\n");
    tetras = tetras.replaceAll(",", " ");
    tetras = tetras.replaceAll("[^0-9 \n]", "");

    scanner = new Scanner(tetras);
    while (scanner.hasNextLine()) {
      line = new Scanner(scanner.nextLine());

      // Create tetra
      t = getTetra(Triangulation.greatestTetra() + 1);
      Triangulation.putTetra(t);

      // Fill out verts, create vertices, add to face
      for (int i = 0; i < verts.length; i++) {
        index = line.nextInt();
        v = getVertex(index);
        v.addTetra(t);
        t.addVertex(v);
        verts[i] = v;
        // add to other vertices
        for (int j = 0; j < i; j++) {
          if (!verts[j].isAdjVertex(v)) {
            verts[j].addVertex(v);
            v.addVertex(verts[j]);
          }
        }
      }

      // Build edges
      for (int i = 0; i < verts.length; i++) {
        for (int j = 0; j < i; j++) {
          T = new TriPosition(verts[i].getIndex(), verts[j].getIndex());
          e = edgeList.get(T);
          if (e == null) {
            e = new Edge(Triangulation.greatestEdge() + 1);
            Triangulation.putEdge(e);
            edgeList.put(T, e);
            for (Edge e2 : verts[i].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            for (Edge e2 : verts[j].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            verts[i].addEdge(e);
            verts[j].addEdge(e);
            e.addVertex(verts[j]);
            e.addVertex(verts[i]);
          }
          e.addTetra(t);
          t.addEdge(e);
        }
      }

      // Build faces
      Edge eij, eik, ejk;
      for (int i = 0; i < verts.length; i++) {
        for (int j = 0; j < i; j++) {
          for (int k = 0; k < j; k++) {
            T = new TriPosition(verts[i].getIndex(), verts[j].getIndex(),
                verts[k].getIndex());
            f = faceList.get(T);
            if (f == null) {
              f = new Face(Triangulation.greatestFace() + 1);
              Triangulation.putFace(f);
              faceList.put(T, f);
              // Vertices
              f.addVertex(verts[k]);
              f.addVertex(verts[j]);
              f.addVertex(verts[i]);
              verts[i].addFace(f);
              verts[j].addFace(f);
              verts[k].addFace(f);

              // Edges
              eij = edgeList.get(new TriPosition(verts[i].getIndex(), verts[j]
                  .getIndex()));
              eik = edgeList.get(new TriPosition(verts[i].getIndex(), verts[k]
                  .getIndex()));
              ejk = edgeList.get(new TriPosition(verts[j].getIndex(), verts[k]
                  .getIndex()));
              for (Face f2 : ejk.getLocalFaces()) {
                f.addFace(f2);
                f2.addFace(f);
              }
              for (Face f2 : eik.getLocalFaces()) {
                f.addFace(f2);
                f2.addFace(f);
              }
              for (Face f2 : eij.getLocalFaces()) {
                f.addFace(f2);
                f2.addFace(f);
              }
              eij.addFace(f);
              eik.addFace(f);
              ejk.addFace(f);
              f.addEdge(ejk);
              f.addEdge(eik);
              f.addEdge(eij);
            } else {
              for (Tetra t2 : f.getLocalTetras()) {
                t.addTetra(t2);
                t2.addTetra(t);
              }
            }
            f.addTetra(t);
            t.addFace(f);
          }
        }
      }
    }
  }

  @Deprecated
  public static void write2DTriangulationFile(String filename) {
    try {
      write2DTriangulationFile(new PrintStream(filename));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Deprecated
  public static void write2DTriangulationFile(PrintStream out) {
    for (Vertex v : Triangulation.vertexTable.values()) {
      out.printf(v + ":\n");
      for (Vertex v2 : v.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : v.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : v.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
    }
    for (Edge e : Triangulation.edgeTable.values()) {
      out.printf(e + ":\n");
      for (Vertex v2 : e.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : e.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : e.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
    }
    for (Face f : Triangulation.faceTable.values()) {
      out.printf(f + ":\n");
      for (Vertex v2 : f.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : f.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : f.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
    }
    out.close();
  }

  @Deprecated
  public static void write3DTriangulationFile(String filename) {
    try {
      write3DTriangulationFile(new PrintStream(filename));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Deprecated
  public static void write3DTriangulationFile(PrintStream out) {
    for (Vertex v : Triangulation.vertexTable.values()) {
      out.printf(v + ":\n");
      for (Vertex v2 : v.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : v.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : v.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
      for (Tetra t2 : v.getLocalTetras()) {
        out.print(t2.getIndex() + " ");
      }
      out.println();
    }
    for (Edge e : Triangulation.edgeTable.values()) {
      out.printf(e + ":\n");
      for (Vertex v2 : e.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : e.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : e.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
      for (Tetra t2 : e.getLocalTetras()) {
        out.print(t2.getIndex() + " ");
      }
      out.println();
    }
    for (Face f : Triangulation.faceTable.values()) {
      out.printf(f + ":\n");
      for (Vertex v2 : f.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : f.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : f.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
      for (Tetra t2 : f.getLocalTetras()) {
        out.print(t2.getIndex() + " ");
      }
      out.println();
    }
    for (Tetra t : Triangulation.tetraTable.values()) {
      out.printf(t + ":\n");
      for (Vertex v2 : t.getLocalVertices()) {
        out.print(v2.getIndex() + " ");
      }
      out.println();
      for (Edge e2 : t.getLocalEdges()) {
        out.print(e2.getIndex() + " ");
      }
      out.println();
      for (Face f2 : t.getLocalFaces()) {
        out.print(f2.getIndex() + " ");
      }
      out.println();
      for (Tetra t2 : t.getLocalTetras()) {
        out.print(t2.getIndex() + " ");
      }
      out.println();
    }
    out.close();
  }

}
