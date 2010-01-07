package groove.io.loaders;

import java.io.*;
import java.util.*;

@SuppressWarnings("all")
public class AUTwriter implements LTSwriter {

  private PrintStream file;
  private String name;
  private final static String header="des(?,?,?)                                           ";
  private Hashtable table;
  private String[] label;
  private int nextlbl;
  private int init=-1;
  private int tcount=0;
  private int smax=0;

  public AUTwriter(String name) throws IOException {
	this.name=name;
	file=new PrintStream(new FileOutputStream(name));
	file.println(header);
	table=new Hashtable();
	nextlbl=0;
	label=new String[16];
  }

  public int putLabel(String lbls){
	Integer i=(Integer)table.get(lbls);
	if (i==null){
		if (label.length==nextlbl){
			String[] tmp=new String[label.length+(label.length>>2)];
			for(int j=0;j<nextlbl;j++) tmp[j]=label[j];
			label=tmp;
		}
		label[nextlbl]=lbls;
		i=new Integer(nextlbl);
		nextlbl++;
		table.put(lbls,i);
	}
	return i.intValue();
  }

  public void putTrans(int src,int lbl,int dst) throws IOException {
	if (src>smax) smax=src;
	if (dst>smax) smax=dst;
	tcount++;
	if (lbl<0 || lbl >= nextlbl) throw new Error("illegal argument");
	file.print("(");
	file.print(src);
	file.print(",");
	file.print(label[lbl]);
	file.print(",");
	file.print(dst);
	file.println(")");
  }

  public void putInitialState(int state){
	if (init!=-1) throw new Error("AUT allows precisely one initial state");
	init=state;
	if (state>smax) smax=state;
  }

  public void close() throws IOException {
	if (init==-1) throw new Error("initial state must be set");
	String hdr="des("+init+","+tcount+","+(smax+1)+")";
	if (hdr.length()>=header.length()) throw new Error("reserved header space not big enough");
	file.close();
	RandomAccessFile f=new RandomAccessFile(name,"rw");
	f.write(hdr.getBytes());
	f.close();
  }

}

