package dacapo.jython;

import java.io.File;

import org.python.util.jython;
import org.python.core.Py;

import dacapo.Benchmark;
import dacapo.parser.Config;


public class JythonHarness extends Benchmark {
  
  public JythonHarness(Config config, File scratch) throws Exception {
    super(config, scratch);
  }
  
  protected void prepare() throws Exception {
    super.prepare();
    jython.main(new String[] { cacheDir(), fileInScratch("jython/noop.py")} );
  }

  /**
   * jython.main doesn't expect to be called multiple times, so we've hacked
   * Py.setArgv to allow us to reset the command line arguments that the python
   * script sees.  Hence the Py.setArgv call, followed by the jython.main call.
   */
  public void iterate(String arg) throws Exception {
    int size = 2;
    if (arg.equals("small")) {
      size = 1;
    } else if (arg.equals("large")) {
      size = 3;
    }

    int multiplier = (size == 3) ? 5 : 1;
    Py.setArgv(fileInScratch("jython/heapsort.py"),
            new String[] { Integer.toString(200*multiplier) });
    jython.main(new String[] {cacheDir(), 
            fileInScratch("jython/heapsort.py"), 
            Integer.toString(200*multiplier) } );

    if (size == 1) return;

    Py.setArgv(fileInScratch("jython/sieve.py"),
            new String[] { Integer.toString(150*multiplier)} );
    jython.main(new String[] {cacheDir(),
            fileInScratch("jython/sieve.py"),
            Integer.toString(150*multiplier)} );

    Py.setArgv(fileInScratch("jython/sieve.py"), 
            new String[] {Integer.toString(100*multiplier)});
    jython.main(new String[] {cacheDir(), 
            fileInScratch("jython/sieve.py"), 
            Integer.toString(100*multiplier) });

    Py.setArgv(fileInScratch("jython/objinst.py"), new String[] {Integer.toString(3500*multiplier)});
    jython.main(new String[] {cacheDir(), 
            fileInScratch("jython/objinst.py"), 
            Integer.toString(3500*multiplier)});
  }

  /**
   * @return
   */
  private String cacheDir() {
    return "-Dpython.cachedir="+fileInScratch("cachedir");
  }
  
  public void cleanup() {
    super.cleanup();
    deleteTree(new File(scratch,"cachedir"));
  }
}
