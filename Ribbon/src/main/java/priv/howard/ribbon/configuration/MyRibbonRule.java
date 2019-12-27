package priv.howard.ribbon.configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRibbonRule {
    /**
     * @Description 自定义负载均衡策略配置类
     */
    @Bean
    public IRule myRule() {
        return new RoundRobinRule();
    }
}
