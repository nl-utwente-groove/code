package groove.io.loaders;

import java.io.*;

@SuppressWarnings("all")
public class LTSio {

  public static void main(String args[]){
	try {
		System.err.println("copying "+args[0]+" to "+args[1]);
		LTSreader input=null;
		if (args[0].endsWith(".aut")) input=new AUTreader(args[0]);

		LTSwriter output=null;
		if (args[1].endsWith(".aut")) output=new AUTwriter(args[1]);

		output.putInitialState(input.getInitialState());
		while(input.getNext()){
			output.putTrans(input.src(),output.putLabel(input.lbls()),input.dst());
		}
		input.close();
		output.close();
	} catch (Exception e) {
		System.err.println("oops:");
		throw new Error(e);
	}
  }
}


