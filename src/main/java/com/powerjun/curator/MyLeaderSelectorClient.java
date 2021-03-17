package com.powerjun.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyLeaderSelectorClient {

    private static final Logger logger = LoggerFactory.getLogger(MyLeaderSelectorClient.class);
    private String hostId;
    public static final String leaderInfoPath = "/test/leader";
    private String leaderSelectorPath = "/test/selector";
    private CuratorFramework client;
    private LeaderSelector leaderSelector;

    public MyLeaderSelectorClient(String hostId) {
        this.hostId = hostId;
    }

    public void start() throws Exception {

        client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryForever(100));
        client.start();

        logger.info("leader selector start");
        leaderSelector = new LeaderSelector(client, leaderSelectorPath, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                logger.info("Host[{}] is taking leader ship ", hostId);

                //更新当前节点信息到ZK
                updatePrimaryInfoToZK();

                logger.info("Host[{}] is doing something...", hostId);
                TimeUnit.DAYS.sleep(1);

                logger.info("Host[{}] end taking leader ship", hostId);
            }
        });

        leaderSelector.setId(hostId);
        leaderSelector.start();
        leaderSelector.autoRequeue();
        logger.info("leader selector started and auto requeue");

        //启动 Primary 节点监听
        PrimaryListener primaryListener = new PrimaryListener(client, hostId);
        primaryListener.start();
        logger.info("Primary listener started");
    }

    private void updatePrimaryInfoToZK() throws Exception {
        client.create()
                .orSetData()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(leaderInfoPath, hostId.getBytes());
    }

    public void close(){
        leaderSelector.close();
        client.close();
    }
}
