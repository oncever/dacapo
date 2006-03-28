/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2005
 */
import dacapo.Callback;

public class MyCallback extends Callback {
  public void startWarmup(String benchmark) {
    System.err.println("my hook starting warmup " + benchmark);
    super.startWarmup(benchmark);
  };
  public void stopWarmup(String benchmark) {
    super.stopWarmup(benchmark);
    System.err.println("my hook finished warmup " + benchmark);
    System.err.flush();
  };
  public void start(String benchmark) {
    System.err.println("my hook starting " + benchmark);
    super.start(benchmark);
  };
  public void stop(String benchmark) {
    super.stop(benchmark);
    System.err.println("my hook finished " + benchmark);
    System.err.flush();
  };
}
