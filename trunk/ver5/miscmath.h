/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#ifndef MISCMATH_H_
#define MISCMATH_H_

#include <vector>
using namespace std;

/* Returns a list of simplex indices that are common between the two lists
 * given. */
vector<int> listIntersection(vector<int>* list1, vector<int>* list2);

/* Returns a list of simplex indices that are in the first list and not in
 * the second.*/
vector<int> listDifference(vector<int>* list1, vector<int>* list2);

#endif /* MISCMATH_H_ */
