/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.graphs.adapters.CategoryTableAdapter;
import edu.anu.thylacine.graph.graphs.adapters.GraphAdapter;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.Database;

/**
 * Query that produces a single number per benchmark per VM.  
 * Best graphed as a column chart.
 * 
 * The caller should provide a date.
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
public class PerfComparisonQuery extends PerfRatioQuery {
  
  private final Column<Double> stat;
  
  public PerfComparisonQuery(int iteration) {
    switch (iteration) {
    case 1: stat = PerfData.ITER1; break;
    case 2: stat = PerfData.ITER2; break;
    case 3: stat = PerfData.ITER3; break;
    default:
      Err.die("Unknown iteration %d",iteration);
      stat = null;
    }
  }

  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.Query#getAdapter(java.lang.Class)
   */
  @Override
  public <T extends Dataset> GraphAdapter<T> getAdapter(Class<T> resultClass) {
    if (CategoryDataset.class.isAssignableFrom(resultClass))
      return (GraphAdapter<T>)new CategoryTableAdapter<T>(resultClass,
          PerfData.JVM,             // Label
          Database.BENCHMARK,       // Category 
          stat);          // Value
    else {
      Err.die("Cannot produce a %s from a PerfComparisonQuery", resultClass.toString());
      return null;
    }
  }

}
