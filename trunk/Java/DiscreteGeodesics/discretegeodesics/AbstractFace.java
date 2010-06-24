package discretegeodesics;

import java.lang.Math;
import java.lang.Exception;
//import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import de.jreality.math.Rn;

public class AbstractFace {
	
	//some explanation of the 'local' vs 'global' indices referred to throughout: 
	//local indices are how the object (eg vertex) is stored in the AbstractFace, and global indices are unique identifiers
	//the need for both is to check if objects occurring in different faces are actually the same. 
	//for example, to see if vertex i in one face and vertex j in another face are actually the same vertex in the manifold,
	//we look up the 'global' indices (stored in vert_global_indices_) using the 'local' indices (i and j).  same idea for edges.
	
	//for approximation on intersections, for numerical robustness
	private static final double eps_ = 0.01; 

	//vertex/coordinate data
	private int vert_count_ = 0;
	private int[] vert_global_indices_; //global vertex indices (a vertex shared between faces should have the same global index despite differing local indices)
	private TreeMap<Integer, Integer> vert_local_indices_ = new TreeMap<Integer, Integer>(); //quick lookup of local index from global index
	private double[][] vert_coords_; //2d coordinates for each vertex
	private double[] vert_angles_; //interior angles at each vertex
	private double[] vert_straightangles_; //half the sum of all interior angles sharing this vertex, used to compute 'straightest geodesics'
	
	//data to go from local coords to world coords, for display purposes:  see getWorldCoordsFromLocalCoords
	private double[] origin_,tangent_x_,tangent_y_; 
	
	//edge transition data (NOTE: edge k goes from vertex k to (k+1)%n where n = nverts/edges)
	private int edge_count_ = 0; //= vert_count_, but less confusing when looping through edges
	private int[] edge_global_indices_; //global edge indices (an edge linking two faces should have the same global index despite differing local indices)
	private TreeMap<Integer, Integer> edge_local_indices_ = new TreeMap<Integer, Integer>(); //quick lookup of local index from global index
	private AbstractFace[] incident_face_; //references to incident faces, indexed by edge
	private double[][][] trans_matrix_; //affine 2d transformations stored as 3x3 matrices 
	
	public AbstractFace(){ }
	
	//methods to set data
	//===========================================================================================
	public void setCoords(double[] origin, double[] tangent_x, double[] tangent_y){
		origin_ = origin;
		tangent_x_ = tangent_x;
		tangent_y_ = tangent_y;
	}
	
	public void setVerts(int[] vert_global_indices, double[][] vert_coords){
		
		vert_count_ = vert_global_indices.length;
		
		//set verts
		vert_global_indices_ = vert_global_indices;
		vert_coords_ = vert_coords;
		vert_angles_ = new double[vert_count_];
		for(int i=0; i<vert_count_; i++){ 
			vert_local_indices_.put(vert_global_indices[i],i);
		}
		
		//prepare edge stuff
		edge_count_ = vert_count_;
		edge_global_indices_ = new int[edge_count_];
		incident_face_ = new AbstractFace[edge_count_];
		trans_matrix_ = new double[edge_count_][3][3];
		
		//compute interior angles
		for(int i=0; i<vert_count_; i++){
			int v0 = i-1; if(i==0){ v0 = vert_count_-1; }
			int v1 = i+1; if(i==vert_count_-1){ v1 = 0; }
			double[] tangent0 = Rn.normalize(null,Rn.subtract(null,vert_coords_[v0],vert_coords_[i]));
			double[] tangent1 = Rn.normalize(null,Rn.subtract(null,vert_coords_[v1],vert_coords_[i]));
			vert_angles_[i] = Math.acos(Rn.innerProduct(tangent0,tangent1));
		}
	}
	
	public void setVertStraightAngles(double[] vert_straightangles){
		vert_straightangles_ = vert_straightangles;
	}
	
	public void setEdge(int local_index, int global_index, double[][] trans_matrix, AbstractFace incident_face){
		//setverts should be called first
		edge_global_indices_[local_index] = global_index;
		edge_local_indices_.put(global_index,local_index);
		incident_face_[local_index] = incident_face;
		trans_matrix_[local_index] = trans_matrix;
	}
	
