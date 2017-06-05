package com.ideal.dzqd.data.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by yaloo on 2017/6/1.
 */
public class ZipUtils {

  public static void gunzip(String source) {
    byte[] buffer = new byte[1024];
    try {
      GZIPInputStream gzis =
          new GZIPInputStream(new FileInputStream(source));

      FileOutputStream out =
          new FileOutputStream(source.replace(".gz", ""));

      int len;
      while ((len = gzis.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }

      gzis.close();
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void main(String[] args){
    gunzip("/Users/yaloo/Downloads/20170530_13_SX_JT_SHIDIAN.DAT.gz");
  }
}
