package dacapo.eclipse;

import java.io.File;

import dacapo.Benchmark;
import dacapo.parser.Config;

public class EclipseHarness extends Benchmark {

  public EclipseHarness(Config config, File scratch) throws Exception {
    super(config, scratch);
  }
  
  public void preIteration(String size) throws Exception {
    super.preIteration(size);
    if (preserve)
      deleteTree(new File(scratch,"eclipse/test-workspace"));
    File wsdir = new File(scratch,"eclipse/test-workspace");
    wsdir.mkdir();
    unpackZipFileResource(fileInScratch("eclipse/workspace.zip"),wsdir);
  }

  public void iterate(String size) throws Exception {
    String[] args = config.getArgs(size);
    
    String oldOsgiOs = System.getProperty("osgi.os");
    String oldOsgiWs = System.getProperty("osgi.ws");
    String oldOsgiArch = System.getProperty("osgi.arch");
    try {
      System.setProperty("osgi.os","linux");
      System.setProperty("osgi.ws","gtk");
      System.setProperty("osgi.arch","x86");
      
      System.out.println("Running eclipse benchmark");
      String[] cmdArgs = new String[] {
              "-data",fileInScratch("eclipse/test-workspace"),
              "-application","dacapo.eclipse.dacapoHarness",args[0]};
      for (int i=0; i < cmdArgs.length; i++)
        System.out.print(cmdArgs[i]+" ");
      System.out.println();
      new org.eclipse.core.launcher.Main().run(cmdArgs);
      System.out.println("Eclipse benchmark complete");
    } finally {
      if (oldOsgiOs != null) System.setProperty("osgi.os",oldOsgiOs);
      if (oldOsgiWs != null) System.setProperty("osgi.ws",oldOsgiWs);
      if (oldOsgiArch != null) System.setProperty("osgi.arch",oldOsgiArch);
    }
  }

  public void postIteration(String size) throws Exception {
    super.postIteration(size);
    if (!preserve)
      deleteTree(new File(scratch,"eclipse/test-workspace"));
  }

}