	//recursive method to compute curvature at a specified global vertex
	//===========================================================================================
	private double getAngleSum_Step(double current_sum, int global_vi, int source_edge, int first_edge){
		
		//global_vi is connected to two edges, source_edge and next_edge (the latter is to be found)
		//add interior angle at the vertex, then call this function for the face across next_edge
		//stop once next_edge = first_edge
		int local_vi;
		try{ local_vi = getLocalVertIndexFromGlobal(global_vi); }
		catch(Exception e){
			System.err.println("(getAngleSum_Step) " + e.getMessage());
			return 0; 
		}
		
		int[] incident_edges = new int[2];
		getEdgesIncidentToVertex(incident_edges,local_vi);
		
		int next_edge_local;
		if(edge_global_indices_[incident_edges[0]] == source_edge){ next_edge_local = incident_edges[1]; }
		else if(edge_global_indices_[incident_edges[1]] == source_edge){ next_edge_local = incident_edges[0]; }
		else{
			System.err.println("*(getAngleSum_Step) Error: source_edge not found in face*");
			return 0;
		}
		
		int next_edge = edge_global_indices_[next_edge_local];
		if(next_edge == first_edge){ 
			//completed walk around the flower
			return current_sum + vert_angles_[local_vi];
		}else{
			//continue the walk
			return incident_face_[next_edge_local].getAngleSum_Step(current_sum + vert_angles_[local_vi], global_vi, next_edge, first_edge);
		}
	}
	
	//this is called by computeAbstractFacesFromGeometry and the results are stored in vert_straightangles_
	public double getAngleSum(int global_vi){
		
		//recursively completes a walk around the flower at global_vi, adding up interior angles
		int local_vi;
		try{ local_vi = getLocalVertIndexFromGlobal(global_vi); }
		catch(Exception e){
			System.err.println("(getAngleSum) " + e.getMessage());
			return 0; 
		}
		
		int[] incident_edges = new int[2];
		getEdgesIncidentToVertex(incident_edges,local_vi);
		
		return incident_face_[incident_edges[1]].getAngleSum_Step(vert_angles_[local_vi],global_vi,edge_global_indices_[incident_edges[1]],edge_global_indices_[incident_edges[0]]);
	}
	
	//various lookups for vertices/edges specified by their global indices (see notes at the top)
	//===========================================================================================
	public double[] getVertCoords(int index){ 
		//returns local coords from a vertex
		return vert_coords_[index]; 
	}
	
	private int getLocalVertIndexFromGlobal(int global_vi) throws Exception{
		//throws exception if the global vertex does not occur in this face, otherwise returns local index
		Integer local_vi = vert_local_indices_.get(global_vi);
		if(local_vi == null){ throw new Exception("Error: global_vi not found in face"); }
		else{ return local_vi.intValue(); } 
	}

	private int getLocalEdgeIndexFromGlobal(int global_ei) throws Exception{
		//throws exception if the global edge does not occur in this face, otherwise returns local index
		Integer local_ei = edge_local_indices_.get(global_ei);
		if(local_ei == null){ throw new Exception("Error: global_ei not found in face"); }
		else{ return local_ei.intValue(); } 
	}
	
	private void getEdgesIncidentToVertex(int[] ret_ei, int local_vi){
		//given a local vertex index, returns local edge indices incident to the vertex
		ret_ei[0] = local_vi;
		if(local_vi == 0){ ret_ei[1] = edge_count_-1; }else{ ret_ei[1] = local_vi-1; }
	}
	
	//conversions between abstract 2d coords and 3d world coords relative to embedding
	//the 3d coords are only used for display purposes
	//===========================================================================================
	public double[] getLocalCoordsFromWorldCoords(double[] worldcoords){
		//dot (worldcoords - origin) with x and y vectors
		double[] localcoords = new double[2];
		localcoords[0] = Rn.innerProduct(Rn.subtract(null,worldcoords,origin_), tangent_x_);
		localcoords[1] = Rn.innerProduct(Rn.subtract(null,worldcoords,origin_), tangent_y_);
		return localcoords;
	}
	
	public double[] getWorldCoordsFromLocalCoords(double[] localcoords){
		//origin + x*tangent_x + y*tangent_y
		double[] worldcoords = new double[3];
		worldcoords = Rn.add(null,origin_,Rn.add(null, Rn.times(null,localcoords[0],tangent_x_), Rn.times(null,localcoords[1],tangent_y_)));
		return worldcoords;
	}
	
