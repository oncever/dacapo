/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
  
  protected final boolean PROFILE = false;
  
  public static final Column<Double> ELAPSED = Database.newColumn("elapsed",Double.class);
  
  private static Column[] columns = new Column[] {
    Database.BENCHMARK,
    PerfData.JVM,
    PerfData.TIME,
    Iterations.ITER,
    ELAPSED,
  };
  
  private static Column[] iter1Columns = new Column[] {
    Database.BENCHMARK, PerfData.JVM, PerfData.TIME, PerfData.ITER1,
  };
  private static Column[] iter2Columns = new Column[] {
    Database.BENCHMARK, PerfData.JVM, PerfData.TIME, PerfData.ITER2,
  };
  private static Column[] iter3Columns = new Column[] {
    Database.BENCHMARK, PerfData.JVM, PerfData.TIME, PerfData.ITER3,
  };
  
  private static Map<Column,Column> iter1Map = new TreeMap<Column,Column>();
  private static Map<Column,Column> iter2Map = new TreeMap<Column,Column>();
  private static Map<Column,Column> iter3Map = new TreeMap<Column,Column>();
  
  static {
    iter1Map.put(PerfData.ITER1,ELAPSED);
    iter2Map.put(PerfData.ITER2,ELAPSED);
    iter3Map.put(PerfData.ITER3,ELAPSED);
  }
  
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
    
    Table iterations = Database.the.getTable("iterations");
    
    if (PROFILE) System.out.println("PerfHistoryQuery:");
    long start = now();
    Table raw = perf.join(benchmarks);
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): initial join\n",elapsed(start));
    
    Table result = Database.newTable(columns);
    
    Table iteration1 = iterations.select(Database.bind(Iterations.ITER, 1));
    Table iteration2 = iterations.select(Database.bind(Iterations.ITER, 2));
    Table iteration3 = iterations.select(Database.bind(Iterations.ITER, 3));
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): created iteration tables\n",elapsed(start));
    result = result.union(raw.project(iter1Columns).join(iteration1).rename(iter1Map));
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): insert 1st iteration\n",elapsed(start));
    result = result.union(raw.project(iter2Columns).join(iteration2).rename(iter2Map));
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): insert 2nd iteration\n",elapsed(start));
    result = result.union(raw.project(iter3Columns).join(iteration3).rename(iter3Map));
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): insert 3rd iteration\n",elapsed(start));
    
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): renamed columns\n",elapsed(start));
    Table last = result.select(bindings);
    if (PROFILE) System.out.printf("PerfHistoryQuery (%4.2f): selected by bindings\n",elapsed(start));
    return last;
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
  
  protected long now() {
    return System.nanoTime();
  }
  
  protected double elapsed(long since) {
    return ((double)(now() - since))/1.0E9;
  }
}
