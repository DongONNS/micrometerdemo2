package com.csu.micrometer.controller;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.TimeWindowFixedBoundaryHistogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/demo")
public class SummaryController {
    //注入注册表
    @Autowired
    MeterRegistry registry;

    private AtomicInteger try_to_get_mean;

    @PostConstruct
    private void init(){
        try_to_get_mean = registry.gauge("try_to_get_mean",new AtomicInteger(0));

    }

    @RequestMapping("/summary")
    public void recording(){
        DistributionSummary summary = Metrics.summary("summary","name","summary");
        DistributionSummary fixed_time_windows_summary = DistributionSummary.builder("fixed_time_windows_summary")
                                       //这个是是否发布一个histogram，但他的数据数累计值；而且有默认的数值设定；
                                       .publishPercentileHistogram(true)
                                       //设定bucketd的下限
                                       .minimumExpectedValue(1l)
                                       //这个相当于添加了一个可控的边界进去了，整型数据都可以int char long
//                                       .sla(60,70,75,80,90,95)
                                       //设定bucket的上限
                                       .baseUnit("个数")
                                       .maximumExpectedValue(100L)
                                       //发布一个百分位近似的数,也是一个累积值
                                       .publishPercentiles(0.90,0.95,1.00)
                                       //此处设定的是精度，默认是1，越大得到的百分比近似值越高
//                                       .percentilePrecision(1)
                                       //这样就可以设定数据只记录这30s的，缓冲区在30s后会自动的清空
                                       //这个是具体可证的，但是关于BufferLength的数据就是会累加起来
                                       .distributionStatisticExpiry(Duration.ofSeconds(30))
                                       //设置缓冲区的大小,这个length代表的是直方图的数目，默认是三张直方图数据
                                       .distributionStatisticBufferLength(1)
//                                       .scale(0.5d)
                                       .register(registry);

        for(int i=0;i<10;i++){
            int a = new Random().nextInt(100);
            System.out.println(a);
            fixed_time_windows_summary.record(a);
        }

//        AbstractTimeWindowHistogram
        HistogramGauges histogramGauges;

        TimeWindowFixedBoundaryHistogram twfbh ;

//        System.out.println("summary每一次记录数据的和的95%： "+summary2.totalAmount()*0.95);
//        System.out.println("summary每一次记录数据的和值： "+summary2.totalAmount());

//        summary.record(new Random().nextInt(100));
//        summary.record(new Random().nextInt(1000));
//        summary.record(new Random().nextInt(10000));
//
//        //这里有个问题就是只能是整型的数字，不能是float、double等类型；
//        //曲线救国的例子；
        try_to_get_mean.set((int)fixed_time_windows_summary.mean());

//        System.out.println(summary.measure());
//        System.out.println(summary.count());
//        System.out.println(summary.max());
//        System.out.println(summary.mean());
//        System.out.println(summary.totalAmount());
    }
}
