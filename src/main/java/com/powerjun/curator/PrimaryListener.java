package com.powerjun.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.powerjun.curator.MyLeaderSelectorClient.leaderInfoPath;

public class PrimaryListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryListener.class);
    private NodeCache nodeCache;
    private String hostId;
    private CuratorFramework client;

    public PrimaryListener(CuratorFramework client, String hostId) {
        this.client = client;
        this.hostId = hostId;
    }

    public void start() throws Exception {
        nodeCache = new NodeCache(client, leaderInfoPath);
        nodeCache.getListenable().addListener(this::primaryChanged);
        nodeCache.start();
    }

    private void primaryChanged() {
        LOGGER.info("found primary changed!");
        ChildData currentData = nodeCache.getCurrentData();
        if (currentData != null ) {
            String newPrimary = new String(currentData.getData());
            if(!hostId.equals(newPrimary)){
                LOGGER.info("Host[{}] will follow the new primary[{}]", hostId, newPrimary);
            }
        }
    }

}