	//general routines
	//===========================================================================================
	public static double[][] computeMatrixProduct(double[][] A, double[][] B){
		
		double[][] result = new double[3][3];
		result[0][0] = A[0][0]*B[0][0] + A[0][1]*B[1][0] + A[0][2]*B[2][0];
		result[0][1] = A[0][0]*B[0][1] + A[0][1]*B[1][1] + A[0][2]*B[2][1];
		result[0][2] = A[0][0]*B[0][2] + A[0][1]*B[1][2] + A[0][2]*B[2][2];
		result[1][0] = A[1][0]*B[0][0] + A[1][1]*B[1][0] + A[1][2]*B[2][0];
		result[1][1] = A[1][0]*B[0][1] + A[1][1]*B[1][1] + A[1][2]*B[2][1];
		result[1][2] = A[1][0]*B[0][2] + A[1][1]*B[1][2] + A[1][2]*B[2][2];
		result[2][0] = A[2][0]*B[0][0] + A[2][1]*B[1][0] + A[2][2]*B[2][0];
		result[2][1] = A[2][0]*B[0][1] + A[2][1]*B[1][1] + A[2][2]*B[2][1];
		result[2][2] = A[2][0]*B[0][2] + A[2][1]*B[1][2] + A[2][2]*B[2][2];
		return result;
	}
	
	public static double[] computeAffineTransform(double[][] T, double[] v){
		
		//apply T to the point (v[0], v[1], 1)
		double[] result = new double[2];
		result[0] = T[0][0]*v[0] + T[0][1]*v[1] + T[0][2];
		result[1] = T[1][0]*v[0] + T[1][1]*v[1] + T[1][2];
		return result;
	}
	
	public static double[] computeAffineTransformVector(double[][] T, double[] v){
	
		//apply T to the vector (v[0], v[1], 0)
		double[] result = new double[2];
		result[0] = T[0][0]*v[0] + T[0][1]*v[1];
		result[1] = T[1][0]*v[0] + T[1][1]*v[1];
		return result;
	}
	
	public static boolean computePointOnRay(double[] point, double[] direction){
		//assumes ray is coming from origin; for general ray, use overloaded method below
		
		double[] unit_dir = Rn.normalize(null,direction); //actually DiscreteGeodesics will always pass unit vector, but might as well not assume it has to be
		
		//t value (on line t*unit_dir) of closest point is given by v.unit_dir
		double t = Rn.innerProduct(point,unit_dir); 
		if(t < 0){ return false; }
		
		//now check that perp dist < eps:  perp dist = dist between point and t*unit_dir
		double dsq = Rn.euclideanDistanceSquared(point,Rn.times(null,t,unit_dir));
		if(dsq < eps_*eps_){ return true; } return false;
	}
	
	public static boolean computePointOnRay(double[] point, double[] position, double[] direction){

		return computePointOnRay(Rn.subtract(null, point, position),direction);
	}

	//intersects the line segment a0 to a1 and the ray specified by position/direction
	public static boolean computeRayIntersection(double[] result, double[] a0, double[] a1, double[] position, double[] direction){
		
		//intersection of the lines a0+t(a1-a0) and p+s*d can be found by
		//[ t ]   [  |      |  ]-1 [  |  ]
		//[   ] = [a0-a1    d  ]   [a0-p ]
		//[ s ]   [  |      |  ]   [  |  ]
		//det of the matrix must be nonzero, t \in (0,1), and s > 0
		
		//create matrix
		double[][] mat = new double[2][2];
		mat[0] = new double[] {a0[0]-a1[0], direction[0]};
		mat[1] = new double[] {a0[1]-a1[1], direction[1]};
		
		//check determinant
		double det = mat[0][0]*mat[1][1] - mat[0][1]*mat[1][0];
		if(Math.abs(det) < eps_){ return false; }
		
		//create vector and find s and t values (i.e., compute [mat inverse]*[vect])
		double[] vect = new double[] {a0[0]-position[0], a0[1]-position[1]};
		double t = (mat[1][1]*vect[0] - mat[0][1]*vect[1]) / det;
		double s = (mat[0][0]*vect[1] - mat[1][0]*vect[0]) / det;
		if((t <= 0) || (t >= 1) || (s <= 0)){ return false; } //use eps_ here?
		//if((t <= eps_) || (t >= 1-eps_) || (s <= eps_)){ return false; } 
		
		//now compute actual intersection point
		result[0] = a0[0] + t*(a1[0] - a0[0]);
		result[1] = a0[1] + t*(a1[1] - a0[1]);
		return true;
	}
	
