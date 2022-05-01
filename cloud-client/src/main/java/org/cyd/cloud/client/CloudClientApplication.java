package org.cyd.cloud.client;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class CloudClientApplication {

    @Autowired
    private EurekaClient discoveryClient;

    @Autowired
    private DiscoveryClient discoveryClient2;


    @Autowired
    RestTemplate restTemplate;




    @RequestMapping("/")
    public String home() {
        InstanceInfo server = discoveryClient.getNextServerFromEureka("cloud-server", false);
        return server.getHomePageUrl();
    }

    @RequestMapping("/getServerHome")
    public String getServerHome(){
        InstanceInfo server = discoveryClient.getNextServerFromEureka("cloud-server", false);
        String forObject = restTemplate.getForObject(server.getHomePageUrl(), String.class);
        System.out.println("----------------------------执行-------");
        return forObject;
    }

    @RequestMapping("/serviceUrl/{version}")
    public String serviceUrl(@PathVariable("version")String version) {
        List<ServiceInstance> list = discoveryClient2.getInstances("cloud-server");
        if (list != null && list.size() > 0 ) {

            Optional<ServiceInstance> first = list.stream().filter(a -> a.getMetadata().containsKey("version") && a.getMetadata().get("version").equals(version)).findFirst();
            ServiceInstance serviceInstance = first.orElse(new EurekaServiceInstance(InstanceInfo.Builder.newBuilder().getRawInstance()));
            return serviceInstance.getInstanceId();

        }
        return null;
    }

    public static void main(String[] args) {
        SpringApplication.run(CloudClientApplication.class, args);
    }



}
