/*
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
import java.lang.reflect.Method;

/**
 * @date $Date$
 * @id $Id$
 */
public class MMTkHarness {
  private static final boolean verbose = false;
  private final String harnessClassName = "org.mmtk.plan.Plan";

  private Method beginMethod, endMethod;

  /**
   * Locate the class where the harness resides in various versions of JikesRVM,
   * and then retain Method references to the correct one.
   */
  public MMTkHarness() {
    boolean found = false;
    try {
      Class harnessClass = Class.forName(harnessClassName);
      beginMethod = harnessClass.getMethod("harnessBegin"); // Just check for the method
      endMethod = harnessClass.getMethod("harnessEnd");     // Just check for the method
      found = true;
    } catch (ClassNotFoundException c) {
      if (verbose)
        System.err.println("Could not locate " + harnessClassName);
    } catch (SecurityException e) {
      if (verbose)
        System.err.println("harness method of " + harnessClassName + " is not accessible");
    } catch (NoSuchMethodException e) {
      if (verbose)
        System.err.println("harness method of " + harnessClassName + " not found");
    }
    if (!found) {
      throw new RuntimeException("Unable to find MMTk Harness in any known parent class");
    }
  }

  public void harnessBegin() {
    try {
      beginMethod.invoke(null);
    } catch (Exception e) {
      throw new RuntimeException("Error running MMTk harnessBegin", e);
    }
  }

  public void harnessEnd() {
    try {
      endMethod.invoke(null);
    } catch (Exception e) {
      throw new RuntimeException("Error running MMTk harnessEnd", e);
    }
  }
}
