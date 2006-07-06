package dacapo.eclipse;

public class EclipseHarnessLock {
  static EclipseHarnessLock singleton = new EclipseHarnessLock();
  String[] args = null;
  boolean complete = false;
  
  public static void start(String[] args) {
    synchronized (singleton) {
      singleton.args = args;
      singleton.notify();
      while (singleton.args != null) {
        try {
          System.err.println(singleton.hashCode()+ "Starter is waiting...");
          singleton.wait(); 
          System.err.println(singleton.hashCode()+ "Starter has woken!");
        } catch (InterruptedException e) { }
      }
       System.err.println(singleton.hashCode()+ "starter has finished!");
    }
  }
  public static void finish() {
    System.err.println(singleton.hashCode()+ "nearly got a finish!");
    System.err.flush();
    synchronized (singleton) {
      System.err.println(singleton.hashCode()+ "got a finish!");
      singleton.args = null;
      singleton.notify();
    }
  }
  public static void cleanup() {
    synchronized (singleton) {
      singleton.complete = true;
      singleton.notify();
    }
  }
  public static String[] waitToRun() {
    String[] rtn;
    System.err.println(singleton.hashCode()+ "... about to wait...");
    synchronized (singleton) {
      while (singleton.args == null || singleton.complete) {
        try {
          System.err.println(singleton.hashCode()+ "XXXXXXXXXX waiting to run...");
          singleton.wait(); 
          System.err.println(singleton.hashCode()+ "done waiting to run...");
        } catch (InterruptedException e) { }
      }
      rtn = singleton.complete ? null : singleton.args;
      if (singleton.complete) 
         System.err.println(singleton.hashCode()+ "Got complete!");
      else
        System.err.println(singleton.hashCode()+ "Got incomplete!");
    } 
    return rtn;
  }
}
