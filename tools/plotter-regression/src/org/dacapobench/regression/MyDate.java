package org.dacapobench.regression;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A date that 'toString's with a specific format
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
class MyDate extends Date {
  private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
  public MyDate(long time) { super(time); }
  public String toString() {
    return fmt.format(this);
  }
  private static final long serialVersionUID = 0;
}
