/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#include "miscmath.h"

vector<int> listIntersection(vector<int>* list1, vector<int>* list2){
  vector<int> sameAs;
             
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) {
	sameAs.push_back((*list1)[i]);
	break;
      }
    }
  }
  return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2){
  vector<int> diff;
            
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) break;
      if(j == (*list2).size() - 1) 
	diff.push_back((*list1)[i]);
    }
  }
  return diff;
}


