package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import development.LineSegment;
import development.Vector;
import triangulation.Face;

/* Represents a path on the manifold
 * 
 * Useful for representing e.g. geodesic paths on the manifold
 * The path is stored in segments, organized by face rather than in linear order
 */

public class ManifoldPath {

  //little data structure to hold a line segment residing in a single face
  public static class Segment{
    public Face face;
    public LineSegment ls;
    
    public Segment(Face face, Vector startPos, Vector endPos){
      this.face = face;
      this.ls = new LineSegment(startPos, endPos);
    }
    
    public Segment(Segment s){
      this.face = s.face;
      this.ls = new LineSegment(s.ls);
    }
  }
  
  private HashMap<Face,ArrayList<Segment>> sortedSegmentList;
  
  public ManifoldPath(){
    sortedSegmentList = new HashMap<Face,ArrayList<Segment>>();
  }
  
  public ManifoldPath(ManifoldPath path){
    
    //copy constructor
    sortedSegmentList = new HashMap<Face,ArrayList<Segment>>();
    
    //get the sorted segment list from the given path
    HashMap<Face,ArrayList<Segment>> copySortedSegmentList = path.getSortedSegmentList();
    
    Set<Face> faceList = copySortedSegmentList.keySet();
    for(Face f : faceList){
      
      //make a new list of segments for this face
      ArrayList<Segment> segmentList = new ArrayList<Segment>();
      
      //copy the segment list from the given path (the vectors in the segments get duplicated) 
      ArrayList<Segment> copySegmentList = copySortedSegmentList.get(f);
      for(Segment s : copySegmentList){ segmentList.add(new Segment(s)); }
      
      //add the new segment list
      sortedSegmentList.put(f, segmentList);
      
      //report, since path did not intersect this face before
      reportFaceChange(f, true);
    }
  }
  
  //this is overridden in VisiblePath so that it can notify ManifoldObjectHandler
  //-----------------------------------------
  protected void reportFaceChange(Face f, boolean newIntersection){}
  
  //adding and removing path segments
  //-----------------------------------------
  public void clear(){
    
    //report removal from all faces
    Set<Face> faceList = sortedSegmentList.keySet();
    for(Face f : faceList){ reportFaceChange(f, false); }
      
    //set up new empty sorted segment list
    sortedSegmentList = new HashMap<Face,ArrayList<Segment>>();
  }
  
  public void addSegment(Segment pathSegment){
    Face f = pathSegment.face;
    ArrayList<Segment> segments = sortedSegmentList.get(f);
    
    if(segments == null){
      segments = new ArrayList<Segment>();
      sortedSegmentList.put(f,segments);
      //report, since path did not intersect this face before
      reportFaceChange(f,true);
    }
    
    segments.add(pathSegment);
  }
  
  //getting path segments
  //-----------------------------------------
  protected HashMap<Face,ArrayList<Segment>> getSortedSegmentList(){
    return sortedSegmentList;
  }
  
  public ArrayList<Segment> getPathSegmentsInFace(Face f){
    return sortedSegmentList.get(f);
  }
  
  public boolean intersectsFace(Face f){
    ArrayList<Segment> segments = sortedSegmentList.get(f);
    if(segments == null){ return false; }
    if(segments.size() == 0){ return false; }
    return true;
  }
  
}
