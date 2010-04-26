#include "volume_second_partial.h"
#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, VolumeSecondPartial*, TriPositionCompare> VolumeSecondPartialIndex;
static VolumeSecondPartialIndex* Index = NULL;

VolumeSecondPartial::VolumeSecondPartial( Vertex& v, Vertex& w, Tetra& t ){
  wrt = 0;
  StdTetra st;

  if( v.getIndex() == w.getIndex() ){
    st = labelTetra( t, v );
    volume_partial = VolumePartial::At(v, t);
    sameVertices = true;
  } else {
    sameVertices = false;
    int vI = v.getIndex();
    int wI = w.getIndex();

    vector<int>* localVertices;
    vector<int>* localEdges = t.getLocalEdges();
    for(int ii = 0; ii < localEdges->size(); ii++){
      Edge& e = Triangulation::edgeTable[ localEdges->at(ii) ];
      
      localVertices = e.getLocalVertices();
      int v0 = localVertices->at(0);
      int v1 = localVertices->at(1);

      if( (v0 == vI && v1 == wI) || (v0 == wI && v1 == vI) ){
	st = labelTetra( t, e );
	break;
      }
    }
  }

  rad[0] = Radius::At( Triangulation::vertexTable[ st.v1 ] );
  rad[1] = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  rad[2] = Radius::At( Triangulation::vertexTable[ st.v3 ] );
  rad[3] = Radius::At( Triangulation::vertexTable[ st.v4 ] );

  alpha[0] = Alpha::At( Triangulation::vertexTable[ st.v1 ] );
  alpha[1] = Alpha::At( Triangulation::vertexTable[ st.v2 ] );
  alpha[2] = Alpha::At( Triangulation::vertexTable[ st.v3 ] );
  alpha[3] = Alpha::At( Triangulation::vertexTable[ st.v4 ] );

  for(int ii = 0; ii < 4; ii++) {
    rad[ii]->addDependent( this );
    alpha[ii]->addDependent( this );
  }
  
  eta[0] = Eta::At( Triangulation::edgeTable[ st.e12 ] );
  eta[1] = Eta::At( Triangulation::edgeTable[ st.e13 ] );
  eta[2] = Eta::At( Triangulation::edgeTable[ st.e14 ] );
  eta[3] = Eta::At( Triangulation::edgeTable[ st.e23 ] );
  eta[4] = Eta::At( Triangulation::edgeTable[ st.e24 ] );
  eta[5] = Eta::At( Triangulation::edgeTable[ st.e34 ] );

  for(int ii = 0; ii < 6; ii++) eta[ii]->addDependent( this );
}

VolumeSecondPartial::VolumeSecondPartial( Vertex& v, Edge& e, Tetra& t ){
  wrt = 1;
  sameVertices = false;
  if( !e.isAdjTetra(t.getIndex()) || !v.isAdjTetra(t.getIndex()) ) {
    // ERROR
    return;
  }
  StdTetra st;

  st = labelTetra( t, v, e );
  
  if( e.isAdjVertex(v.getIndex()) ){
    locality = 0;
  } else{
    locality = 1;
  }

  rad[0] = Radius::At( Triangulation::vertexTable[ st.v1 ] );
  rad[1] = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  rad[2] = Radius::At( Triangulation::vertexTable[ st.v3 ] );
  rad[3] = Radius::At( Triangulation::vertexTable[ st.v4 ] );

  alpha[0] = Alpha::At( Triangulation::vertexTable[ st.v1 ] );
  alpha[1] = Alpha::At( Triangulation::vertexTable[ st.v2 ] );
  alpha[2] = Alpha::At( Triangulation::vertexTable[ st.v3 ] );
  alpha[3] = Alpha::At( Triangulation::vertexTable[ st.v4 ] );

  for(int ii = 0; ii < 4; ii++) {
    rad[ii]->addDependent( this );
    alpha[ii]->addDependent( this );
  }
  
  eta[0] = Eta::At( Triangulation::edgeTable[ st.e12 ] );
  eta[1] = Eta::At( Triangulation::edgeTable[ st.e13 ] );
  eta[2] = Eta::At( Triangulation::edgeTable[ st.e14 ] );
  eta[3] = Eta::At( Triangulation::edgeTable[ st.e23 ] );
  eta[4] = Eta::At( Triangulation::edgeTable[ st.e24 ] );
  eta[5] = Eta::At( Triangulation::edgeTable[ st.e34 ] );

  for(int ii = 0; ii < 6; ii++) eta[ii]->addDependent( this );
}

