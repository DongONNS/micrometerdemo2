package com.csu.micrometer;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;

@SpringBootApplication
public class MicrometerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrometerApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName){
        return registry -> registry.config()
                .commonTags( Arrays.asList(Tag.of("Company", "Doumi"), Tag.of("Dep", "busi-tech")))
                //忽略demo.registry.counter的信息
//                .meterFilter(MeterFilter.ignoreTags("demo.registry.counter"))

//                .meterFilter(new MeterFilter() {
//                    @Override
//                    public MeterFilterReply accept(Meter.Id id) {
//                        if(id.getName().contains("server")) {
//                            return MeterFilterReply.DENY;
//                        }
//                        return MeterFilterReply.NEUTRAL;
//                    }
//                })
//              拒绝采集jvm开头的metrics
//                .meterFilter(new MeterFilter() {
//                    @Override
//                    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
//                        if(id.getName().startsWith("summary")) {
//                            return DistributionStatisticConfig.builder()
////                                   在所有的以summary开头的指标中都发布0.95分位数
//                                    .percentiles(0.90,0.95,1.00)
//                                    .build()
//                                    .merge(config);
//                        }
//                        return config;
//                    }
//                })
                .meterFilter(MeterFilter.denyNameStartsWith("jvm"))
                .meterFilter(MeterFilter.denyNameStartsWith("system"))
                .meterFilter(MeterFilter.denyNameStartsWith("tomcat"))
                .meterFilter(MeterFilter.denyNameStartsWith("process"))
                .meterFilter(MeterFilter.denyNameStartsWith("logback"));
//        return registry -> registry.config().commonTags("application", applicationName);

    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public CountedAspect countedAspect(MeterRegistry registry){
        return new CountedAspect(registry);
    }

}
