#include <stdlib.h>
#include <map>
#include "radius_partial.h"
#include "nehr_second_partial.h"
#include "matrix.h"
#include "utilities.h"
#include "miscmath.h"

typedef map<TriPosition, RadiusPartial*, TriPositionCompare> RadiusPartialIndex;
static RadiusPartialIndex* Index = NULL;

static Matrix<NEHRSecondPartial*>* nehr_rad_rad = NULL;
static Matrix<NEHRSecondPartial*>* nehr_rad_eta = NULL;

RadiusPartial::RadiusPartial(Vertex& v, Edge& e) {
  if(nehr_rad_rad == NULL || nehr_rad_eta == NULL) {
    build();
  }
  map<int, Edge>::iterator eit;
  int ii, jj;
  int vertSize = Triangulation::vertexTable.size();
  for(ii = 0; ii < vertSize; ii++) {
    for(jj = 0; jj < vertSize; jj++) {
      (*nehr_rad_rad)[ii][jj]->addDependent(this);
    }
  }
  jj = 0;
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++, jj++) {
    if(eit->first == e.getIndex()) {
      for(ii = 0; ii < vertSize; ii++) {
        (*nehr_rad_eta)[ii][jj]->addDependent(this);
      }
      break;
    }
  }
}

void RadiusPartial::recalculate(){
  calculateRadiiPartials();
}

void RadiusPartial::remove() {
  deleteDependents();
  map<int, Edge>::iterator eit;
  int ii, jj;
  int vertSize = Triangulation::vertexTable.size();
  for(ii = 0; ii < vertSize; ii++) {
    for(jj = 0; jj < vertSize; jj++) {
      (*nehr_rad_rad)[ii][jj]->removeDependent(this);
    }
  }

  jj = 0;
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++, jj++) {
      for(ii = 0; ii < vertSize; ii++) {
        (*nehr_rad_eta)[ii][jj]->removeDependent(this);
      }
  }
  Index->erase(pos);
  delete this;
}

RadiusPartial::~RadiusPartial(){}

RadiusPartial* RadiusPartial::At( Vertex& v, Edge& e ){
  TriPosition T( 2, v.getSerialNumber(), e.getSerialNumber() );
  if( Index == NULL ) Index = new RadiusPartialIndex();
  RadiusPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    RadiusPartial* val = new RadiusPartial( v, e );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void RadiusPartial::CleanUp(){
  if( Index == NULL ) return;
  RadiusPartialIndex::iterator iter;
  RadiusPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }

  delete Index;
  Index = NULL;
}

void RadiusPartial::build() {
  int vertSize = Triangulation::vertexTable.size();
  int edgeSize = Triangulation::edgeTable.size();
  map<int, Vertex>::iterator vit, vit2;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin(),
                             vEnd = Triangulation::vertexTable.end();
  map<int, Edge>::iterator eit;
  int ii, jj;
  ii = 0;
  if(nehr_rad_rad == NULL) {
    nehr_rad_rad = new Matrix<NEHRSecondPartial*>(vertSize, vertSize);
    for(vit = vBegin; vit != vEnd; vit++, ii++) {
      jj = 0;
      for(vit2 = vBegin; vit2 != vEnd; vit2++, jj++) {
        (*nehr_rad_rad)[ii][jj] = NEHRSecondPartial::At(vit->second, vit2->second);
      }
    }
  }
  
  if(nehr_rad_eta == NULL) {
    nehr_rad_eta = new Matrix<NEHRSecondPartial*>(vertSize, edgeSize);
    ii = 0;

    for(vit = vBegin; vit != vEnd; vit++, ii++) {
      jj = 0;
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++, jj++) {
        (*nehr_rad_eta)[ii][jj] = NEHRSecondPartial::At(vit->second, eit->second);
      }
    }
  }
}

void RadiusPartial::deconstruct() {
  delete nehr_rad_rad;
  delete nehr_rad_eta;
  nehr_rad_eta = nehr_rad_rad = NULL;
}

void RadiusPartial::calculateRadiiPartials() {
  int vertSize = Triangulation::vertexTable.size();
  int edgeSize = Triangulation::edgeTable.size();

  map<int, Vertex>::iterator vit;
  map<int, Edge>::iterator eit;
  int ii, jj, kk;

  if(nehr_rad_rad == NULL || nehr_rad_eta == NULL) {
    build();
  }
  
  Matrix<double> nehr_radius_partials(vertSize, vertSize);
  double nehr_rad_eta_partials[vertSize];
  double values[vertSize];
  // For each eta, solve the system
  jj = 0;
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++, jj++) {
    ii = 0;
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++, ii++) {
      nehr_rad_eta_partials[ii] = -1 * (*nehr_rad_eta)[ii][jj]->getValue();
    }
    
    for(ii = 0; ii < vertSize; ii++) {
      for(kk = 0; kk < vertSize; kk++) {
        nehr_radius_partials[ii][kk] = (*nehr_rad_rad)[ii][kk]->getValue();
      }
    }
    
    for(ii = 0; ii < vertSize; ii++) {
      values[ii] = 0;
    }
    
    LinearEquationsSolver(nehr_radius_partials, nehr_rad_eta_partials, values, vertSize);

    double length = 0;
    for(ii = 0; ii < vertSize; ii++) {
      length += values[ii] * values[ii];
    }
    length = sqrt(length);
    for(ii = 0; ii < vertSize; ii++) {
      values[ii] = values[ii] / length;
    }

    ii = 0;
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++, ii++) {
      RadiusPartial::At(vit->second, eit->second)->setValue(values[ii]);
    }
  }
}
