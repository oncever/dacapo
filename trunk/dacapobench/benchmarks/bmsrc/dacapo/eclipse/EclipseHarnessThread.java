package dacapo.eclipse;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class EclipseHarnessThread extends Thread {
  /*  private Integer lock;
   private String[] args;
   private boolean complete; */
  private EclipseHarness parent;
//  Class lock;
  URLClassLoader ulc;
//  Method startMethod;
//  Method finishMethod;
//  Method waitMethod;
//  Method cleanupMethod;
  
  EclipseHarnessThread(EclipseHarness parent, String name) {
    super(name);
    this.parent = parent;
    try {
      URL startupjar = new File(parent.fileInScratch("eclipse/startup.jar")).toURL();
//      URL build = new File("/home/steveb/devel/dacapo/dacapo-benchmarks/benchmarks/build").toURL();
      URL[] url = {startupjar};
      ulc = new URLClassLoader(url,Thread.currentThread().getContextClassLoader());
//      lock = Class.forName("dacapo.eclipse.EclipseHarnessLock", true, ulc);
//      Class[] sig = {Class.forName("[Ljava.lang.String;")};
//      startMethod = lock.getMethod("start", sig);
//      finishMethod = lock.getMethod("finish", (Class[]) null);
//      waitMethod = lock.getMethod("waitToRun", (Class[]) null);
//      cleanupMethod = lock.getMethod("cleanup", (Class[]) null);
    } catch (Exception e) { e.printStackTrace(); }
    /*    this.args = null;
     this.complete = false;
     this.lock = new Integer(0); */
  }
  
  public void run() {
    try {
      System.err.println("HT waiting");
      String[] args = EclipseHarnessLock.waitToRun();
      System.err.println("HT finished waiting");
      invokePlugin(args);
      System.err.println("HT finished plugin");
    } catch (Exception e) { e.printStackTrace();}
    System.out.println("plugin finished...");
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
//    System.out.println(launcher.getName()); 
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
  /*
  public void startBenchmark(String[] args) {
    try {
      Object[] a = {args};
     startMethod.invoke(null, a);
    } catch (Exception e) { e.printStackTrace(); }
  }
  public void finishBenchmark() {
    try {
      finishMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
  }
  public void cleanupBenchmark() {
    try {
      cleanupMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
  } 
  public String[] waitToRun() {
    String[] rtn = null;
    try {
       rtn = (String[]) waitMethod.invoke(null,(Object[]) null);
    } catch (Exception e) { e.printStackTrace(); }
    return rtn;
  }*/ 
  /*
   public void startBenchmark(String[] theArgs) {
   System.err.println(this.hashCode()+"startBenchmark...");
   synchronized (lock) {
   args = theArgs;
   lock.notify();
   while (args != null) {
   try {
   System.err.println(this.hashCode()+" Starter is waiting...");
   lock.wait(); 
   System.err.println(this.hashCode()+" Starter has woken!");
   } catch (InterruptedException e) { }
   }
   System.err.println(this.hashCode()+"starter has finished!");
   }
   }
   public void finishBenchmark() {
   System.err.println(this.hashCode()+"nearly got a finish!");
   System.err.flush();
   synchronized (lock) {
   System.err.println(this.hashCode()+"got a finish!");
   args = null;
   lock.notify();
   }
   }
   public void cleanupBenchmark() {
   synchronized (lock) {
   complete = true;
   lock.notify();
   }
   }
   public String[] waitToRun() {
   String[] rtn;
   synchronized (lock) {
   while (args == null || complete) {
   try {
   System.err.println(this.hashCode()+" Waiting to run...");
   lock.wait(); 
   System.err.println(this.hashCode()+"done waiting to run...");
   } catch (InterruptedException e) { }
   }
   rtn = complete ? null : args;
   if (complete) 
   System.err.println(this.hashCode()+"Got complete!");
   else
   System.err.println(this.hashCode()+"Got incomplete!");
   } 
   return rtn;
   }
   */
}
