package dacapo;

public class Callback {
  long timer;
  
  /* Start the timer and announce the begining of an iteration */
  public void start(String benchmark) {
    start(benchmark, "");
  };
  public void startWarmup(String benchmark) {
    start(benchmark, "Warmup ");
  };
  private void start(String benchmark, String description) {
    timer = System.currentTimeMillis();
    System.err.println("===== DaCapo "+ benchmark + " Starting " + description + "=====");
    System.err.flush();
  }
  /* Stop the timer */
  public void stop() {
    timer = System.currentTimeMillis() - timer;
  }
  public void stopWarmup() {
    timer = System.currentTimeMillis() - timer;
  }
  /* Announce completion of the benchmark (pass or fail) */
  public void complete(String benchmark, boolean valid) {
    complete(benchmark, valid, "");
  };
  public void completeWarmup(String benchmark, boolean valid) {
    complete(benchmark, valid, "Warmup ");
  };
  private void complete(String benchmark, boolean valid, String description) {
    System.err.print("===== DaCapo "+ benchmark + (valid ? " PASSED " : " FAILED ") + description);
    if (valid) {
      System.err.print("in " + timer + " msec ");   
    }
    System.err.println("=====");
    System.err.flush();
  }
}
