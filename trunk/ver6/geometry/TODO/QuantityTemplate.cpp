#ifndef QUANTITY_H_
#define QUANTITY_H_

class Quantity;
#typedef map<TriPosition, Quantity*, TriPositionCompare> QuantityIndex;

class Quantity : public virtual GeoQuant {
private:
  QuantityIndex* index;

protected:
  Quantity( SIMPLICES );
  void recalculate();

public:
  ~Quantity()
  static Quantity* At( SIMPLICES );
  static void CleanUp();
};
QuantityIndex* Quantity::index = NULL;

Quantity::Quantity( SIMPLICIES ){}

Quantity::~Quantity(){}

Quantity* Quantity::At( SIMPLICES ){
  TriPosition T( NUMSIMPLICES, SIMPLICES );
  if( index == NULL ) index = new QuantityIndex();
  QuantityIndex::iterator iter = index->find( T );

  if( iter == index->end() ){
    retval = new Quantity( SIMPLICES );
    iter = index->insert( make_pair( T, retval ) );
  } 

  return retval->second;
}

void Quantity::CleanUp(){
  if( index == NULL) return;
  QuantityIndex::iterator iter;
  for(iter = index->begin(); iter != eta_index->end(); iter++)
    delete iter->second;
  delete index;
}

#endif /* QUANTITY_H_ */
