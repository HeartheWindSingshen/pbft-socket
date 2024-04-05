package com.pbft.maintest;

import com.pbft.Message;
import com.pbft.PbftNode;
import com.pbft.Utils.sendUtil;
import com.pbft.constant.Constant;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class pbftMain1 {
    public static void main(String[] args) throws IOException {
        PbftNode pbftNode = new PbftNode(0, "127.0.0.1", 9001,true);
        pbftNode.start();
//        PbftNode pbftNode = new PbftNode(1, "127.0.0.1", 9002);
//        pbftNode.start();
//        PbftNode pbftNode = new PbftNode(2, "127.0.0.1", 9003);
//        pbftNode.start();
//        PbftNode pbftNode = new PbftNode(3, "127.0.0.1", 9004);
//        pbftNode.start();
        //实际client
//        PbftNode pbftNode = new PbftNode(-1, "127.0.0.1", 9000);
//        pbftNode.start();
        if(pbftNode.getNode()==-1){
            Scanner scanner = new Scanner(System.in);
            int msgnumber=0;
            while(true){
                String value = scanner.next();
                Message msgClient = new Message();
                msgClient.setType(Constant.REQUEST);
                msgClient.setToNode(0);
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(-1);
                msgClient.setNumber(msgnumber++);
                msgClient.setView(1);
                msgClient.setValue(value);
                sendUtil.sendNode("127.0.0.1",9001,msgClient);
            }
        }


    }
}
