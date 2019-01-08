package nl.naturalis.rdf.util;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("resource")
public class IOBufferOutputStreamTest {

  @Test
  public void test01() throws IOException {
    IOBufferOutputStream os = new IOBufferOutputStream(2);
    os.write(new byte[] {1, 2, 3});
    assertEquals(4, os.size());
    assertEquals(3, os.position());
    assertArrayEquals(new byte[] {1, 2, 3}, os.toArray());
    InputStream is = os.flip();
    assertEquals(0, os.position());
    assertEquals(3, is.available());
  }

  @Test
  public void test02() throws IOException {
    IOBufferOutputStream os = new IOBufferOutputStream(2, 3);
    os.write(new byte[] {1, 2, 3});
    os.write(new byte[] {4, 5, 6});
    assertEquals(2 + 3 + 3, os.size());
    assertEquals(6, os.position());
    assertArrayEquals(new byte[] {1, 2, 3, 4, 5, 6}, os.toArray());
    InputStream is = os.flip();
    assertEquals(0, os.position());
    assertEquals(6, is.available());
  }

  @Test
  public void test03() throws IOException {
    IOBufferOutputStream os = new IOBufferOutputStream(2, 8);
    os.write(new byte[] {1, 2, 3}, 1, 2);
    os.write(new byte[] {4, 5, 6}, 2, 1);
    assertEquals(2 + 8, os.size());
    assertEquals(3, os.position());
    assertArrayEquals(new byte[] {2, 3, 6}, os.toArray());
    InputStream is = os.flip();
    assertEquals(0, os.position());
    assertEquals(3, is.available());
  }

  @Test
  public void test04() throws IOException {
    IOBufferOutputStream os = new IOBufferOutputStream(2, 8);
    os.write(new byte[] {1, 2, 3}, 0, 2);
    os.write(new byte[] {4, 5, 6}, 1, 0);
    assertEquals(2, os.size());
    assertEquals(2, os.position());
    assertArrayEquals(new byte[] {1, 2}, os.toArray());
    InputStream is = os.flip();
    assertEquals(0, os.position());
    assertEquals(2, is.available());
  }

  @Test
  public void test05() throws IOException {
    IOBufferOutputStream os = new IOBufferOutputStream(2, 8);
    os.write(new byte[0]);
    assertEquals(2, os.size());
    assertEquals(0, os.position());
    assertArrayEquals(new byte[0], os.toArray());
    InputStream is = os.flip();
    assertEquals(0, os.position());
    assertEquals(0, is.available());
  }
}
