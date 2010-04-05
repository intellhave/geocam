#include "dih_angle_partial.h"
#include "miscmath.h"

#include <stdio.h>
#include <cmath>

typedef map<TriPosition, DihedralAnglePartial*, TriPositionCompare> DihedralAnglePartialIndex;
static DihedralAnglePartialIndex* Index = NULL;

DihedralAnglePartial::DihedralAnglePartial( Edge& e1, Edge& e2, Tetra& t ){
  StdTetra st = labelTetra( t, e2 );  
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
  
  if(e1.getIndex() == e2.getIndex()) {
     locality = 0;                 
  } else if( t.isAdjEdge(e1.getIndex()) ) {
     if( e2.isAdjEdge(e1.getIndex()) ) {
         locality = 1;    
     } else {
         locality = 2;       
     }
  } else {
     locality = 3;
  }
}

void DihedralAnglePartial::recalculate(){
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

  
  if(locality == 0) {
    value = (-((-(r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
          (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) -
          (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (2.*pow(alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))/
          (sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))) +
          ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))) +
          (r1*r2*pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2))/
          (2.*pow(alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))*
          (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))/
          (2.*sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          pow(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))),1.5)) +
          ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
          ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2)))) +
          (r1*r2*pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2))/
          (2.*pow(alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))/
          (2.*pow(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))),1.5)*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))))/
          sqrt(1 - pow(-((2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))),2)/
          ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          (1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))));
  } else if(locality == 1) {
     value = (-(((r1*r3)/
          (sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2),1.5)*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) -
          (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) -
          (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4))/
          (2.*pow(alpha1*pow(r1,2) +
          2*Eta13*r1*r3 + alpha3*pow(r3,2),1.5)*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))/
          (sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))) +
          (((r1*r3*pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2))/
          (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2),2)) -
          (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
          ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))/
          (2.*pow(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))),1.5)*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))))/
          sqrt(1 - pow(-((2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))),2)/
          ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          (1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))));
  } else if(locality == 2) {
     value = (r3*r4)/(sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))*
          sqrt(1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))))*
          sqrt(1 - pow(-((2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))) +
          (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))*
          sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2))),2)/
          ((1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*pow(r3,2))))*
          (1 - pow(2*alpha1*pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*pow(r2,2))*
          (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*pow(r4,2)))))));      
  } else {
     value = 0;       
  }  
}

void DihedralAnglePartial::remove() {
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

DihedralAnglePartial::~DihedralAnglePartial(){}

DihedralAnglePartial* DihedralAnglePartial::At( Edge& e1, Edge& e2, Tetra& t ){
  // The additional e2 is to "set it apart" from e1. Otherwise, the computer 
  // would believe d(dih(e1,t))/d(e2) = d(dih(e2,t))/d(e1)
  TriPosition T( 4, e1.getSerialNumber(), e2.getSerialNumber(), t.getSerialNumber(), e2.getSerialNumber());
  if( Index == NULL ) Index = new DihedralAnglePartialIndex();
  DihedralAnglePartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DihedralAnglePartial* val = new DihedralAnglePartial( e1, e2, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DihedralAnglePartial::CleanUp(){
  if( Index == NULL ) return;
  DihedralAnglePartialIndex::iterator iter;
  DihedralAnglePartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void DihedralAnglePartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DihedralAnglePartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
