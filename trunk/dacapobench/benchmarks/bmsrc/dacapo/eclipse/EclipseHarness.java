package dacapo.eclipse;

import java.io.File;
import java.net.*;
import java.io.*;
import java.lang.reflect.Method;

import dacapo.Benchmark;
import dacapo.parser.Config;

public class EclipseHarness extends Benchmark {

  private static final String wsDirectory = "workspace";
  
  public EclipseHarness(Config config, File scratch) throws Exception {
    super(config, scratch);
  }
  
  public void preIteration(String size) throws Exception {
    super.preIteration(size);

/*    if (!preserve)
      deleteTree(new File(scratch, wsDirectory));
*//*    File wsdir = new File(scratch, wsDirectory);
        wsdir.mkdir();
	unpackZipFileResource(fileInScratch("eclipse/plugins/org.eclipse.jdt.core.tests.performance_3.1.2/full-source-R3_0.zip"),wsdir); */
  }

  public void iterate(String size) throws Exception {
    //    System.out.println("Running eclipse benchmark");
    invokePlugin(config.getArgs(size));
    //    System.out.println("Eclipse benchmark complete");
  }

  private void invokePlugin(String[] pluginArgs) throws Exception {
    String[] eclipseArgs = new String[4 + pluginArgs.length];
    eclipseArgs[0] = "-data";
    eclipseArgs[1] = fileInScratch(wsDirectory);
    eclipseArgs[2] = "-application";
    eclipseArgs[3] = "dacapo.eclipse.dacapoHarness";
    for (int i = 0; i < pluginArgs.length; i++)
      eclipseArgs[4+i] = pluginArgs[i];
/* */
/*    for (int i = 0; i < eclipseArgs.length; i++) {
      System.out.print(eclipseArgs[i]+" ");
    }
    System.out.println();*/
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
      URL startupjar = new File(fileInScratch("eclipse/startup.jar")).toURL();
      URL[] url = {startupjar};
      URLClassLoader ulc = new URLClassLoader(url);
      Class launcher = Class.forName("org.eclipse.core.launcher.Main", true, ulc);
      /*      System.out.println(launcher.getName()); */
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
  
  public void postIteration(String size) throws Exception {
    super.postIteration(size);
    if (!preserve)
      deleteTree(new File(scratch,wsDirectory));
  }

}
