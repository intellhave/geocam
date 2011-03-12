package development;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*
 * This is an easy-to-use class to keep track of timing statistics for any piece of code
 * 
 * Instructions:
 * - Say you want to measure how long it takes some method (or part of a method, etc) to run:
 * -  (i) Choose an integer (taskType) to denote this type of task
 * -  (ii) At the beginning of the method, do:  long taskID = TimingStatistics.startTask(taskType);
 * -  (iii) At the end of the method, do: TimingStatistics.endTask(taskID);
 * -  (optional) At any time, call 'nameTask' to associate a String to the taskType (just to clarify output)
 * - Call printData() at any time to show the statistics (for example, when the program exits)
 * 
 * Important Notes:
 * - No need to initialize, can just start using it whenever
 * - Can 'disable' the class by setting TIMING_ENABLED = false
 * - Multiple tasks of the same type can be running simultaneously (the taskID differentiates them)
 */

public class TimingStatistics {
  
  //can force all timing to be disabled by setting this false
  private static final boolean TIMING_ENABLED = true;
  
  //next unique task identifier
  private static long curTaskID = 0;
  //active tasks
  private static HashMap<Long,TaskRecord> activeTasks = new HashMap<Long,TaskRecord>();
  //completed tasks
  private static HashMap<Integer,ArrayList<TaskRecord>> sortedData = new HashMap<Integer,ArrayList<TaskRecord>>();
  //list of (optional) names for task types
  private static HashMap<Integer,String> typeNames = new HashMap<Integer,String>();
  
  //the object which keeps track of each individual task's type and start/end times
  private static class TaskRecord{
    
    private boolean finished = false;
    private int taskType = 0;
    private long startTime = 0, endTime = 0, totalTime = 0;
    
    public int getTaskType(){ return taskType; }
    public long getTotalTime(){ return totalTime; }
    public boolean hasFinished(){ return finished; }
    
    public TaskRecord(int taskType_){
      startTime = System.currentTimeMillis();
      taskType = taskType_;
    }
    
    public void endTask(){
      endTime = System.currentTimeMillis();
      totalTime = endTime-startTime;
      finished = true;
    }
  }
  
  private TimingStatistics(){}
  
  public static void nameTask(Integer taskType, String name){
    
    //only works the first time for a particular task type
    if(typeNames.get(taskType) == null){
      typeNames.put(taskType,name);
    }
  }
  
  public static long startTask(Integer taskType){
    
    if(!TIMING_ENABLED){ return 0; }
    
    //start a new task
    curTaskID++;
    activeTasks.put(curTaskID,new TaskRecord(taskType));
    return curTaskID;
  }
  
  public static void endTask(long taskID){
    
    if(!TIMING_ENABLED){ return; }
    
    //get specified task
    TaskRecord task = activeTasks.get(curTaskID);
    task.endTask();
    
    //get list of finished tasks for this task type
    ArrayList<TaskRecord> tlist = sortedData.get(task.getTaskType());
    if(tlist == null){
      tlist = new ArrayList<TaskRecord>();
      sortedData.put(task.getTaskType(), tlist);
    }
    
    //add the finished task
    tlist.add(task);
  }
  
  public static void printData(){
    
    if(!TIMING_ENABLED){ return; }
    
    System.out.println("==BEGIN TIMING DATA==");
    
    //read the sorted data
    Iterator<Integer> keyiter = sortedData.keySet().iterator();
    while(keyiter.hasNext()){
     
      //get task type
      int taskType = keyiter.next();
      String name = typeNames.get(taskType);
      if(name == null){
        System.out.println("Task Type " + taskType + ":");
      }else{
        System.out.println("Task Type \"" + name + "\":");
      }
      
      //statistics to print
      int numFinished = 0, numUnfinished = 0;
      long timeTotal = 0, timeMin = 0, timeMax = 0;
      boolean initialized = false;
      
      //loop through recorded tasks
      ArrayList<TaskRecord> curList = sortedData.get(taskType);
      Iterator<TaskRecord> taskiter = curList.iterator();
      
      while(taskiter.hasNext()){
        
        //get the task record
        TaskRecord task = taskiter.next();
        
        if(task.hasFinished()){
          //if this task was finished, record the statistics
          numFinished++;
          long timeCurTask = task.getTotalTime();
          timeTotal += timeCurTask;
          //check min and max times
          if(initialized){
            if(timeCurTask < timeMin){ timeMin = timeCurTask; }
            if(timeCurTask > timeMax){ timeMax = timeCurTask; }
          }else{
            initialized = true;
            timeMin = timeCurTask;
            timeMax = timeCurTask;
          }
        }else{
          //if not, record an unfinished task
          numUnfinished++;
        }
      }
      
      //print info for this task type
      System.out.println(" - Finished Tasks: " + numFinished);
      if(numFinished > 0){
        System.out.println(" - Average: " + timeTotal/numFinished + "ms (Total: " + timeTotal + "ms)");
        System.out.println(" - Fastest: " + timeMin + "ms, Slowest: " + timeMax + "ms");
      }
    }
    
    System.out.println("==END TIMING DATA==");
  }
  
}
