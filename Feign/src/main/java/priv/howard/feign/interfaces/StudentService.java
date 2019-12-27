package priv.howard.feign.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import priv.howard.feign.entity.Student;
import priv.howard.feign.fallback.StudentFallback;

import java.util.Collection;

//value为绑定的服务名，fallback为回调类(即服务熔断或故障时使用的替代类)
@FeignClient(value = "StudentProvider", fallback = StudentFallback.class)
public interface StudentService {
    /**
     * @Description 服务调用接口，添加@FeignClient(value="服务名")，Feign将会对对应的服务和该接口进行绑定，通过调用该接口来调用服务
     * 通过SpringMVC注解@XXXMapping来指定对应的服务映射URL
     * Spring Cloud Feign内部还封装了Ribbon负载均衡和Hystrix服务容错、服务降级支持
     */

//    相当于 StudentProvider地址/students
    @GetMapping("/students")
    public Collection<Student> findAll();

//    相当于 StudentProvider地址/students/port
    @GetMapping("/students/port")
    public String getPort();
}
