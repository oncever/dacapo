package dacapo.eclipse;

public class EclipseHarnessLock {
  static EclipseHarnessLock singleton = new EclipseHarnessLock();
  String[] args = null;
  boolean complete = false;
  
  /**
   * Signal from the harness to the benchmark thread that it should
   * start a new iteration
   * @param args The command line arguments for the plugin
   */
  public static void start(String[] args) {
    synchronized (singleton) {
      singleton.args = args;
      singleton.notify();
      /* wait until benchmark thread is ready/available */
      while (singleton.args != null) {
        try {
          singleton.wait(); 
        } catch (InterruptedException e) { }
      }
    }
  }
  
  /**
   * Signal from the harness to the benchmark thread to indicate that
   * all iterations have completed and that it can now shut down
   */
  public static void cleanup() {
    synchronized (singleton) {
      singleton.complete = true;
      singleton.notify();
    }
  }
  
  /**
   * The benchmark thread waits for the harness to be ready for
   * a new benchmark iteration.   When the harness is ready, the
   * return value will be the command line arguments for this
   * iteration, or null if this is the last iteration.
   * @return the command line arguments for this iteration, or
   * null if this is the last iteration
   */
  public static String[] waitToRun() {
    String[] rtn;
    synchronized (singleton) {
      while (singleton.args == null && !singleton.complete) {
        try {
          singleton.wait(); 
        } catch (InterruptedException e) { }
      }
      rtn = singleton.complete ? null : singleton.args;
    } 
    return rtn;
  }
  
  /**
   * Signal from the benchmark thread to tell the harness that it has
   * finished an iteration
   */
  public static void finish() {
    synchronized (singleton) {
      singleton.args = null;
      singleton.notify();
    }
  }
}
