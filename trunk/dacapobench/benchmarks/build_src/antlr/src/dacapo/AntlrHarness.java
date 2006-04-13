package dacapo;

import java.io.File;
import java.util.Vector;
import antlr.Tool;

public class AntlrHarness extends Benchmark {
  
  public AntlrHarness(String name, File scratch) throws Exception {
    super(name,scratch);
  }
  
  protected void prepare() throws Exception {
    super.prepare();
    copyFileTo(new File(scratch,"antlr/CommonTokenTypes.txt"),scratch);
  }
  
  public boolean validate() { 
    return true; // FIXME need a proper validation procedure. 
  }
  
  /** After each iteration, delete the output files */
  public void postIteration(String args[]) {
    super.postIteration(args);
    //deleteTree(new File(scratch,"antlr/output"));
  }
  
  public void iterate(String[] args) {
    int firstGrammarIndex = 0;
    int nIterations = 1;
    String[] newArgs = null;
    Vector v = new Vector(args.length);
    for (int i=0; i < args.length; i++) {
      if (args[i].equals("-grammars")) {
        firstGrammarIndex = i+1;
        newArgs = new String[v.size()+1];
        for (int j=0; j < v.size(); j++) {
          newArgs[j] = (String)v.elementAt(j);
        }
        break;
      } else if (args[i].equals("-iterations")) {
        nIterations = Integer.parseInt(args[++i]);
      } else {
        v.addElement(args[i]);
      }
    }
      
    for (int iteration=0; iteration < nIterations; iteration++) {
      for (int iGrammar=firstGrammarIndex;
           iGrammar < args.length;
           iGrammar++) {
        String grammarFile = (new File(scratch,args[iGrammar])).getPath();
        newArgs[newArgs.length-1] = grammarFile;
        System.out.println("Running antlr on grammar "+args[iGrammar]);
        Tool.main(newArgs);
      }
    }
  }
}
