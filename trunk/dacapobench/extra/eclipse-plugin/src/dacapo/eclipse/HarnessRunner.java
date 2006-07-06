package dacapo.eclipse;


import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.IPlatformRunnable;

public class HarnessRunner implements IPlatformRunnable {
  /**
   * The main entrypoint into this class.
   * 
   * @param args The commandline arguments for running this class
   * @see org.eclipse.core.runtime.IPlatformRunnable
   */
  public Object run(Object args) throws Exception {
    System.err.print(ClassLoader.getSystemClassLoader().hashCode());
    System.err.println(" " + ClassLoader.getSystemClassLoader());
    System.err.print(Thread.currentThread().getContextClassLoader().hashCode());
    System.err.println(" " + Thread.currentThread().getContextClassLoader());
    System.err.print(Thread.currentThread().getContextClassLoader().getParent().hashCode());
    System.err.println(" " + Thread.currentThread().getContextClassLoader().getParent());
    System.err.print(Thread.currentThread().getContextClassLoader().getParent().getParent().hashCode());
    System.err.println(" " + Thread.currentThread().getContextClassLoader().getParent().getParent());
    
    try {
      do {
        System.err.println("Starting an iteration!");
        EclipseTests.initialize();
        EclipseTests.runtests((String[]) args);
        System.err.println("Finishing an iteration!");
        finish();
        System.err.println("XX Finished!");
      } while (waitToRun() != null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.err.println("finished all iterations!");
    return null; 
  }
  
  
  static Class lock;
  static Method startMethod;
  static Method finishMethod;
  static Method waitMethod;
  static Method cleanupMethod;
  
  static {
    try {
    lock = Class.forName("dacapo.eclipse.EclipseHarnessLock", true, ClassLoader.getSystemClassLoader());
    Class[] sig = {Class.forName("[Ljava.lang.String;")};
    startMethod = lock.getMethod("start", sig);
    finishMethod = lock.getMethod("finish", (Class[]) null);
    waitMethod = lock.getMethod("waitToRun", (Class[]) null);
    cleanupMethod = lock.getMethod("cleanup", (Class[]) null);
    } catch (Exception ex){ ex.printStackTrace(); }
  }
  
  public static void finish() {
    try {
      finishMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  public static String[] waitToRun() {
    String[] rtn = null;
    try {
      rtn = (String[]) waitMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
    return rtn;
  }
}

