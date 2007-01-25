/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import edu.anu.thylacine.graph.Benchmark;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;

/**
 * Defines and populates a reference tables of times to normalise
 * benchmark scores against.
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: ReferenceTimes.java,v 1.1 2006/11/20 09:06:40 robing Exp $
 *
 */
public class ReferenceTimes {
  public static final Column<Double> time = Database.newColumn("time", Double.class);

  /**
   * Create a table with reference times to normalise benchmark times against
   *
   * These are the best scores across all iterations, all benchmarks , all available VMs,
   * on vole (Pentium D, 2.8GHz) 18/11/2006
   */
  public static void createReferenceTable() {
    Table ref = Database.newTable(Database.BENCHMARK,time);
    addScore(ref, "antlr", 2640);
    addScore(ref, "bloat", 8576);
    addScore(ref, "chart", 7813);
    addScore(ref, "eclipse", 38913);
    addScore(ref, "fop", 2609);
    addScore(ref, "hsqldb", 4384);
    addScore(ref, "jython", 5870);
    addScore(ref, "luindex", 10556);
    addScore(ref, "lusearch", 5493);
    addScore(ref, "pmd", 6302);
    addScore(ref, "xalan", 6070);
    Database.the.putTable("reference", ref);
  }

  /**
   * @param ref
   * @param benchmark TODO
   * @param refTime TODO
   */
  private static void addScore(Table ref, String benchmark, double refTime) {
    ref.insert(Database.bind(Database.BENCHMARK,Benchmark.valueOf(benchmark)),
               Database.bind(time,refTime));
  }


}
