package dacapo.eclipse;


import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.IPlatformRunnable;

public class HarnessRunner implements IPlatformRunnable {
  /*
   * The following are used to allow us to reflectively access a lock
   * in the default class loader so we can synchronize with the harness.
   * The problem is that eclipse can only be invoked once, so we have
   * to run it in a thread which polls for iterations.  To do this we
   * need synchronization with the harness, which runs in a different
   * classloader.
   */
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


  /**
   * The main entrypoint into this class.
   * 
   * @param args The commandline arguments for running this class
   * @see org.eclipse.core.runtime.IPlatformRunnable
   */
  public Object run(Object args) throws Exception {
    try {
      do {
        EclipseTests.initialize();
        EclipseTests.runtests((String[]) args);
        finish();
      } while (waitToRun() != null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null; 
  }
  
  /**
   * Signal to the harness that an iteration has finished
   */ 
  private static void finish() {
    try {
      finishMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  /**
   * Wait for the harness to tell us that we need to run another iteration.
   */ 
  private static String[] waitToRun() {
    String[] rtn = null;
    try {
      rtn = (String[]) waitMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
    return rtn;
  }
}

