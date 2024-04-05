package com.pbft;

import com.pbft.Utils.sendUtil;
import com.pbft.Utils.timeTaskUtil;
import com.pbft.constant.Constant;
import com.pbft.constant.Varible;
import com.pbft.pojo.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class pbftMain {

    public static void main(String[] args) throws IOException, InterruptedException {
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
            //请求view
            Message msgClientQuest = new Message();
            msgClientQuest.setClientPort(pbftNode.getPort());
            msgClientQuest.setClientIp(pbftNode.getIp());
            msgClientQuest.setType(Constant.GETVIEW);
            msgClientQuest.setNumber(Varible.number++);
            msgClientQuest.setValue("请求获得集群的view");
            msgClientQuest.setOrgNode(pbftNode.getNode());

            Random random = new Random();
            List<Node> listNode = pbftNode.getNodeList();
            int i = random.nextInt(listNode.size());
            msgClientQuest.setToNode(listNode.get(i).getNode());
            sendUtil.sendNode(listNode.get(i).getIp(),listNode.get(i).getPort(),msgClientQuest);


            Scanner scanner = new Scanner(System.in);
            while(true){
//                while(!pbftNode.getQueue().isEmpty()){
//                    System.out.println("hahaha");
//                    Message messageTop = pbftNode.getQueue().poll();
//                    int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
//                    messageTop.setToNode(mainIndex);
//                    messageTop.setTime(LocalDateTime.now());
//                    messageTop.setView(pbftNode.getView());
//                    sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(),pbftNode.getNodeList().get(mainIndex).getPort(),messageTop);
//                    System.out.println("序号为"+messageTop.getNumber()+"的消息重发成功！！！");
//                }
                //这种要等第二次输入时候才会运行上面while循环，是因为到String value = scanner.next();卡住了
                //当我队列pbftNode.getQueue()不空时候，想着能快速的进入里面while然后快速弄完队列的任务，但是问题在于我原本的空队列加入数据时候，线程已经阻塞到next（）语句上，只有执行完输入线程才会重新走到上面，清空队列的任务，并且我想让队列任务比之后输入任务更前。

                String value = scanner.next();
                while(!pbftNode.getQueue().isEmpty()){
                    System.out.println("重发节点开始");
                    Message messageTop = pbftNode.getQueue().poll();
                    int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
                    messageTop.setToNode(mainIndex);
                    messageTop.setTime(LocalDateTime.now());
                    messageTop.setView(pbftNode.getView());
                    //重复发送（重传）消息之前，先清空自己记录
                    pbftNode.getReplyVoteList().remove(messageTop.getNumber());
                    sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(),pbftNode.getNodeList().get(mainIndex).getPort(),messageTop);
                    System.out.println("序号为"+messageTop.getNumber()+"的消息重发成功！！！");
                }
                Thread.sleep(10000);
                Message msgClient = new Message();
                msgClient.setType(Constant.REQUEST);
                msgClient.setToNode(pbftNode.getView()%pbftNode.getNodeList().size());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(pbftNode.getNode());
                msgClient.setNumber(Varible.number++);
                msgClient.setView(pbftNode.getView());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setValue(value);
                //只在消息上弄客户端ip，端口
                msgClient.setClientIp(pbftNode.getIp());
                msgClient.setClientPort(pbftNode.getPort());
                int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
                sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(),pbftNode.getNodeList().get(mainIndex).getPort(),msgClient);
                //因为这个序列号，发送了自动++，所以才使用序列号-1
                timeTaskUtil.addTimeTask(Varible.number-1,pbftNode,msgClient);
                //主要用于当主节点作恶时候，reply阶段判断返回共识消息是否是共识消息
                pbftNode.getMessageValueCheckList().put(msgClient.getNumber(),value);
            }
        }


    }

}
