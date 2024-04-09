package com.pbft.Utils;


import com.pbft.Message;
import com.pbft.PbftNode;
import com.pbft.constant.Constant;
import com.pbft.constant.Varible;
import com.pbft.pojo.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 用于client判断发送消息是否超时，从而发起view change
 */
public class timeTaskUtil {
    public static Map<Integer,Timer>timerMapList=new HashMap<>();
    public static Map<Integer,TimerTask>timeroutTaskMapList=new HashMap<>();
    public static void addTimeTask(int Messagenumber, PbftNode node,Message message){
        //原来普通消息请求的编号
        System.out.println("启动消息编号"+Messagenumber+"超时判断");
        TimerTask timeoutTask=new TimerTask() {
            @Override
            public void run() {
//                System.out.println("卡住了，消息"+number+"要太阳系广播了");
                //广播view-change
                node.getQueue().offer(message);
                System.out.println("超时队列为 "+node.getQueue());
                int msgNumber = Constant.CLIENTVIEWCHANGE;
                for (Node nodeElse : node.getNodeList()) {
                    Message message = new Message();
                    message.setOrgNode(node.getNode());
                    message.setToNode(nodeElse.getNode());
                    message.setClientIp(node.getIp());
                    message.setClientPort(node.getPort());
                    //将客户端发送消息的序号，剥离到Varible类中
                    message.setNumber(msgNumber);
                    message.setType(Constant.CHANGEVIEW);
                    message.setValue("要进行View-change了！");
                    message.setTime(LocalDateTime.now());

                    String ipSend = nodeElse.getIp();
                    int portSend = nodeElse.getPort();
                    try {
                        if(nodeElse.getNode()!=(node.getView()%node.getNodeList().size()))
                        sendUtil.sendNode(ipSend,portSend,message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //TODOFinished 估计要重发之前的消息，因为之前消息client没有收到是吧,还要修改进行视图变更之后我的client的view值++
                }
                node.setView(node.getView()+1);
            }
        };
        Timer timer=new Timer();
        timer.schedule(timeoutTask,2000);
        //加入Map
        timerMapList.put(Messagenumber,timer);
        timeroutTaskMapList.put(Messagenumber,timeoutTask);
    }
    public static void cancelTimeTask(int number){
        TimerTask timeoutTask=timeroutTaskMapList.get(number);
        Timer timer = timerMapList.get(number);
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timer.purge(); // 清除取消的任务
            System.out.println("已取消消息编号"+number+"超时操作");
        }
    }
}
