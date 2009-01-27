/**************************************************************
Class: Triangulation
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "triangulation.h" // class's header file
#include <cstdlib>
#include <map>
#include "triangulationmath.h"
#include <algorithm>
#include <vector>
#include <iostream>
#include "spherical/sphericalmath.h"
#include "3DTriangulation/3Dtriangulationmath.h"


map<int, Vertex> Triangulation::vertexTable;
map<int, Edge> Triangulation::edgeTable;
map<int, Face> Triangulation::faceTable;
map<int, Tetra> Triangulation::tetraTable;

// class constructor
Triangulation::Triangulation()
{
}

// class destructor
Triangulation::~Triangulation()
{
}

void Triangulation::putVertex(int key, Vertex v)
{
     vertexTable.insert(pair<int, Vertex>(key, v));
}

void Triangulation::putEdge(int key, Edge v)
{
     edgeTable.insert(pair<int, Edge>(key, v));
}

void Triangulation::putFace(int key, Face v)
{
     faceTable.insert(pair<int, Face>(key, v));
}
void Triangulation::putTetra(int key, Tetra v)
{
     tetraTable.insert(pair<int, Tetra>(key, v));
}

bool Triangulation::containsVertex(int key)
{
    map<int, Vertex>::iterator vit;
    for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
    {
            if(vit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsEdge(int key)
{
    map<int, Edge>::iterator eit;
    for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
    {
            if(eit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsFace(int key)
{
    map<int, Face>::iterator fit;
    for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
    {
            if(fit->first == key)
            return true;
    }
    return false;
}
bool Triangulation::containsTetra(int key)
{
    map<int, Tetra>::iterator tit;
    for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
    {
            if(tit->first == key)
            return true;
    }
    return false;
}

int Triangulation::greatestVertex()
{
    int greatest = 0;
    map<int, Vertex>::iterator vit;
    for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
    {
            if(vit->first > greatest)
            greatest = vit->first;
    }
    return greatest;
}

int Triangulation::greatestEdge()
{
    int greatest = 0;
    map<int, Edge>::iterator eit;
    for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
    {
            if(eit->first > greatest)
            greatest = eit->first;
    }
    return greatest;
}

int Triangulation::greatestFace()
{
    int greatest = 0;
    map<int, Face>::iterator fit;
    for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
    {
            if(fit->first > greatest)
            greatest = fit->first;
    }
    return greatest;
}
int Triangulation::greatestTetra()
{
    int greatest = 0;
    map<int, Tetra>::iterator tit;
    for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
    {
            if(tit->first > greatest)
            greatest = tit->first;
    }
    return greatest;
}

void Triangulation::eraseVertex(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeVertex(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeVertex(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeVertex(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeVertex(key);
     }
     vertexTable.erase(key);
}

void Triangulation::eraseEdge(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeEdge(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeEdge(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeEdge(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeEdge(key);
     }
     edgeTable.erase(key);
}

void Triangulation::eraseFace(int key)
{
       map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeFace(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeFace(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeFace(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeFace(key);
     }
     faceTable.erase(key);
}

void Triangulation::eraseTetra(int key)
{
     map<int, Vertex>::iterator vit;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
         vit->second.removeTetra(key);
     }
     map<int, Edge>::iterator eit;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
         eit->second.removeTetra(key);
     }
     map<int, Face>::iterator fit;
     for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
     {
         fit->second.removeTetra(key);
     }
     map<int, Tetra>::iterator tit;
     for(tit = tetraTable.begin(); tit != tetraTable.end(); tit++)
     {
         tit->second.removeTetra(key);
     }
     tetraTable.erase(key);
}

void Triangulation::resetTriangulation()
{
     vertexTable.clear();
     edgeTable.clear();
     faceTable.clear();
     tetraTable.clear();
}

double Triangulation::netCurvature()
{
       double net = 0;
       map<int, Vertex>::iterator it;
       for(it = vertexTable.begin(); it != vertexTable.end(); it++)
       {
              net += curvature((*it).second);
       }
       return net;
}

double Triangulation::netSphericalCurvature()
{
       double net = 0;
       map<int, Vertex>::iterator it;
       for(it = vertexTable.begin(); it != vertexTable.end(); it++)
       {
              net += sphericalCurvature((*it).second);
       }
       return net;
}

double Triangulation::netHyperbolicCurvature()
{
       double net = 0;
       map<int, Vertex>::iterator it;
       for(it = vertexTable.begin(); it != vertexTable.end(); it++)
       {
              net += hyperbolicCurvature((*it).second);
       }
       return net;
}

void Triangulation::setRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       vit->second.setRadius(radii[i]);
       i++;
     }
}

void Triangulation::getRadii(double* radii)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       radii[i] = vit->second.getRadius();
       i++;
     }
}

void Triangulation::setLengths(double* lengths)
{
     map<int, Edge>::iterator eit;
     int i = 0;
     for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
     {
       eit->second.setLength(lengths[i]);
       i++;
     }
}

double Triangulation::net3DCurvature() 
{
       double net = 0;
       map<int, Vertex>::iterator it;
       for(it = vertexTable.begin(); it != vertexTable.end(); it++)
       {
              net += (*it).second.getCurvature();
       }
       return net; 
}

void Triangulation::setDihedralAngles()
{
   map<int, Edge>::iterator eit;
   for(eit = edgeTable.begin(); eit != edgeTable.end(); eit++)
   {
      eit->second.setDihedralAngles();
   }
}

void Triangulation::setAngles()
{
   map<int, Face>::iterator fit;
   for(fit = faceTable.begin(); fit != faceTable.end(); fit++)
   {
      fit->second.setAngles();
   }
}
