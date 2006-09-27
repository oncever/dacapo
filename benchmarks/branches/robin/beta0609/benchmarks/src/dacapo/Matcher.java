/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package dacapo;

/**
 * Attempt to match a string character by character.  
 * 
 * @author Robin Garner
 * @date $Date:$
 * @id $Id:$
 *
 */
class Matcher {
  /**
   * This is the string we are looking for
   */
  private final char[] tagBuf;
  
  /**
   * The state of the matcher is the current character in the buffer that we have
   * matched to date.
   */
  private int state = 0;
  
  private final String replacement;
  
  /**
   * Create a matcher for the given string.
   * 
   * @param tag
   */
  public Matcher(String tag, String replacement) {
    tagBuf = tag.toCharArray();
    outBuf = new char[Math.max(tag.length(),replacement.length())];
    this.replacement = replacement;
  }
  
  /**
   * Has the target string been matched ?
   * 
   * @return
   */
  boolean matched() { return state == tagBuf.length; }
  
  /**
   * Is there a partial match in progress ?
   * 
   * @return
   */
  boolean matching() { return state != 0; }
  
  /**
   * Reset the state of the match
   */
  void reset() { state  = 0; }
  
//  /**
//   * Process the next character in the input stream.
//   * 
//   * @param c
//   * @return
//   */
//  void match(char c) {
//    lastState = state;
//    outCount = 0;
//    if (matched()) reset();
//    if (c == tagBuf[state])
//      state++;
//    if (state > 0 && c == tagBuf[state-1]) {
//      if (matched()) {
//        outCount = replacement.length();
//        for (int i=0; i < outCount; i++)
//          outBuf[i] = replacement.charAt(i);
//        state = 0;
//      }
//    } else {
//      /* 
//       * Shift the start of the match forward, pushing skipped bytes into 
//       * the outbuf as we go.
//       */
//      while (state > 0 && c != tagBuf[state-1]) {
//        int nextState = nextMatch[state-1];
//        //System.out.println(" (state = "+state+", c = "+c+", nextState = "+nextState+")");
//        for (int i=0; i < nextState; i++)
//          outBuf[outCount++] = tagBuf[i];
//        state -= nextState;
//        if (state >= 0 && c == tagBuf[state])
//          state++;
//      }
//      if (state <= 0)
//        outBuf[outCount++] = c;
//    }
//  }
  
  private static final int NOMATCH = 1;
  private static final int MATCHING = 2;
  private static final int MATCHED = 3;
  
  private int st = NOMATCH;
  
  public void match(char c) {
    outCount = 0;
    if (tagBuf[state] == c) {
      state++;
      st = MATCHING;
    } else {
      st = NOMATCH;
    }
    if (matched()) {
      st = MATCHED;
      outCount = replacement.length();
      for (int i=0; i < outCount; i++)
        outBuf[i] = replacement.charAt(i);
      state = 0;
    } else if (state > 0 && st == MATCHING) {
      // Do nothing - match in progress
    } else if (state == 0 && st == NOMATCH) {
      // Failed to match first character in tag
      outCount = 1;
      outBuf[0] = c;
    } else { // st == NOMATCH
      // Partial match that failed.
      System.err.println("Failing partial at state="+state);
      int i=1;
      outBuf[0] = tagBuf[0];
      state--;
      boolean matched = false;
      while (state > 0 && !matched) {
        // Search forward to the start of a match
        while (tagBuf[i] != tagBuf[0] && state > 0) {
          outBuf[i] = tagBuf[i];
          i++;
          state--;
        }
        
        // Match the current tag buffer agains the new output stream prefix
        matched = true;
        for (int j=0; j < state && matched; j++) {
          if (tagBuf[j] != tagBuf[i+j])
            matched = false;
        }
        matched &= tagBuf[state] == c;
        if (matched)
          state++;
        else {
          outBuf[i-1] = tagBuf[i-1];
          i++;
          state--;
        }
      }
      if (!matched)
        outBuf[i++] = c;
      outCount = i;
    }
  }
  
  public void flush() {
    System.err.println("Flushing");
    for (int i=0; i < state; i++) {
      outBuf[i] = tagBuf[i];
    }
    outCount = state;
  }
  
  private int outCount = 0;
  public char outBuf[];
  
  public int emit() {
    // Number of characters of outbuf to emit.
    return outCount;
  }
  
  /**
   * The length of the match string.
   * 
   * @return
   */
  int length() { return tagBuf.length; }
  
  /**
   * 
   */
  String getTag() {
    return new String(tagBuf);
  }
}