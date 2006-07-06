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
          singleton.wait(); 
        } catch (InterruptedException e) { }
      }
    }
  }
  public static void finish() {
    synchronized (singleton) {
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
    synchronized (singleton) {
      while (singleton.args == null || singleton.complete) {
        try {
          singleton.wait(); 
        } catch (InterruptedException e) { }
      }
      rtn = singleton.complete ? null : singleton.args;
    } 
    return rtn;
  }
}
