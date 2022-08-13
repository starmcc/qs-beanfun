package com.starmcc.beanfun.event;

import org.springframework.context.ApplicationEvent;

public class UpdateProgramEvent extends ApplicationEvent {

    public UpdateProgramEvent(String downloadUrl) {
        super(downloadUrl);
    }
}
