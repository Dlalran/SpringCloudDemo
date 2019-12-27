package priv.howard.zuulgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 添加@EnableZuulProxy来将其作为Zuul网关运行,也可以添加@EnableZuulServer,
 * 第一个比第二个多三个过滤器,分别是
 *      1.PreDecorationFilter:根据路由规则映射地址,以及请求上下文的设置
 *      2.RibbonRoutingFilter:只对通过serviceId配置路由规则的请求生效,使用ribbon和hystrix来向服务实例发起请求
 *      3.SimpleHostRoutingFilter:只对通过url配置路由规则的请求生效,直接通过HttpClient发起请求
 */
@EnableZuulProxy
@SpringBootApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
