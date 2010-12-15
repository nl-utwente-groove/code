package groove.io.loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

@SuppressWarnings("all")
public class AUTreader implements LTSreader {

    private BufferedReader file;
    private int init;
    private int trans;
    private int count = 0;
    private int current_src;
    private String current_lbls;
    private int current_lbl;
    private int current_dst;

    private Hashtable table = new Hashtable();
    private String[] label = new String[16];
    private int nextlbl = 0;

    public AUTreader(String name) throws IOException {
        this.file = new BufferedReader(new FileReader(name));
        String line;
        int open_par;
        int last_comma;
        int first_comma;
        int close_par;
        line = this.file.readLine();
        open_par = line.indexOf('(');
        first_comma = line.indexOf(',');
        last_comma = line.lastIndexOf(',');
        close_par = line.lastIndexOf(')');
        this.init =
            Integer.parseInt(line.substring(open_par + 1, first_comma).trim());
        this.trans =
            Integer.parseInt(line.substring(first_comma + 1, last_comma).trim());
    }

    public boolean getNext() throws IOException {
        if (this.count == this.trans) {
            return false;
        }
        String line = this.file.readLine();
        int open_par = line.indexOf('(');
        int first_comma = line.indexOf(',');
        int last_comma = line.lastIndexOf(',');
        int close_par = line.lastIndexOf(')');
        this.current_src =
            Integer.parseInt(line.substring(open_par + 1, first_comma).trim());
        this.current_lbls = line.substring(first_comma + 1, last_comma).trim();
        Integer ilabel = (Integer) this.table.get(this.current_lbls);
        if (ilabel == null) {
            if (this.label.length == this.nextlbl) {
                String[] tmp =
                    new String[this.label.length + (this.label.length >> 2)];
                for (int j = 0; j < this.nextlbl; j++) {
                    tmp[j] = this.label[j];
                }
                this.label = tmp;
            }
            this.label[this.nextlbl] = this.current_lbls;
            ilabel = Integer.valueOf(this.nextlbl);
            this.nextlbl++;
            this.table.put(this.current_lbls, ilabel);
        }
        this.current_lbl = ilabel.intValue();
        this.current_dst =
            Integer.parseInt(line.substring(last_comma + 1, close_par).trim());
        this.count++;
        return true;
    }

    public int src() {
        return this.current_src;
    }

    public int lbl() {
        return this.current_lbl;
    }

    public String lbls() {
        return this.current_lbls;
    }

    public int dst() {
        return this.current_dst;
    }

    public void close() throws IOException {
        this.file.close();
        this.file = null;
    }

    public int getInitialState() {
        return this.init;
    }

}
