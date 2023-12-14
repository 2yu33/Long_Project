package com.example.usercenter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@Slf4j
public class testController {
    @Resource
    ThreadPoolExecutor threadPoolExecutor;
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('test')")
    public String getName(){
        System.out.println("成功！！！！");
        return "恭喜你！！！";
    }
   @GetMapping("/add")
   public void add(String name){
       CompletableFuture.runAsync(()->{
           System.out.println("任务进行中"+ name);
           log.info("任务进行中"+ name+"执行人"+Thread.currentThread().getName());
       },threadPoolExecutor);
       try {
           Thread.sleep(60000);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }
   @GetMapping("/get01")
    public String get(){
       List<String>list = new ArrayList<>();
       list.add("队列长度"+threadPoolExecutor.getQueue().size());
       list.add("任务总数"+threadPoolExecutor.getTaskCount());
       list.add("已经成功完成的任务数量"+threadPoolExecutor.getCompletedTaskCount());
       list.add("正在执行的线程数量"+threadPoolExecutor.getActiveCount());
       return list.toString();
   }
}
