package groove.io.loaders;

import java.io.*;

@SuppressWarnings("all")
public interface LTSwriter {

  public int putLabel(String lbls) throws IOException ;

  public void putTrans(int src,int lbl,int dst) throws IOException ;

  public void putInitialState(int state) throws IOException ;

  public void close() throws IOException ;

}

