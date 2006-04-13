package dacapo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ValidatingPrintStream extends PrintStream {

  private int lineCount = 0;
  int writeCount = 0;
  int byteCount = 0;
  
  public ValidatingPrintStream(OutputStream arg0) {
    super(arg0);
  }

  public ValidatingPrintStream(OutputStream arg0, boolean arg1) {
    super(arg0, arg1);
  }

  public ValidatingPrintStream(OutputStream arg0, boolean arg1, String arg2)
      throws UnsupportedEncodingException {
    super(arg0, arg1, arg2);
  }
  
  /*
   * Overrides from PrintStream
   */
  public void println() {
    super.println();
    lineCount++;
  }
  
  public void write(int arg0) {
    writeCount++;
    super.write(arg0);
  }

  public void write(byte[] arg0) throws IOException {
    //byteCount += arg0.length;
    super.write(arg0);
  }
  
  

  public void print(char[] arg0) {
    byteCount += arg0.length;
    super.print(arg0);
  }

  public void println(char[] arg0) {
    byteCount += arg0.length;
    lineCount++;
    super.println(arg0);
  }

  public void println(String arg0) {
    byteCount += arg0.length();
    lineCount++;
    super.println(arg0);
  }

  /*
   * Additional methods
   */
  public int getLineCount() { return lineCount; }

  public void reset() {
    lineCount = 0;
    byteCount = writeCount = 0;
  }

  public void write(byte[] arg0, int arg1, int arg2) {
    // TODO Auto-generated method stub
    super.write(arg0, arg1, arg2);
  }
}