	//methods for computing the 'straightest geodesic'
	//===========================================================================================
	
	//for indicating different types of nodes in the exp path
	public static enum PathNodeType{ PATH_START, EDGE_INTERSECTION, VERTEX_INTERSECTION, PATH_END, PATH_DEBUG } 
	
	//a packaged 'struct' of data to pass and return for computeExpPath
	public static class ExpInitialConditions{
		
		public AbstractFace face = null;
		public double[] position = null;
		public double[] direction = null;
		
		//the 'exclude' parameters are important, to avoid detecting the wrong intersections:
		//	if the line crosses a vertex, then 'exclude' that vertex from being checked in the next face
		//		the next face must also not check the edges that are incident to that excluded vertex
		//		because it might be that the path actually traverses one of those edges
		//	if the line crosses an edge, then exclude that edge from being checked in the next face
		public Integer edge_exclude = null;
		public Integer vert_exclude = null;
	};
	
	public static class Position2d{
		
		public AbstractFace face = null;
		public double[] position = null;
	};
	
	//recursive function to find where the path should go on crossing a vertex
	private AbstractFace computeStraightestDirection(double[] new_position, double[] new_direction, double current_angle, int global_vi, int source_edge){
		
		//global_vi is connected to two edges, source_edge and another one (to be found), next_edge
		//see if straightangle[global_vi] occurs on this face--if not, call this function for next_edge
		//returns face index
		
		int local_vi;
		try{ local_vi = getLocalVertIndexFromGlobal(global_vi); }
		catch(Exception e){
			System.err.println("(computeStraightestDirection) " + e.getMessage());
			return null; 
		} 
		
		double remaining_angle = vert_straightangles_[local_vi] - current_angle;
		if(remaining_angle <= vert_angles_[local_vi]){
			//the straightest direction occurs on this face
			int other_vi; //either next or previous vertex, depending on source_edge
			int source_edge_local;
			try{ source_edge_local = getLocalEdgeIndexFromGlobal(source_edge); }
			catch(Exception e){ 
				System.err.println("(computeStraightestDirection) " + e.getMessage());
				return null; 
			}
			
			double cos = Math.cos(remaining_angle), sin = Math.sin(remaining_angle);
			if(source_edge_local == local_vi){  //in this case, rotate the edge [ local_vi, (local_vi+1)%nverts ] CCW by remaining_angle
				if(local_vi == vert_count_-1){ other_vi = 0; }else{ other_vi = local_vi+1; }
			}else{  //in this case, rotate the edge [ local_vi, (local_vi-1)%nverts ] CW by remaining_angle
				if(local_vi == 0){ other_vi = vert_count_-1; }else{ other_vi = local_vi-1; }
				sin = -sin; //to rotate CW
			}
			double[] edge = Rn.normalize(null, Rn.subtract(null, vert_coords_[other_vi], vert_coords_[local_vi]));
			new_position[0] = vert_coords_[local_vi][0]; new_position[1] = vert_coords_[local_vi][1];
			new_direction[0] = cos*edge[0]-sin*edge[1];
			new_direction[1] = sin*edge[0]+cos*edge[1];
			return this;
			
		}else{
			//move to the next face
			int[] incident_edges = new int[2];
			getEdgesIncidentToVertex(incident_edges,local_vi);
			
			int next_edge_local;
			if(edge_global_indices_[incident_edges[0]] == source_edge){ next_edge_local = incident_edges[1]; }
			else if(edge_global_indices_[incident_edges[1]] == source_edge){ next_edge_local = incident_edges[0]; }
			else{ 
				System.err.println("(computeStraightestDirection) Error: source_edge not found in face");
				return null;
			}
			
			return incident_face_[next_edge_local].computeStraightestDirection(new_position, new_direction, current_angle + vert_angles_[local_vi], global_vi, edge_global_indices_[next_edge_local]);
		}
	}
	
