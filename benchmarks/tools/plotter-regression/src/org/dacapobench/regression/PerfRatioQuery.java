/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Arrays;
import java.util.HashSet;
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

  public static QuotientColumn RATIO = new QuotientColumn("RATIO",
      ReferenceTimes.time,ELAPSED);
    
  public static final Column<Double> MEAN = Database.newColumn("MEAN",Double.class);

  private static Column[] columns = new Column[] {
    Database.BENCHMARK,
    PerfData.JVM,
    PerfData.TIME,
    Iterations.ITER,
    RATIO,
  };
  
  private static final Table meanBenchmark = createMeanBenchmarkTable();
  private static final Table meanJVM = createMeanJVMTable();

  private static Table createMeanBenchmarkTable() {
    Schema schema = new DefaultSchema(Database.BENCHMARK);
    Table result = new DefaultTable(schema);
    result.insert(Database.bind(Database.BENCHMARK,Benchmark.geomean));
    return result;
  }

  private static Table createMeanJVMTable() {
    Schema schema = new DefaultSchema(PerfData.JVM);
    Table result = new DefaultTable(schema);
    result.insert(Database.bind(PerfData.JVM,"geomean"));
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
    long start = now();
    Table perf = super.exec();
    if (PROFILE) System.out.printf("PerfRatioQuery (%4.2f): Super-query\n", elapsed(start));
    Table ref = Database.the.getTable("reference");
    
    Table perfNorm = perf.join(ref).append(RATIO);
    if (PROFILE) System.out.printf("PerfRatioQuery (%4.2f): append ratio\n", elapsed(start));

    Table result = perfNorm.project(columns);
    if (PROFILE) System.out.printf("PerfRatioQuery (%4.2f): project\n", elapsed(start));

    Table meanByJVM = result.aggregate(
        new HashSet<Column>(Arrays.asList(PerfData.TIME,PerfData.JVM,Iterations.ITER)),  // Group-by 
        new GeoMean<Double>(RATIO,MEAN)).join(meanBenchmark);
    if (PROFILE) System.out.printf("PerfRatioQuery (%4.2f): aggregate and form means\n", elapsed(start));

    Map<Column,Column> renames = new HashMap<Column,Column>();
    renames.put(MEAN,RATIO);
    meanByJVM = meanByJVM.rename(renames).project(columns);

    Table finalResult = result.union(meanByJVM);
    return finalResult.select(bindings);
  }

}
