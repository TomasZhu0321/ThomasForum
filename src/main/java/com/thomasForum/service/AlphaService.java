package com.thomasForum.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {
    public AlphaService(){
        System.out.println("Construct Application");
    }
    @PostConstruct
    public void init(){
        System.out.println("Initialize application");
    }
    @PreDestroy
    public void destroy(){
        System.out.println("Destory Application");
    }
}
