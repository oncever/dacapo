package dacapo.eclipse;

import java.io.File;

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
*/    File wsdir = new File(scratch, wsDirectory);
    wsdir.mkdir();
     unpackZipFileResource(fileInScratch("eclipse/plugins/org.eclipse.jdt.core.tests.performance_3.1.2/full-source-R3_0.zip"),wsdir); 
  }

  public void iterate(String size) throws Exception {
    System.out.println("Running eclipse benchmark");
    invokePlugin(config.getArgs(size));
    System.out.println("Eclipse benchmark complete");
  }

  private void invokePlugin(String[] args) throws Exception {
    String oldOsgiOs = System.getProperty("osgi.os");
    String oldOsgiWs = System.getProperty("osgi.ws");
    String oldOsgiArch = System.getProperty("osgi.arch");
    try {
      System.setProperty("osgi.os","linux");
      System.setProperty("osgi.ws","gtk");
      System.setProperty("osgi.arch","x86");
      
      String[] cmdArgs = new String[] {
              "-data",fileInScratch(wsDirectory),
              "-application","dacapo.eclipse.dacapoHarness",args[0]};
      new org.eclipse.core.launcher.Main().run(cmdArgs);
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
