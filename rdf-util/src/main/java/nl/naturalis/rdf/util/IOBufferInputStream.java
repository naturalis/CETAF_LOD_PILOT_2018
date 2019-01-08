package nl.naturalis.rdf.util;

import java.io.InputStream;

class IOBufferInputStream extends InputStream {

  private byte[] buf;
  private int limit;

  private int pos = 0;

  IOBufferInputStream(byte[] buf, int limit) {
    this.buf = buf;
    this.limit = limit;
  }

  @Override
  public int read() {
    return pos >= limit ? -1 : buf[pos++];
  }

  @Override
  public int read(byte[] b) {
    int l = Math.min(b.length, limit - pos);
    if (l <= 0) {
      return -1;
    }
    System.arraycopy(buf, pos, b, 0, l);
    pos += l;
    return l;
  }

  @Override
  public int read(byte[] b, int off, int len) {
    if (off < 0 || len < 0 || len > b.length - off) {
      throw new ArrayIndexOutOfBoundsException();
    }
    if (len == 0) {
      return 0;
    }
    int l = Math.min(len, limit - pos);
    if (l <= 0) {
      return -1;
    }
    System.arraycopy(buf, pos, b, off, l);
    pos += l;
    return l;
  }

  @Override
  public int available() {
    return Math.max(0, limit - pos);
  }

}
