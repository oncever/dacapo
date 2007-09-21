/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import edu.anu.thylacine.graph.Benchmark;
import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.graph.graphs.adapters.GraphAdapter;
import edu.anu.thylacine.graph.graphs.adapters.TimeSeriesTableAdapter;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.relational.Tuple;

/**
 * Mean across all benchmarks of performance
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: PerfMeanQuery.java,v 1.1 2006/11/20 09:06:40 robing Exp $
 *
 */
public class PerfMeanQuery extends PerfRatioQuery {
  
  private static Column[] resultColumns = new Column[] {
    PerfData.JVM,
    PerfData.TIME,
    RATIO,
  };


  /* (non-Javadoc)
   * @see org.dacapobench.regression.PerfHistoryQuery#exec(edu.anu.thylacine.relational.ColumnValue[])
   */
  @Override
  public Table exec(ColumnValue... bindings) throws NoDataException {
    Table result = super.exec().select(
          Database.bind(Database.BENCHMARK,Benchmark.geomean));
    Table finalResult = result.select(bindings).project(resultColumns);
    //finalResult.orderBy(PerfData.TIME).print();
    return finalResult;
  }

  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#getAdapter(java.lang.Class)
   */
  public <T extends Dataset> GraphAdapter<T> getAdapter(Class<T> resultClass) {
    if (XYDataset.class.isAssignableFrom(resultClass))
      return (GraphAdapter<T>)new TimeSeriesTableAdapter<T>(resultClass,
          PerfData.TIME,
          PerfData.JVM);
    else {
      Err.die("Cannot produce a %s from a PerfMeanQuery", resultClass.toString());
      return null;
    }
  }

}
