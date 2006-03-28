package dacapo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.io.BufferedReader;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

public class TestHarness {
  String className;
  String methodName;
  String methodSignature;
  Hashtable configurations = new Hashtable();
  private final static int QUOTE = (int)'\"';
  
  private static boolean verbose = false;
  
  private static boolean allowOpenFromFileSystem = false;
  
  public static URL getURL(File f) {
    return getURL(f.getPath());
  }
  
  public static URL getURL(String fn) {
    ClassLoader cl = TestHarness.class.getClassLoader();
    if (verbose)
      System.out.println("TestHarness.getURL: returns "+cl.getResource(fn));
    return cl.getResource(fn);    
  }
  
  public static boolean exists(File f) {
    return exists(f.getPath());
  }
  
  public static boolean exists(String fn) {
    boolean result = getURL(fn) != null;
    if (!result && allowOpenFromFileSystem) {
      if (verbose)
        System.out.println("TestHarness.exists: going to file system for "+fn);
      File file = new File(fn);
      result =  file.exists();
    }
    return result;
  }
  
  public static InputStream getInputStream(File f) throws FileNotFoundException {
    return getInputStream(f.getPath());
  }
  
  public static InputStream getInputStream(String fn) throws FileNotFoundException {
    try {
      if (verbose)
        System.out.println("Extracting f="+fn+" from jar.");
      InputStream is = null;
      java.net.URL sourceURL = getURL(fn);
      if (sourceURL != null) {
        InputStream ios = sourceURL.openStream();
        if (ios != null)
          return ios;
      }
    } catch (Exception e) {
    }
    
    if (allowOpenFromFileSystem) {
      if (verbose)
        System.out.println("TestHarness.getInputStream: going to file system for "+fn);
      return new FileInputStream(fn);
    } else {
      throw new FileNotFoundException("Failed to open " + fn + " from jar file.");
    }
  }
  
