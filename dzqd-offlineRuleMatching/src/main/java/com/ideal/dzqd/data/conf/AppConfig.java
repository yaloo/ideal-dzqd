package com.ideal.dzqd.data.conf;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.Sources;

/**
 * Created by yaloo on 2017/5/24.
 */
@HotReload
@Sources("classpath:offline.properties")
public interface AppConfig  extends Config {

  @Key("app.test")
  String test();

  @Key("hive.driver")
  String drvier();

  @Key("hive.jdbc")
  String jdbc();

  @Key("hdfs.public.path")
  String dianquPublicPath();

  @Key("app.file.save.path")
  String fileSavePath();

  @Key("app.data.exec.cycle")
  String execDataCycle();

  @Key("app.data.exec.provinceCode")
  String provinceCode();

  @Key("app.ftp.url")
  String ftpUrl();

  @Key("app.ftp.user.anhui")
  String userAnhui();

  @Key("app.ftp.user.hunan")
  String userHunan();

  @Key("app.ftp.pwd.anhui")
  String pwdAnhui();

  @Key("app.ftp.pwd.hunan")
  String pwdHunan();

}