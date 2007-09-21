/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Date;

import edu.anu.thylacine.graph.Err;
import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.graph.graphs.DriverQuery;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.relational.Tuple;
import edu.anu.thylacine.relational.aggregate.Max;

/**
 * Graph driver query that  selects the most recent date from the 'perf' table
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
public class MostRecent extends DriverQuery {

  /**
   * @param columns
   */
  public MostRecent() {
    super();
  }
  
  /* (non-Javadoc)
   * @see edu.anu.thylacine.graph.graphs.DriverQuery#exec(edu.anu.thylacine.relational.ColumnValue[])
   */
  @Override
  public Table exec(ColumnValue... b) throws NoDataException {
    Table table = Database.newTable(BINDINGS);
    
    Table perf = Database.the.getTable("perf");
    if (perf == null)
      Err.noData("perf");

    Table date = perf.project(PerfData.TIME).aggregate(null, new Max<Date>(PerfData.TIME,PerfData.TIME));
    
    // There should only be one tuple, with one 
    for (Tuple tup : date) {
      for (ColumnValue cv : tup.getColumnValues()) {
        table.insert(Database.bind(BINDINGS,new ColumnValue[]{cv}));
      }
    }
    return table;
  }


}
