package dacapo.eclipse;

import java.io.File;
import java.net.*;
import java.io.*;
import java.lang.reflect.Method;

import dacapo.Benchmark;
import dacapo.parser.Config;

public class EclipseHarness extends Benchmark {
  
  static final String wsDirectory = "workspace";
//  private static EclipseHarnessThread eclipseThread = null;
  
  public EclipseHarness(Config config, File scratch) throws Exception {
    super(config, scratch);
  }
  
  public void preIteration(String size) throws Exception {
    super.preIteration(size);
    File wsdir = new File(scratch, wsDirectory);
    wsdir.mkdir();
    unpackZipFile(fileInScratch("eclipse/plugins/org.eclipse.jdt.core.tests.performance_3.1.2/full-source-R3_0.zip"),wsdir);  
  }
  
  public void iterate(String size) throws Exception {
    try {
      if (!org.eclipse.core.runtime.adaptor.EclipseStarter.isRunning())
        startup(size);
      org.eclipse.core.runtime.adaptor.EclipseStarter.run(null);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  } 
  
  public void postIteration(String size) throws Exception {
    super.postIteration(size);
    if (!preserve)
      deleteTree(new File(scratch,wsDirectory));
  }
  
  public void cleanup() {
  }

  private void startup(String size) {
    try {
      System.setProperty("osgi.os","linux");
      System.setProperty("osgi.ws","gtk");
      System.setProperty("osgi.arch","x86");
      System.setProperty("osgi.install.area",  "file:"+fileInScratch("eclipse/"));
      System.setProperty("osgi.noShutdown", "true");
      System.setProperty("osgi.framework","file:"+fileInScratch("eclipse/plugins/org.eclipse.osgi_3.1.2.jar"));
      String[] pluginArgs = config.getArgs(size);
      String[] args = new String[4 + pluginArgs.length];
      args[0] = "-data";
      args[1] = fileInScratch("workspace");
      args[2] = "-application";
      args[3] = "dacapo.eclipse.dacapoHarness";
      for (int i = 0; i < pluginArgs.length; i++)
        args[4+i] = pluginArgs[i];
      org.eclipse.core.runtime.adaptor.EclipseStarter.startup(args, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
