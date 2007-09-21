/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import edu.anu.thylacine.graph.Benchmark;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.relational.TableColumn;

/**
 * Defines and populates a reference tables of times to normalise
 * benchmark scores against.
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/20 09:06:40 $
 * @id $Id: ReferenceTimes.java,v 1.1 2006/11/20 09:06:40 robing Exp $
 *
 */
public class Iterations {
  private static final String COLUMN_NAME = "ITER";
  private static final String TABLE_NAME = "iterations";
  
  public static final Column<Integer> ITER = Database.newColumn(COLUMN_NAME, Integer.class);
  public static final TableColumn ITER_TABLECOLUMN = new TableColumn(TABLE_NAME,COLUMN_NAME);

  /**
   * Create a table with benchmark iterations
   */
  public static void createIterationsTable() {
    Table iter = Database.newTable(Iterations.ITER);
    iter.insert(Database.bind(ITER,1));
    iter.insert(Database.bind(ITER,2));
    iter.insert(Database.bind(ITER,3));
    Database.the.putTable(TABLE_NAME, iter);
  }
}
