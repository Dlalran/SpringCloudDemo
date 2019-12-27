package priv.howard.nativeconfigclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class TestController {

    @Value("${server.port}")
    private String port;

    @Value("${version}")
    private String version;

    @GetMapping
    public String getConfig() {
        return "端口名:" + port + " ,自定义变量version:" + version;
    }
}
