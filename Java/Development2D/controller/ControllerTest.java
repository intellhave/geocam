package controller;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class ControllerTest {

  public static void main(String args[]){
    Controllers allControls = new Controllers();
    try {
      allControls.create();
    } catch (LWJGLException e) {
      System.err.println("Error: Unable to initialize controllers.");
      e.printStackTrace();
    }
    
    System.out.println("# Controllers Found:" + allControls.getControllerCount());  
     Controller ourController = allControls.getController(0);
     int numButtons = ourController.getButtonCount();
     System.out.println("# Buttons on controller: "+ numButtons);
    for(int i=0;i<numButtons;i++)
      System.out.println("Button "+i+" is called "+ourController.getButtonName(i));
    
    int numAxes = ourController.getAxisCount();
    System.out.println("Number of Axes "+ourController.getAxisCount());
    for(int i=0;i<numAxes;i++)
      System.out.println("Axes name "+ourController.getAxisName(i));
    
    float xAxisValue = ourController.getXAxisValue();
    float yAxisValue = ourController.getYAxisValue();
    System.out.println("X Axis Value is: "+xAxisValue);
    System.out.println("Y Axis Value is:"+yAxisValue);
    
    
    while(true){
      ourController.poll();
      for(int i=0;i<numButtons;i++){
        if(ourController.isButtonPressed(i))
          System.out.println(ourController.getButtonName(i)+" is pressed");
       if(ourController.getXAxisValue() !=0.0 || ourController.getYAxisValue()!=0.0){
         System.out.println("X Axis Value is: "+ourController.getXAxisValue());
         System.out.println("Y Axis Value is: "+ourController.getYAxisValue());
      }
    }
    
  }
  } 
}
