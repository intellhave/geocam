#ifndef BITSTRING_H_
#define BITSTRING_H_

#include <new>
#include <stdlib.h>

#define INTSIZE sizeof(int)

class Bitstring {
private:
  unsigned int* bits;
  int spec_length;
  int length;

public:
  Bitstring(int numBits){
    if(numBits <= 0) exit(0); 
    spec_length = numBits;
    length = numBits/INTSIZE;
    if(numBits % INTSIZE != 0) length++;
    bits = new unsigned int[length];
    
    for(int ii = 0; ii < length; ii++)
      bits[ii] = 0;
  }

  ~Bitstring(){
    delete [] bits;
  }

  void setBit(int toSet);
  void lowerBit(int toLower);
  bool checkBit(int toCheck);
  int bitCount();


  bool contains(Bitstring* other);
  void add(Bitstring* other);
  void subtract(Bitstring* other);
  bool isEmpty();
};

#endif /* BITSTRING_H_ */
