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
  
  public static final Column<Integer> ITER = Database.newColumn("ITER", Integer.class);
  public static final Column<Double> RATIO = Database.newColumn("PERF", Double.class);

  private static Column[] inColumns = new Column[] {
    PerfData.JVM,
    PerfData.TIME,
    ITER,
    RATIO,
  };
  
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
    Table table = super.exec().select(
          Database.bind(Database.BENCHMARK,Benchmark.geomean));
    Table result = Database.newTable(inColumns);
    for (Tuple tuple : table) {
      result.insert(
          Database.bind(PerfData.JVM,PerfData.JVM.getValue(tuple)),
          Database.bind(PerfData.TIME, PerfData.TIME.getValue(tuple)),
          Database.bind(ITER,1),
          Database.bind(RATIO,PerfRatioQuery.ITER1_RATIO.getValue(tuple)));
      result.insert(
          Database.bind(PerfData.JVM,PerfData.JVM.getValue(tuple)),
          Database.bind(PerfData.TIME, PerfData.TIME.getValue(tuple)),
          Database.bind(ITER,2),
          Database.bind(RATIO,PerfRatioQuery.ITER2_RATIO.getValue(tuple)));
      result.insert(
          Database.bind(PerfData.JVM,PerfData.JVM.getValue(tuple)),
          Database.bind(PerfData.TIME, PerfData.TIME.getValue(tuple)),
          Database.bind(ITER,3),
          Database.bind(RATIO,PerfRatioQuery.ITER3_RATIO.getValue(tuple)));
    }
    return result.select(bindings).project(resultColumns);
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
      Err.die("Cannot produce a %s from a PerfHistoryQuery", resultClass.toString());
      return null;
    }
  }

}
