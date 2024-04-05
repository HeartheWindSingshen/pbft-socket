package com.pbft.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主要用于我们本地导入其他节点信息的实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private int node;
    private String ip;
    private int port;

    @Override
    public String toString() {
        return "Node{" +
                "node=" + node +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
