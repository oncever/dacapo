/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2005
 */
import dacapo.Callback;

public class GcCallback extends Callback {
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
    System.gc();
    super.start(benchmark);
  };
  public void stop(String benchmark) {
    super.stop(benchmark);
    System.err.flush();
  };
}
