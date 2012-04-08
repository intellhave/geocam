package view;

import java.awt.Color;
import java.util.ArrayList;

import objects.ObjectAppearance;
import objects.PathAppearance;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import development.LineSegment;
import development.Node3D;
import development.Trail;
import development.Vector;

/**
 * useful methods of generating SceneGraphComponents
 */
public class SGCMethods {
  
  // Returns geometry for a collection of 2D or 3D points
  public static Geometry objectGeometryFromList(ArrayList<Vector> images, ObjectAppearance app, boolean mode2D, double zvalue){
    
    PointSetFactory psf = new PointSetFactory();
    
    double[][] verts = new double[images.size()][3];
    Color[] colors = new Color[images.size()];
    Color c = app.getColor();
    for(int i=0; i<images.size(); i++){
      Vector v = images.get(i);
      if(mode2D){ verts[i] = new double[]{ v.getComponent(0), v.getComponent(1), zvalue }; }
      else{ verts[i] = new double[]{ v.getComponent(0), v.getComponent(1), v.getComponent(2) }; }
      colors[i] = c;
    }
    psf.setVertexCount(images.size());
    psf.setVertexCoordinates(verts);
    psf.setVertexColors(colors);
    psf.update();
    
    return psf.getGeometry();
  }
  
  //Returns SGC for a collection of 2D or 3D points (used in each DevelopmentView)
  public static SceneGraphComponent objectSGCFromList(ArrayList<Vector> images, ObjectAppearance app, boolean mode2D, double zvalue){
    
    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(objectGeometryFromList(images,app,mode2D,zvalue));
    sgc.setAppearance(app.getJRealityAppearance());
    return sgc;
  }
  
  // Returns geometry for a collection of 2D or 3D line segments
  public static Geometry pathGeometryFromList(ArrayList<LineSegment> images, PathAppearance app, boolean mode2D, double zvalue){

    IndexedLineSetFactory ilsf = new IndexedLineSetFactory();

    int lineCount = images.size();
    double[][] verts = new double[lineCount*2][3];
    Color[] vertcolors = new Color[lineCount*2];
    int[][] edges = new int[lineCount][2];
    Color[] tubecolors = new Color[lineCount];
    
    for(int i=0; i<lineCount; i++){
      LineSegment s = images.get(i);
      Vector v0 = s.getStart();
      Vector v1 = s.getEnd();
      if(mode2D){
        verts[i*2] = new double[]{ v0.getComponent(0), v0.getComponent(1), zvalue };
        verts[i*2+1] = new double[]{ v1.getComponent(0), v1.getComponent(1), zvalue };
      }else{
        verts[i*2] = new double[]{ v0.getComponent(0), v0.getComponent(1), v0.getComponent(2) };
        verts[i*2+1] = new double[]{ v1.getComponent(0), v1.getComponent(1), v1.getComponent(2) };
      }
      vertcolors[i*2] = app.getVertexColor();
      vertcolors[i*2+1] = app.getVertexColor();
      edges[i] = new int[]{ i*2, i*2+1 };
      tubecolors[i] = app.getTubeColor();
    }
    
    ilsf.setVertexCount(lineCount*2);
    ilsf.setVertexCoordinates(verts);
    ilsf.setVertexColors(vertcolors);
    ilsf.setEdgeCount(lineCount);
    ilsf.setEdgeIndices(edges);
    ilsf.setEdgeColors(tubecolors);
    ilsf.update();
    
    return ilsf.getGeometry();
  }
  
  //Returns SGC for a collection of 2D or 3D line segments (used in each DevelopmentView)
  public static SceneGraphComponent pathSGCFromList(ArrayList<LineSegment> images, PathAppearance app, boolean mode2D, double zvalue){
    
    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(pathGeometryFromList(images,app,mode2D,zvalue));
    sgc.setAppearance(app.getJRealityAppearance());
    return sgc;
  }

  
  public static SceneGraphComponent sgcFromNode(NodeImage node, int dimension) {
    Vector v = node.getPosition();
    if(dimension > 2) 
      v = new Vector(v.getComponent(0), v.getComponent(1), 0);
    if(dimension == 2 && node instanceof SourceNodeImage)
      return node.getSGC(dimension);
    
    return sgcFromPoint(v, node.getRadius(), 1, node.getColor(),
        node.getTransparency());
  }
  
