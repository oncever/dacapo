/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.graph.graphs.adapters.GraphAdapter;
import edu.anu.thylacine.graph.graphs.adapters.TimeSeriesTableAdapter;
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
public class PerfRatioPerVm1IterQuery extends PerfRatioQuery {

  private static Column[] columns = new Column[] {
    Database.BENCHMARK,
    PerfData.TIME,
    RATIO,
  };
  
  /**
   * 
   */
  public PerfRatioPerVm1IterQuery() {
  }

  /* (non-Javadoc)
   * @see org.dacapobench.regression.PerfRatioQuery#exec(edu.anu.thylacine.relational.ColumnValue[])
   */
  @Override
  public Table exec(ColumnValue... bindings) throws NoDataException {
    boolean hasBenchmark = false;
    boolean hasIter = false;
    for (ColumnValue value : bindings) {
      hasBenchmark |= value.getColumn().equals(PerfData.JVM);
      hasIter |= value.getColumn().equals(Iterations.ITER);
    }
    if (!hasBenchmark)
      Err.die("A PerfRatioPerVm1IterQuery will return erroneous results if a JVM isn't specified");
    if (!hasIter)
      Err.die("A PerfRatioPerVm1IterQuery will return erroneous results if an iteration isn't specified");
    
    Table table = super.exec(bindings);
    return table.project(columns);
  }

  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#getAdapter(java.lang.Class)
   */
  public <T extends Dataset> GraphAdapter<T> getAdapter(Class<T> resultClass) {
    if (XYDataset.class.isAssignableFrom(resultClass))
      return (GraphAdapter<T>)new TimeSeriesTableAdapter<T>(resultClass,
          PerfData.TIME, // X-axis column
          Database.BENCHMARK); // Category column
    else {
      Err.die("Cannot produce a %s from a PerfHistoryQuery", resultClass.toString());
      return null;
    }
  }

}
