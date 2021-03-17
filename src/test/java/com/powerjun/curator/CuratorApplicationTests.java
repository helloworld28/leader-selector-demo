package com.powerjun.curator;

import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

class CuratorApplicationTests {

    @Test
    void contextLoads() throws Exception {
        TestingServer testingServer = new TestingServer(2281, true);
        TimeUnit.DAYS.sleep(1);
    }

}
