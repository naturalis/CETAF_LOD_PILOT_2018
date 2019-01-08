package nl.naturalis.rdf.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("resource")
public class IOBufferInputStreamTest {

  @Test
  public void test01() {
    byte[] buf = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
    IOBufferInputStream is = new IOBufferInputStream(buf, 4);
    assertEquals(4, is.available());
    assertEquals(1, is.read());
    assertEquals(2, is.read());
    assertEquals(3, is.read());
    assertEquals(4, is.read());
    assertEquals(-1, is.read());
    assertEquals(-1, is.read());
    assertEquals(-1, is.read());
  }

  @Test
  public void test2() {
    byte[] buf = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
    IOBufferInputStream is = new IOBufferInputStream(buf, 7);
    assertEquals(7, is.available());

    byte[] bytes = new byte[2];
    int i = is.read(bytes);
    assertEquals(2, i);
    assertArrayEquals(new byte[] {1, 2}, bytes);

    bytes = new byte[2];
    i = is.read(bytes);
    assertEquals(2, i);
    assertArrayEquals(new byte[] {3, 4}, bytes);

    bytes = new byte[2];
    i = is.read(bytes);
    assertEquals(2, i);
    assertArrayEquals(new byte[] {5, 6}, bytes);

    bytes = new byte[2];
    i = is.read(bytes);
    assertEquals(1, i);
    byte[] expected = new byte[2];
    expected[0] = 7;
    assertArrayEquals(expected, bytes);

    bytes = new byte[2];
    i = is.read(bytes);
    assertEquals(-1, i);
    expected = new byte[2];
    assertArrayEquals(expected, bytes);

    bytes = new byte[2];
    i = is.read(bytes);
    assertEquals(-1, i);
    expected = new byte[2];
    assertArrayEquals(expected, bytes);
  }

  @Test
  public void test3() {
    byte[] buf = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};
    IOBufferInputStream is = new IOBufferInputStream(buf, 7);
    assertEquals(7, is.available());

    byte[] bytes = new byte[3];
    int i = is.read(bytes, 1, 2);
    assertEquals(2, i);
    assertArrayEquals(new byte[] {0, 1, 2}, bytes);

    bytes = new byte[3];
    i = is.read(bytes, 2, 1);
    assertEquals(1, i);
    assertArrayEquals(new byte[] {0, 0, 3}, bytes);

    bytes = new byte[20];
    i = is.read(bytes, 10, 10);
    assertEquals(4, i);
    assertEquals(0, bytes[9]);
    assertEquals(4, bytes[10]);
    assertEquals(5, bytes[11]);
    assertEquals(6, bytes[12]);
    assertEquals(7, bytes[13]);
    assertEquals(0, bytes[14]);
  }

}
