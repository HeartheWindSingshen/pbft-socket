package coms.pbft.Utils;

import com.alibaba.fastjson.JSON;
import coms.pbft.ControllerCentre;
import coms.pbft.Message;
import coms.pbft.PbftNode;
import coms.pbft.constant.Constant;
import coms.pbft.constant.Varible;
import coms.pbft.pojo.Position;
import coms.pbft.time.TestLongSend;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FlyShareLocation {
    public static Map<Integer, Timer> timerMapList=new HashMap<>();
    public static Map<Integer, TimerTask>timeroutTaskMapList=new HashMap<>();
    public static void addTimeTask(PbftNode pbftnode){

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Position position = new Position();
                position.setX(pbftnode.getX());
                position.setY(pbftnode.getY());
                String value= JSON.toJSONString(position);
                Message msgClient = new Message();
                msgClient.setOriOrgNode(pbftnode.getNode());
                msgClient.setControllerType(Constant.OPERATION1);
                msgClient.setType(Constant.REQUEST);
                //TODO
                msgClient.setToNode(pbftnode.getView()%pbftnode.getNodeList().size());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(pbftnode.getNode());
                msgClient.setNumber(Varible.number++);
                msgClient.setView(pbftnode.getView());
                msgClient.setValue(value);
                //只在消息上弄客户端ip，端口
                msgClient.setClientIp(pbftnode.getIp());
                msgClient.setClientPort(pbftnode.getPort());
                int mainIndex = 0;
                try {
                    sendUtil.sendNode(pbftnode.getNodeList().get(mainIndex).getIp(), pbftnode.getNodeList().get(mainIndex).getPort(), msgClient);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 定义任务执行的间隔时间（以毫秒为单位）
        long delay = 10; // 初始延迟，即第一次执行的延迟时间
        long interval =4000; // 间隔时间，每隔5秒执行一次

        // 启动定时任务
        timer.scheduleAtFixedRate(task, delay, interval);
        timerMapList.put(pbftnode.getNode(),timer);
        timeroutTaskMapList.put(pbftnode.getNode(),task);
    }
    public static void cancelTimeTask(PbftNode pbftnode){
        Timer timer = timerMapList.get(pbftnode.getNode());
        TimerTask task = timeroutTaskMapList.get(pbftnode.getNode());
        if (timer != null && task != null) {
            System.out.println("quxiao"+pbftnode.getNode());
            task.cancel(); // 取消任务
            timer.cancel(); // 取消定时器
            timer.purge(); // 清除已取消的任务
            timerMapList.remove(pbftnode.getNode()); // 从 map 中移除定时器
            timeroutTaskMapList.remove(pbftnode.getNode()); // 从 map 中移除任务
            System.out.println("定时任务已取消");
        } else {
            System.out.println("找不到对应的定时任务");
        }
    }
}
