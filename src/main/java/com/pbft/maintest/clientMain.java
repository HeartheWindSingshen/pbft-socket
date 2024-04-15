package com.pbft.maintest;

import com.pbft.Message;
import com.pbft.PbftNode;
import com.pbft.Utils.sendUtil;
import com.pbft.constant.Constant;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class clientMain {
    public static void main(String[] args) throws IOException {
        //实际client
//        PbftNode pbftNode = new PbftNode(-1, "127.0.0.1", 9000,true);
//        pbftNode.start();
//        if(pbftNode.getNode()==-1){
//            Scanner scanner = new Scanner(System.in);
//            int msgnumber=0;
//            while(true){
//                String value = scanner.next();
//                Message msgClient = new Message();
//                msgClient.setType(Constant.REQUEST);
//                msgClient.setToNode(0);
//                msgClient.setTime(LocalDateTime.now());
//                msgClient.setOrgNode(-1);
//                msgClient.setNumber(msgnumber++);
//                msgClient.setView(1);
//                msgClient.setValue(value);
//        msgClient.setClientIp(pbftNode.getIp());
//        msgClient.setClientPort(pbftNode.getPort());
//                sendUtil.sendNode("127.0.0.1",9001,msgClient);
//            }
//        }
    }
}
