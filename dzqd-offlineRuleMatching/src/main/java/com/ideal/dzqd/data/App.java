package com.ideal.dzqd.data;

import com.google.common.base.Joiner;
import com.ideal.dzqd.data.internal.MatchingServer;
import com.ideal.dzqd.data.tools.MysqlTools;
import com.ideal.dzqd.data.vo.DownloadEvent;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        String proviceCode = "13";
        String date = "20170606";

        /**
         * 命令行，传参
         */
        DownloadEvent event = new DownloadEvent(App.class);
        event.setFtpUser("anhui");
        event.setFtpPwd("anhui@123");
        event.setProvinceCode("13");
        event.setRemote_down_path("/sceneuse/");
        event.setRemoteFileReg(Joiner.on('_').join(date,proviceCode,".*"));
        event.setLocalPath("/Users/yaloo/Downloads/");
        event.setCycle(date);

        try {
            MysqlTools.createTable(Joiner.on('_').join("tm_mkt_scene_user_res",proviceCode,date));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new MatchingServer(event).start();
    }
}