package dacapo.eclipse;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/*
 * This class exists because of two problematic properties of eclipse:
 * 1. org.eclipse.core.launcher.Main may only be called once from within
 *    a single jvm run, so we construct a thread and leave it running
 *    across iterations.
 * 2. Eclipse has its own classloading world, and establishes this on
 *    the basis of the location of the jar from which it starts (startup.jar).
 *    But this jar is not visible in our classpath, so we need to extend
 *    the classpath.  We do this by using a new classloader. 
 */
public class EclipseHarnessThread extends Thread {
  private EclipseHarness parent;
  URLClassLoader ulc;
  
  EclipseHarnessThread(EclipseHarness parent, String name) {
    super(name);
    this.parent = parent;
    try {
      URL startupjar = new File(parent.fileInScratch("eclipse/startup.jar")).toURL();
      URL[] url = {startupjar};
      ulc = new URLClassLoader(url,Thread.currentThread().getContextClassLoader());
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  public void run() {
    try {
      String[] args = EclipseHarnessLock.waitToRun();
      invokePlugin(args);
    } catch (Exception e) { e.printStackTrace();}
  }
  
  private void invokePlugin(String[] pluginArgs) throws Exception {
    String[] eclipseArgs = new String[4 + pluginArgs.length];
    eclipseArgs[0] = "-data";
    eclipseArgs[1] = parent.fileInScratch(parent.wsDirectory);
    eclipseArgs[2] = "-application";
    eclipseArgs[3] = "dacapo.eclipse.dacapoHarness";
    for (int i = 0; i < pluginArgs.length; i++)
      eclipseArgs[4+i] = pluginArgs[i];
    invokeEclipse(eclipseArgs); 
  }
  
  /* we need to invoke Eclipse reflectively because we have to add startup.jar to the classpath */
  private void invokeEclipse(String[] eclipseArgs) throws Exception {
    String oldOsgiOs = System.getProperty("osgi.os");
    String oldOsgiWs = System.getProperty("osgi.ws");
    String oldOsgiArch = System.getProperty("osgi.arch");
    try {
      System.setProperty("osgi.os","linux");
      System.setProperty("osgi.ws","gtk");
      System.setProperty("osgi.arch","x86");
      Class launcher = Class.forName("org.eclipse.core.launcher.Main", true, ulc);
      Class[] sig = {Class.forName("[Ljava.lang.String;")};
      Method m = launcher.getMethod("run", sig);
      Object[] args = {eclipseArgs};
      m.invoke(launcher.newInstance(),args);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (oldOsgiOs != null) System.setProperty("osgi.os",oldOsgiOs);
      if (oldOsgiWs != null) System.setProperty("osgi.ws",oldOsgiWs);
      if (oldOsgiArch != null) System.setProperty("osgi.arch",oldOsgiArch);
    }
  }
}
