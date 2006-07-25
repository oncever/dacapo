package dacapo.eclipse;


import org.eclipse.core.runtime.IPlatformRunnable;

public class HarnessRunner implements IPlatformRunnable { 
  /**
   * The main entrypoint into this class.
   * 
   * @param args The commandline arguments for running this class
   * @see org.eclipse.core.runtime.IPlatformRunnable
   */
  public Object run(Object args) throws Exception {
    try {
      EclipseTests.initialize();
      EclipseTests.runtests((String[]) args);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null; 
  }
}

