/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import edu.anu.thylacine.graph.Benchmark;
import edu.anu.thylacine.graph.data.files.Parser;
import edu.anu.thylacine.graph.data.files.RawCSVFile;
import edu.anu.thylacine.graph.data.files.TupleProvider;
import edu.anu.thylacine.relational.Column;
import edu.anu.thylacine.relational.Database;

/**
 * Read the DaCapo performance data file format.
 * 
 * Comma separated values: 
 *    DATE (yyy-mm-dd)
 *    VM (string)
 *    Benchmark
 *    1st iteration (integer)
 *    2nd iteration (integer)
 *    3rd iteration (integer)
 * 
 * @author Robin Garner
 * @date $Date: 2006/11/17 08:07:13 $
 * @id $Id: PerfData.java,v 1.1 2006/11/17 08:07:13 robing Exp $
 *
 */
public class PerfData extends TupleProvider implements Parser {

  /*
   * These are the columns in the table this provider produces.
   */
  public static Column<Date> TIME = Database.newColumn("TIME", Date.class);
  public static Column<String> JVM = Database.newColumn("JVM", String.class);
  /* then use predefined Benchmark column */
  public static Column<Double> ITER1 = Database.newColumn("ITER1", Double.class);
  public static Column<Double> ITER2 = Database.newColumn("ITER2", Double.class);
  public static Column<Double> ITER3 = Database.newColumn("ITER3", Double.class);
  
  private static final int COLUMNS = 6;
  
  private RawCSVFile rawData;
  /**
   * @param file
   */
  public PerfData(File file) throws IOException, ParseException {
    this(file,"perf");
  }
  
  /**
   * @param file
   */
  public PerfData(File file, String name) throws IOException, ParseException {
    super(name);
    System.out.printf("Inserting into table \"%1$s\" from input file %2$s\n",name,file.getPath());
    rawData = new RawCSVFile(file);
    rawData.parse();
    
    for (String [] row : rawData) {
      parseTuple(row);
    }
  }
  
  private static DateFormat dateFormat = new MyDateFormat("yyyy-MM-dd");
  
  private void parseTuple(String[] row) throws ParseException {
    /* Format: Tstamp,JVM,Benchmark,iter1,iter2,iter3 */
    openTuple();
    setValue(TIME,dateFormat.parse(row[0]));
    setValue(JVM,row[1]);
    setValue(Database.BENCHMARK,Benchmark.valueOf(row[2]));
    for (int i=3; i < COLUMNS; i++) {
      Column<Double> column = getColumn("ITER"+(i-2),Double.class);
      if (i < row.length) {
        Double value = Double.valueOf(row[i]);
        setValue(column,value);
      } else {
        setValue(column,null);
      }
    }
    closeTuple();
  }

}