  /*
   * Returns SGC representing a node in Development3D
   */
  public static SceneGraphComponent sgcFromNode3D(Node3D node) {
    return sgcFromPoint(node.getPosition(), node.getRadius(), 1, node.getColor(),
        node.getTransparency());
  }

  /*
   * Returns SGC with sphere at position of point, with specified
   * radius, color, and transparency in (0,1)
   */
  public static SceneGraphComponent sgcFromPoint(Vector point, double radius, double zvalue,
      Color color, double transparency) {
    SceneGraphComponent sgc_points = new SceneGraphComponent();
    Appearance app_points = new Appearance();

    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);
    app_points.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    app_points.setAttribute(CommonAttributes.TRANSPARENCY, transparency);

    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(radius);
    dps.setDiffuseColor(color);

    sgc_points.setAppearance(app_points);

    double[][] vertlist = new double[1][3];
    if (point.getDimension() == 2) {
      vertlist[0] = new double[] { point.getComponent(0),
          point.getComponent(1), zvalue };
    } else if (point.getDimension() == 3) {
      vertlist[0] = new double[] { point.getComponent(0),
          point.getComponent(1), point.getComponent(2) };
    }

    // create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();
    psf.setVertexCount(1);
    psf.setVertexCoordinates(vertlist);
    psf.update();

    // set geometry
    sgc_points.setGeometry(psf.getGeometry());

