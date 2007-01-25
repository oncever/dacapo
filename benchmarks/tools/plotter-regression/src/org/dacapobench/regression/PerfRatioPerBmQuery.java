/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;

/**
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
public class PerfRatioPerBmQuery extends PerfRatioQuery {

  private static Column[] columns = new Column[] {
    PerfData.JVM,
    PerfData.TIME,
    ITER1_RATIO,
    ITER2_RATIO,
    ITER3_RATIO,
  };
  
  /**
   * 
   */
  public PerfRatioPerBmQuery() {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see org.dacapobench.regression.PerfRatioQuery#exec(edu.anu.thylacine.relational.ColumnValue[])
   */
  @Override
  public Table exec(ColumnValue... bindings) throws NoDataException {
    boolean hasBenchmark = false;
    for (ColumnValue value : bindings) {
      hasBenchmark |= value.getColumn().equals(Database.BENCHMARK);
    }
    if (!hasBenchmark)
      Err.die("A PerfRatioPerBmQuery will return erroneous results if a benchmark isn't specified");
    
    Table table = super.exec(bindings);
    return table.project(columns);
  }

  
}
