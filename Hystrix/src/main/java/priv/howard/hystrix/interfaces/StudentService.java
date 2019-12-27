package priv.howard.hystrix.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import priv.howard.hystrix.entity.Student;

import java.util.Collection;

//value为绑定的服务名，fallback为回调类(即服务熔断或故障时使用的替代类)
@FeignClient("StudentProvider")
public interface StudentService {
    /**
     * @Description Feign的服务绑定接口
     * Hystrix的服务容错(服务降级)在Feign模块中测试
     */

    @GetMapping("/students")
    public Collection<Student> findAll();

    @GetMapping("/students/port")
    public String getPort();
}
