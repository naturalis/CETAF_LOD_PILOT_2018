package nl.naturalis.rdf.util;

import java.io.InputStream;
import java.io.OutputStream;

public class IOBufferOutputStream extends OutputStream {

  private byte[] buf;
  private int inc;

  private int pos = 0;

  public IOBufferOutputStream() {
    this(64);
  }

  public IOBufferOutputStream(int length) {
    this(length, -1);
  }

  public IOBufferOutputStream(int length, int increment) {
    if (length < 1) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.inc = Math.max(0, increment);
    this.buf = new byte[length];
  }

  @Override
  public void write(int b) {
    if (pos > buf.length) {
      int sz = inc == 0 ? buf.length * 2 : buf.length + inc;
      byte[] newBuf = new byte[sz];
      System.arraycopy(buf, 0, newBuf, 0, pos);
      buf = newBuf;
    }
    buf[pos++] = (byte) b;
  }

  @Override
  public void write(byte[] b) {
    int len = b.length;
    if (len != 0) {
      int sz = buf.length;
      while (pos + len > sz) {
        sz = inc == 0 ? sz * 2 : sz + inc;
      }
      byte[] newBuf = new byte[sz];
      System.arraycopy(buf, 0, newBuf, 0, pos);
      buf = newBuf;
      System.arraycopy(b, 0, buf, pos, len);
      pos += len;
    }
  }

  @Override
  public void write(byte[] b, int off, int len) {
    if (off < 0 || len < 0 || len > b.length - off) {
      throw new ArrayIndexOutOfBoundsException();
    }
    if (len != 0) {
      int sz = buf.length;
      while (pos + len > sz) {
        sz = inc == -1 ? sz * 2 : sz + inc;
      }
      byte[] newBuf = new byte[sz];
      System.arraycopy(buf, 0, newBuf, 0, pos);
      buf = newBuf;
      System.arraycopy(b, off, buf, pos, len);
      pos += len;
    }
  }

  public int size() {
    return buf.length;
  }

  public int position() {
    return pos;
  }

  public InputStream flip() {
    IOBufferInputStream is = new IOBufferInputStream(buf, pos);
    pos = 0;
    return is;
  }

  public byte[] toArray() {
    byte[] copy = new byte[pos];
    System.arraycopy(buf, 0, copy, 0, pos);
    return copy;
  }
}
