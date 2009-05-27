#include <map>
#include <cstdio>
#include <new>
#include <string>

using namespace std;

int fooCounter = 0;

class Foo{
public:
  int fooID;
  string fooData;
  
  Foo() {
    fooID = fooCounter;
    fooCounter++;
  }

  Foo(string data){
    fooID = fooCounter;
    fooCounter++;
    fooData = data;
  }

  ~Foo() { }

};

Foo* LoadMap(map<int, Foo> &fooTable){
  char* data[7] = { "what", "is" , "up" , "with" , "objects", "in", "c++?" };
  
  Foo f(data[0]);
  fooTable.insert(pair<int,Foo>(0, f));
	       
  for(int ii = 1; ii < 100000; ii++){
    fooTable.insert(pair<int,Foo>(ii, Foo(data[ii % 7])));
  }
   
  printf("In LoadMap: \n");
  for(int ii = 0; ii < 7; ii++){
    const char* toPrint = (fooTable[ii].fooData).c_str();
    printf("%s %d @: %p\n", toPrint, fooTable[ii].fooID, &fooTable[ii]);
  }

  fooTable[0].fooData.append("66666666666666666666666666666666");
  printf("\n");

  return &f;
}

int main(int argc, char** argv){
  map<int, Foo> fooTable;

  Foo* f = LoadMap(fooTable);
  printf("In Main: \n");
  
  printf("Foo* f: %p\n", f);
  printf("&(fooTable[0]): %p\n", &(fooTable[0]));


  for(int ii = 0; ii < 7; ii++){
    const char* toPrint = (fooTable[ii].fooData).c_str();
    printf("%s %d @: %p\n", toPrint, fooTable[ii].fooID, &fooTable[ii]);
  }

  printf("\nTotal Foos constructed: %d\n", fooCounter);
}

