/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.graph.graphs.adapters.CategoryTableAdapter;
import edu.anu.thylacine.graph.graphs.adapters.GraphAdapter;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;

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
  
  private final int iteration;
  
  public PerfComparisonQuery(int iteration) {
    this.iteration = iteration;
  }
  
  

  @Override
  public Table exec(ColumnValue... bindings) throws NoDataException {
    Table base = super.exec(bindings);
    
    return base.select(Database.bind(Iterations.ITER, iteration));
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
          RATIO);          // Value
    else {
      Err.die("Cannot produce a %s from a PerfComparisonQuery", resultClass.toString());
      return null;
    }
  }

}
