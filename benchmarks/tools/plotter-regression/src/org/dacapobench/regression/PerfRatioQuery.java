/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import edu.anu.thylacine.graph.Benchmark;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Schema;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.relational.aggregate.GeoMean;
import edu.anu.thylacine.relational.def.DefaultSchema;
import edu.anu.thylacine.relational.def.DefaultTable;
import edu.anu.thylacine.relational.def.QuotientColumn;
import edu.anu.thylacine.util.MyHashSet;

/**
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: PerfRatioQuery.java,v 1.1 2006/11/20 09:06:40 robing Exp $
 *
 */
public class PerfRatioQuery extends PerfHistoryQuery {

  public static QuotientColumn ITER1_RATIO = new QuotientColumn("ITER1",
      ReferenceTimes.time,PerfData.ITER1);
  public static QuotientColumn ITER2_RATIO = new QuotientColumn("ITER2",
      ReferenceTimes.time,PerfData.ITER2);
  public static QuotientColumn ITER3_RATIO = new QuotientColumn("ITER3",
      ReferenceTimes.time,PerfData.ITER3);
    
  public static final Column<Double> ITER1_MEAN = Database.newColumn("ITER1",Double.class);
  public static final Column<Double> ITER2_MEAN = Database.newColumn("ITER2",Double.class);
  public static final Column<Double> ITER3_MEAN = Database.newColumn("ITER3",Double.class);

  private static Column[] columns = new Column[] {
    Database.BENCHMARK,
    PerfData.JVM,
    PerfData.TIME,
    ITER1_RATIO,
    ITER2_RATIO,
    ITER3_RATIO,
  };
  
  private static final Table meanBenchmark = createMeanBenchmarkTable();

  private static Table createMeanBenchmarkTable() {
    Schema schema = new DefaultSchema(Database.BENCHMARK);
    Table result = new DefaultTable(schema);
    result.insert(Database.bind(Database.BENCHMARK,Benchmark.geomean));
    return result;
  }

  public Set<String> baseTables() {
    return new MyHashSet<String>("reference").union(super.baseTables());
  }
  
  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#exec(edu.anu.thylacine.relational.ColumnValue[])
   * 
   */
  public Table exec(ColumnValue... bindings) throws NoDataException {
    Table perf = super.exec();
    Table ref = Database.the.getTable("reference");
    
    Table perfNorm = perf.join(ref).append(ITER1_RATIO,ITER2_RATIO,ITER3_RATIO);

    Table result = perfNorm.project(columns);

    Table means = result.aggregate(
        new MyHashSet<Column>(new Column[]{PerfData.TIME,PerfData.JVM}),          // Group-by 
        new GeoMean<Double>(ITER1_RATIO,ITER1_MEAN),
        new GeoMean<Double>(ITER2_RATIO,ITER2_MEAN),
        new GeoMean<Double>(ITER3_RATIO,ITER3_MEAN)).join(meanBenchmark);

    Map<Column,Column> renames = new HashMap<Column,Column>();
    renames.put(ITER1_MEAN,ITER1_RATIO);
    renames.put(ITER2_MEAN,ITER2_RATIO);
    renames.put(ITER3_MEAN,ITER3_RATIO);
    means = means.rename(renames).project(columns);

    return result.union(means).select(bindings);
  }

}
