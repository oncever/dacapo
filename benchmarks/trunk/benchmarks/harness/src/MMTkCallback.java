/*
 * Copyright (c) 2005, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
import org.dacapo.harness.Callback;
import org.dacapo.harness.CommandLineArgs;

/**
 * @date $Date$
 * @id $Id$
 */
public class MMTkCallback extends Callback {

  private final MMTkHarness harness = new MMTkHarness();

  public MMTkCallback(CommandLineArgs args) {
    super(args);
  }

  /* Immediately prior to start of the benchmark */
  public void start(String benchmark) {
    if (!isWarmup())
      harness.harnessBegin();
    super.start(benchmark);
  };

  /* Immediately after the end of the benchmark */
  public void stop() {
    super.stop();
    if (!isWarmup())
      harness.harnessEnd();
  }
}
