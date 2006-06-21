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
    unpackZipFileResource(fileInScratch("eclipse/download/workspace.zip"),new File(scratch,"eclipse/test-workspace"));
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
              "-application","org.dacapo.dacapoHarness",args[0]};
      for (int i=0; i < cmdArgs.length; i++)
        System.out.print(cmdArgs[i]+" ");
      System.out.println();
      org.eclipse.core.launcher.Main.main(cmdArgs);
      System.out.println("Eclipse benchmark complete");
    } finally {
      System.out.println("Eclipse FINALLY block executing");
      System.setProperty("osgi.os",oldOsgiOs);
      System.setProperty("osgi.os",oldOsgiWs);
      System.setProperty("osgi.os",oldOsgiArch);
    }
  }

  public void postIteration(String size) throws Exception {
    super.postIteration(size);
    if (!preserve)
      deleteTree(new File(scratch,"eclipse/test-workspace"));
  }

}
