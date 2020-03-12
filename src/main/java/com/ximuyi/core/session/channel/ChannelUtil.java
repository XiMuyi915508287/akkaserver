package com.ximuyi.core.session.channel;

import java.util.concurrent.TimeUnit;

import com.ximuyi.core.command.response.CmdChannelClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ChannelUtil.class);

    public static void channelClose(NetChannel netChannel, String reason, boolean notifyClient){
        if (netChannel.isClosed()){
            return;
        }
        if (notifyClient){
            netChannel.scheduleClose(new CmdChannelClose(reason), 2,  TimeUnit.SECONDS);
        }
        else {
            netChannel.disConnect(reason);
        }
    }
}
