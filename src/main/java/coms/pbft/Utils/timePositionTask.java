package coms.pbft.Utils;

import com.alibaba.fastjson.JSON;
import coms.pbft.Message;
import coms.pbft.PbftNode;
import coms.pbft.pojo.Field;
import coms.pbft.pojo.Position;
import coms.pbft.target.Target1;
import lombok.Data;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
public class timePositionTask {
    public static Map<Integer, Timer> timerMapList=new HashMap<>();
    public static Map<Integer, TimerTask>timeroutTaskMapList=new HashMap<>();
    public static void addTimeTask(PbftNode node, Field Field){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("自动自动任务执行中....");
                Random random = new Random();
                int iii = random.nextInt(4);
                if(iii==0){
                    if(node.getX()>0){
                        node.setX(node.getX()-1);
                        //这个只针对我们移动位置了，然后target目标看到了触发的
//                        Target1.NodeList.get(node.getNode()).setX(node.getX()-1);
                        Message messagetoTarget1 = new Message();
                        messagetoTarget1.setOriOrgNode(node.getNode());
                        messagetoTarget1.setOrgNode(node.getNode());
                        messagetoTarget1.setToNode(-2);
                        messagetoTarget1.setTime(LocalDateTime.now());
                        //已经减少过了，所以不用减少了
                        Position position = new Position(node.getX(), node.getY());
                        String value = JSON.toJSONString(position);
                        //实际就是将移动的位置坐标，自己移动setX,Y之后，将其发送给target1,作用类似找到目标
                        messagetoTarget1.setValue(value);
                        try {
                            sendUtil.sendNode("127.0.0.1",8888,messagetoTarget1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if(iii==1){
                    if(node.getY()>0){
                        node.setY(node.getY()-1);
//                        Target1.NodeList.get(node.getNode()).setY(node.getY()-1);
                        Message messagetoTarget1 = new Message();
                        messagetoTarget1.setOriOrgNode(node.getNode());
                        messagetoTarget1.setOrgNode(node.getNode());
                        messagetoTarget1.setToNode(-2);
                        messagetoTarget1.setTime(LocalDateTime.now());
                        Position position = new Position(node.getX(), node.getY());
                        String value = JSON.toJSONString(position);
                        //实际就是将移动的位置坐标，自己移动setX,Y之后，将其发送给target1,作用类似找到目标
                        messagetoTarget1.setValue(value);
                        try {
                            sendUtil.sendNode("127.0.0.1",8888,messagetoTarget1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if(iii==2){
                    if(node.getX()<9){
                        node.setX(node.getX()+1);
//                        Target1.NodeList.get(node.getNode()).setX(node.getX()+1);
                        Message messagetoTarget1 = new Message();
                        messagetoTarget1.setOriOrgNode(node.getNode());
                        messagetoTarget1.setOrgNode(node.getNode());
                        messagetoTarget1.setToNode(-2);
                        messagetoTarget1.setTime(LocalDateTime.now());
                        Position position = new Position(node.getX(), node.getY());
                        String value = JSON.toJSONString(position);
                        //实际就是将移动的位置坐标，自己移动setX,Y之后，将其发送给target1,作用类似找到目标
                        messagetoTarget1.setValue(value);
                        try {
                            sendUtil.sendNode("127.0.0.1",8888,messagetoTarget1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                if(iii==3){
                    if(node.getY()<9){
                        node.setY(node.getY()+1);
//                        Target1.NodeList.get(node.getNode()).setY(node.getY()+1);
                        Message messagetoTarget1 = new Message();
                        messagetoTarget1.setOriOrgNode(node.getNode());
                        messagetoTarget1.setOrgNode(node.getNode());
                        messagetoTarget1.setToNode(-2);
                        messagetoTarget1.setTime(LocalDateTime.now());
                        Position position = new Position(node.getX(), node.getY());
                        String value = JSON.toJSONString(position);
                        //实际就是将移动的位置坐标，自己移动setX,Y之后，将其发送给target1,作用类似找到目标
                        messagetoTarget1.setValue(value);
                        try {
                            sendUtil.sendNode("127.0.0.1",8888,messagetoTarget1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        };
        timer.scheduleAtFixedRate(task, 0, 10000);
        timerMapList.put(node.getNode(),timer);
        timeroutTaskMapList.put(node.getNode(),task);

    }
    public static void cancelTimeTask(PbftNode node){
        Timer timer = timerMapList.get(node.getNode());
        TimerTask task = timeroutTaskMapList.get(node.getNode());
        if (timer != null && task != null) {
            System.out.println("quxiao"+node.getNode());
            task.cancel(); // 取消任务
            timer.cancel(); // 取消定时器
            timer.purge(); // 清除已取消的任务
            timerMapList.remove(node.getNode()); // 从 map 中移除定时器
            timeroutTaskMapList.remove(node.getNode()); // 从 map 中移除任务
            System.out.println("定时任务已取消");
        } else {
            System.out.println("找不到对应的定时任务");
        }
    }


}
