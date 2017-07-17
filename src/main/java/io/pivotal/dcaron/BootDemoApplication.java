package io.pivotal.dcaron;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class BootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootDemoApplication.class, args);
	}
}
@RestController
@Slf4j
class Api {

    @Value("${vcap.application.name:local}")
    private String appName;

    @GetMapping(path = "/hello",  produces={MediaType.APPLICATION_JSON_VALUE})
    public String hello() {
		return "Hello, I'm " + appName;
	}


    @RequestMapping(value = "/vcapapplication", method = RequestMethod.GET)
    public Map vcapApplication() throws Exception
    {
        return Utils.getEnvMap("VCAP_APPLICATION");
    }

    @RequestMapping(value = "/vcapservices", method = RequestMethod.GET)
    public Map vcapServices() throws Exception
    {
        return Utils.getEnvMap("VCAP_SERVICES");
    }

    @RequestMapping(value = "/vcapservices_json", method = RequestMethod.GET)
    public String vcapServicesJSON() throws Exception
    {
        return System.getenv().get("VCAP_SERVICES");
    }


    @RequestMapping(value = "/appindex", method = RequestMethod.GET)
    public String appIndex() throws Exception
    {
        String instanceIndex = "N/A";

        try
        {
            instanceIndex =
                    Utils.getEnvMap("VCAP_APPLICATION").getOrDefault("instance_index", "N/A").toString();
        }
        catch (Exception ex)
        {
            log.info("Exception getting application index : " + ex.getMessage());
        }

        return instanceIndex;
    }

    @RequestMapping(value = "/getEnvVariable/{env_var}", method = RequestMethod.GET)
    public String getEnvVariable(@PathVariable String env_var)
    {
        return System.getenv().get(env_var);
    }


}

class Utils
{
    public static Map getEnvMap(String vcap) throws Exception
    {
        String vcapEnv = System.getenv(vcap);
        ObjectMapper mapper = new ObjectMapper();

        if (vcapEnv != null) {
            Map<String, ?> vcapMap = mapper.readValue(vcapEnv, Map.class);
            return vcapMap;
        }

        return new HashMap<String, String>();
    }
}