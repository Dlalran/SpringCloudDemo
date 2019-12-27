package priv.howard.eurekaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EurekaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    /**
     *  加入RestTemplate进入IOC容器,过RestTemplate来对RESTful接口进行调用
     *  注意:在主配置类(或加了@Configuration的其他配置类)中写出方法,并在方法上添加@Bean,等于<bean id="方法名" class="返回值类型"/>
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