	private static void addNode(AbstractFace face, double[] position, PathNodeType nodetype, ArrayList<double[]> path3d, ArrayList<Position2d> path2d, ArrayList<PathNodeType> path_nodetypes){

		//given a point in the abstract manifold (face,position), and a node type, add the node to the arraylists
		if(path3d != null){ path3d.add(face.getWorldCoordsFromLocalCoords(position)); }
		if(path_nodetypes != null){ path_nodetypes.add(nodetype); }
		if(path2d != null){
			Position2d node2d = new Position2d();
			node2d.face = face;
			node2d.position = new double[]{ position[0], position[1] };
			path2d.add(node2d);
		}
	}

	public double computeExponentialMap(ArrayList<double[]> path3d, ArrayList<Position2d> path2d, ArrayList<PathNodeType> path_nodetypes, ExpInitialConditions initial_conditions, double max_length){
		
		//NOTE: assumes initial_conditions.position is inside the face, should check this before proceeding
		//NOTE: assumes that a geodesic through a vertex should go to a different face; if it should actually go through the same face, will have undesirable results
		//for the 'exclude' parameters, see comments in ExpInitialConditions 
		
		if(max_length <= 0){ return 0; }
		
		//references to initial condition data
		double[] position = initial_conditions.position;
		double[] direction = initial_conditions.direction;
		Integer edge_exclude = initial_conditions.edge_exclude; 
		Integer vert_exclude = initial_conditions.vert_exclude; 
		
		double segment_length = 0; //return value, returns length of current segment
		
		//first, see if the path intersects a vertex (don't check intersections with vert_exclude)
		int v_int = -1;
		for(int i=0; i<vert_count_; i++){
			if(vert_exclude != null){
				if(vert_global_indices_[i] == vert_exclude.intValue()){ continue; }
			}
			if(computePointOnRay(vert_coords_[i],position,direction)){ 
				v_int = i; 
				segment_length = Rn.euclideanDistance(vert_coords_[v_int],position);
				break; 
			}
		}
		if((v_int >= 0) && (segment_length <= max_length)){ 
			
			//add this intersection point to the path
			addNode(this,vert_coords_[v_int],PathNodeType.VERTEX_INTERSECTION, path3d,path2d,path_nodetypes);
			//find angle of path with one of the incident edges
			double[] path_vector = Rn.normalize(null, direction); //probably already normalized but no need to assume
			double[] edge_vector = Rn.normalize(null, Rn.subtract(null,vert_coords_[v_int],vert_coords_[(v_int+1)%vert_count_]));
			double current_angle = Math.acos(Rn.innerProduct(path_vector,edge_vector));
			//find straightest geodesic out of this vertex by walking the flower at v_int
			double[] new_position = new double[2];
			double[] new_direction = new double[2];
			AbstractFace next_face = incident_face_[v_int].computeStraightestDirection(new_position,new_direction,current_angle,vert_global_indices_[v_int],edge_global_indices_[v_int]);
			if(next_face == null){ 
				System.err.println("(computeExponentialMap) Error walking flower");
				return max_length; 
			}
			//set new initial conditions and return length
			initial_conditions.face = next_face;
			initial_conditions.position = new_position;
			initial_conditions.direction = new_direction;
			initial_conditions.edge_exclude = null;
			initial_conditions.vert_exclude = new Integer(vert_global_indices_[v_int]);
			return segment_length;
		}
		
		//next, see if the path intersects an edge (don't check for intersections with edge_exclude, or with edges incident to vert_exclude)
		int[] vert_exclude_edge = new int[2]; //note that these are local indices, whereas exclude_edge is global
		if(vert_exclude != null){ //add edges incident to vert_exclude
			try{ getEdgesIncidentToVertex(vert_exclude_edge,getLocalVertIndexFromGlobal(vert_exclude.intValue())); } 
			catch(Exception e){
				System.err.println("(computeExponentialMap) " + e.getMessage());
				return max_length;
			}
		}
		
		int e_int = -1;
		double[] x = new double[2]; //edge intersection point
		for(int i=0; i<edge_count_; i++){   //note: edge i goes from vert[i] to vert[(i+1)%nverts]
			
			if(edge_exclude != null){
				if(edge_global_indices_[i] == edge_exclude.intValue()){ continue; }
			}
			if(vert_exclude != null){
				if((i == vert_exclude_edge[0]) || (i == vert_exclude_edge[1])){ continue; } 
			}
			if(computeRayIntersection(x,vert_coords_[i],vert_coords_[(i+1)%vert_count_],position,direction)){ 
				e_int = i; 
				segment_length = Rn.euclideanDistance(x,position);
				break; 
			}
		}
		if((e_int >= 0) && (segment_length <= max_length)){ 
			
			//get new initial conditions
			double[] new_position = computeAffineTransform(trans_matrix_[e_int],x);
			double[] new_direction = computeAffineTransformVector(trans_matrix_[e_int],direction);
			//add path node
			addNode(this,x,PathNodeType.EDGE_INTERSECTION, path3d,path2d,path_nodetypes);
			//set new initial conditions and return length
			initial_conditions.face = incident_face_[e_int];
			initial_conditions.position = new_position;
			initial_conditions.direction = new_direction;
			initial_conditions.edge_exclude = new Integer(edge_global_indices_[e_int]);
			initial_conditions.vert_exclude = null;
			return segment_length;
		}
		
		//if the code reaches this point, then the segment did not intersect a vertex or edge, so add final path point and stop recursing
		
		double[] new_position = Rn.add(null,position,Rn.times(null,max_length,direction));
		//add final path node
		addNode(this,new_position,PathNodeType.PATH_END, path3d,path2d,path_nodetypes);
		//set new initial conditions [only change is position] and return length
		initial_conditions.position = new_position;
		return max_length;
	}

