[toc]

# Spring Cloud Netflix

官网： [Spring Cloud](https://spring.io/projects/spring-cloud)

---

## 父工程中加入 Spring Cloud 基础依赖控制

```xml
<dependencyManagement>
        <dependencies>
<!--            Spring Cloud基础环境依赖-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
<!--                注意：Spring Boot 2.2.2.RELEASE版本对应Spring Cloud Hoxton版本-->
                <version>Hoxton.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

​	**注意 Spring Boot 和 Spring Cloud 的版本对应关系，见官网或下表**

| Spring Cloud | Spring Boot |
| ------------ | ----------- |
| Hoxton       | 2.2.x       |
| Greenwich    | 2.1.x       |
| Finchley     | 2.0.x       |
| Edgware      | 1.5.x       |
| Dalston      | 1.5.x       |

---

## Eureka 注册中心

​		对于分布式系统的CAP原则(Consistency一致性、Availability可用性、Partition tolerance分区容忍性)，三者最多只能同时实现两个，传统的关系型数据库是CA，而分布式存储系统中都要保证P，Eureka保证的是AP，对应于Zookeeper则是CP。体现之一是Eureka的自我保护机制，即使一个服务已经长时间未处理请求或者故障甚至已经下线，其仍然保留其注册表中的注册数据一段时间，保证了服务的高可用，但是准确的服务状态即一致性被放弃。

![CAP](https://bkimg.cdn.bcebos.com/pic/5bafa40f4bfbfbed9c15b19b72f0f736aec31f81@wm_1,g_7,k_d2F0ZXIvYmFpa2U5Mg==,xp_5,yp_5)

#### 	1. 配置注册中心

- `pom.xml`中加入Eureka Server依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

- `application.yml`中配置如下

```yml
#Eureka Server注册中心端口一般默认为8761
      server:
        port: 8761

      eureka:
        client:
          #    是否将自身作为服务注册到注册中心(注册中心本身不需要)
          register-with-eureka: false
          #    是否从注册中心获取注册信息(可以获取其他注册中心的数据)
          fetch-registry: false
          #    配置注册中心地址
          service-url:
            #      服务端未指定服务
            defaultZone: http://localhost:8761/eureka/
        #     关闭Eureka Server的自我保护模式
        server:
          enable-self-preservation: false
```

- 启动类上加入注解`@EnableEurekaServer`

```java
package priv.howard.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

- 运行主配置类的主函数来启动 Eureka Server 注册中心，并通过配置的端口(默认是 8761)访问，如此例是 <u>localhost:8761</u>

  在 **Instances currently registered with Eureka** 项可以观察已经注册的服务的应用注册名、可用节点以及 URL

  

  #### 2. 注册服务提供者到注册中心

- `pom.xml`中加入Eureka Client依赖，*无论是服务注册方还是服务提供方都使用该依赖注册到Eureka Server*

  **注意还要引入Spring Web依赖，最好加在父工程依赖中进行统一引入**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- `application.yml`中配置如下

```yml
server:
  port: 8081

#指定服务注册名称
spring:
  application:
    name: StudentProvider

eureka:
  client:
#    指定Eureka Server注册中心地址
    service-url:
      defaultZone: http://localhost:8761/eureka/

#  是否将服务的IP地址注册到注册中心
  instance:
    prefer-ip-address: true
```

- 进行服务实现，并通过Spring MVC实现RESTful接口，配置好服务对应的URL

```java
package priv.howard.eurekaprovider.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import priv.howard.eurekaprovider.entity.Student;
import priv.howard.eurekaprovider.repository.StudentRepository;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class StudentController {
    /**
     * @Description Student的Controller,通过SpringMVC来实现RESTful接口
     */

//    通过Spring EL注入配置文件中指定的属性值,这里是获取当前服务端口
    @Value("${server.port}")
    private String port;

//    注入Repository
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public Collection<Student> findAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable("id") int id) {
        return studentRepository.findById(id);
    }

    @PostMapping
    public Student save(@RequestBody Student student) {
        studentRepository.saveOrUpdate(student);
        return student;
    }

    @PutMapping
    public void update(@RequestBody Student student) {
        studentRepository.saveOrUpdate(student);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") int id) {
        studentRepository.deleteById(id);
    }

//    获取当前服务端口，测试负载均衡等功能使用
    @GetMapping("/port")
    public String getPort() {
        return this.port;
    }
}
```

- 启动Spring Boot主配置类，通过Eureka Server可以观察到服务提供者是否被成功注册

```java
package priv.howard.eurekaprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//注意:服务注册方(Eureka客户端)只要加入Eureka-Client依赖就会自动注册服务,可以省略@EnableEurekaClient注解
//@EnableEurekaClient
@SpringBootApplication
public class EurekaProvider1Application {
    public static void main(String[] args) {
        SpringApplication.run(EurekaProvider1Application.class, args);
    }
}
```



#### 	3. 注册服务消费者到注册中心

- `pom.xml`中Eureka Client依赖(*同上*)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

- `application.yml`中配置如下，**注意服务名和端口号要唯一**

```yml
server:
  port: 8082

spring:
  application:
    name: StudentConsumer

eureka:
    client:
#    指定Eureka Server注册中心地址
      service-url:
        defaultZone: http://localhost:8761/eureka/
#  是否将服务的IP地址注册到注册中心
    instance:
      prefer-ip-address: true
```

- 通过Spring提供的 `RestTemplate` 来对REST HTTP服务进行访问，使用方式见代码，这里调用了对于Student资源的增(Post)删(Delelte)改(Put)查(Get)服务

```java
package priv.howard.eurekaconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import priv.howard.eurekaconsumer.entity.Student;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class ConsumerController {
    /**
     * @Description 消费者Controller,使用RestTemplate通过URL远程调用服务的REST接口 {@link StudentConroller}
     * 注意:不在Eureka Server注册也可以使用(即以REST风格对指定URL发出请求),但是注册后才能成为服务消费者并使用Spring Cloud
     */

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Collection<Student> findAll() {

        final String url = "http://localhost:8081/students";
        /**
         * 通过RestTemplate的方法来以REST指定的请求方法来访问对应的url(第一个参数),并返回结果对象(第二个参数指定)
         * getForEntity返回的是整个Response的封装对象ResponseEntity,可以通过其getBody方法获得指定类型结果
         * getForObject则是返回封装后的指定类型结果对象
         */
//        Collection result = restTemplate.getForEntity("http://localhost:8081/students", Collection.class).getBody();
        Collection result = restTemplate.getForObject(url, Collection.class);
        return result;
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable("id") int id) {
//        地址传入参数时,注意将参数拼接进url
        final String url = "http://localhost:8081/students/" + id;
        Student result = restTemplate.getForObject(url, Student.class);
        return result;
    }

    @PostMapping
    public Student save(@RequestBody Student student) {
        final String url = "http://localhost:8081/students";
        /**
         * postForEntity第二个参数传递增加操作的数据对象,第三个指定返回的类型
         * 标准的RESTful设计中,新增后需要返回新增的完整对象,则第三个参数指定为对象类型本身(注意服务实现中要返回)
         */
        Student result = restTemplate.postForObject(url, student, Student.class);
        return result;
    }

    @PutMapping
    public void update(@RequestBody Student student) {
//         put()方法没有返回值
        final String url = "http://localhost:8081/students";
        restTemplate.put(url, student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        final String url = "http://localhost:8081/students/" + id;
        restTemplate.delete(url);
    }
}
```

- 在主配置类中注入RestTemplate的Bean，并启动服务消费者

```java
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
```

- 在Eureka Server中查看消费者是否成功注册，并访问URL来测试对应的服务是否能成功调用

---

## Zuul 服务网关

​		Netfilx Zuul实现服务网关，其负责对请求转发给对应的服务提供者，使得可以通过应用名来对服务进行访问，并自带了基于轮询策略的负载均衡

- pom.xml中加入Zuul依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

- application.yml中配置

```yml
zuul:
routes:
#    简写方式:  服务注册名: 映射地址(当前情况映射地址也可以省略)
    StudentProvider: /StudentProvider/**
#    完整形式1(服务注册名转发):  服务注册名:
#                                   path:映射地址
#                                   service-id:处理请求的服务注册名
#    StudentProvider:
#      path: /StudentProvider/**
#      service-id: StudentProvider
#    完整形式2(URL转发):        服务注册名:
#                                   path:映射地址
#                                   url:转发的url
#    StudentProvider:
#      path: /StudentProvider/**
#      url: http://localhost:8081
```

- 主配置类中加入注解`@EnableZuulProxy`使其作为Zuul网关启动

```java
/**
 * 添加@EnableZuulProxy来将其作为Zuul网关运行,也可以添加@EnableZuulServer,
 * 第一个比第二个多三个过滤器,分别是
 *      1.PreDecorationFilter:根据路由规则映射地址,以及请求上下文的设置
 *      2.RibbonRoutingFilter:只对通过serviceId配置路由规则的请求生效,使用ribbon和hystrix来向服务实例发起请求
 *      3.SimpleHostRoutingFilter:只对通过url配置路由规则的请求生效,直接通过HttpClient发起请求
 */
@EnableZuulProxy
```

- 通过指定的地址(http://网关地址:网关端口/路由地址)来访问指定的服务

---

## Ribbon 负载均衡

#### 	1. 使用Ribbon

​		Spring Cloud Ribbon基于Netfilx Ribbon，通过对于注册中心Eureka Server中服务可调用的服务提供者列表来进行负载均衡，因此也需要注册到注册中心(即加入Eureka Client依赖)。

​		*Ribbon已被集成在Eureka Client中因此不需要额外依赖*

- 在主配置类注入`RestTemplate`的方法上添加注解`@LoadBalanced`以开启Ribbon负载均衡

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

- 开启多个服务提供者以测试负载均衡，可以通过新建多个主配置类，在运行前改变端口并运行不同的配置类来启动多个服务提供者
- 服务提供者Controller中实现获取当前服务端口的方法

```java
//    通过Spring EL注入配置文件中指定的属性值,这里是获取当前服务端口
    @Value("${server.port}")
    private String port;

//    获取当前服务端口，测试负载均衡等功能使用
    @GetMapping("/port")
    public String getPort() {
        return this.port;
    }
```

- 调用上述服务，**注意RestTemplate开启负载均衡后，URL只能指定为服务注册名而不能是具体的地址**

```java
	@GetMapping
    public String testRibbon() {
//        注意：RestTemplate开启负载均衡(添加@LoadBalanced)后，url只能指定为服务注册名而不能是具体的地址
        String result = restTemplate.getForObject("http://StudentProvider/students/port", String.class);
        return "当前访问服务端口:" + result;
    }
```

- 重复访问以上服务，查看获得的端口号是否变化，**注意Ribbon默认的负载均衡是随机**

​	2. 自定义负载均衡策略

​		**有以下两种自定义负载均衡策略的方式**

- 1. 在主配置类中注入名为`ribbonRule`的Bean，返回需要使用的负载均衡策略类

  ```java
  @Bean
  public IRule ribbonRule() {
      /**
       * 返回负载均衡策略对应的类:1、RandomRule(默认):随机策略
       *                        2、RoundRobinRule:轮询策略
       *                        3、AvailabilityFilteringRule:过滤故障、熔断的服务，并在剩下服务中轮询
       *                        4、WeightedResponseTimeRule:根据平均响应时间分配权重
       *                        5、RetryRule:轮询访问服务，失败时在指定时间内重试
       *                        6、BestAvaibleRule:过滤故障、熔断服务，再选择并发量最小的服务
       *                        7、ZoneAvoidanceRule:复合判断服务所在Zone性能和服务本身的性能
       */
      return new RoundRobinRule();
  }
  ```

- 2. 创建自定义配置类
     - 新建一个配置类(在类上添加注解`@Configuration`)，注入一个名称任意的负载均衡策略类

  ```
  package priv.howard.ribbon.configuration;
  
  import com.netflix.loadbalancer.IRule;
  import com.netflix.loadbalancer.RoundRobinRule;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  @Configuration
  public class MyRibbonRule {
      /**
       * @Description 自定义负载均衡策略配置类
       */
      @Bean
      public IRule myRule() {
          return new RoundRobinRule();
      }
  }
  ```

  - 在主配置类上添加注解`@RibbonClient`，属性name指定负载均衡的服务名，configuration指定负载均衡策略的配置类，来对指定服务实现指定策略的负载均衡

  ```java
  @RibbonClient(name = "StudentProvider", configuration = MyRibbonRule.class)
  ```

---

## Feign接口式调用

​		Spring Cloud Feign基于Netfilx Feign，使得服务消费者可以通过接口来调用HTTP服务API，同时整合了Ribbon和下面介绍的Hystrix。

​		*这里使用的实际上是OpenFeign，与Feign的区别是增加了对于Spring MVC注解的*

- `pom.xml`中加入Feign依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

- 新建接口

  ​		用于绑定服务类，在接口上添加注解`@FeignClient`,参数value为绑定的服务名，fallback(可省略)为回调类(服务降级用，即服务熔断或故障时使用的替代类)，接口方法通过Spring MVC的Mapping注解通过URL来和服务方法进行绑定

```java
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
```

- 主配置类中加入注解`@EnableFeignClients`扫描所有标有注解的服务接口

- 在Controller中注入该接口来调用服务，代替RestTemplate

```java
	@Autowired
    private StudentService studentService;

    @GetMapping
    public Collection<Student> findAll() {
        Collection<Student> result = studentService.findAll();
        return result;
    }
```

---

## Hystrix服务降级与监控

​		Netfilx Feign的功能包括服务熔断或故障时的服务隔离、服务降级以及服务运作时的监控与告警功能。

​		*由于其被Feign集成，因此使用时需要借助Feign*

- 在pom.xml中加入依赖，**注意仅使用服务降级时加入Feign依赖即可，其余是服务监控所需依赖**

```xml
<dependencies>
<!--        Feign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
<!--        Actuator(Spring Boot应用监控与管理)-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
<!--        Hystrix-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
<!--        Hystrix Dashboard(可视化)-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
    </dependencies>
```

- application.yml中开启Hystrix功能

```yml
#开启Hystrix支持
feign:
  hystrix:
    enabled: true
```

- 主配置类也要加入Feign的扫描注解`@EnableFeignClients`

#### 	1. 服务降级

- 首先在主配置类中加入注解`@EnableCircuitBreaker`来提供熔断器功能

##### 		服务容错类

- 新建服务容错类

  ​		方法与Feign服务接口对应，用于在对应服务熔断或故障时替代服务返回自定义信息或处理，用于实现服务降级或服务容错

```java
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
```

- 在Feign的服务接口类上的`@FeignClient`注解添加参数fallback指定为该类

```java
@FeignClient(value = "StudentProvider", fallback = StudentFallback.class)
```

- 主配置类中还要添加Feign的注解`@EnableFeignClients`

  ##### 服务容错方法

- 在服务类、接口或者Controller中需要进行容错的方法上加入注解`@HystrixCommand`并添加参数fallbackMethod来指定服务故障时顶替的方法名

```java
    @GetMapping("/port")
//    通过@HystrixCommand注解的属性fallbackMethod指定服务失败时代替的方法名(同接口的代替类)
    @HystrixCommand(fallbackMethod = "portFallback")
    public String port() {
        return studentService.getPort();
    }
```

- 编写负责容错时被调用的方法

```java
//    该方法作为port替代方法
    public String portFallback() {
        return "服务当前不可用!";
    }
```

#### 	2. 服务监控

- application.yml中再添加监控节点配置

```yml
#配置暴露的监控节点
management:
  endpoints:
    web:
      exposure:
        include: hystrix.stream
```

- 主配置类中加入注解

```java
//启用Hystrix可视化监控
@EnableHystrixDashboard
```

- 访问监控台

  ​		通过本应用的URL后添加`/hystrix`访问监控台Hystrix Dashboard，输入监控数据源URL后添加`/actuator/hystrix.stream`，并点击**Monitor Stream**进入监控台，访问该服务的请求的相关数据将被记录并显示

#### 3. 集群监控

​		通过Turbine对一个服务的集群进行监控

- pom.xml中加入Turbine依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
</dependency>
```

- application.yml中加入Turbine配置，如监控的服务名、集群名等

```yml
turbine:
  #  监控的服务名
  app-config:
  #    监控的集群名，默认为default，即服务下的所有提供者为一个集群
  aggregator:
    cluster-config: default
  #    通过主机名-端口名来对服务提供者进行区分，默认就为true
  combine-host-port: true
```

- 主配置类上添加注解`@EnableTurbine`
- 运行后通过本应用的URL后添加`/turbine.stream?cluster=监控集群名`来进行监控，可以同服务监控一样加入Hystrix Dashboard进行可视化监控

---

## Config Server 远程配置中心

​		**注意这里所说的本地是指相对于配置中心，即配置文件和配置中心在同一主机中，远程指的是在Git仓库如GitHub中**

- pom.xml中加入Config Server依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

#### 	1. 本地配置中心 

- 添加本地配置文件

  ​		在应用路径下新建存放配置文件的文件夹，如config，并在其中新建配置文件，命名为 使用该配置的应用名-环境名.yml，在里面给出配置内容，例如：

```
server:
  port: 8070

#自定义变量
version: 1.0.0
```

- application.yml中配置，包含配置中心的注册名、配置环境、本地配置文件的路径

```yml
spring:
  application:
    name: NativeConfigServer
#  环境配置
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
#          指定本地配置文件的存放路径
          search-locations: classpath:/shared
```

- 主配置类中添加注解`@EnableConfigServer`使其作为Config Server启动

  #### 2. 远程配置中心

- GitHub中添加配置仓库，新建配置文件，文件名命名规则同上

- application.yml中配置，包含配置中心的注册名、使用的配置环境、Git仓库URI、检索路径等

```yml
spring:
  application:
    name: RemoteConfigServer
  cloud:
    config:
      server:
        git:
#          设置Git仓库地址，公开仓库使用HTTPS URI不需要用户名密码
          uri: https://github.com/Dlalran/SpringCloudDemo.git
#          强制拉取
#          force-pull: true
#          仓库检索配置文件的路径
          search-paths: config
#          公开仓库不需要配置用户名和密码名
#          username:
#          password:
#          也可以通过SSH方式登录，换行粘贴SSH私钥 TODO 尝试不成功
#          private-key: |
```



## Config Client 使用远程配置

- pom.xml中加入Config Client依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
```

#### 	1. 使用本地配置中心

- **<u>bootstrap.xml</u>**中指定配置，包括Config Server配置中心的地址、使用的环境名

```yml
spring:
  application:
    name: NativeConfigClient
#    从Config Server中指定的配置文件目录中，
#    获取名字为 应用名(spring.application.name)-使用环境名(profiles-active).yml 的配置文件
  profiles:
    active: dev
  cloud:
#    配置Config Server的地址
    config:
      uri: http://localhost:8762
#      开启快速失败，检验Config Server是否正常运作，否则立即报错
      fail-fast: true
```

#### 	2. 使用远程配置中心

- `bootstrap.xml`中指定配置，包括Config Server配置中心的服务名、配置文件名、获取项目分支名

```yml
spring:
  cloud:
    config:
#      获取配置文件的文件名
      name: remoteconfigclient
#      获取的项目分支名
      label: master
#      通过服务名发现Config Server
      discovery:
        enabled: true
#        Config Server的注册名
        service-id: RemoteConfigServer
```

---

## Sleuth + Zipkin 服务跟踪

​		Zipkin链路跟踪工具可以查看分布式应用下服务调用关系、调用延迟等数据，Spring Cloud中实际上通过Spring Cloud Sleuth进行链路追踪，并将数据传给Zipkin分布式数据追踪系统进行监控。

​		*Zipkin Server监控台建议从[官网](https://github.com/openzipkin/zipkin)下载可执行jar包来代替手动组装。*

#### <u>被监控</u>服务的配置

- pom.xml中添加Zipkin依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

- application.yml中添加Sleuth配置和Zipkin监控台URL

```yml
spring:
  application:
    name: ZipkinClient
#  
  sleuth:
    web:
      client:
#        开启服务追踪
        enabled: true
#    设置采样比例，默认为1.0
#    TODO 据说高版本的Spring Cloud需要用sampler.rate代替sampler.probability来指定采样率，未详细测试
    sampler:
      rate: 1
#      probability: 1.0
#  Zipkin的URL
  zipkin:
    base-url: http://localhost:9411/
```

- 运行Zipkin Server和被监控应用以查看数据
