/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2005
 */
import dacapo.Callback;

import com.ibm.JikesRVM.memoryManagers.mmInterface.MM_Interface;

public class MMTkCallback extends Callback {
  /* Immediatly prior to start of the benchmark */
  public void start(String benchmark) {
    super.start(benchmark);
    MM_Interface.harnessBegin();
  };
  /* Immediatly after the end of the benchmark */
  public void stop() {
    MM_Interface.harnessEnd();
    super.stop();
  }
}
