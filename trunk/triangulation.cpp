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


map<int, Vertex> Triangulation::vertexTable;
map<int, Edge> Triangulation::edgeTable;
map<int, Face> Triangulation::faceTable;

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

bool Triangulation::containsVertex(int key)
{
    map<int, Vertex>::iterator vit;
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
    {
            if(vit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsEdge(int key)
{
    map<int, Edge>::iterator eit;
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
            if(eit->first == key)
            return true;
    }
    return false;
}

bool Triangulation::containsFace(int key)
{
    map<int, Face>::iterator fit;
    for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++)
    {
            if(fit->first == key)
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
     faceTable.erase(key);
}

void Triangulation::resetTriangulation()
{
     vertexTable.clear();
     edgeTable.clear();
     faceTable.clear();
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

void Triangulation::setWeights(double* weights)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       vit->second.setWeight(weights[i]);
       i++;
     }
}

void Triangulation::getWeights(double* weights)
{
     map<int, Vertex>::iterator vit;
     int i = 0;
     for(vit = vertexTable.begin(); vit != vertexTable.end(); vit++)
     {
       weights[i] = vit->second.getWeight();
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
