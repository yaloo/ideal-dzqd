package com.ideal.dzqd.data.tools;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by yaloo on 2017/5/12.
 */
public class DbPoolConnection {

  private static DbPoolConnection databasePool = null;
  private static DruidDataSource dds = null;

  static {
    Properties properties = loadPropertyFile("/db_server.properties");
    try {
      dds = (DruidDataSource) DruidDataSourceFactory
          .createDataSource(properties);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private DbPoolConnection() {
  }

  public static synchronized DbPoolConnection getInstance() {
    if (null == databasePool) {
      databasePool = new DbPoolConnection();
    }
    return databasePool;
  }

  public DruidPooledConnection getConnection() throws SQLException {
    return dds.getConnection();
  }

  public static Properties loadPropertyFile(String fullFile) {
    if (null == fullFile || fullFile.equals(""))
      throw new IllegalArgumentException(
          "Properties file path can not be null : " + fullFile);
    InputStream inputStream = DbPoolConnection.class.getResourceAsStream(fullFile);
    Properties p = null;
    try {
      p = new Properties();
      p.load(inputStream);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Properties file not found: "
          + fullFile);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Properties file can not be loading: " + fullFile);
    } finally {
      try {
        if (inputStream != null)
          inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return p;
  }
}