/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package dacapo;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Output stream that looks for a character sequence and replaces it with a substitute.
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
public class SubstitutingOutputStream extends FilterOutputStream {
  /**
   * The strings we're looking for, along with the dynamic match state
   */
  private final Matcher match;
  
  /**
   * @param out
   */
  public SubstitutingOutputStream(OutputStream out, String lookFor, String replaceWith) {
    super(out);
    match = new Matcher(lookFor,replaceWith);
  }

  /* (non-Javadoc)
   * @see java.io.FilterOutputStream#write(int)
   */
  public void write(int c) throws IOException {
    match.match((char)c);
    for (int i=0; i < match.emit(); i++)
      super.write(match.outBuf[i]);
  }
  
  public void close() throws IOException {
    flush();
    super.close();
  }

  public void flush() throws IOException {
    match.flush();
    for (int i=0; i < match.emit(); i++)
      super.write(match.outBuf[i]);
    super.flush();
  }
  
//  /* (non-Javadoc)
//   * @see java.io.FilterOutputStream#write(int)
//   */
//  public void write(int c) throws IOException {
//    boolean wasMatching = match.matching();
//    if (match.match((char)c)) {
//      for (int i=0; i < replacement.length(); i++) {
//        super.write(replacement.charAt(i));
//      }
//      match.reset();
//    } else if (match.matching()) { // Still matching ?  Return until we know what's what
//        return;
//    } else {
//      if (wasMatching) {                // Match failed, emit the partial match
//        String partial = match.getPartialMatch();
//        for (int i=0; i < partial.length(); i++) {
//          super.write(partial.charAt(i));
//        }
//        write(c);
//        return;
//      }
//      super.write(c);
//    }
//  }
  
  public static void main(String[] args) {
    PrintStream newOut = new PrintStream(new SubstitutingOutputStream(
        System.out,"fred","jim"));
    test(newOut,"hi there","hi there");
    test(newOut,"fred", "jim");
    test(newOut,"alfreddy", "'aljimdy'");
    test(newOut,"ffrfrefred", "'ffrfrejim'");
    
    newOut = new PrintStream(new SubstitutingOutputStream(
        System.out,"xxy","***"));
    
    test(newOut, "x", "x");
    test(newOut, "xx","xx");
    test(newOut, "xy", "xy");
    test(newOut, "xxy", "***");
    test(newOut, "xxxy", "x***");
    test(newOut, "xxxxy", "xx***");
    test(newOut, "xxxxxy", "xxx***");
    test(newOut, "xxyxxy", "******");
    test(newOut, "xxxyxy", "x***xy");
  }

  private static void test(PrintStream newOut, String inString, String outString) {
    System.err.println("Input string "+inString);
    newOut.print(inString); 
    newOut.flush(); 
    System.err.flush();
    System.out.println(" <- should read "+outString);
  }
}
