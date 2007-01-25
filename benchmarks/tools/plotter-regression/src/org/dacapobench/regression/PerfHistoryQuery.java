/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Set;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.graph.graphs.Query;
import edu.anu.thylacine.graph.graphs.adapters.GraphAdapter;
import edu.anu.thylacine.graph.graphs.adapters.TimeSeriesTableAdapter;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.util.MyHashSet;

/**
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: PerfHistoryQuery.java,v 1.2 2006/11/20 09:06:40 robing Exp $
 *
 */
public class PerfHistoryQuery implements Query {
  
  private static Column[] columns = new Column[] {
    Database.BENCHMARK,
    PerfData.JVM,
    PerfData.TIME,
    PerfData.ITER1,
    PerfData.ITER2,
    PerfData.ITER3,
  };
  
  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#baseTables()
   */
  public Set<String> baseTables() {
    return new MyHashSet<String>("perf");
  }
  
  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#exec(edu.anu.thylacine.relational.ColumnValue[])
   * 
   */
  public Table exec(ColumnValue... bindings) throws NoDataException {
    Table perf = Database.the.getTable("perf");
    if (perf == null)
      Err.noData("perf");
    Table benchmarks = Database.the.getTable("benchmarks");
    
    return perf.join(benchmarks).select(bindings).project(columns);
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
