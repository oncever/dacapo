/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2005
 */
import dacapo.Callback;

import com.ibm.JikesRVM.memoryManagers.mmInterface.MM_Interface;

public class MMTkCallback extends Callback {
  public void startWarmup(String benchmark) {
    System.err.println("Started warmup " + benchmark);
    super.startWarmup(benchmark);
  };
  public void stopWarmup(String benchmark) {
    super.stopWarmup(benchmark);
    System.err.println("Finished warmup " + benchmark);
    System.err.flush();
  };
  public void start(String benchmark) {
    super.start(benchmark);
    MM_Interface.harnessBegin();
  };
  public void stop(String benchmark) {
    MM_Interface.harnessEnd();
    super.stop(benchmark);
    System.err.flush();
  };
}
