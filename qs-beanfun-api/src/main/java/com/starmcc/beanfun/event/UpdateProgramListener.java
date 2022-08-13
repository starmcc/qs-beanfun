package com.starmcc.beanfun.event;

import com.starmcc.beanfun.client.UpdateClient;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UpdateProgramListener implements ApplicationListener<UpdateProgramEvent> {

    @Async
    @Override
    public void onApplicationEvent(UpdateProgramEvent event) {
        String downloadUrl = (String) event.getSource();
        // 异步更新
        UpdateClient.getInstance().update(downloadUrl);
    }
}
