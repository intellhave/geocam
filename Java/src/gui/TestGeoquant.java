package gui;
import static org.junit.Assert.*;
import inputOutput.TriangulationIO;
import org.junit.Test;
import triangulation.*;
import geoquant.*;

/***************************************************************************
 * 
 * Class:	TestGeoquant
 * Purpose:	Checks the validity of the geoqaunt values
 * 
 * @author Joesph Thomas
 * @author Jeremy Mirchandani
 * 
 ***************************************************************************/
public class TestGeoquant {

	private static final double EPSILON = 0.0001; // range for assertions involving floats
	private static final String TETRAPATH = "Data/Triangulations/2DManifolds/tetrahedron.xml"; // File path for a tetraheddron
	private static final String TRIPRISMPATH = "Data/Triangulations/2DManifolds/triangularPrism.xml"; // File path for a triangular prism
	
	/*
	 * Feature Tested: The 2D topology of a tetrahedron is correctly loaded from an xml file.
	 * Parameters: A standard tetrahedron from our library of 2D manifolds.
	 */
	@Test
	public void testTetrahedronTopology(){
		TriangulationIO.readTriangulation(TETRAPATH);
		assertEquals(4, Triangulation.vertexTable.size());
		assertEquals(6, Triangulation.edgeTable.size());
		assertEquals(4, Triangulation.faceTable.size());
	}
	
