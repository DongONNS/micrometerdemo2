package com.csu.micrometer.countnumber;

import com.csu.micrometer.MicrometerApplication;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/demo")
public class FixedTimeCount {

    @Autowired
    MeterRegistry registry;

    private AtomicInteger esSearchCount;

    //用于暂存总条数，避免因为时间的不对应导致为控制
    int tempGap = 0;
    //记录每次的求和数量
    int sumGap = 0;
    //验证每次的gap和是从新出现的;
    int testGap = 0;

    @PostConstruct
    private void init(){
        esSearchCount = registry.gauge("es_search_count",new AtomicInteger(0));
    }

    @RequestMapping("/search")
    public void search() throws InterruptedException {

        for (int i=0;i<new Random().nextInt(5);){
            int num = new Random().nextInt(50)+10;
            sumGap+=num;
        }
        //暂存总和，避免每次都是重设的0；
        tempGap = sumGap;
        //将这个暂存值设为我们的Gauge展示值
        esSearchCount.set(tempGap);

        System.out.println(MicrometerApplication.start_time_of_application);
        //从程序启动到目前所消耗的时间
        long current = System.currentTimeMillis();


        long duration = System.currentTimeMillis() - MicrometerApplication.start_time_of_application;

        System.out.println("执行循环所消耗的时间："+duration);
        System.out.println("数据的有效时长为："+MicrometerApplication.expiry);

        //如果时间从，那就将它重置
        if (duration >= MicrometerApplication.expiry){
            sumGap=0;
        }
        System.out.println("数据的总差值为："+sumGap);
        System.out.println("验证重设以后我们的暂存总和是没有发生改变的   "+tempGap);
        System.out.println("--------------------------------");
    }
}