VolumeSecondPartial::VolumeSecondPartial( Edge& e, Edge& f, Tetra& t ){
  wrt = 2;
  sameVertices = false;
  if( !e.isAdjTetra(t.getIndex()) || !f.isAdjTetra(t.getIndex()) ) {
    // ERROR
    return;
  }
  StdTetra st;

  st = labelTetra( t, e, f );
  
  if( e.getIndex() == f.getIndex() ){
    locality = 0;
  } else if(e.isAdjEdge(f.getIndex())){
    locality = 1;
  } else {
    locality = 2;
  }


  rad[0] = Radius::At( Triangulation::vertexTable[ st.v1 ] );
  rad[1] = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  rad[2] = Radius::At( Triangulation::vertexTable[ st.v3 ] );
  rad[3] = Radius::At( Triangulation::vertexTable[ st.v4 ] );

  alpha[0] = Alpha::At( Triangulation::vertexTable[ st.v1 ] );
  alpha[1] = Alpha::At( Triangulation::vertexTable[ st.v2 ] );
  alpha[2] = Alpha::At( Triangulation::vertexTable[ st.v3 ] );
  alpha[3] = Alpha::At( Triangulation::vertexTable[ st.v4 ] );

  for(int ii = 0; ii < 4; ii++) {
    rad[ii]->addDependent( this );
    alpha[ii]->addDependent( this );
  }
  
  eta[0] = Eta::At( Triangulation::edgeTable[ st.e12 ] );
  eta[1] = Eta::At( Triangulation::edgeTable[ st.e13 ] );
  eta[2] = Eta::At( Triangulation::edgeTable[ st.e14 ] );
  eta[3] = Eta::At( Triangulation::edgeTable[ st.e23 ] );
  eta[4] = Eta::At( Triangulation::edgeTable[ st.e24 ] );
  eta[5] = Eta::At( Triangulation::edgeTable[ st.e34 ] );

  for(int ii = 0; ii < 6; ii++) eta[ii]->addDependent( this );
}

