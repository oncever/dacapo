package dacapo;

public class Callback {
  long timer;

  public void startWarmup(String benchmark) {
    timer = System.currentTimeMillis();
    System.err.println("===== DaCapo "+ benchmark + " Starting Warmup =====");
    System.err.flush();
  };
  public void stopWarmup(String benchmark) {
    System.err.print("===== DaCapo "+ benchmark + " Finished Warmup in ");
    System.err.print((System.currentTimeMillis() - timer));
    System.err.println(" msec =====");
    System.err.flush();
  };
  public void start(String benchmark) {
    timer = System.currentTimeMillis();
    System.err.println("===== DaCapo "+ benchmark + " Starting =====");
    System.err.flush();
  };
  public void stop(String benchmark) {
    System.err.print("===== DaCapo "+ benchmark + " Finished in ");
    System.err.print((System.currentTimeMillis() - timer));
    System.err.println(" msec =====");
    System.err.flush();
  };
}
