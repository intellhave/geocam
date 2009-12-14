#include "dih_angle_partial.h"
#include "miscmath.h"

#include <stdio.h>
#include <cmath>

typedef map<TriPosition, DihedralAnglePartial*, TriPositionCompare> DihedralAnglePartialIndex;
static DihedralAnglePartialIndex* Index = NULL;

DihedralAnglePartial::DihedralAnglePartial( Edge& e1, Edge& e2, Tetra& t ){
  StdTetra st = labelTetra( t, e2 );  
  ri = Radius::At( Triangulation::vertexTable[ st.v1 ] );
  rj = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  rk = Radius::At( Triangulation::vertexTable[ st.v3 ] );
  rl = Radius::At( Triangulation::vertexTable[ st.v4 ] );
  eij = Eta::At(Triangulation::edgeTable[ st.e12 ] );
  eik = Eta::At(Triangulation::edgeTable[ st.e13 ] );
  eil = Eta::At(Triangulation::edgeTable[ st.e14 ] );
  ejk = Eta::At(Triangulation::edgeTable[ st.e23 ] );
  ejl = Eta::At(Triangulation::edgeTable[ st.e24 ] );
  ekl = Eta::At(Triangulation::edgeTable[ st.e34 ] );

  ri->addDependent( this );
  rj->addDependent( this );
  rk->addDependent( this );
  rl->addDependent( this );
  eij->addDependent( this );
  eik->addDependent( this );
  eil->addDependent( this );
  ejk->addDependent( this );
  ejl->addDependent( this );
  ekl->addDependent( this );
  
  if(e1.getIndex() == e2.getIndex()) {
     locality = 0;                 
  } else if(e2.isAdjEdge(e1.getIndex())) {
     locality = 1;       
  } else if(t.isAdjEdge(e1.getIndex())) {
     locality = 2;
  } else {
     locality = 3;
  }
}

void DihedralAnglePartial::recalculate(){
  double r1 = ri->getValue();
  double r2 = rj->getValue();
  double r3 = rk->getValue();
  double r4 = rl->getValue();
  double Eta12 = eij->getValue();
  double Eta13 = eik->getValue();
  double Eta14 = eil->getValue();
  double Eta23 = ejk->getValue();
  double Eta24 = ejl->getValue();
  double Eta34 = ekl->getValue();

  if(locality == 0) {
    value = (-((-(r1*r2*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
           (2.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) - 
          (r1*r2*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (2.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (r1*r2*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
             (2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (2.*pow(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2),2)*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))))/
        (sqrt(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))
               ))*sqrt(1 - 
            pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
               )))) + ((-((r1*r2*
               (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
             ((pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
               (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
               )) + (r1*r2*
             pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
           (2.*pow(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2),2)*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))
         *(-((2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (2*pow(r1,2) + 2*Eta13*r1*r3 + 
             2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
           (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2)))))/
      (2.*sqrt(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))
         *pow(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))),
         1.5)) + ((-((r1*r2*
               (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                 2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
             ((pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
               (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))
               )) + (r1*r2*
             pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
           (2.*pow(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2),2)*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))
         *(-((2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (2*pow(r1,2) + 2*Eta13*r1*r3 + 
             2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
           (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2)))))/
      (2.*pow(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))),
         1.5)*sqrt(1 - pow(2*pow(r1,2) + 
             2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4
             ,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))
        ))/sqrt(1 - pow(-((2*pow(r1,2) + 
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 
              2*Eta23*r2*r3)*
            (2*pow(r1,2) + 2*Eta12*r1*r2 + 
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
         (4.*(pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
           sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
             pow(r3,2))*
           sqrt(pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
           ) + (2*pow(r1,2) + 2*Eta13*r1*r3 + 
           2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
         (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
             pow(r3,2))*
           sqrt(pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
           ),2)/
      ((1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))
         *(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))
        ));
  } else if(locality == 1) {
     value = (-(((r1*r3)/
           (sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (r1*r3*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
             (2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             pow(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2),1.5)*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) - 
          (r1*r3*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (2.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) - 
          (r1*r3*(2*pow(r1,2) + 2*Eta13*r1*r3 + 
               2*Eta14*r1*r4 - 2*Eta34*r3*r4))/
           (2.*pow(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2),1.5)*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))))/
        (sqrt(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))
               ))*sqrt(1 - 
            pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
               )))) + (((r1*r3*
             pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
           (2.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             pow(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2),2)) - 
          (r1*r3*(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
           ((pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))
         *(-((2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (2*pow(r1,2) + 2*Eta13*r1*r3 + 
             2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
           (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2)))))/
      (2.*pow(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))),
         1.5)*sqrt(1 - pow(2*pow(r1,2) + 
             2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4
             ,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))
        ))/sqrt(1 - pow(-((2*pow(r1,2) + 
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 
              2*Eta23*r2*r3)*
            (2*pow(r1,2) + 2*Eta12*r1*r2 + 
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
         (4.*(pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
           sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
             pow(r3,2))*
           sqrt(pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
           ) + (2*pow(r1,2) + 2*Eta13*r1*r3 + 
           2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
         (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
             pow(r3,2))*
           sqrt(pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
           ),2)/
      ((1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))
         *(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
             2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))
        ));
  } else if(locality == 2) {
     value = (r3*r4)/(sqrt(pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))*
     sqrt(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
          (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))))*
     sqrt(pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))*
     sqrt(1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
        (4.*(pow(r1,2) + 2*Eta12*r1*r2 + pow(r2,2))*
          (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))))*
     sqrt(1 - pow(-((2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*pow(r1,2) + 2*Eta12*r1*r2 + 
                2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
           (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
               pow(r2,2))*
             sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))) + 
          (2*pow(r1,2) + 2*Eta13*r1*r3 + 
             2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
           (2.*sqrt(pow(r1,2) + 2*Eta13*r1*r3 + 
               pow(r3,2))*
             sqrt(pow(r1,2) + 2*Eta14*r1*r4 + 
               pow(r4,2))),2)/
        ((1 - pow(2*pow(r1,2) + 2*Eta12*r1*r2 + 
               2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta13*r1*r3 + pow(r3,2))
               ))*(1 - pow(2*pow(r1,2) + 
               2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 
               2*Eta24*r2*r4,2)/
             (4.*(pow(r1,2) + 2*Eta12*r1*r2 + 
                 pow(r2,2))*
               (pow(r1,2) + 2*Eta14*r1*r4 + pow(r4,2))
               )))));       
  } else {
     value = 0;       
  } 
}

void DihedralAnglePartial::remove() {
    deleteDependents();
    ri->removeDependent( this );
    rj->removeDependent( this );
    rk->removeDependent( this );
    rl->removeDependent( this );
    eij->removeDependent( this );
    eik->removeDependent( this );
    eil->removeDependent( this );
    ejk->removeDependent( this );
    ejl->removeDependent( this );
    ekl->removeDependent( this );    
    Index->erase(pos);
    delete this;
}

DihedralAnglePartial::~DihedralAnglePartial(){}

DihedralAnglePartial* DihedralAnglePartial::At( Edge& e1, Edge& e2, Tetra& t ){
  TriPosition T( 3, e1.getSerialNumber(), e2.getSerialNumber(), t.getSerialNumber());
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