/* WRT f_i */
double VolumeSecondPartial::calculateRadRadCase() {
  double r1 = rad[0]->getValue();
  double r2 = rad[1]->getValue();
  double r3 = rad[2]->getValue();
  double r4 = rad[3]->getValue();
  
  double alpha1 = alpha[0]->getValue();
  double alpha2 = alpha[1]->getValue();
  double alpha3 = alpha[2]->getValue();
  double alpha4 = alpha[3]->getValue();
  
  double Eta12 = eta[0]->getValue();  
  double Eta13 = eta[1]->getValue();
  double Eta14 = eta[2]->getValue(); 
  double Eta23 = eta[3]->getValue();
  double Eta24 = eta[4]->getValue();  
  double Eta34 = eta[5]->getValue();

  double result = 0;
  if( sameVertices ){
    result = -((alpha2*alpha4*pow(Eta13,2) + 2*alpha4*Eta12*Eta13*Eta23 -
        pow(Eta14,2)*pow(Eta23,2) + 2*Eta13*Eta14*Eta23*Eta24 -
        pow(Eta13,2)*pow(Eta24,2) +
        alpha3*(alpha4*pow(Eta12,2) + alpha2*pow(Eta14,2) +
        2*Eta12*Eta14*Eta24) + 2*alpha2*Eta13*Eta14*Eta34 +
        2*Eta12*Eta14*Eta23*Eta34 + 2*Eta12*Eta13*Eta24*Eta34 -
        pow(Eta12,2)*pow(Eta34,2) +
        alpha1*(alpha4*pow(Eta23,2) + alpha3*pow(Eta24,2) +
        2*Eta23*Eta24*Eta34 + alpha2*(-(alpha3*alpha4) +
        pow(Eta34,2))))*pow(r1,2)*pow(r2,2)*pow(r3,2)*pow(r4,2)*
        (-(pow(Eta23,2)*pow(r2,2)*pow(r3,2)) +
        2*Eta23*r2*r3*r4*(Eta24*r2 + Eta34*r3 + alpha4*r4) +
        r4*(-(pow(Eta24*r2 - Eta34*r3,2)*r4) +
        alpha3*pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) +
        alpha2*pow(r2,2)*(alpha3*pow(r3,2) + r4*(2*Eta34*r3 +
        alpha4*r4))))/
        (6.*pow(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
        + alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*pow(r1,2)*pow(r2,2)*
        pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
        alpha1*pow(Eta24,2))*pow(r1,2)*pow(r2,2)) -
        2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34
        + alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
        pow(r1,2) - 2*(alpha4*Eta13*Eta23 +
        alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
        (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
        alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*pow(r2,2))*
        pow(r3,2))*pow(r4,2),1.5));
        
        result += volume_partial->getValue(); /* Extra term resulting from f_i's*/
  } else {
   result = r1*r2*(-((-2*(alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*r1*pow(r2,2)*
        pow(r3,2) + 2*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3
        + Eta13*Eta34*r3))*r4 +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - pow(Eta12,2)*Eta34*r2 +
        alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 + Eta13*Eta14*Eta23*r3
        + alpha1*alpha3*Eta24*r3 - pow(Eta13,2)*Eta24*r3 +
        alpha1*Eta23*Eta34*r3 +
        Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-2*(alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
        alpha1*pow(Eta24,2))*r1*pow(r2,2) -
        2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
        2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-2*alpha1*(alpha3*alpha4 - pow(Eta34,2))*r1 +
        2*(alpha4*pow(Eta13,2) +
        Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24)
        + Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
        pow(r3,2))*pow(r4,2))*
        (-2*(alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*pow(r1,2)*r2*
        pow(r3,2) + 2*r1*r3*(alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3
        + Eta13*Eta34*r3))*r4 + 2*r1*r2*r3*(-(pow(Eta12,2)*Eta34*r1) +
        Eta23*(alpha1*Eta24*r1 - Eta14*Eta23*r3 + Eta13*Eta24*r3) +
        alpha2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3) +
        Eta12*(Eta14*Eta23*r1 + Eta13*Eta24*r1 + alpha3*Eta24*r3 +
        Eta23*Eta34*r3))*r4 -
        (-2*(alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
        alpha1*pow(Eta24,2))*pow(r1,2)*r2 -
        2*(alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r1*r2*r3 -
        2*r1*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24)
        + Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1 +
        2*(alpha4*pow(Eta23,2) +
        Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4)
        + pow(Eta34,2)))*r2)*pow(r3,2))*pow(r4,2))) +
        4*(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*pow(r1,2)*
        pow(r2,2)*pow(r3,2)) +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
        alpha1*pow(Eta24,2))*pow(r1,2)*pow(r2,2)) -
        2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 +
        2*Eta13*Eta34))*pow(r1,2) - 2*
        (alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
        (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34)
        + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*pow(r2,2))*
        pow(r3,2))*pow(r4,2))*
        (-2*alpha2*pow(Eta13,2)*r1*r2*pow(r3,2) -
        4*Eta12*Eta13*Eta23*r1*r2*pow(r3,2) +
        4*alpha2*Eta13*Eta14*r1*r2*r3*r4 + 4*Eta12*Eta14*Eta23*r1*r2*r3*r4
        + 4*Eta12*Eta13*Eta24*r1*r2*r3*r4 -
        4*pow(Eta12,2)*Eta34*r1*r2*r3*r4 +
        2*Eta13*Eta14*Eta23*r1*pow(r3,2)*r4 -
        2*pow(Eta13,2)*Eta24*r1*pow(r3,2)*r4 +
        2*Eta12*Eta13*Eta34*r1*pow(r3,2)*r4 -
        2*Eta14*pow(Eta23,2)*r2*pow(r3,2)*r4 +
        2*Eta13*Eta23*Eta24*r2*pow(r3,2)*r4 +
        2*alpha2*Eta13*Eta34*r2*pow(r3,2)*r4 +
        2*Eta12*Eta23*Eta34*r2*pow(r3,2)*r4 -
        2*alpha4*pow(Eta12,2)*r1*r2*pow(r4,2) -
        2*alpha2*pow(Eta14,2)*r1*r2*pow(r4,2) -
        4*Eta12*Eta14*Eta24*r1*r2*pow(r4,2) +
        2*alpha4*Eta12*Eta13*r1*r3*pow(r4,2) -
        2*pow(Eta14,2)*Eta23*r1*r3*pow(r4,2) +
        2*Eta13*Eta14*Eta24*r1*r3*pow(r4,2) +
        2*Eta12*Eta14*Eta34*r1*r3*pow(r4,2) +
        2*alpha2*alpha4*Eta13*r2*r3*pow(r4,2) +
        2*alpha4*Eta12*Eta23*r2*r3*pow(r4,2) +
        2*Eta14*Eta23*Eta24*r2*r3*pow(r4,2) -
        2*Eta13*pow(Eta24,2)*r2*r3*pow(r4,2) +
        2*alpha2*Eta14*Eta34*r2*r3*pow(r4,2) +
        2*Eta12*Eta24*Eta34*r2*r3*pow(r4,2) +
        alpha4*Eta13*Eta23*pow(r3,2)*pow(r4,2) +
        Eta14*Eta23*Eta34*pow(r3,2)*pow(r4,2) +
        Eta13*Eta24*Eta34*pow(r3,2)*pow(r4,2) -
        Eta12*pow(Eta34,2)*pow(r3,2)*pow(r4,2) +
        alpha3*pow(r3,2)*(-2*pow(Eta12,2)*r1*r2 +
        Eta12*r4*(2*Eta14*r1 + 2*Eta24*r2 + alpha4*r4) +
        Eta14*r4*(2*alpha2*r2 + Eta24*r4)) +
        2*alpha1*r1*(-(pow(Eta23,2)*r2*pow(r3,2)) +
        Eta23*r3*r4*(2*Eta24*r2 + Eta34*r3 + alpha4*r4) +
        Eta24*r4*(alpha3*pow(r3,2) - Eta24*r2*r4 + Eta34*r3*r4) +
        alpha2*r2*(alpha3*pow(r3,2) + r4*(2*Eta34*r3 +
        alpha4*r4)))))/
        (24.*pow(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
        + alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*pow(r1,2)*pow(r2,2)*
        pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
        alpha1*pow(Eta24,2))*pow(r1,2)*pow(r2,2)) -
        2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34
        + alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
        pow(r1,2) - 2*(alpha4*Eta13*Eta23 +
        alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
        (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
        alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*pow(r2,2))*
        pow(r3,2))*pow(r4,2),1.5));
    }
    return result;   
}

