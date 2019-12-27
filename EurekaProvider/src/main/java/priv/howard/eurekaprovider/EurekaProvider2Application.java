package priv.howard.eurekaprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//注意:服务注册方(Eureka客户端)只要加入Eureka-Client依赖就会自动注册服务,可以省略@EnableEurekaClient注解
//@EnableEurekaClient
@SpringBootApplication
public class EurekaProvider2Application {
    public static void main(String[] args) {
        SpringApplication.run(EurekaProvider2Application.class, args);
    }
}
