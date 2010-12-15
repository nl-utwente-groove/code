package groove.io.loaders;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.Hashtable;

@SuppressWarnings("all")
public class AUTwriter implements LTSwriter {

    private PrintStream file;
    private String name;
    private final static String header =
        "des(?,?,?)                                           ";
    private Hashtable table;
    private String[] label;
    private int nextlbl;
    private int init = -1;
    private int tcount = 0;
    private int smax = 0;

    public AUTwriter(String name) throws IOException {
        this.name = name;
        this.file = new PrintStream(new FileOutputStream(name));
        this.file.println(header);
        this.table = new Hashtable();
        this.nextlbl = 0;
        this.label = new String[16];
    }

    public int putLabel(String lbls) {
        Integer i = (Integer) this.table.get(lbls);
        if (i == null) {
            if (this.label.length == this.nextlbl) {
                String[] tmp =
                    new String[this.label.length + (this.label.length >> 2)];
                for (int j = 0; j < this.nextlbl; j++) {
                    tmp[j] = this.label[j];
                }
                this.label = tmp;
            }
            this.label[this.nextlbl] = lbls;
            i = Integer.valueOf(this.nextlbl);
            this.nextlbl++;
            this.table.put(lbls, i);
        }
        return i.intValue();
    }

    public void putTrans(int src, int lbl, int dst) throws IOException {
        if (src > this.smax) {
            this.smax = src;
        }
        if (dst > this.smax) {
            this.smax = dst;
        }
        this.tcount++;
        if (lbl < 0 || lbl >= this.nextlbl) {
            throw new Error("illegal argument");
        }
        this.file.print("(");
        this.file.print(src);
        this.file.print(",");
        this.file.print(this.label[lbl]);
        this.file.print(",");
        this.file.print(dst);
        this.file.println(")");
    }

    public void putInitialState(int state) {
        if (this.init != -1) {
            throw new Error("AUT allows precisely one initial state");
        }
        this.init = state;
        if (state > this.smax) {
            this.smax = state;
        }
    }

    public void close() throws IOException {
        if (this.init == -1) {
            throw new Error("initial state must be set");
        }
        String hdr =
            "des(" + this.init + "," + this.tcount + "," + (this.smax + 1)
                + ")";
        if (hdr.length() >= header.length()) {
            throw new Error("reserved header space not big enough");
        }
        this.file.close();
        RandomAccessFile f = new RandomAccessFile(this.name, "rw");
        f.write(hdr.getBytes());
        f.close();
    }

}
