package org.dacapo.harness;

import java.io.File;
import java.lang.reflect.Constructor;

import org.dacapo.harness.Benchmark;
import org.dacapo.parser.Config;

/**
 * Benchmark harness for the Tomcat benchmark
 *
 * @author Robin Garner
 * @date $Date: 2008-07-27 12:53:06 +1000 (Sun, 27 Jul 2008) $
 * @id $Id: $
 *
 */
public class Tomcat extends Benchmark {

  private final Class<?> clazz;
  private final Constructor<Runnable> clientConstructor;
  private final Object controller;

  @SuppressWarnings("unchecked")
  public Tomcat(Config config, File scratch) throws Exception {
    super(config,scratch);
    this.clazz = Class.forName("org.dacapo.tomcat.Control", true, loader);
    this.method = clazz.getMethod("exec",String.class);
    
    /* Create a constructor for the tomcat controller */
    Constructor<?> controlConstructor = clazz.getConstructor(File.class,ClassLoader.class,int.class);
    this.controller = controlConstructor.newInstance(scratch,loader,7080);
    
    /* Create a constructor for the tomcat client */
    Class<Runnable> clientClass = (Class<Runnable>)Class.forName("org.dacapo.tomcat.Client", true, loader);
    this.clientConstructor = clientClass.getConstructor(
        File.class, int.class, int.class, boolean.class);
  }

  /**
   * One-off setup
   */
  @Override
  public void prepare(String size) throws Exception {
    super.prepare(size);
    
    try {
      useBenchmarkClassLoader();
      try {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "info");
        System.setProperty("catalina.home", scratch.getAbsolutePath());
        System.setProperty("catalina.config", new File(fileInScratch("catalina.properties")).toURL().toExternalForm());
        method.invoke(controller,"prepare");
        
        System.out.println("Server thread created");
        
        // Run one iteration to get static startup costs out of the way
        System.out.println("Pre-benchmark warmup");
        method.invoke(controller, "startIteration");
        clientConstructor.newInstance(scratch,0,1,isVerbose()).run();
        method.invoke(controller, "stopIteration");
        System.out.println("Pre-benchmark warmup complete");
        postIterationCleanup(size);
      } finally {
        revertClassLoader();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  /**
   * After each iteration, delete the output files
   */
  public void postIteration(String size) throws Exception {
    super.postIteration(size);
  }

  /**
   * An iteration of the benchmark - runs in the benchmark classloader
   */
  public void iterate(String size) throws Exception {
    System.out.println("Loading web application");
    method.invoke(controller, "startIteration");
    
    final int threadCount = getThreadCount(config,size);
    String[] args = config.getArgs(size);
    final int iterations = Integer.parseInt(args[0]);
    
    /*
     * In case the # iterations doesn't evenly divide among the processors,
     * we ensure that some threads do one more iteration than others
     */
    final int iterationsPerClient = iterations / threadCount;
    final int oddIterations = iterations - (iterationsPerClient * threadCount);
    
    final Thread[] threads = new Thread[threadCount];
    System.out.println("Creating client threads");
    for (int i=0; i < threadCount; i++) {
      Runnable client = clientConstructor.newInstance(
          scratch,
          i,
          iterationsPerClient + (i < oddIterations ? 1 : 0),
          isVerbose());
      threads[i] = new Thread(client);
      threads[i].start();
    }
    System.out.println("Waiting for clients to complete");
    for (int i=0; i < threadCount; i++) {
      threads[i].join();
    }
    System.out.println("Client threads complete ... unloading web application");
    method.invoke(controller, "stopIteration");
  }
  
  @Override
  protected void postIterationCleanup(String size) {
    super.postIterationCleanup(size);
    /* Delete the tomcat cache */
    deleteTree(new File(scratch,"work"));
  }


  @Override
  public void cleanup() {
    try {
      method.invoke(controller,"cleanup");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println("Server stopped ... iteration complete");
    super.cleanup();
  }


  
  @SuppressWarnings("unused")
  private void dumpThreads() {
    ThreadGroup tg = Thread.currentThread().getThreadGroup();
    int nThreads = tg.activeCount();
    Thread[] threads = new Thread[nThreads*2];
    nThreads = Thread.enumerate(threads);
    System.out.printf("==================== Dumping %d Threads: ====================%n",nThreads);
    System.out.flush();
    for (int i=0; i < nThreads; i++) {
      if (threads[i] != null) {
        System.out.print(threads[i].getName()+": ");
        StackTraceElement[] stack = threads[i].getStackTrace();
        for (int j=0; j < stack.length; j++) {
          for (int k=0; k < j; k++) System.out.print("  ");
          System.out.println(stack[j].getClassName()+"."+stack[j].getMethodName()+":"+stack[j].getLineNumber()+" <- ");
        }
      } else {
        System.out.print("null ");
      }
      System.out.flush();
    }
    System.out.println();
    System.out.flush();
    System.out.printf("==================== Thread Dump End ====================%n");
  }
  
  /**
   * Stub which exists <b>only</b> to facilitate whole program
   * static analysis on a per-benchmark basis.  See also the "split-deps"
   * ant build target, which is also provided to enable whole program
   * static analysis.
   */
  public static void main(String args[]) throws Exception {
    // create dummy harness and invoke with dummy arguments
      (new Tomcat(null, null)).run(null, "");
  }
}
