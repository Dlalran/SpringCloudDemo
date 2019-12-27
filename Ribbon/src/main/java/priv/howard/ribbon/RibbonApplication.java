package priv.howard.ribbon;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import priv.howard.ribbon.configuration.MyRibbonRule;

//1可以通过@RibbonClient，name指定负载均衡的服务名，configuration指定负载均衡策略的配置类，来对指定服务实现指定策略的负载均衡
//@RibbonClient(name = "StudentProvider", configuration = MyRibbonRule.class)

@SpringBootApplication
public class RibbonApplication {
    public static void main(String[] args) {
        SpringApplication.run(RibbonApplication.class, args);
    }


//    在RestTemplate的Bean上添加@LoadBalanced来使用Ribbon负载均衡

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    2直接在主配置类注入Bean来指定负载均衡的策略
    @Bean
    public IRule ribbonRule() {
        /**
         * 返回负载均衡策略对应的类:1、RandomRule(默认):随机策略
         *                        2、RoundRobinRule:轮询策略
         *                        3、AvailabilityFilteringRule:过滤故障、熔断的服务，并在剩下服务中轮询
         *                        4、WeightedResponseTimeRule:根据平均响应时间分配权重
         *                        5、RetryRule:轮询访问服务，失败时在指定时间内重试
         *                        6、BestAvaibleRule:过滤故障、熔断服务，再选择并发量最小的服务
         *                        7、ZoneAvoidanceRule:复合判断服务所在Zone性能和服务本身的性能
         */
        return new RoundRobinRule();
    }
}
