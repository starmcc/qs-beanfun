package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.client.BeanfunClient;

public class VideoHandler {


    public static boolean run(boolean status) {
        try {
            BeanfunClient.run().heartbeat("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }


}
