#include "volume_partial.h"

#include <stdio.h>

typedef map<TriPosition, VolumePartial*, TriPositionCompare> VolumePartialIndex;
static VolumePartialIndex* Index = NULL;

VolumePartial::VolumePartial( Vertex& v, Tetra& t ){
  wrtRadius = true;
  isIncident = t.isAdjVertex( v.getIndex() );
  if( ! isIncident ) return;

  StdTetra st = labelTetra( t, v );

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

VolumePartial::VolumePartial( Edge& e, Tetra& t) {
  wrtRadius = false;
  isIncident = t.isAdjEdge( e.getIndex() );
  if( ! isIncident ) return;
   
  StdTetra st = labelTetra( t, e );
   
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

double VolumePartial::calculateRadiusCase() {

  double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
         Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
  r1 = rad[0]->getValue();
  r2 = rad[1]->getValue();
  r3 = rad[2]->getValue();
  r4 = rad[3]->getValue();
  
  alpha1 = alpha[0]->getValue();
  alpha2 = alpha[1]->getValue();
  alpha3 = alpha[2]->getValue();
  alpha4 = alpha[3]->getValue();
  
  Eta12 = eta[0]->getValue();  
  Eta13 = eta[1]->getValue();
  Eta14 = eta[2]->getValue(); 
  Eta23 = eta[3]->getValue();
  Eta24 = eta[4]->getValue();  
  Eta34 = eta[5]->getValue();
               
  double result = (-2*(alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*r1*pow(r2,2)*
        pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 +
        Eta13*Eta24)*r1*r2 - pow(Eta12,2)*Eta34*r1*r2 +
        Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
        pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
        Eta14*pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
        Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
        Eta23*Eta34*r2)*r3 +
        alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 +
        2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - pow(Eta12,2)*Eta34*r2 +
        alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 + Eta13*Eta14*Eta23*r3 +
        alpha1*alpha3*Eta24*r3 - pow(Eta13,2)*Eta24*r3 +
        alpha1*Eta23*Eta34*r3 +
        Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
        Eta13*Eta34*r3))*r4 -
        (-2*(alpha1*alpha2*alpha4 - alpha4*pow(Eta12,2) -
        alpha2*pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*pow(Eta24,2))*r1*
        pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
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
        pow(r3,2))*pow(r4,2))/
        (12.*sqrt(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
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
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 +
        2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2)));  

    return result * r1;

}

double VolumePartial::calculateEtaCase() {

  double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
         Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
       
  r1 = rad[0]->getValue();
  r2 = rad[1]->getValue();
  r3 = rad[2]->getValue();
  r4 = rad[3]->getValue();
  
  alpha1 = alpha[0]->getValue();
  alpha2 = alpha[1]->getValue();
  alpha3 = alpha[2]->getValue();
  alpha4 = alpha[3]->getValue();
  
  Eta12 = eta[0]->getValue();  
  Eta13 = eta[1]->getValue();
  Eta14 = eta[2]->getValue(); 
  Eta23 = eta[3]->getValue();
  Eta24 = eta[4]->getValue();  
  Eta34 = eta[5]->getValue();
  
  double result = (r1*r2*(alpha3*pow(r3,2)*(-(Eta12*r1*r2) + r4*(Eta14*r1 + Eta24*r2 +
        alpha4*r4)) +
        Eta13*r1*r3*(-(Eta23*r2*r3) + r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) +
        r4*(-(Eta12*r1*r2*(2*Eta34*r3 + alpha4*r4)) + Eta14*r1*(Eta23*r2*r3 -
        Eta24*r2*r4 + Eta34*r3*r4) +
        r3*(Eta34*(Eta24*r2 - Eta34*r3)*r4 + Eta23*r2*(Eta34*r3 +
        alpha4*r4)))))/
        (6.*sqrt(-((alpha3*pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
        alpha1*(-(alpha2*alpha3) + pow(Eta23,2)))*
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
        (alpha4*pow(Eta13,2) + Eta14*(alpha3*Eta14 +
        2*Eta13*Eta34))*pow(r1,2) -
        2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
        Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
        r2 + (alpha4*pow(Eta23,2) + Eta24*(alpha3*Eta24 +
        2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + pow(Eta34,2)))*
        pow(r2,2))*pow(r3,2))*pow(r4,2)));
        
  return result;
}

void VolumePartial::recalculate(){
  if( ! isIncident ){
    value = 0.0;
    return;
  } else if(wrtRadius) {
    value = calculateRadiusCase();
  } else {
    value = calculateEtaCase();
  } 
}

void VolumePartial::remove() {
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
  Index->erase(pos);
  delete this;
}

VolumePartial::~VolumePartial(){}

VolumePartial* VolumePartial::At( Vertex& v, Tetra& t ){
  TriPosition T( 2, v.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumePartialIndex();
  VolumePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumePartial* val = new VolumePartial( v, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

VolumePartial* VolumePartial::At( Edge& e, Tetra& t ){
  TriPosition T( 2, e.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumePartialIndex();
  VolumePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumePartial* val = new VolumePartial( e, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumePartial::CleanUp(){
  if( Index == NULL ) return;
  VolumePartialIndex::iterator iter;
  VolumePartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void VolumePartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  VolumePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
