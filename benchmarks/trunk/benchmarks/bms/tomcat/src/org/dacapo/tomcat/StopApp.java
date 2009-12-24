/*
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0.
 * You may obtain the license at
 * 
 *    http://www.opensource.org/licenses/apache2.0.php
 */
package org.dacapo.tomcat;

/**
 * Request tomcat to stop serving the named application
 * 
 * @date $Date$
 * @id $Id$
 */
public class StopApp extends HttpGet {

  /**
   * @param path The path to the webapp to stop
   */
  public StopApp(String path) {
    super("/manager/stop?path=" + path, 200, null);
  }
}
