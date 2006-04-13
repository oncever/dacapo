package dacapo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Each DaCapo benchmark is represented by an instance of this
 * abstract class.  It defines the methods that the benchmark harness
 * calls during the running of the benchmark.
 * 
 * @author Robin Garner
 *
 */
public abstract class Benchmark {
  private static final int BUFFER = 2048;
  protected static final boolean verbose = true;
  protected final File scratch;
  protected final String name;
  
  protected static ValidatingPrintStream out = new ValidatingPrintStream(System.out);
  protected static ValidatingPrintStream err = new ValidatingPrintStream(System.err);
  
  private static PrintStream savedOut = System.out;
  private static PrintStream savedErr = System.err;
  
  /**
   * When an instance of a Benchmark is created, it is expected to prepare 
   * its scratch directory, unloading files from the jar file if required.
   * 
   * @param scratch Scratch directory
   */
  public Benchmark(String name, File scratch) throws Exception {
    this.scratch = scratch;
    this.name = name;
    prepare();
  }
  
  /**
   * Perform pre-benchmark preparation.  By default it unpacks the zip file
   * data/<code>name</code>.zip into the scratch directory.
   */
  protected void prepare() throws Exception {
    unpackZipFileResource("data/"+name+".zip", scratch);
  }
  
  /**
   * Perform post-benchmark cleanup, deleting output files etc.
   * By default it deletes a subdirectory of the scratch directory with
   * the same name as the benchmark.
   */
  public void cleanup() {
    deleteTree(new File(scratch,name));
  }
  
  /**
   * Perform validation of output
   * 
   * @return true if the output was correct
   */
  public abstract boolean validate();
  
  /**
   * Per-iteration preparation, outside the timing loop.
   * Arguments are expected to be the same as are passed to the
   * subsequent iteration() pass
   * 
   * @param args Arguments to the benchmark
   */
  public void preIteration(String [] args) {
    System.setOut(out);
    System.setErr(err);
    out.reset();
    err.reset();
  }
  
  /**
   * An actual iteration of the benchmark.  This is what is
   * timed.
   * 
   * @param args Arguments to the benchmark
   */
  public abstract void iterate(String [] args) throws Exception;
  
  /**
   * Per-iteration cleanup, outside the timing loop.  Args
   * should be the same as the immediately preceding call to
   * 'iteration()'
   * 
   * @param args Arguments to the benchmark
   */
  public void postIteration(String [] args) {
    System.setOut(savedOut);
    System.setErr(savedErr);
    System.out.println("Output lines "+out.getLineCount()+", error lines "+err.getLineCount());
    System.out.println("Output writes "+out.writeCount+", error writes "+err.writeCount);
    System.out.println("Output bytes "+out.byteCount+", error bytes "+err.byteCount);
  }
  
  /*************************************************************************************
   *  Utility methods
   */
  
  public static void copyFileTo(File inputFile, File outputDir) throws IOException {
    copyFile(inputFile,new File(outputDir,inputFile.getName()));
  }
  
  public static void copyFile(File inputFile, File outputFile) throws IOException {
    FileInputStream input = new FileInputStream(inputFile);
    FileOutputStream output = new FileOutputStream(outputFile);
    while (true) {
      byte[] buffer = new byte[BUFFER];
      int read = input.read(buffer);
      if (read == -1) break;
      output.write(buffer, 0, read);
    }
    input.close();
    output.flush();
    output.close();
  }
  
  /**
   * @param fn
   * @return
   */
  public static URL getURL(String fn) {
    ClassLoader cl = Benchmark.class.getClassLoader();
    if (verbose)
      System.out.println("Util.getURL: returns "+cl.getResource(fn));
    return cl.getResource(fn);    
  }
  
  public String fileInScratch(String name) {
    return (new File(scratch,name)).getPath();
  }

  /**
   * Unpack a zip file resource into the specified directory.  The directory structure
   * of the zip archive is preserved.
   * 
   * @param name
   * @param destination
   * @throws IOException
   */
  public static void unpackZipFileResource(String name, File destination) throws Exception {
    URL resource = getURL(name);
    if (resource == null)
      throw new DacapoException("No such zip file, "+name);
    
    ZipInputStream input = new ZipInputStream(new BufferedInputStream(resource.openStream()));
    ZipEntry entry;
    while((entry = input.getNextEntry()) != null) {
      if (verbose)
        System.out.println("Unpacking "+entry.getName());
      File file = new File(destination,entry.getName());
      if (entry.isDirectory()) {
        if (!file.exists())
          file.mkdir();
      } else {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = input.read(data, 0, BUFFER)) != -1) {
          //System.out.write(x);
          dest.write(data, 0, count);
        }
        dest.flush();
        dest.close();
      }
    }
    input.close();
  }

  public static void deleteTree(File tree) {
    if (verbose) 
      System.out.println("Deleting "+tree.getName());
    if (!tree.isDirectory())
      tree.delete();
    else {
      File[] files = tree.listFiles();
      for (int i=0; i < files.length; i++)
        deleteTree(files[i]);
      tree.delete();
    }
  }
  
  public static void deleteFile(File file) {
    if (verbose) 
      System.out.println("Deleting "+file.getName());
    if (file.exists() && !file.isDirectory())
      file.delete();
  }
  
  public static void deleteFiles(File dir, final String pattern) {
    FilenameFilter filter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.matches(pattern);
      }
    };
    File[] files = dir.listFiles(filter);
    for (int i=0; i < files.length; i++) {
      deleteFile(files[i]);
    }
  }
}