	/*
	 * Feature Tested: The geoquants correctly compute lengths from radii and inversive distances.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex.
	 *  - Etas: 1.0 at every edge.
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronLengths00(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			assertEquals( 2.0, Length.at(ee).getValue(), EPSILON );
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly re-computes lengths from radii and inversive distances when the initial data is changed.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Initial/Final Radii: 1.0 at every vertex except for one vertex vv, which has radius 2.0.
	 *  - Initial Etas: 1.0 at every edge.
	 *  - Final Etas: 0.0 on edges that contain vv, 1.0 otherwise.
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronLengths01(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		Vertex vv = Triangulation.vertexTable.get(1);
		Radius.at(vv).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			if( ee.isAdjVertex(vv) ){
				assertEquals( 3.0, Length.at(ee).getValue(), EPSILON );
			} else {
				assertEquals( 2.0, Length.at(ee).getValue(), EPSILON );
			}
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			if(ee.isAdjVertex(vv))
				Eta.at(ee).setValue(0.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			if( ee.isAdjVertex(vv) ){
				assertEquals( Math.sqrt(5.0), Length.at(ee).getValue(), EPSILON );
			} else {
				assertEquals( 2, Length.at(ee).getValue(), EPSILON );
			}
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly computes lengths from radii and inversive distances when all radii and inverse distances are not the same.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 
	 *  		Vertex1 = 1.0 
	 *  		Vertex2 = 7.0
	 *  		Vertex3 = 2.0
	 *  		Vertex4 = 6.0
	 *  - Etas: 
	 *  		Edge12 = 1.0
	 *  		Edge13 = 5.0/6.0
	 *  		Edge14 = 3.0
	 *  		Edge23 = 0.5
	 *  		Edge24 = 2.0
	 *  		Edge34 = 0.25
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronLengths02(){
		Vertex [] vertices = new Vertex[5];
		int i = 1;
		for(Vertex vv : Triangulation.vertexTable.values()){
			vertices[i] = vv;
			i++;
		}
		
		Radius.at(vertices[1]).setValue(1.0);
		Radius.at(vertices[2]).setValue(7.0);
		Radius.at(vertices[3]).setValue(2.0);
		Radius.at(vertices[4]).setValue(6.0);
		
		Edge edge12 = vertices[1].getEdge(vertices[2]);
		Edge edge13 = vertices[1].getEdge(vertices[3]);
		Edge edge14 = vertices[1].getEdge(vertices[4]);
		Edge edge23 = vertices[2].getEdge(vertices[3]);
		Edge edge24 = vertices[2].getEdge(vertices[4]);
		Edge edge34 = vertices[3].getEdge(vertices[4]);
		
		Eta.at(edge12).setValue(1.0);
		Eta.at(edge13).setValue(5.0/6.0);
		Eta.at(edge14).setValue(3.0);
		Eta.at(edge23).setValue(0.5);
		Eta.at(edge24).setValue(2.0);
		Eta.at(edge34).setValue(0.25);
		
		
		assertEquals(8.0, Length.at(edge12).getValue(), EPSILON);
		assertEquals(2.8868, Length.at(edge13).getValue(), EPSILON);
		assertEquals(8.5440, Length.at(edge14).getValue(), EPSILON);
		assertEquals(8.1854, Length.at(edge23).getValue(), EPSILON);
		assertEquals(15.9060, Length.at(edge24).getValue(), EPSILON);
		assertEquals(6.7823, Length.at(edge34).getValue(), EPSILON);

	}

	/*
	 * Feature Tested: The geoquants correctly compute areas of the faces.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex.
	 *  - Etas: 1.0 at every edge.
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronArea00(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Face f : Triangulation.faceTable.values()){
			assertEquals( Math.sqrt(3.0), Area.at(f).getValue(), EPSILON );
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly compute areas of the faces.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex except for one vertex vv, which has radius 2.0.
	 *  - Etas: 1.0 for all edges
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronArea01(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		
		Vertex vv = Triangulation.vertexTable.get(1);
		Radius.at(vv).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Face f : Triangulation.faceTable.values()){
			if(f.isAdjVertex(vv))
				assertEquals( Math.sqrt(8.0), Area.at(f).getValue(), EPSILON );
			else
				assertEquals( Math.sqrt(3.0), Area.at(f).getValue(), EPSILON );
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly compute anlges of the faces.
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every 
	 *  - Etas: 1.0 for all edges
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronAngle00(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Face f : Triangulation.faceTable.values()){
			for(Vertex vv : Triangulation.vertexTable.values()){
				if(f.isAdjVertex(vv))
					assertEquals( Math.PI/3.0 , Angle.at(vv, f).getValue(), EPSILON );
			}
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly compute angles
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex except for one vertex vv, which has radius 2.0.
	 *  - Etas: 1.0 for all edges
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronAngle01(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		Vertex vv = Triangulation.vertexTable.get(1);
		Radius.at(vv).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Face f : Triangulation.faceTable.values()){
			for(Vertex v : Triangulation.vertexTable.values()){
				if(f.isAdjVertex(v)){
					if(v.equals(vv) )
						assertEquals( 0.67967 , Angle.at(v, f).getValue(), EPSILON );
					else if(f.isAdjVertex(vv))
						assertEquals( 1.230959 , Angle.at(v, f).getValue(), EPSILON );
					else	
						assertEquals( Math.PI/3.0 , Angle.at(v, f).getValue(), EPSILON );
					
				}
			}
		}
	}
	
	
	/*
	 * Feature Tested: The geoquants correctly compute partialEdges
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex except for one vertex vv, which has radius 2.0.
	 *  - Etas: 1.0 for all edges
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronPartialEdges00(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		Vertex vv = Triangulation.vertexTable.get(1);
		Radius.at(vv).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			Eta.at(ee).setValue(1.0);
		}
		
		for(Vertex v : Triangulation.vertexTable.values()){
			for(Edge ee : Triangulation.edgeTable.values()){
				if(v.isAdjEdge(ee))
					assertEquals(Radius.at(v).getValue(), PartialEdge.at(v, ee).getValue(), EPSILON);
			}
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly compute partialEdges
	 * Parameters: 
	 * 	- Manifold: Tetrahedron.
	 *  - Radii: 1.0 at every vertex except for one vertex vv, which has radius 2.0.
	 *  - Etas: 1.0 for all edges adjacent to vv, 1.0/3.0 for all others
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronPartialEdges01(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		Vertex vv = Triangulation.vertexTable.get(1);
		Radius.at(vv).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			if(vv.isAdjEdge(ee))
				Eta.at(ee).setValue(1.0);
			else
				Eta.at(ee).setValue(1.0/3.0);
		}
		
		for(Vertex v : Triangulation.vertexTable.values()){
			for(Edge ee : Triangulation.edgeTable.values()){
				if(v.isAdjEdge(ee))
					if(ee.isAdjVertex(vv))
						assertEquals(Radius.at(v).getValue(), PartialEdge.at(v, ee).getValue(), EPSILON);
					else
						assertEquals((4.0/3.0)/(Math.sqrt(8.0/3.0)), PartialEdge.at(v, ee).getValue(), EPSILON);
			}
		}
	}
	
	/*
	 * Feature Tested: The 2D topology of a triangular prism is correctly loaded from an xml file.
	 * 	- Manifold: Triangular Prism.
	 */
	@Test
	public void testTriangularPrismTopology00(){
		TriangulationIO.readTriangulation(TRIPRISMPATH);
		
		assertEquals(5, Triangulation.vertexTable.size());
		assertEquals(9, Triangulation.edgeTable.size());
		assertEquals(6, Triangulation.faceTable.size());
		
		
		// Tests that each edge has two adjacent faces
		// Tests that if a face is adjacent to an edge the edge is adjacent to the face
		for(Edge ee : Triangulation.edgeTable.values()){
			int i = 0;
			for(Face f : Triangulation.faceTable.values()){
				if(ee.isAdjFace(f))
					i++;
				
				assertEquals(f.isAdjEdge(ee), ee.isAdjFace(f));
			}
			assertEquals(2, i);
		}
	}
	
