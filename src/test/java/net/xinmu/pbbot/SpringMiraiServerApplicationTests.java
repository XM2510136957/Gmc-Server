package net.xinmu.pbbot;

import net.xinmu.pbbot.plugin.DianGePlugin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringMiraiServerApplicationTests {

    @Autowired
    DianGePlugin dianGePlugin;
    @Test
    void contextLoads() {
        dianGePlugin.requestApi("三国杀");
    }

}
