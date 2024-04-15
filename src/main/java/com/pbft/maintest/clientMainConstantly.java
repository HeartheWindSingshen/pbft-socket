package com.pbft.maintest;

import com.pbft.Message;
import com.pbft.PbftNode;
import com.pbft.Utils.sendUtil;
import com.pbft.constant.Constant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

public class clientMainConstantly {
    public static void main(String[] args) throws IOException, InterruptedException {
        PbftNode pbftNode = new PbftNode(-1, "127.0.0.1", 9000,true);
        pbftNode.start();
        if(pbftNode.getNode()==-1){
            for(int i=0;i<1;i++){
                Message msgClient = new Message();
                msgClient.setType(Constant.REQUEST);
                msgClient.setToNode(0);
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(-2);
                msgClient.setNumber(i);
                msgClient.setView(1);
                msgClient.setValue("hello "+i);
                msgClient.setClientIp(pbftNode.getIp());
                msgClient.setClientPort(pbftNode.getPort());
                sendUtil.sendNode("127.0.0.1",9001,msgClient);
//                Thread.sleep(1000);
            }
        }
    }
}
