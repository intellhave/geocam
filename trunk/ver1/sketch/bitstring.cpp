#include "bitstring.h"

void Bitstring::setBit(int toSet){
  if(toSet < 0) return;
  int posn = toSet/INTSIZE;
  if(posn >= length) return;
  bits[posn] |= (1 << (toSet - posn * INTSIZE));
}

void Bitstring::lowerBit(int toLower){
  if(toLower < 0) return;
  int posn = toLower/INTSIZE;
  if(posn >= length) return;
  bits[posn] ^= (1 << (toLower - posn * INTSIZE));
}

bool Bitstring::checkBit(int toCheck){
  if(toCheck < 0) return false;
  int posn = toCheck/INTSIZE;
  if(posn >= length) return false;
  return (bits[posn] & (1 << (toCheck - posn * INTSIZE))) > 0;
}

int Bitstring::bitCount(){
  int sum = 0;
  for(int ii = 0; ii < length; ii++){
    unsigned int pos = 1;
    for(unsigned int jj = 0; jj < INTSIZE; jj++){
      if( (bits[ii] & pos) != 0) sum++;
      pos = pos >> 1;
    }
  }

  return sum;
}


bool Bitstring::contains(Bitstring* other){
  if(other->spec_length != spec_length) return false;
  for(int ii = 0; ii < length; ii++)
    if( (bits[ii] & other->bits[ii]) != other->bits[ii] )
      return false;
  
  return true;
}

void Bitstring::add(Bitstring* other){
  if(other->spec_length != spec_length) return;
  for(int ii = 0; ii < length; ii++)
    bits[ii] |= other->bits[ii];
}

void Bitstring::subtract(Bitstring* other){
  if(other->spec_length != spec_length) return;
  for(int ii = 0; ii < length; ii++)
    bits[ii] ^= (bits[ii] & other->bits[ii]);
}

bool Bitstring::isEmpty(){
  for(int ii = 0; ii < length; ii++)
    if(bits[ii] != 0)
      return false;
  
  return true;
}