	/*
	 * Feature Tested: The geoquants correctly computes lengths from radii and inversive distances when all radii are not the same.
	 * Parameters: 
	 * 	- Manifold: Triangular Prism
	 *  - Radii: 
	 *  		Vertex1 = 1.0 
	 *  		Vertex2 = 5.0
	 *  		Vertex3 = 102.0
	 *  		Vertex4 = 3.0
	 *  		Vertex5 = 4.0
	 *  - Etas: 1.0 for all edges
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTriangularPrismLengths00(){
		TriangulationIO.readTriangulation(TRIPRISMPATH);

		Vertex [] vertices = new Vertex[6];
				
		vertices[1] = Triangulation.vertexTable.get(1);
		vertices[2] = Triangulation.vertexTable.get(2);
		vertices[3] = Triangulation.vertexTable.get(3);
		vertices[4] = Triangulation.vertexTable.get(4);
		vertices[5] = Triangulation.vertexTable.get(5);
		
		Edge edge12 = vertices[1].getEdge(vertices[2]);
		Edge edge14 = vertices[1].getEdge(vertices[4]);
		Edge edge15 = vertices[1].getEdge(vertices[5]);
		Edge edge24 = vertices[2].getEdge(vertices[4]);
		Edge edge25 = vertices[2].getEdge(vertices[5]);
		Edge edge23 = vertices[2].getEdge(vertices[3]);
		Edge edge34 = vertices[3].getEdge(vertices[4]);
		Edge edge35 = vertices[3].getEdge(vertices[5]);
		Edge edge45 = vertices[4].getEdge(vertices[5]);
		
		Radius.at(vertices[1]).setValue(1.0);
		Radius.at(vertices[2]).setValue(5.0);
		Radius.at(vertices[3]).setValue(102.0);
		Radius.at(vertices[4]).setValue(3.0);
		Radius.at(vertices[5]).setValue(4.0);
		
		for(Edge ee : Triangulation.edgeTable.values())
				Eta.at(ee).setValue(1.0);
			
		assertEquals(6.0, Length.at(edge12).getValue(), EPSILON);
		assertEquals(4.0, Length.at(edge14).getValue(), EPSILON);
		assertEquals(5.0, Length.at(edge15).getValue(), EPSILON);
		assertEquals(8.0, Length.at(edge24).getValue(), EPSILON);
		assertEquals(9.0, Length.at(edge25).getValue(), EPSILON);
		assertEquals(107.0, Length.at(edge23).getValue(), EPSILON);
		assertEquals(105.0, Length.at(edge34).getValue(), EPSILON);
		assertEquals(106.0, Length.at(edge35).getValue(), EPSILON);
		assertEquals(7.0, Length.at(edge45).getValue(), EPSILON);

	}
	
	/*
	 * Feature Tested: The geoquants correctly compute Edge Heights
	 * Parameters: 
	 * 	- Manifold: Tetrahedron
	 *  - Radii: 1.0 at every vertex except for one vertex v1, which has radius 2.0.
	 *  - Etas: 1.0 for all edges except for edges adjacent to v1, which has a eta of 2.0
	 *  Formulas: Glickenstein 2009 Page 27
	 */
	@Test
	public void testTetrahedronEdgeHeights00(){
		TriangulationIO.readTriangulation(TETRAPATH);
		
		for(Vertex vv : Triangulation.vertexTable.values()){
			Radius.at(vv).setValue(1.0);
		}
		
		Vertex v1 = Triangulation.vertexTable.get(1);
		
		double base_EH = 1.0 / Math.sqrt(3);
		double bottom_EH = 4.0 / Math.sqrt(12);
		double top_EH = 4.0 / Math.sqrt(39);
		
		Radius.at(v1).setValue(2.0);
		
		for(Edge ee : Triangulation.edgeTable.values()){
			if( ee.isAdjVertex(v1))
				Eta.at(ee).setValue(2.0);
			else
				Eta.at(ee).setValue(1.0);
		}
		
		for(Face ff : Triangulation.faceTable.values()){
			for(Edge ee : ff.getLocalEdges()){
				if(ff.isAdjVertex(v1)){
					if(ee.isAdjVertex(v1))
						assertEquals(EdgeHeight.at(ee, ff).getValue(), top_EH, EPSILON);
					else
						assertEquals(EdgeHeight.at(ee, ff).getValue(), bottom_EH, EPSILON);
				}
				else
					assertEquals(EdgeHeight.at(ee, ff).getValue(), base_EH, EPSILON);
			}
		}
	}
}
