package groove.io.loaders;

import java.io.*;

@SuppressWarnings("all")
public interface LTSreader {

  public boolean getNext() throws IOException;

  public int src();
  public int lbl();
  public String lbls();
  public int dst();

  public void close() throws IOException ;

  public int getInitialState();

}

