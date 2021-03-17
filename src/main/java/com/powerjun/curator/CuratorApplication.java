package com.powerjun.curator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CuratorApplication {

    public static void main(String[] args) throws Exception {
        MyLeaderSelectorClient myLeaderSelectorClient = new MyLeaderSelectorClient(args[0]);
        myLeaderSelectorClient.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            myLeaderSelectorClient.close();
        }));

        Executors
                .newSingleThreadExecutor()
                .awaitTermination(1, TimeUnit.DAYS);
    }


}
