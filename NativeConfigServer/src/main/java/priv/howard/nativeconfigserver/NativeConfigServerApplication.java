package priv.howard.nativeconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

//添加@EnableConfigServer使该应用作为Config Server启动
@EnableConfigServer
@SpringBootApplication
public class NativeConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NativeConfigServerApplication.class, args);
    }
}
