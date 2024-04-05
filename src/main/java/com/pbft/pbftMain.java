package com.pbft;

import com.pbft.Utils.sendUtil;
import com.pbft.Utils.timeTaskUtil;
import com.pbft.constant.Constant;
import com.pbft.constant.Varible;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
public class pbftMain {

    public static void main(String[] args) throws IOException {
        PbftNode pbftNode1 = new PbftNode(0, "127.0.0.1", 9001,false);
        pbftNode1.start();
        PbftNode pbftNode2 = new PbftNode(1, "127.0.0.1", 9002,true);
        pbftNode2.start();
        PbftNode pbftNode3 = new PbftNode(2, "127.0.0.1", 9003,true);
        pbftNode3.start();
        PbftNode pbftNode4 = new PbftNode(3, "127.0.0.1", 9004,true);
        pbftNode4.start();
        //实际client
        PbftNode pbftNode = new PbftNode(-1, "127.0.0.1", 9000,true);
        pbftNode.start();
        if(pbftNode.getNode()==-1){
            Scanner scanner = new Scanner(System.in);
            while(true){
                String value = scanner.next();
                Message msgClient = new Message();
                msgClient.setType(Constant.REQUEST);
                msgClient.setToNode(0);
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(-1);
                msgClient.setNumber(Varible.number++);
                msgClient.setView(1);
                msgClient.setValue(value);
                //只在消息上弄客户端ip，端口
                msgClient.setClientIp(pbftNode.getIp());
                msgClient.setClientPort(pbftNode.getPort());
                sendUtil.sendNode("127.0.0.1",9001,msgClient);
                //因为这个序列号，发送了自动++，所以才使用序列号-1
                timeTaskUtil.addTimeTask(Varible.number-1,pbftNode);
                //主要用于当主节点作恶时候，reply阶段判断返回共识消息是否是共识消息
                pbftNode.setOrgClientMessageValue(value);
            }
        }


    }

}
