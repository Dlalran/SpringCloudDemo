package priv.howard.feign.fallback;

import org.springframework.stereotype.Service;
import priv.howard.feign.entity.Student;
import priv.howard.feign.interfaces.StudentService;

import java.util.Collection;

@Service
public class StudentFallback implements StudentService {
    /**
     * @Description 当指定服务熔断或故障时，通过该类返回自定义的信息,用于实现Hystrix服务降级或服务容错1(对应Dubbo中的mock)
     * 在接口中@FeignClient指定属性fallback为该类
     */
    @Override
    public Collection<Student> findAll() {
        return null;
    }

    @Override
    public String getPort() {
        return "服务暂时不可用";
    }
}
