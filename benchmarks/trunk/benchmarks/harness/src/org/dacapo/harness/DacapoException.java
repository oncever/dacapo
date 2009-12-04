/*******************************************************************************
 * Copyright (c) 2006, 2009 The Australian National University.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 *
 * @date $Date$
 * @id $Id$
 *******************************************************************************/
package org.dacapo.harness;

/**
 * Exception class for local error conditions.
 * 
 * @author Robin Garner
 * @date $Date$
 * @id $Id$
 * 
 */
public class DacapoException extends Exception {
  public DacapoException(String text) {
    super(text);
  }

  public DacapoException(Exception e) {
    super(e);
  }

  private static final long serialVersionUID = 3726765834685635667l;
}