	//methods for computing the 'mall map'
	//===========================================================================================
	
	//class designed to make it easy to use an IndexedFaceSetFactory
	public static class MallMapGeometry{
		
		private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
		private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();
		
		public void addFace(double[][] faceverts){
			
			int nverts = faceverts.length;
			int vi = geometry_verts.size();
			
			int[] newface = new int[nverts];
			for(int k=0; k<nverts; k++){
				double[] newvert = new double[3];
				newvert[0] = faceverts[k][0];
				newvert[1] = faceverts[k][1];
				newvert[2] = 1.0;
				geometry_verts.add(newvert);
				newface[k] = vi++;
			}
			geometry_faces.add(newface);
		}
		
		public double[][] getVerts(){
			return (double[][])geometry_verts.toArray(new double[0][0]);
		}
		public int[][] getFaces(){
			return (int[][])geometry_faces.toArray(new int[0][0]);
		}
	};
	
	private static class Frustum2d{
		
		double[] source_;
		//rays (which may be null) are indicated by vectors based at the source; "positive" space
		//is that swept out by right_ray_ moving CCW to left_ray_; the rest is "negative" space
		double[] left_ray_ = null;
		double[] right_ray_ = null;
		//can also have 'full' (all positive) or 'empty' (all negative) frustum; in either case rays are null
		boolean isFull = true;
		boolean isEmpty = false;
		
		//set content of frustum
		//--------------------------------------
		public Frustum2d(double[] source){ 
			//make a full frustum
			source_ = source; 
			isFull = true; isEmpty = false;
		}
		
		public Frustum2d(double[] source, double[] left_ray, double[] right_ray){
			//make a frustum, if either ray is null, it will be made empty
			source_ = source; 
			isFull = false; 
			if((left_ray == null) || (right_ray == null)){ 
				isEmpty = true; 
				left_ray_ = null; right_ray_ = null;
			}else{
				isEmpty = false;
				left_ray_ = left_ray; right_ray_ = right_ray;
			}
		}
		
		public double[] getSource(){ return source_; }
		
		//computations with frustum
		//--------------------------------------
		public void computeAffineTransformFrustum(double[][] T){

			source_ = computeAffineTransform(T,source_);
			if(left_ray_ != null){ left_ray_ = computeAffineTransformVector(T,left_ray_); }
			if(right_ray_ != null){ right_ray_ = computeAffineTransformVector(T,right_ray_); }
		}

		private boolean containsVector(double[] v){
			
			if(isFull){ return true; }else if(isEmpty){ return false; }
			
			//first see if the point is ON a ray
			if(computePointOnRay(v,left_ray_)){ return true; }
			if(computePointOnRay(v,right_ray_)){ return true; }
			
			//if not, check for containment
			if((right_ray_[0]*v[1] - right_ray_[1]*v[0]) <= 0){ return false; } //z coord of RxV should be > 0
			if(( left_ray_[0]*v[1] -  left_ray_[1]*v[0]) >= 0){ return false; } //z coord of LxV should be < 0
			return true;
		}
		
