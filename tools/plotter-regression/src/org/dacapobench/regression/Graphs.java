/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.io.File;
import java.util.Set;

import org.jfree.chart.plot.PlotOrientation;

import edu.anu.thylacine.graph.graphs.*;
import edu.anu.thylacine.graph.output.OutputFormat;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.relational.TableColumn;

/**
 * The set of graphs produced for the DaCapo nightly regressions
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: Graphs.java,v 1.3 2006/11/20 09:06:40 robing Exp $
 *
 */
public class Graphs extends edu.anu.thylacine.graph.Graphs {
  
  private static final TableColumn BENCHMARKS = Database.columnName("perf","benchmark");
  private static final TableColumn JVMS = Database.columnName("perf","JVM");
  
  /**
   * Driver query to graph all available benchmarks with measurement data.
   */
  static final DriverQuery perBenchmark = new DriverQuery(BENCHMARKS); 
  static final DriverQuery today = new MostRecent();
  
  static final DriverQuery iterations = 
    new DriverQuery(Iterations.ITER_TABLECOLUMN);
  
  static final DriverQuery perIterationPerBenchmark = 
    new DriverQuery(BENCHMARKS,Iterations.ITER_TABLECOLUMN);
  static final DriverQuery perIterationPerJVM = 
    new DriverQuery(JVMS,Iterations.ITER_TABLECOLUMN);
  
  static final Query perfHistoryByBm = new PerfRatioPerBm1IterQuery();
  static final Query perfHistoryByJVM = new PerfRatioPerVm1IterQuery();
  static final Query perfMeans = new PerfMeanQuery();
  static final Query iter1Comparison = new PerfComparisonQuery(1);
  static final Query iter2Comparison = new PerfComparisonQuery(2);
  static final Query iter3Comparison = new PerfComparisonQuery(3);
  
  public JFreeGraph perfHistoryGraph(DriverQuery driver, Query query) {
    return new TimeSeriesGraph(
        driver,                                // Query that generates the family of graphs
        query,                                 // The query for the given graph
        "perf",                                // Stem for the filename
        null,                                  // Graph title
        null,                                  // X axis label
        "Relative performance",                // Y axis label
        640,480)                               // Width, height
        .setColourer(new PerfLineColour(1))
        .setLegend()
        .stretchY(0,1.25);
  }
  
  public JFreeGraph perfComparisonGraph(DriverQuery driver, int iteration) {
    return new BarGraph(
        driver,                                  // Driver query
        new PerfComparisonQuery(iteration),      // Data query
        "iter"+iteration,                        // Filename stem
        "Iteration "+iteration,                  // Title
        "Benchmark",                             // X axis label
        "Relative performance",                  // Y Axis label
        640,                                     // Width
        640,                                     // Height
        PlotOrientation.HORIZONTAL)              // Orientation
        .unStacked(0.0)
        .setColourer(new PerfColumnColour())
        .setLegend();
  }
  
  public JFreeGraph perfHistoryMeanGraph(DriverQuery driver, Query query) {
    return new TimeSeriesGraph(
        driver,                                // Query that generates the family of graphs
        query,                                 // The query for the given graph
        "perf",                                // Stem for the filename
        null,                                  // Graph title
        null,                                  // X axis label
        "Relative performance",                // Y axis label
        640,480)                               // Width, height
        .setColourer(new PerfLineColour(1))
        .setLegend()
        .stretchY(0,1.25);
  }
  
  //public final JFreeGraph PERFORMANCE = perfHistoryGraph(perBenchmark,perfHistoryByBm);
  public final JFreeGraph BY_BM = perfHistoryGraph(perIterationPerBenchmark,perfHistoryByBm);
  public final JFreeGraph BY_JVM = perfHistoryGraph(perIterationPerJVM,perfHistoryByJVM);
  public final JFreeGraph GEOMEAN = perfHistoryMeanGraph(iterations,perfMeans);
  public final JFreeGraph COMP1 = perfComparisonGraph(today,1);
  public final JFreeGraph COMP2 = perfComparisonGraph(today,2);
  public final JFreeGraph COMP3 = perfComparisonGraph(today,3);
  
  /**
   * 
   */
  public Graphs() {
    graphSet("BY_BM", new GraphSet(BY_BM));
    graphSet("BY_JVM", new GraphSet(BY_JVM));
    //graphSet("PERFORMANCE", new GraphSet(PERFORMANCE));
    graphSet("MEAN", new GraphSet(GEOMEAN));
    graphSet("COMP", new GraphSet(COMP1,COMP2,COMP3));
    graphSet("COMP1", new GraphSet(COMP1));
  }
  
  @Override
  public void preGraphs(File dir, Set<OutputFormat> formats) {
    ReferenceTimes.createReferenceTable();
    Iterations.createIterationsTable();
  }
}
