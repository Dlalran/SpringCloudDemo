package priv.howard.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//添加@EnableFeignClients扫描所有标注@FeignClient的类，即Feign服务绑定接口
@EnableFeignClients
@SpringBootApplication
public class FeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }
}