double VolumeSecondPartial::calculateRadEtaCase() {
  double r1 = rad[0]->getValue();
  double r2 = rad[1]->getValue();
  double r3 = rad[2]->getValue();
  double r4 = rad[3]->getValue();
  
  double alpha1 = alpha[0]->getValue();
  double alpha2 = alpha[1]->getValue();
  double alpha3 = alpha[2]->getValue();
  double alpha4 = alpha[3]->getValue();
  
  double Eta12 = eta[0]->getValue();  
  double Eta13 = eta[1]->getValue();
  double Eta14 = eta[2]->getValue(); 
  double Eta23 = eta[3]->getValue();
  double Eta24 = eta[4]->getValue();  
  double Eta34 = eta[5]->getValue();

  double result = 0;
  
  if(locality == 0) {
    result = (-2*r1*r2*(-2*(alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*r1*
        pow(r2,2)*pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - pow(Eta12,2)*Eta34*r2 +
        alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 +
        Eta13*Eta14*Eta23*r3 + alpha1*alpha3*Eta24*r3 -
        pow(Eta13,2)*Eta24*r3 + alpha1*Eta23*Eta34*r3 +
        Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-2*(alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        r1*pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
        2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-2*alpha1*(alpha3*alpha4 - pow(Eta34,2))*r1 +
        2*(alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
        pow(r3,2))*pow(r4,2))*(-((alpha3*Eta12 + Eta13*Eta23)*r1*r2*pow(r3,2)) +
        r3*((Eta14*Eta23 + Eta13*Eta24)*r1*r2 - 2*Eta12*Eta34*r1*r2 +
        (alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3)*r4 -
        ((Eta14*r1 - Eta34*r3)*(Eta24*r2 - Eta34*r3) + alpha4*(Eta12*r1*r2 -
        r3*(Eta13*r1 + Eta23*r2 + alpha3*r3)))*pow(r4,2)) -
        4*r2*(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
        pow(r1,2)*pow(r2,2)*pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2
        - pow(Eta12,2)*Eta34*r1*r2 +
        Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
        pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
        Eta14*pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        pow(r1,2)*pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
        alpha1*alpha4*Eta23 - pow(Eta14,2)*Eta23 +
        Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
        Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3
        + (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2))*
        (alpha3*pow(r3,2)*(2*Eta12*r1*r2 - r4*(2*Eta14*r1 + Eta24*r2 +
        alpha4*r4)) +
        2*Eta13*r1*r3*(Eta23*r2*r3 - r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) +
        r4*(2*Eta12*r1*r2*(2*Eta34*r3 + alpha4*r4) - 2*Eta14*r1*(Eta23*r2*r3
        - Eta24*r2*r4 + Eta34*r3*r4) -
        r3*(Eta34*(Eta24*r2 - Eta34*r3)*r4 + Eta23*r2*(Eta34*r3 +
        alpha4*r4)))))/
        (24.*pow(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
        + alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
        pow(r1,2)*pow(r2,2)*pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 +
        Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
        pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
        Eta14*pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        pow(r1,2)*pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
        alpha1*alpha4*Eta23 - pow(Eta14,2)*Eta23 +
        Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
        Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2),1.5));
  } else {
    result = (-2*r2*r3*(-2*(alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*r1*
        pow(r2,2)*pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - pow(Eta12,2)*Eta34*r2 +
        alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 +
        Eta13*Eta14*Eta23*r3 + alpha1*alpha3*Eta24*r3 -
        pow(Eta13,2)*Eta24*r3 + alpha1*Eta23*Eta34*r3 +
        Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 - (-2*(alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        r1*pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
        Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
        2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-2*alpha1*(alpha3*alpha4 - pow(Eta34,2))*r1 +
        2*(alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
        pow(r3,2))*pow(r4,2))*(Eta12*r1*r2*(-(Eta13*r1*r3) +
        r4*(Eta14*r1 + Eta34*r3 + alpha4*r4)) +
        alpha1*pow(r1,2)*(-(Eta23*r2*r3) + r4*(Eta24*r2 + Eta34*r3 +
        alpha4*r4)) + r4*(-(pow(Eta14,2)*pow(r1,2)*r4) - (alpha4*Eta23 +
        Eta24*Eta34)*r2*r3*r4 + Eta13*r1*r3*(Eta14*r1 + Eta24*r2 + alpha4*r4) +
        Eta14*r1*(-2*Eta23*r2*r3 + Eta24*r2*r4 + Eta34*r3*r4))) -
        4*r2*r3*(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
        + alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
        pow(r1,2)*pow(r2,2)*pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2
        - pow(Eta12,2)*Eta34*r1*r2 +
        Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
        pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
        Eta14*pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        pow(r1,2)*pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
        alpha1*alpha4*Eta23 - pow(Eta14,2)*Eta23 +
        Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
        Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3
        + (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2))*
        (Eta12*r2*(2*Eta13*r1*r3 - r4*(2*Eta14*r1 + Eta34*r3 + alpha4*r4)) +
        2*alpha1*r1*(Eta23*r2*r3 - r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) -
        r4*(Eta13*r3*(2*Eta14*r1 + Eta24*r2 + alpha4*r4) +
        Eta14*(-2*Eta23*r2*r3 - 2*Eta14*r1*r4 + Eta24*r2*r4 + Eta34*r3*r4))))/
        (24.*pow(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
        + alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
        pow(r1,2)*pow(r2,2)*pow(r3,2)) + 2*r1*r2*r3*
        (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 +
        Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
        pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
        Eta14*pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 - (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*
        pow(r1,2)*pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
        alpha1*alpha4*Eta23 - pow(Eta14,2)*Eta23 +
        Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 +
        (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
        Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
        (-(alpha1*(alpha3*alpha4 - pow(Eta34,2))*pow(r1,2)) +
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2),1.5));
  }
  
  return result * r1;
}

double VolumeSecondPartial::calculateEtaEtaCase() {
  double r1 = rad[0]->getValue();
  double r2 = rad[1]->getValue();
  double r3 = rad[2]->getValue();
  double r4 = rad[3]->getValue();

  double alpha1 = alpha[0]->getValue();
  double alpha2 = alpha[1]->getValue();
  double alpha3 = alpha[2]->getValue();
  double alpha4 = alpha[3]->getValue();

  double Eta12 = eta[0]->getValue();
  double Eta13 = eta[1]->getValue();
  double Eta14 = eta[2]->getValue();
  double Eta23 = eta[3]->getValue();
  double Eta24 = eta[4]->getValue();
  double Eta34 = eta[5]->getValue();

  double result = 0;
  if(locality == 0) {
    result = -(pow(r1,2)*pow(r2,2)* (-(pow(Eta13,2)*pow(r1,2)*pow(r3,2)) +
        2*Eta13*r1*r3*r4*(Eta14*r1 + Eta34*r3 + alpha4*r4) +
        r4*(-(pow(Eta14*r1 - Eta34*r3,2)*r4) +
        alpha3*pow(r3,2)*(2*Eta14*r1 + alpha4*r4)) + alpha1*pow(r1,2)*
        (alpha3*pow(r3,2) + r4*(2*Eta34*r3 + alpha4*r4)))*
        (-(pow(Eta23,2)*pow(r2,2)*pow(r3,2)) + 2*Eta23*r2*r3*r4*
        (Eta24*r2 + Eta34*r3 + alpha4*r4) +
        r4*(-(pow(Eta24*r2 - Eta34*r3,2)*r4) +
        alpha3*pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) + alpha2*pow(r2,2)*
        (alpha3*pow(r3,2) + r4*(2*Eta34*r3 + alpha4*r4))))/
        (6.*pow(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
        pow(r1,2)*pow(r2,2)*pow(r3,2)) +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
        Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
        pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
        alpha1*alpha3*Eta24*r1*r3 - pow(Eta13,2)*Eta24*r1*r3 +
        alpha1*Eta23*Eta34*r1*r3 - Eta14*pow(Eta23,2)*r2*r3 +
        Eta13*Eta23*Eta24*r2*r3 + Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
        alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
        alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
        (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) - alpha2*pow(Eta14,2) -
        2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*pow(r1,2)*pow(r2,2)) -
        2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
        pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
        alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
        Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
        alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 + (-(alpha1*
        (alpha3*alpha4 - pow(Eta34,2))* pow(r1,2)) + (alpha4*pow(Eta13,2) +
        Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
        (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
        alpha2*(-(alpha3*alpha4) + pow(Eta34,2))
        )*pow(r2,2))*pow(r3,2))*pow(r4,2),1.5));
  } else if(locality == 1) {
    result = -(pow(r1,2)*r2*r3*(-(pow(Eta23,2)*pow(r2,2)*
           pow(r3,2)) +
        2*Eta23*r2*r3*r4*
         (Eta24*r2 + Eta34*r3 + alpha4*r4) +
        r4*(-(pow(Eta24*r2 - Eta34*r3,2)*r4) +
           alpha3*pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) +
        alpha2*pow(r2,2)*
         (alpha3*pow(r3,2) +
           r4*(2*Eta34*r3 + alpha4*r4)))*
      (Eta12*r1*r2*(Eta13*r1*r3 -
           r4*(Eta14*r1 + Eta34*r3 + alpha4*r4)) +
        alpha1*pow(r1,2)*
         (Eta23*r2*r3 -
           r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) -
        r4*(-(pow(Eta14,2)*pow(r1,2)*r4) -
           (alpha4*Eta23 + Eta24*Eta34)*r2*r3*r4 +
           Eta13*r1*r3*
            (Eta14*r1 + Eta24*r2 + alpha4*r4) +
           Eta14*r1*(-2*Eta23*r2*r3 + Eta24*r2*r4 +
              Eta34*r3*r4))))/
   (6.*pow(-((alpha3*pow(Eta12,2) +
            Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
          pow(r1,2)*pow(r2,2)*pow(r3,2)) +
       2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
          Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
          pow(Eta12,2)*Eta34*r1*r2 +
          Eta13*Eta14*Eta23*r1*r3 +
          alpha1*alpha3*Eta24*r1*r3 -
          pow(Eta13,2)*Eta24*r1*r3 +
          alpha1*Eta23*Eta34*r1*r3 -
          Eta14*pow(Eta23,2)*r2*r3 +
          Eta13*Eta23*Eta24*r2*r3 +
          Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
             alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
          alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
             alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
       (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
               alpha2*pow(Eta14,2) -
               2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2)
               )*pow(r1,2)*pow(r2,2)) -
          2*r1*r2*((alpha4*Eta12*Eta13 +
                alpha1*alpha4*Eta23 -
                pow(Eta14,2)*Eta23 +
                Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
                alpha1*Eta24*Eta34)*r1 +
             (alpha4*Eta12*Eta23 +
                Eta24*(Eta14*Eta23 - Eta13*Eta24 +
                   Eta12*Eta34) +
                alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*
           r3 + (-(alpha1*
                (alpha3*alpha4 - pow(Eta34,2))*
                pow(r1,2)) +
             (alpha4*pow(Eta13,2) +
                Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
              pow(r1,2) -
             2*(alpha4*Eta13*Eta23 +
                alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                Eta34*(Eta14*Eta23 + Eta13*Eta24 -
                   Eta12*Eta34))*r1*r2 +
             (alpha4*pow(Eta23,2) +
                Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                alpha2*(-(alpha3*alpha4) + pow(Eta34,2))
                )*pow(r2,2))*pow(r3,2))*pow(r4,2),
      1.5));
  } else {
    result = (r1*r2*r4*(-2*r3*(2*Eta12*r1*r2 - Eta13*r1*r3 -
          Eta23*r2*r3 - Eta14*r1*r4 - Eta24*r2*r4 +
          2*Eta34*r3*r4)*
        (-((alpha3*pow(Eta12,2) +
               Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
               alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))
              *pow(r1,2)*pow(r2,2)*pow(r3,2)) +
          2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
             Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
             pow(Eta12,2)*Eta34*r1*r2 +
             Eta13*Eta14*Eta23*r1*r3 +
             alpha1*alpha3*Eta24*r1*r3 -
             pow(Eta13,2)*Eta24*r1*r3 +
             alpha1*Eta23*Eta34*r1*r3 -
             Eta14*pow(Eta23,2)*r2*r3 +
             Eta13*Eta23*Eta24*r2*r3 +
             Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
             alpha2*r2*(Eta13*Eta14*r1 +
                alpha1*Eta34*r1 + alpha3*Eta14*r3 +
                Eta13*Eta34*r3))*r4 -
          (-((alpha1*alpha2*alpha4 -
                  alpha4*pow(Eta12,2) -
                  alpha2*pow(Eta14,2) -
                  2*Eta12*Eta14*Eta24 -
                  alpha1*pow(Eta24,2))*pow(r1,2)*
                pow(r2,2)) -
             2*r1*r2*((alpha4*Eta12*Eta13 +
                   alpha1*alpha4*Eta23 -
                   pow(Eta14,2)*Eta23 +
                   Eta13*Eta14*Eta24 +
                   Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)
                  *r1 +
                (alpha4*Eta12*Eta23 +
                   Eta24*
                    (Eta14*Eta23 - Eta13*Eta24 +
                     Eta12*Eta34) +
                   alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2
                )*r3 + (-(alpha1*
                   (alpha3*alpha4 - pow(Eta34,2))*
                   pow(r1,2)) +
                (alpha4*pow(Eta13,2) +
                   Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
                 pow(r1,2) -
                2*(alpha4*Eta13*Eta23 +
                   alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                   Eta34*
                    (Eta14*Eta23 + Eta13*Eta24 -
                     Eta12*Eta34))*r1*r2 +
                (alpha4*pow(Eta23,2) +
                   Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                   alpha2*
                    (-(alpha3*alpha4) + pow(Eta34,2)))*
                 pow(r2,2))*pow(r3,2))*pow(r4,2)) -
       (2*r1*r2*r3*(-(pow(Eta12,2)*r1*r2) +
             alpha2*Eta13*r2*r3 +
             Eta12*(Eta13*r1 + Eta23*r2)*r3 +
             alpha1*r1*(alpha2*r2 + Eta23*r3)) -
          r3*(-2*r1*r2*(Eta12*Eta14*r1 +
                alpha1*Eta24*r1 + alpha2*Eta14*r2 +
                Eta12*Eta24*r2) +
             2*(alpha1*Eta34*pow(r1,2) +
                Eta13*r1*(Eta14*r1 - Eta24*r2) +
                r2*(-(Eta14*Eta23*r1) +
                   2*Eta12*Eta34*r1 + Eta23*Eta24*r2 +
                   alpha2*Eta34*r2))*r3)*r4)*
        (-((alpha3*Eta12 + Eta13*Eta23)*r1*r2*
             pow(r3,2)) +
          r3*((Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
             2*Eta12*Eta34*r1*r2 +
             (alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3)*r4
           - ((Eta14*r1 - Eta34*r3)*
              (Eta24*r2 - Eta34*r3) +
             alpha4*(Eta12*r1*r2 -
                r3*(Eta13*r1 + Eta23*r2 + alpha3*r3)))*
           pow(r4,2))))/
   (12.*pow(-((alpha3*pow(Eta12,2) +
            Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
          pow(r1,2)*pow(r2,2)*pow(r3,2)) +
       2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
          Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
          pow(Eta12,2)*Eta34*r1*r2 +
          Eta13*Eta14*Eta23*r1*r3 +
          alpha1*alpha3*Eta24*r1*r3 -
          pow(Eta13,2)*Eta24*r1*r3 +
          alpha1*Eta23*Eta34*r1*r3 -
          Eta14*pow(Eta23,2)*r2*r3 +
          Eta13*Eta23*Eta24*r2*r3 +
          Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
             alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
          alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
             alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
       (-((alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
               alpha2*pow(Eta14,2) -
               2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2)
               )*pow(r1,2)*pow(r2,2)) -
          2*r1*r2*((alpha4*Eta12*Eta13 +
                alpha1*alpha4*Eta23 -
                pow(Eta14,2)*Eta23 +
                Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
                alpha1*Eta24*Eta34)*r1 +
             (alpha4*Eta12*Eta23 +
                Eta24*(Eta14*Eta23 - Eta13*Eta24 +
                   Eta12*Eta34) +
                alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*
           r3 + (-(alpha1*
                (alpha3*alpha4 - pow(Eta34,2))*
                pow(r1,2)) +
             (alpha4*pow(Eta13,2) +
                Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
              pow(r1,2) -
             2*(alpha4*Eta13*Eta23 +
                alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                Eta34*(Eta14*Eta23 + Eta13*Eta24 -
                   Eta12*Eta34))*r1*r2 +
             (alpha4*pow(Eta23,2) +
                Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                alpha2*(-(alpha3*alpha4) + pow(Eta34,2))
                )*pow(r2,2))*pow(r3,2))*pow(r4,2),
      1.5));
  }
  return result;
}

void VolumeSecondPartial::recalculate(){
  if(wrt == 0) {
    value = calculateRadRadCase();
  } else if(wrt == 1) {
    value = calculateRadEtaCase();
  } else {
    value = calculateEtaEtaCase();
  }
}

VolumeSecondPartial::~VolumeSecondPartial(){}

void VolumeSecondPartial::remove() {
  deleteDependents();
  rad[0]->removeDependent(this);
  rad[1]->removeDependent(this);
  rad[2]->removeDependent(this);
  rad[3]->removeDependent(this);
  
  alpha[0]->removeDependent(this);
  alpha[1]->removeDependent(this);
  alpha[2]->removeDependent(this);
  alpha[3]->removeDependent(this);
  
  eta[0]->removeDependent(this);
  eta[1]->removeDependent(this);
  eta[2]->removeDependent(this);
  eta[3]->removeDependent(this);
  eta[4]->removeDependent(this);
  eta[5]->removeDependent(this);
  if(sameVertices) {
    volume_partial->removeDependent(this);
  }
  Index->erase(pos);
  delete this;
}

VolumeSecondPartial* VolumeSecondPartial::At( Vertex& v, Vertex& w, Tetra& t ){
  TriPosition T( 3, v.getSerialNumber(), w.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeSecondPartialIndex();
  VolumeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumeSecondPartial* val = new VolumeSecondPartial( v, w, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

VolumeSecondPartial* VolumeSecondPartial::At( Vertex& v, Edge& e, Tetra& t ){
  TriPosition T( 3, v.getSerialNumber(), e.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeSecondPartialIndex();
  VolumeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumeSecondPartial* val = new VolumeSecondPartial( v, e, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

VolumeSecondPartial* VolumeSecondPartial::At( Edge& e, Edge& f, Tetra& t ){
  TriPosition T( 3, e.getSerialNumber(), f.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeSecondPartialIndex();
  VolumeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumeSecondPartial* val = new VolumeSecondPartial( e, f, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumeSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  VolumeSecondPartialIndex::iterator iter;
  VolumeSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void VolumeSecondPartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  VolumeSecondPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}


