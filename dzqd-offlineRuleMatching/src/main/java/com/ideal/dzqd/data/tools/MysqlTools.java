package com.ideal.dzqd.data.tools;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ideal.dzqd.data.po.SceneChannelSale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by yaloo on 2017/5/6.
 */
public final class MysqlTools {

  /**
   * 获取规则对应子场景，销售品，渠道 等信息
   *
   *  SELECT
   c.province_code,
   c.sale_id,
   c.channel_id,
   c.order_id,
   s.scene_type_code,
   s.scene_id,
   c.sub_scene_id
   FROM
   tc_cfg_sub_scene s
   JOIN
   tc_cfg_rel_scene_sale c ON s.sub_scene_id = c.sub_scene_id
   JOIN
   tc_cfg_rel_scene_rule r ON r.sub_scene_id = s.sub_scene_id
   WHERE
   c.state = 1 and r.state = 1
   AND NOW() BETWEEN c.eff_date AND c.exp_date
   AND NOW() BETWEEN r.eff_date AND r.exp_date
   */
  public static List<SceneChannelSale> getSceneChannelSales(String province_code) throws SQLException {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT");
    sb.append(
        " c.province_code,c.sale_id,c.channel_id,c.order_id,s.scene_type_code,s.scene_id,c.sub_scene_id");
    sb.append(" FROM");
    sb.append(" tc_cfg_sub_scene s");
    sb.append(" JOIN");
    sb.append(" tc_cfg_rel_scene_sale c ON s.sub_scene_id = c.sub_scene_id");
    sb.append(" JOIN");
    sb.append(" tc_cfg_rel_scene_rule r ON r.sub_scene_id = s.sub_scene_id");

    sb.append(" WHERE");
    sb.append("  c.state = 1 AND r.state = 1");
    sb.append(" AND r.province_code=? ");
    sb.append(" AND NOW() BETWEEN c.eff_date AND c.exp_date");
    sb.append(" AND NOW() BETWEEN r.eff_date AND r.exp_date");
    List<SceneChannelSale> css = Lists.newArrayList();

    DbPoolConnection dbp = DbPoolConnection.getInstance();
    DruidPooledConnection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      connection = dbp.getConnection();
      statement = connection.prepareStatement(sb.toString());
      statement.setString(1, province_code);
      rs = statement.executeQuery();
      while (rs.next()) {
        css.add(
            new SceneChannelSale(
                rs.getInt("order_id"),
                rs.getString("scene_type_code"),
                rs.getString("scene_id"),
                rs.getString("sub_scene_id"),
                rs.getString("province_code"),
                rs.getString("channel_id"),
                rs.getString("sale_id")
            )
        );
      }
    } finally {
      close(rs, statement, connection);
    }

    return css;
  }
  /**
   * 创建目标用户群结果表，如果存在将删除重建
   *
   * @param table 表名
   */
  public static void createTable(String table) throws SQLException {

    StringBuffer buffer = new StringBuffer();
    buffer.append("DROP TABLE IF EXISTS `").append(table).append("`;");
    buffer.append("CREATE TABLE `").append(table).append("` (");
    buffer.append("`id` INT(10) NOT NULL AUTO_INCREMENT,");
    buffer.append("`stat_date` VARCHAR(8) DEFAULT NULL,");
    buffer.append("`access_num` VARCHAR(50) DEFAULT NULL,");
    buffer.append("`province_code` VARCHAR(2) DEFAULT NULL,");
    buffer.append("`channel_id` VARCHAR(20) DEFAULT NULL,");
    buffer.append("`sub_scene_id` VARCHAR(20) DEFAULT NULL,");
    buffer.append("`sale_id` VARCHAR(50) DEFAULT NULL,");
    buffer.append("`order_id` INT(10) DEFAULT NULL,");
    buffer.append("PRIMARY KEY (`id`),");
    buffer.append("KEY `phone` (`access_num`) USING HASH,");
    buffer.append("KEY `NBR` (`sale_id`) USING HASH");
    buffer.append(")  ENGINE=INNODB DEFAULT CHARSET=UTF8;");

    DbPoolConnection dbp = DbPoolConnection.getInstance();
    DruidPooledConnection connection = null;
    Statement statement = null;

    connection = dbp.getConnection();
    statement = connection.createStatement();
    statement.executeUpdate(buffer.toString());

  }

  /**
   * rename table
   *
   * @param src 需要重命令的表
   * @param target 重命令后的表名
   */
  public static void renameTable(String src, String target) throws SQLException {
    String sql = Joiner.on(" ").join("ALTER TABLE", src, "RENAME TO", target);
    DbPoolConnection dbp = DbPoolConnection.getInstance();
    DruidPooledConnection connection = null;
    Statement statement = null;

    connection = dbp.getConnection();
    statement = connection.createStatement();
    statement.executeUpdate(sql);

  }

  private static void close(ResultSet resultSet, Statement statement, Connection connection) {
    try {
      if (resultSet != null) {
        resultSet.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (connection != null) {
        connection.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}