		private boolean containsPoint(double[] point){
			
			return containsVector(Rn.subtract(null,point,source_));
		}
		
		public Frustum2d clipFrustum(Frustum2d that){
			
			//returns intersection of this frustum with another (NOTE: assumed to be from the same source point)
			if(this.isEmpty){ return this; }else if(this.isFull){ return that; }
			if(that.isEmpty){ return that; }else if(that.isFull){ return this; }
			
			//start from this.right, check if inside or outside that
			if(that.containsVector(this.right_ray_)){
				//the right edge of the intersection is this.right
				if(this.containsVector(that.left_ray_)){
					//the left edge of the intersection is that.left
					Frustum2d retfrustum = new Frustum2d(source_, that.left_ray_, this.right_ray_);
					return retfrustum;
				}
				else{ return this; } //this is completely contained in that
				
			}else{
				if(this.containsVector(that.right_ray_)){
					//the right edge of the intersection is that.right
					if(that.containsVector(this.left_ray_)){
						//the left edge of the intersection is this.left
						Frustum2d retfrustum = new Frustum2d(source_, this.left_ray_, that.right_ray_);
						return retfrustum;
					}
					else{ return that; } //that is completely contained in this	
				}
				else{
					//the intersection is empty
					Frustum2d retfrustum = new Frustum2d(source_, null, null);
					return retfrustum;
				}
			}
		}
		
		public double[][] clipFace(double[][] faceverts, boolean[] edgevisible){
			
			//given vertices in CCW order, clip this face and produce new list of vertices, or return false if face is obscured completely
			//many optimizations can be made to this, as it works as is for arbitrary (eg possibly concave) faces
			//edgevisible assumed to be already initialized
			
			int nverts = faceverts.length;
			
			//if empty, return nothing
			if(isEmpty){
				for(int i=0; i<nverts; i++){
					edgevisible[i] = false;
				}
				return null; 
			}

			//if full, return original face
			double[][] new_faceverts = new double[nverts][2];
			if(isFull){
				for(int i=0; i<nverts; i++){ 
					edgevisible[i] = true;
					new_faceverts[i][0] = faceverts[i][0]; new_faceverts[i][1] = faceverts[i][1];
				}
				return new_faceverts;
			}
			
			//see which vertices are inside frustum
			boolean[] keepverts = new boolean[nverts];
			boolean foundtrue = false;
			for(int i=0; i<nverts; i++){
				if(containsPoint(faceverts[i])){
					keepverts[i] = true;
					foundtrue = true;
				}
				else{ keepverts[i] = false; }
			}
			if(foundtrue == false){ return null; }
			
			//now walk around faceverts, adding vertices as necessary
			ArrayList<double[]> newverts = new ArrayList<double[]>();
			
			for(int i=0; i<nverts; i++){
				//first check vertex
				if(keepverts[i]){ newverts.add(faceverts[i]); }
				//now see if rays intersect edge v[i]-v[(i+1)%n]
				double[] left_intersection = new double[2];
				double[] right_intersection = new double[2];
				boolean hitleft = computeRayIntersection(left_intersection,faceverts[i],faceverts[(i+1)%nverts],source_,left_ray_);
				boolean hitright = computeRayIntersection(right_intersection,faceverts[i],faceverts[(i+1)%nverts],source_,right_ray_);
				//determine how edge is oriented relative to the source point
				double[] edge_vect_1 = Rn.subtract(null,faceverts[i],source_);
				double[] edge_vect_2 = Rn.subtract(null,faceverts[(i+1)%nverts],source_);
				double orient = edge_vect_1[0]*edge_vect_2[1] - edge_vect_1[1]*edge_vect_2[0];
				//add 'new' vertices
				if(hitleft && hitright){
					if(orient > 0){
						newverts.add(right_intersection);
						newverts.add(left_intersection);
					}else{
						newverts.add(left_intersection);
						newverts.add(right_intersection);
					}
				}else{
					if(hitleft){ newverts.add(left_intersection); }
					if(hitright){ newverts.add(right_intersection); }
				}
				//determine edge visibility (edge i is v[i] to v[(i+1)%n])
				//visible if either vertex is in frustum; if neither is in frustum, then if either ray intersects
				edgevisible[i] = false;
				if(orient > 0){
					if(keepverts[i] && keepverts[(i+1)%nverts]){
						//both verts in frustum, then visible
						edgevisible[i] = true;
					}else if(keepverts[i] || keepverts[(i+1)%nverts]){
						//just one vert in frustum, then must have an interior hit
						edgevisible[i] = hitleft || hitright;
					}
				}
			}
			
			return newverts.toArray(new double[0][0]);
		}
	};
	
