package groove.io.loaders;

import java.io.*;
import java.util.*;

@SuppressWarnings("all")
public class AUTreader implements LTSreader {

  private BufferedReader file;
  private int init;
  private int trans;
  private int states;
  private int count=0;
  private int current_src;
  private String current_lbls;
  private int current_lbl;
  private int current_dst;

  private Hashtable table=new Hashtable();
  private String[] label=new String[16];
  private int nextlbl=0;

  public AUTreader(String name) throws IOException {
	file=new BufferedReader(new FileReader(name));
	String line;
	int open_par;
	int last_comma;
	int first_comma;
	int close_par;
	line=file.readLine();
	open_par=line.indexOf('(');
	first_comma=line.indexOf(',');
	last_comma=line.lastIndexOf(',');
	close_par=line.lastIndexOf(')');
	init=Integer.parseInt(line.substring(open_par+1,first_comma).trim());
	trans=Integer.parseInt(line.substring(first_comma+1,last_comma).trim());
	states=Integer.parseInt(line.substring(last_comma+1,close_par).trim());
  }

  public boolean getNext() throws IOException {
	if(count==trans) return false;
	String line=file.readLine();
	int open_par=line.indexOf('(');
	int first_comma=line.indexOf(',');
	int last_comma=line.lastIndexOf(',');
	int close_par=line.lastIndexOf(')');
	current_src=Integer.parseInt(line.substring(open_par+1,first_comma).trim());
	current_lbls=line.substring(first_comma+1,last_comma).trim();
	Integer ilabel=(Integer)table.get(current_lbls);
	if (ilabel==null) {
		if (label.length==nextlbl){
			String[] tmp=new String[label.length+(label.length>>2)];
			for(int j=0;j<nextlbl;j++) tmp[j]=label[j];
			label=tmp;
		}
		label[nextlbl]=current_lbls;
		ilabel=new Integer(nextlbl);
		nextlbl++;
		table.put(current_lbls,ilabel);
	}
	current_lbl=ilabel.intValue();
	current_dst=Integer.parseInt(line.substring(last_comma+1,close_par).trim());
	count++;
	return true;
  }

  public int src(){
	return current_src;
  }
  public int lbl(){
	return current_lbl;
  }
  public String lbls(){
	return current_lbls;
  }
  public int dst(){
	return current_dst;
  }

  public void close() throws IOException {
	file.close();
	file=null;
  }

  public int getInitialState(){
	return init;
  }

}

