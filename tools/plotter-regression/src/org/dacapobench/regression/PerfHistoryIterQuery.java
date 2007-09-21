/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.util.Map;

import edu.anu.thylacine.graph.NoDataException;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.ColumnValue;
import edu.anu.thylacine.relational.Database;
import edu.anu.thylacine.relational.Table;
import edu.anu.thylacine.util.MyHashMap;

/**
 * Performance history of a single iteration statistic
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/17 08:07:13 $
 * @id $Id: PerfHistoryIterQuery.java,v 1.1 2006/11/17 08:07:13 robing Exp $
 *
 */
public class PerfHistoryIterQuery extends PerfHistoryQuery {
  public static final Column<Double> ELAPSED = Database.newColumn("elapsed", Double.class);
  
  private final Column[] columns;
  
  private final Column<Double> stat;

  private final Map<Column,Column> renames = new MyHashMap<Column,Column>();
  
  public PerfHistoryIterQuery(int iteration) {
    switch (iteration) {
    case 1: stat = PerfData.ITER1; break;
    case 2: stat = PerfData.ITER2; break; 
    case 3: stat = PerfData.ITER3; break;
    default:
      stat = null;
    }
    columns = new Column[] {
        Database.BENCHMARK,
        PerfData.JVM,
        PerfData.TIME,
        stat
      };
    renames.put(stat,ELAPSED);
  }

  /* (non-Javadoc)
   * @see org.dacapobench.regression.PerfHistoryQuery#exec(edu.anu.thylacine.relational.ColumnValue[])
   */
  @Override
  public Table exec(ColumnValue... bindings) throws NoDataException {
    return super.exec(bindings).project(columns).rename(renames);
  }


}