	public void computeMallMap(int recursion_max_depth, double[] source_point, double[] source_direction, MallMapGeometry geometry){
		
		double[] dir = Rn.normalize(null,source_direction); //probably already normalized but just in case
		
		//initial transformation subtracts source_point, then rotates so that source_direction -> (1,0)
		double[][] initial_transformation = new double[3][3];
		//initial_transformation[0] = new double[] { 1.0, 0.0, -source_point[0] };
		//initial_transformation[1] = new double[] { 0.0, 1.0, -source_point[1] };
		initial_transformation[0] = new double[] {  dir[0], dir[1], -dir[0]*source_point[0] - dir[1]*source_point[1] };
		initial_transformation[1] = new double[] { -dir[1], dir[0],  dir[1]*source_point[0] - dir[0]*source_point[1] };
		initial_transformation[2] = new double[] { 0.0, 0.0, 1.0 };
		
		
		Frustum2d initial_frustum = new Frustum2d(source_point); //make a 'full' frustum
		computeMallMap_Step(0, recursion_max_depth, null, geometry, initial_frustum, initial_transformation);
	}
	
	private void computeMallMap_Step(int recursion_cur_depth, int recursion_max_depth, Integer referring_edge, MallMapGeometry geometry, Frustum2d frustum, double[][] transformation){
		
		//in 2d, clipping can be sufficiently described by two rays
		//in 3d, it can be sufficiently described by a spherical polygon, where each vertex is a 'ray'; i.e., a polygonal frustum; these can be intersected on each step
		
		if(recursion_cur_depth > recursion_max_depth){ return; }
		
		int referring_edge_local = -1; //local index of referring edge (if any)
		double[][] new_transformation = transformation; //transformation sticking this face onto referring_edge
		
		//get local_ei and new_transformation
		if(referring_edge != null){
			//get local_ei
			try{ referring_edge_local = getLocalEdgeIndexFromGlobal(referring_edge.intValue()); }
			catch(Exception e){
				System.err.println("(computeMallMap) " + e.getMessage());
				return; 
			} 
			//multiply current transformation on right by appropriate trans_matrix_
			new_transformation = computeMatrixProduct(transformation,trans_matrix_[referring_edge_local]);
		}
		
		//clip this face
		boolean[] edgevisible = new boolean[vert_count_];
		double[][] newface = frustum.clipFace(vert_coords_,edgevisible);
		if(newface == null){ 
			//should never happen, since 'hidden' edges are not used
			System.err.println("(computeMallMap_Step) Warning: processing obscured face");
			return; 
		} 
		for(int i=0; i<newface.length; i++){
			newface[i] = computeAffineTransform(new_transformation,newface[i]);
		}
		geometry.addFace(newface);
		
		//do recursion
		for(int i=0; i<edge_count_; i++){
			if(referring_edge != null){
				if(i == referring_edge_local){ continue; }
			}
			
			//for each edge which intersects the current frustum:
			//make a new frustum made by rays from source_point through its vertices
			//intersect this frustum with the current one, then transform it over the edge and pass it to the next face
			if(edgevisible[i]){
				
				//new frustum
				double[] source_point = frustum.getSource();
				double[] new_right_ray = Rn.subtract(null,vert_coords_[i],source_point);
				double[] new_left_ray = Rn.subtract(null,vert_coords_[(i+1)%vert_count_],source_point);
				Frustum2d new_frustum0 = new Frustum2d(source_point,new_left_ray,new_right_ray);
				Frustum2d new_frustum = frustum.clipFrustum(new_frustum0); //intersect with old
				new_frustum.computeAffineTransformFrustum(trans_matrix_[i]);
				
				incident_face_[i].computeMallMap_Step(
						recursion_cur_depth+1, recursion_max_depth,
						new Integer(edge_global_indices_[i]),
						geometry,
						new_frustum, new_transformation);
			}	
		}
	}
}
