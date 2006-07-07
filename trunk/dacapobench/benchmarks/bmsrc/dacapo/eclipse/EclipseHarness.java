package dacapo.eclipse;

import java.io.File;
import java.net.*;
import java.io.*;
import java.lang.reflect.Method;

import dacapo.Benchmark;
import dacapo.parser.Config;

public class EclipseHarness extends Benchmark {
  
  static final String wsDirectory = "workspace";
  private static EclipseHarnessThread eclipseThread = null;
  
  public EclipseHarness(Config config, File scratch) throws Exception {
    super(config, scratch);
  }
  
  public void preIteration(String size) throws Exception {
    super.preIteration(size);
    File wsdir = new File(scratch, wsDirectory);
    wsdir.mkdir();
    unpackZipFile(fileInScratch("eclipse/plugins/org.eclipse.jdt.core.tests.performance_3.1.2/full-source-R3_0.zip"),wsdir); 
    
    if (eclipseThread == null) {
      eclipseThread = new EclipseHarnessThread(this, "EclipseThread");
      eclipseThread.start();
    }
  }
  
  public void iterate(String size) throws Exception {
    EclipseHarnessLock.start(config.getArgs(size));
  }
  
  public void postIteration(String size) throws Exception {
    super.postIteration(size);
    if (!preserve)
      deleteTree(new File(scratch,wsDirectory));
  }
  
  public void cleanup() {
    EclipseHarnessLock.cleanup();
  }
}