    // return
    return sgc_points;
  }
  
  public static SceneGraphComponent sgcFromVertices(double zvalue, Vector...vectors) {
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

//    Color[] colorList = new Color[colors.size()];
//    for (int i = 0; i < colors.size(); i++) {
//      colorList[i] = colors.get(i);
//    }
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    double[][] vectorList = new double[vectors.length][vectors[0].getDimension()];
    
    for(int i = 0; i < vectors.length; i++) {
      vectorList[i] = vectors[i].getVectorAsArray();
    }
    geometry.addFace(vectorList, zvalue);

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.update();
    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(ifsf.getGeometry());
    return sgc;
  }
  
  public static SceneGraphComponent sgcFromVertices(double zvalue, ArrayList<Vector> vectors) {
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

//    Color[] colorList = new Color[colors.size()];
//    for (int i = 0; i < colors.size(); i++) {
//      colorList[i] = colors.get(i);
//    }
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    double[][] vectorList = new double[vectors.size()][vectors.get(0).getDimension()];
    
    for(int i = 0; i < vectors.size(); i++) {
      vectorList[i] = vectors.get(i).getVectorAsArray();
    }
    geometry.addFace(vectorList, zvalue);

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.update();
    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(ifsf.getGeometry());
    return sgc;
  }

  /*
   * Returns SGC with line from origin to v, with specified radius.
   * Color is blue.
   */
  public static SceneGraphComponent sgcFromVector(Vector v, double radius) {
    SceneGraphComponent sgc = new SceneGraphComponent();
    Appearance app_points = new Appearance();
    app_points.setAttribute(CommonAttributes.TUBE_RADIUS, radius);
    // set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.01);
    dps.setDiffuseColor(Color.BLUE);
    sgc.setAppearance(app_points);

    IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
    ilsf.setVertexCount(2);
    ilsf.setEdgeCount(1);

    ilsf.setVertexCoordinates(new double[][] { { 0, 0, 1 },
        { v.getComponent(0), v.getComponent(1), 1 } });
    ilsf.setEdgeIndices(new int[][] { { 0, 1 } });
    ilsf.update();
    sgc.setGeometry(ilsf.getGeometry());
    return sgc;
  }

  public static Appearance getFaceAppearance(double transparency) {
    // create appearance for developed faces
    Appearance app_face = new Appearance();

    // set some basic attributes
    app_face.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_face.setAttribute(CommonAttributes.EDGE_DRAW, false);
    app_face.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_face.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_face.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);

    // set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_face, true);

    // line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);

    // polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(Color.WHITE);
    dps.setTransparency(transparency);

    return app_face;
  }

  public static Appearance getDevelopmentAppearance() {
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app.setAttribute(CommonAttributes.TUBE_RADIUS, 0.01);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    app.setAttribute(CommonAttributes.TRANSPARENCY, 0.4d);
    app.setAttribute(CommonAttributes.PICKABLE, true);
    return app;
  }
  
  public static Appearance getDevelopment3DAppearance() {
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.FACE_DRAW, true);
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(true);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);
//    dls.setDiffuseColor(Color.BLUE);
    
      
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    //dps.setDiffuseColor(color);
 //   dps.setTransparency(0.89d);
 //   dps.setDiffuseCoefficient(10.0);
 //   dps.setDiffuseColor(Color.BLUE);
    dps.setAmbientCoefficient(0.2);
    dps.setAmbientColor(Color.WHITE);
    dps.setSmoothShading(false);
 //   dps.setSpecularCoefficient(10.0);
    dps.setTransparency(0.8d);
     return app;
  }

  // class designed to make it easy to use an IndexedFaceSetFactory
  public static class DevelopmentGeometry {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();

    public void addFace(double[][] faceverts, double zvalue) {

      int nverts = faceverts.length;
      int vi = geometry_verts.size();

      int[] newface = new int[nverts];
      
      for (int k = 0; k < nverts; k++) {
        double[] newvert = new double[3];
        newvert[0] = faceverts[k][0];
        newvert[1] = faceverts[k][1];
        if(faceverts[k].length > 2)
          newvert[2] = faceverts[k][2];
        else
          newvert[2] = zvalue;
        geometry_verts.add(newvert);
        newface[k] = vi++;
      }
      geometry_faces.add(newface);
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }
  };

  // class designed to make it easy to use an IndexedFaceSetFactory
  public static class DevelopmentGeometrySim3D {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();
    private ArrayList<int[]> geometry_edges = new ArrayList<int[]>();

    public void addFace(double[][] faceverts, double height) {

      int n = faceverts.length;
      double[][] ifsf_verts = new double[2 * n][3];
      int[][] ifsf_edges = new int[3 * n][2];
      int[][] ifsf_faces = new int[2][n];

      for (int i = 0; i < n; i++) {
        // for some reason, switching '-' sign makes light work
        // but colors are flipped either way
        ifsf_verts[i] = new double[] { faceverts[i][0], faceverts[i][1], height };
        ifsf_verts[i + n] = new double[] { faceverts[i][0], faceverts[i][1],
            -height };
      }

      for (int i = 0; i < n; i++) {
        int j = (i + 1) % n;
        ifsf_edges[i] = new int[] { i + geometry_verts.size(),
            j + geometry_verts.size() };
        ifsf_edges[i + n] = new int[] { i + n + geometry_verts.size(),
            j + n + geometry_verts.size() };
        ifsf_edges[i + n + n] = new int[] { i + geometry_verts.size(),
            i + n + geometry_verts.size() };
      }

      for (int i = 0; i < n; i++) {
        ifsf_faces[0][i] = geometry_verts.size() + i;
        ifsf_faces[1][i] = n + (n - 1) - i + geometry_verts.size();
      }

      geometry_faces.add(ifsf_faces[0]);
      geometry_faces.add(ifsf_faces[1]);

      for (int i = 0; i < 2 * n; i++) {
        geometry_verts.add(ifsf_verts[i]);
        geometry_edges.add(ifsf_edges[i]);
      }
      for (int i = 2 * n; i < 3 * n; i++) {
        geometry_edges.add(ifsf_edges[i]);
      }
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }

    public int[][] getEdges() {
      return (int[][]) geometry_edges.toArray(new int[0][0]);
    }
  }

  public static SceneGraphComponent sgcFromTrail(Trail trail) {
    SceneGraphComponent sgc = new SceneGraphComponent();
    Appearance app_points = new Appearance();
    app_points.setAttribute(CommonAttributes.TUBE_RADIUS, 0.05);
    // set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.01);
    dps.setDiffuseColor(trail.getColor());
    sgc.setAppearance(app_points);

    IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
    ilsf.setVertexCount(2);
    ilsf.setEdgeCount(1);

    Vector s = trail.getStart();
    Vector e = trail.getEnd();

    ilsf.setVertexCoordinates(new double[][] {
        { s.getComponent(0), s.getComponent(1), 1 },
        { e.getComponent(0), e.getComponent(1), 1 } });
    ilsf.setEdgeIndices(new int[][] { { 0, 1 } });
    ilsf.update();
    sgc.setGeometry(ilsf.getGeometry());
    return sgc;
  };

}
