#include "gqindex.h"
#include <cstdio>

GeoQuantIndex::GeoQuantIndex(){
  quantities = new vector<GeoQuant*>*[TOTALTYPES];
  for(int ii = 0; ii < TOTALTYPES; ii++)
    quantities[ii] = new vector<GeoQuant*>();
}

GeoQuantIndex::~GeoQuantIndex(){
  for(int ii = 0; ii < TOTALTYPES; ii++)
    delete quantities[ii];
  delete [] quantities;
}

bool GeoQuantIndex::add(GeoQuant* gq){
  quantID qid = gq->getType();
  TriPosition* pos = gq->getPosition();
  
  if(qid > TOTALTYPES) return false;
  
  /* Is there another quantity of the same type at this position? 
  ** If so, don't add this new quantity.*/
  GeoQuant* other = lookup(qid, pos); 
  if(other != NULL) return false;
  
  quantities[qid]->push_back(gq);
  return true;
}

GeoQuant* GeoQuantIndex::remove(quantID type, TriPosition position){
  if(type > TOTALTYPES) return NULL;
  
  vector<GeoQuant*>* listing = quantities[type];
  vector<GeoQuant*>::iterator iter = listing->begin();
  
  GeoQuant* curr;
  for(unsigned int ii = 0; ii < listing->size(); ii++, iter++){
    curr = listing->at(ii);
    if(position.compare(curr->getPosition()) == 0){
      listing->erase(iter);
      return curr;
    } 
  }
  
  return NULL;
} 

void GeoQuantIndex::printTables(){
  for(int ii = 0; ii < TOTALTYPES; ii++){
    printf("Type %d: ", ii);
    for(int jj = 0; jj < quantities[ii]->size(); jj++){
      TriPosition* tp = quantities[ii]->at(jj)->getPosition();
      tp->print();
      printf(" ");
    }
    printf("\n");
  }
}


GeoQuant* GeoQuantIndex::lookup(quantID type, TriPosition position){
  if(type > TOTALTYPES) return NULL;
  
  printf("\nSearching for: "); position.print(); printf("\n");
  printTables();

  vector<GeoQuant*>* listing = quantities[type];
  GeoQuant* curr;
  for(unsigned int ii = 0; ii < listing->size(); ii++){
    curr = listing->at(ii);        
    if(position.compare( curr->getPosition() ) == 0)
      return curr;
  }
  
  return NULL;
}

vector<GeoQuant*>* GeoQuantIndex::clear(quantID type){
  if(type > TOTALTYPES) return NULL;
  vector<GeoQuant*>* retval = quantities[type];
  quantities[type] = new vector<GeoQuant*>();

  return retval;
}

int GeoQuantIndex::size(){
  int sum = 0;
  for(int ii = 0; ii < TOTALTYPES; ii++){
    sum += quantities[ii]->size();
  }
  return sum;
}
