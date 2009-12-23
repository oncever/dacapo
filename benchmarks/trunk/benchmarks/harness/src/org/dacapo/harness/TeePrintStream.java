/*
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
package org.dacapo.harness;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @date $Date: 2009-12-04 14:33:59 +1100 (Fri, 04 Dec 2009) $
 * @id $Id: Slice.java 659 2009-12-04 03:33:59Z jzigman $
 */
public class TeePrintStream extends PrintStream {

  /**
   * @param dest The destination stream (around which this class wraps)
   * @param logFile Log a copy to this file.
   */
  public TeePrintStream(OutputStream dest, File logFile) {
    super(new TeeOutputStream(dest, logFile));
  }

  public void enableOutput(boolean enable) {
    ((TeeOutputStream) out).enableOutput(enable);
  }

  /**
   * Start a new log file, creating an archived version of the current one.
   * 
   */
  public void version() {
    ((TeeOutputStream) out).version();
  }

  public void openLog() {
    ((TeeOutputStream) out).openLog();
  }

  public void closeLog() {
    ((TeeOutputStream) out).closeLog();
  }
}