  public static void main(String[] args) {
    try {
      InputStream ins = System.in;
      String config = "default";
      Callback callback = null;
      int iterations = 1;
      boolean info = false;
      
      /* No options - print usage and die */
      if (args.length == 0) {
        printUsage();
        return;
      }
      
      /* get global options */
      int i = 0;
      for (; i < args.length; i++) {
        if (args[i].equals("-s")) {
          // size name name
          config = args[++i];
        } else if (args[i].equals("-i")) {
          // display benchmark information
          info = true;
        } else if (args[i].equals("-h")) {
          printUsage();
        } else if (args[i].equals("-v")) {
          // display detailed information
          verbose = true;
        } else if (args[i].equals("-c")) {
          // use a callback
          Class cls = null;
          try {
            cls = Class.forName(args[++i]);
          } catch (Exception e) {
            System.err.println(e);
            System.err.println("Could not find callback class "+args[i]);
            System.exit(10);
          } 
          if (!(Class.forName("dacapo.Callback").isAssignableFrom(cls))) {
            System.err.println(args[i] + " is not an instance of dacapo.Callback");
            System.exit(11);
          } else {
            callback = (Callback) cls.newInstance();
          }
        } else if (args[i].equals("-F")) {
          // display detailed information
          allowOpenFromFileSystem = true;
        } else if (args[i].equals("-two")) {
          // Run twice, showing the second iteration
          iterations = 2;
        } else if (args[i].equals("-n")) {
          // Run twice, showing the second iteration
          iterations = Integer.parseInt(args[++i]);
        } else
          break;
      }
      if (callback == null) {
        callback = new Callback();
      }
      
      File scratchDir = new File("scratch");
      rmdir(scratchDir);
      scratchDir.mkdir();
      
      // now get the benchmark names and run them
      for (; i < args.length; i++) {
        // check if it is a benchmark name
        // name of file containing configurations
        String bm = args[i];
        String cnf = "dacapo/"+bm+".cnf";
        ins = TestHarness.class.getClassLoader().getResourceAsStream(cnf);
        if (ins == null) {
          System.err.println("Unknown benchmark: "+args[i]);
          System.exit(12);
        }
        
        TestHarness h = new TestHarness();
        h.parseStream(ins);
        
        if (info) {
          h.bmInfo();
        } else {
          if (verbose)
            h.dump();
          
          Method m = h.findMethod();
          
          while (iterations > 1) {
            callback.startWarmup(bm);
            h.invokeConfiguration(m, config);
            callback.stopWarmup(bm);
            iterations--;
          }
          
          callback.start(bm);
          h.invokeConfiguration(m, config);
          callback.stop(bm);
        }
      }
    }
    catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * Print a usage message to stdout
   */
  private static void printUsage() {
    System.out.println("Usage: java Harness [options ...] [benchmarks ...]");
    System.out.println("    -c <callback>           Use class <callback> to bracket benchmark runs");
    System.out.println("    -F                      Allow input data from filesystem");
    System.out.println("    -h                      Print this help");
    System.out.println("    -i                      Display benchmark information");
    System.out.println("    -s small|default|large  Size of input data");
    System.out.println("    -two                    Run the benchmark twice");
    System.out.println("    -v                      Verbose output");
  }
  
  private static void rmdir(File dir) {
    String[] files = dir.list();
    if (files != null) {
      for (int f = 0; f < files.length; f++) {
        File file = new File(dir, files[f]);
        if (file.isDirectory())
          rmdir(file);
        if (!file.delete())
          System.err.println("Could not delete "+files[f]);
      }
    }
  }
  
  public static int TEST(int i) {
    System.err.println("In TEST");
    System.err.println(i);
    return 2*i;
  }
  
  private void bmInfo() {
    for (Enumeration e = configurations.keys() ; e.hasMoreElements()  ;) {
      String v = (String)e.nextElement();
      if (v.startsWith("desc-")) {
        System.err.print(v+" ");
        Vector args = (Vector)configurations.get(v);
        for (Enumeration a = args.elements() ; a.hasMoreElements() ;) {
          System.err.print(" "+a.nextElement());
        }
        System.err.println();
      }
    }
  }
  
  private void dump() {
    System.err.println("Class name: "+className);
    System.err.println("Method name: "+methodName);
    
    System.err.println("Configurations:");
    for (Enumeration e = configurations.keys() ; e.hasMoreElements()  ;) {
      String v = (String)e.nextElement();
      if (!v.startsWith("desc")) {
        System.err.print(v+" -");
        Vector args = (Vector)configurations.get(v);
        for (Enumeration a = args.elements() ; a.hasMoreElements() ;) {
          System.err.print(" "+a.nextElement());
        }
        System.err.println();
      }
    }
  }
  
  private void parseStream(InputStream stream) {
    try {
      Reader r = new BufferedReader(new InputStreamReader(stream));
      StreamTokenizer tokenizer = new StreamTokenizer(r);
      int tokenType;
      
      tokenizer.eolIsSignificant(true);
      tokenizer.wordChars('_', '_');
      tokenizer.wordChars('.', ',');
      tokenizer.wordChars('$', '$');
      tokenizer.wordChars('-', '-');
      
      tokenType = tokenizer.nextToken();
      if (tokenType == QUOTE || tokenType == StreamTokenizer.TT_WORD)
        className = tokenizer.sval;
      else {
        System.err.println("Unexpected token type for className:  "+tokenType);
        System.exit(-1);
      }
      
      tokenType = tokenizer.nextToken();
      if (tokenType == QUOTE || tokenType == StreamTokenizer.TT_WORD)
        methodName = tokenizer.sval;
      else {
        System.err.println("Unexpected token type for methodName:  "+tokenType);
        System.err.println(" className: "+className);
        System.exit(-1);
      }
      
      tokenType = tokenizer.nextToken();
      if (tokenType == QUOTE || tokenType == StreamTokenizer.TT_WORD)
        methodSignature = tokenizer.sval;
      
      String configuration = null;
      
      
      configLoop:
        do {
          Vector args = new Vector();
          
          do {
            tokenType = tokenizer.nextToken();
            if (tokenType == StreamTokenizer.TT_EOF)
              break configLoop;
          } while (tokenType == StreamTokenizer.TT_EOL);
          
          if (tokenType == StreamTokenizer.TT_WORD) {
            configuration = tokenizer.sval;
          }
          else {
            System.err.println("Unexpected token type for arg:  "+tokenType);
            System.exit(-1);
          }
          
          argLoop:
            while (tokenType != StreamTokenizer.TT_EOF ) {
              tokenType = tokenizer.nextToken();
              switch (tokenType) {
                case QUOTE:
                  args.addElement(tokenizer.sval);
                  break;
                case StreamTokenizer.TT_WORD:
                  args.addElement(tokenizer.sval);
                  break;
                case StreamTokenizer.TT_NUMBER:
                  args.addElement(new Double(tokenizer.nval));
                  break;
                case StreamTokenizer.TT_EOL:
                  break argLoop;
                case StreamTokenizer.TT_EOF:
                  break;
                default:
                  System.err.println("Unexpected token type for arg:  "+tokenType);
                System.exit(-1);
                break;
              }
            }
          
          configurations.put(configuration, args);
        } while (tokenType != StreamTokenizer.TT_EOF);
    }
    catch(IOException e) {
      System.err.println(e);
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  private Method findMethod() {
    try {
      Class klass = Class.forName(className);
      Method[] methods = klass.getDeclaredMethods();
      for (int i=0; i < methods.length; i++) {
        Method m = methods[i];
        if (m.getName().equals(methodName)) {
          return m;
        }
      }
    }
    catch (ClassNotFoundException e) {
      System.err.println(e);
      e.printStackTrace();
      System.exit(-1);
    }
    
    System.err.println("Method not found: "+methodName);
    System.exit(-1);
    return null;
  }
  
  private void invokeConfiguration(Method m, String configuration) {
    try {
      Vector configArgs = (Vector) configurations.get(configuration);
      if (configArgs == null) {
        System.err.println("Can't find configuration "+ configuration);
        System.exit(-1);
      }
      
      Class[] parameters = m.getParameterTypes();
      Object[] invocationArgs = new Object[parameters.length];
      
      // Special case to handle Spec benchmarks, 'main' programs,
      // etc, which take a single argument that is an array of strings
      if (parameters.length == 1 &&
              parameters[0].isArray() &&
              parameters[0].getComponentType() == "".getClass()) {
        
        String[] sArray = new String[configArgs.size()];
        for (int i = 0; i < configArgs.size(); i++)
          sArray[i] = (String)configArgs.elementAt(i);
        invocationArgs[0] = sArray;
      }
      else {
        for (int i = 0; i < parameters.length; i++) {
          Class ptype = parameters[i];
          Object arg = configArgs.elementAt(i);
          if (ptype.isInstance(arg)) {
            invocationArgs[i] = arg;
          }
          else if (ptype == Boolean.TYPE) {
            invocationArgs[i] = new Boolean((String)arg);
          }
          else if (ptype == Byte.TYPE) {
            invocationArgs[i] = new Byte(((Double)arg).byteValue());
          }
          else if (ptype == Short.TYPE) {
            invocationArgs[i] = new Short(((Double)arg).shortValue());
          }
          else if (ptype == Integer.TYPE) {
            invocationArgs[i] = new Integer(((Double)arg).intValue());
          }
          else if (ptype == Long.TYPE) {
            invocationArgs[i] = new Long(((Double)arg).longValue());
          }
          else if (ptype == Float.TYPE) {
            invocationArgs[i] = new Float(((Double)arg).floatValue());
          }
          else if (ptype == Double.TYPE) {
            invocationArgs[i] = new  
            Double(((Double)arg).doubleValue());
          }
        }
      }
      
      Object instance = null;
      
      if (verbose) {
        System.out.println("method is: "+m);
      }
      
      
      if (!Modifier.isStatic(m.getModifiers()))
        instance = m.getDeclaringClass().newInstance();
      
      Object result = m.invoke(instance, invocationArgs);
      
      if (result != null) {
        System.out.println("Invocation result was: "+result);
      }
    }
    catch (Exception e) {
      System.err.println(e);
//      e.printStackTrace();
      System.exit(-1);
    }
  }
}
