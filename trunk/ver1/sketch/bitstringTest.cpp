#include <new>
#include <stdio.h>
#include <stdlib.h>
#include "bitstring.h"

void print(const char* label, Bitstring* b){
  printf("%s : ", label);
  for(int ii = 0; ii < 10; ii++){
    if(b->checkBit(ii)){ printf("1"); }
    else{ printf("0"); }
  }
  printf("\n");
}

void compare(Bitstring* b, Bitstring* b2){
  if(b->contains(b2)){ printf("B contains B2.\n"); }
  else{ printf("B doesn't contain B2.\n"); }
}

int main(int argc, char** argv){
  Bitstring* b = new Bitstring(10);
  print("B", b);

  if(b->isEmpty()){ printf("B is empty\n"); }
  else{ printf("B is nonempty\n"); }

  b->setBit(0); b->setBit(1); b->setBit(5);
  print("B", b);

  Bitstring* b2 = new Bitstring(10);
  
  compare(b, b2);
  b2->setBit(0); 
  compare(b, b2);
  b2->setBit(1);
  compare(b, b2);
  b2->setBit(5);
  compare(b, b2);
  b2->setBit(7);
  compare(b, b2);

  print("B2", b2);
  b->subtract(b2);  
  print("B", b);


 




  if(b->isEmpty()){ printf("B is empty\n"); }
  else{ printf("B is nonempty\n"); }

  delete b;
  delete b2;

  return 0;
}
