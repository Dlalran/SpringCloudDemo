package priv.howard.ribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/ribbon")
public class RibbonController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public String testRibbon() {
//        注意：RestTemplate开启负载均衡(添加@LoadBalanced)后，url只能指定为服务注册名而不能是具体的地址
        String result = restTemplate.getForObject("http://StudentProvider/students/port", String.class);
        return "当前访问服务端口:" + result;
    }
}
