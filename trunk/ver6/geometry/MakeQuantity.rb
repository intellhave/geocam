def createSrc( quantName )
name = quantName;
capname = name.upcase();

form = <<-END_OF_STATEMENT
#ifndef #{capname}_H_
#define #{capname}_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

class #{name};
typedef map<TriPosition, #{name}*, TriPositionCompare> #{name}Index;

class #{name} : public virtual GeoQuant {
private:
  static #{name}Index* Index;

protected:
  #{name}( SIMPLICES );
  void recalculate();

public:
  ~#{name}();
  static #{name}* At( SIMPLICES );
  static void CleanUp();
};
#{name}Index* #{name}::Index = NULL;

#{name}::#{name}( SIMPLICES ){}

void #{name}::recalculate(){}

#{name}::~#{name}(){}

#{name}* #{name}::At( SIMPLICES ){
  TriPosition T( NUMSIMPLICES, SIMPLICES );
  if( Index == NULL ) Index = new #{name}Index();
  #{name}Index::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    #{name}* val = new #{name}( SIMPLICES );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void #{name}::CleanUp(){
  if( Index == NULL ) return;
  #{name}Index::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* #{capname}_H_ */

END_OF_STATEMENT
return form
end

##############################################################

puts createSrc( ARGV[0] )
