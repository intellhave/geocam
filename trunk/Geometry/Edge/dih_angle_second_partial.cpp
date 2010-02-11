#include "dih_angle_second_partial.h"
#include "miscmath.h"

#include <stdio.h>
#include <cmath>

typedef map<TriPosition, DihedralAngleSecondPartial*, TriPositionCompare> DihedralAngleSecondPartialIndex;
static DihedralAngleSecondPartialIndex* Index = NULL;

DihedralAngleSecondPartial::DihedralAngleSecondPartial( Edge& e1, Edge& e2, Edge& e3, Tetra& t ){
  int nm = e1.getIndex();
  int op = e2.getIndex();
  int ij = e3.getIndex();

  StdTetra st;
  // t = ij,kl
  if(!t.isAdjEdge(nm) || !t.isAdjEdge(op) ) {
    locality = 0;
    st = labelTetra( t, e3, e1 );
  } else if(ij == nm) {
    st = labelTetra(t, e1, e2);
    if(nm == op) {
      locality = 1; // A (nm = ij, op = ij)
    } else if(e2.isAdjEdge(nm)) {
      locality = 6; // G , I (nm = ij, op = ik)
    } else {
      locality = 4; // D, E (nm = ij, op = kl)
    }
  } else if(e3.isAdjEdge(nm)) {
    st = labelTetra(t, e3, e1);
    if(nm == op) {
      locality = 3; // C (nm = ik, op = ik)
    } else if(ij == op) {
      locality = 6; // G, I (nm = ik, op = ij)
    } else if(e2.isAdjEdge(ij) && e2.isAdjEdge(nm)) {
      if(e2.isAdjVertex(st.v1)) {
        locality = 10; // ? (nm = ik, op = il)
      } else {
        locality = 10; // ? (nm = ik, op = jk)
      }
    } else if(e2.isAdjEdge(ij)) {
      locality = 5; // F (nm = ik, op = jl)
    } else {
      locality = 7; // H, J (nm = ik, op = kl)
    }
  } else {
    st = labelTetra(t, e3, e2);
    if(nm == op) {
      locality = 2; // B (nm = kl, op = kl)
    } else if(ij == op) {
      locality = 4; // D, E (nm = kl, op = ij)
    } else if(e2.isAdjEdge(ij)) {
      locality = 7; // H, J (nm = kl, op = ik)
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

void DihedralAngleSecondPartial::recalculate(){
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
    value = 0;
    
  } else if(locality == 1) {
     value = -((-((-(r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)* (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/ (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)* sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))* sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))))/ (sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))* (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))))* sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))* (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/ ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))) +
        (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)* (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/ (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))* sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        (2.*sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),1.5)) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2)))) +
        (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)* (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/ (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))* sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        (2.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))))*
        ((-2*(-(r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/(2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))) +
        (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))*
        pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2))/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2)))) +
        (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2))/
        (pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),2)*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))))/
        (2.*pow(1 - pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))),2)/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))),1.5)) +
        (-(((-2*pow(r1,2)*pow(r2,2))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*pow(r1,2)*pow(r2,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3))/(pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*pow(r1,2)*pow(r2,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4))/(pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (2*pow(r1,2)*pow(r2,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/(pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),3)*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))/
        (sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))*
        (-(r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/(2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),1.5)) +
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2)))) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (-(r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) -
        (3*pow(-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))
        ) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (4.*sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2.5)) +
        (((-2*pow(r1,2)*pow(r2,2))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))) + (4*pow(r1,2)*pow(r2,2)*(2*alpha1*pow(r1,2) +
        2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (2*pow(r1,2)*pow(r2,2)*pow(2*alpha1*pow(r1,2) +
        2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),3)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (2.*sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),1.5)) -
        ((-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2)))
        ) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))
        ) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/(2.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),1.5)) -
        (3*pow(-((r1*r2*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2)))
        ) + (r1*r2*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),2)*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (4.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),2.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) +
        (((-2*pow(r1,2)*pow(r2,2))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2)))
        + (4*pow(r1,2)*pow(r2,2)*(2*alpha1*pow(r1,2) +
        2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
        (pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2),2)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))) -
        (2*pow(r1,2)*pow(r2,2)*pow(2*alpha1*pow(r1,2) +
        2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (pow(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2),3)*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/(2.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))))/
        sqrt(1 - pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))));
              
  } else if(locality == 2) {
     value = -((pow(r3,2)*pow(r4,2)*(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        ((alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))),
        1.5)*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))*
        pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))),
        1.5)*pow(1 - pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))))),1.5)));
        
  } else if(locality == 3){
     value = -((-(((r1*r3)/(sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2),1.5)*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4))/(2.*pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2),1.5)*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))))/(sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))))*sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))) +
        (((r1*r3*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2)) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))/
        (2.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))))*
        ((-2*((r1*r3)/(sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))) +
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/(4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),1.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4))/(2.*pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2),1.5)*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) +
        (((r1*r3*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2)) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2))),2))/(pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))),2)*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))))/
        (2.*pow(1 - pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))),1.5)) +
        (-(((-2*pow(r1,2)*pow(r3,2))/(pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2),1.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (3*pow(r1,2)*pow(r3,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (pow(r1,2)*pow(r3,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),1.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (3*pow(r1,2)*pow(r3,2)*(2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        2*Eta14*r1*r4 - 2*Eta34*r3*r4))/
        (2.*pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +alpha3*pow(r3,2),2.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))/
        (sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2)))))) +
        (((r1*r3*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2)) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))))*((r1*r3)/(sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),1.5)*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4))/ (2.*pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2),1.5)* sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        (pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
        - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) -
        (3*pow((r1*r3*pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        (2.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2)) -
        (r1*r3*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3))/ ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))),
        2)*(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        (4.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),2.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))) +
        (((-2*pow(r1,2)*pow(r3,2)*pow(2*alpha1*pow(r1,2) +
        2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),3)) +
        (4*pow(r1,2)*pow(r3,2)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
        ((alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        pow(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2),2)) -
        (2*pow(r1,2)*pow(r3,2))/((alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        alpha2*pow(r2,2))*(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))))*
        (-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
        2*Eta34*r3*r4)/(2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 +
        alpha3*pow(r3,2))*sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 +
        alpha4*pow(r4,2)))))/
        (2.*pow(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 +
        2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))),1.5)*
        sqrt(1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
        - 2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))))/
        sqrt(1 - pow(-((2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3)*
        (2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))) +
        (2*alpha1*pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
        (2.*sqrt(alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))*
        sqrt(alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))),2)/
        ((1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
        2*Eta23*r2*r3,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta13*r1*r3 + alpha3*pow(r3,2))))*
        (1 - pow(2*alpha1*pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
        2*Eta24*r2*r4,2)/
        (4.*(alpha1*pow(r1,2) + 2*Eta12*r1*r2 + alpha2*pow(r2,2))*
        (alpha1*pow(r1,2) + 2*Eta14*r1*r4 + alpha4*pow(r4,2))))));
  }  
}

void DihedralAngleSecondPartial::remove() {
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

DihedralAngleSecondPartial::~DihedralAngleSecondPartial(){}

DihedralAngleSecondPartial* DihedralAngleSecondPartial::At( Edge& e1, Edge& e2, Edge& e3, Tetra& t ){
  TriPosition T( 4, e1.getSerialNumber(), e2.getSerialNumber(), e3.getSerialNumber(), t.getSerialNumber());
  if( Index == NULL ) Index = new DihedralAngleSecondPartialIndex();
  DihedralAngleSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    DihedralAngleSecondPartial* val = new DihedralAngleSecondPartial( e1, e2, e3, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void DihedralAngleSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  DihedralAngleSecondPartialIndex::iterator iter;
  DihedralAngleSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void DihedralAngleSecondPartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  DihedralAngleSecondPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}
