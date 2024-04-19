package coms.pbft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import coms.pbft.Utils.sendUtil;
import coms.pbft.Utils.timeTaskUtil;
import coms.pbft.constant.Varible;
import coms.pbft.PbftNode;
import coms.pbft.pojo.*;
import coms.pbft.constant.Constant;
import coms.pbft.Message;
import coms.pbft.target.Target1;
import coms.pbft.time.TestLongSend;
import lombok.Data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
@Data
public class ControllerCentre {
    private Long sum=0L;
    private Long countt=0L;
    private Long minx=100000000000L;
    private int node;
    private int view;
    private String ip;
    private int port;
    private int [][]graph=new int[10][10];
    //所有集群节点信息
    private List<Node> NodeList=new ArrayList<Node>();
    //reply记录返回
    private Map<Integer, Map<String,Integer>>replyVoteList=new HashMap<>();

    private Set<String>defendVoteList=new HashSet<>();
    public ControllerCentre(int node,String ip,int port) throws FileNotFoundException {
        this.node=node;
        this.ip=ip;
        this.port=port;
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                graph[i][j] = -1;
            }
        }
        LoadNodes();
    }
    public void start(){
        this.Operation5();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket;
                try {
                    serverSocket = new ServerSocket(port);
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        InputStream inputStream = clientSocket.getInputStream();
                        while (true) {
                            byte[] bytes = new byte[Constant.BYTE_LENGTH];
                            int count = inputStream.read(bytes, 0, Constant.BYTE_LENGTH);
                            if (count > 0) {
                                Message message=JSON.parseObject(new String(bytes), Message.class);
                                //用于分行，更好的显示输出
                                System.out.println("节点" + (node) + " 收到来源于" + message.getOrgNode() + "的消息" + message);
                                doAction(message);
                            } else {
                                break;
                            }
                        }
                    }
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    private void doAction(Message message){
        if(message!=null){
            switch(message.getType()){
//                case Constant.REQUEST:
//                    onRequest(message);
//                    break;
//                case Constant.PRE_PREPARE:
//                    onPrePrepare(message);
//                    break;
//                case Constant.PREPARE:
//                    onPrepare(message);
//                    break;
//                case Constant.COMMIT:
//                    onCommit(message);
//                    break;
                case Constant.REPLY:
                    onReply(message);
                    break;
                case Constant.NEWVIEW:
                    onNewView(message);
                default:
                    break;
            }

        }
    }


    private void onReply(Message message) {
        int controllerType = message.getControllerType();
        System.out.println("???"+controllerType);
        if(controllerType!= Constant.TIMEOPERATION5){
            if(defendVoteList.contains("reply"+message.getNumber())){
                return;
            }
            int msgNumber = message.getNumber();
            String msgValue = message.getValue();
            Map<String, Integer> voteValue = replyVoteList.get(msgNumber);
            if(voteValue==null)return;
            Set<String> voteKeySet = voteValue.keySet();
            if(voteKeySet==null)return;
            for (String voteKey : voteKeySet) {
                Integer count = voteValue.get(voteKey);
                int maxf=(NodeList.size()-1)/3;
                //&&voteKey==this.orgClientMessageValue
                //这里不知道为啥只能用equals  voteKey.equals(this.orgClientMessageValue)
                if(count>=maxf+1){
                    System.out.println("Client "+node+"接收到共识reply，共识完成！共实现息为"+message.getValue());
                    defendVoteList.add("reply"+message.getNumber());
                }
            }
        }else{
//            System.out.println(message.getType());
            String status = message.getValue();
            try {
                Status flyStatus = JSON.parseObject(status, Status.class);
                Node nodeFly = NodeList.get(message.getOrgNode());
                int oldX = nodeFly.getX();
                int oldY = nodeFly.getY();
                nodeFly.setX(flyStatus.getX());
                nodeFly.setY(flyStatus.getY());
                int newX = nodeFly.getX();
                int newY = nodeFly.getY();
//            System.out.println("更新无人机" +nodeFly.getNode() + "位置为"+flyStatus.getX()+" , "+flyStatus.getY());
                graph[oldX][oldY] = -1;
                graph[newX][newY] = nodeFly.getNode();
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (graph[i][j] == -1) {
                            System.out.print("*  ");
                        } else {
                            System.out.print(graph[i][j] + "  ");
                        }
                    }
                    System.out.println();
                }
            }catch (Exception e){
                System.out.println("传入的是坏节点的错误消息");
            }finally {
                TestLongSend.endTime = System.nanoTime();
                if ((TestLongSend.endTime - TestLongSend.startTime)/1000000<300){
                    sum+=TestLongSend.endTime - TestLongSend.startTime;
                    countt++;
                    minx=(TestLongSend.endTime - TestLongSend.startTime)<minx?(TestLongSend.endTime - TestLongSend.startTime): minx;
                    System.out.println("指挥塔发送主节点传送消息时长为：" + (TestLongSend.endTime - TestLongSend.startTime) / 1000000);
                    System.out.println("平均通信时长为: "+(sum/countt)/1000000);
                    System.out.println("最短通信时长为: "+minx/1000000);
                }
            }
        }

    }
    private void onNewView(Message message) {

        this.view=message.getView();
        System.out.println("控制台修改了它的view");
    }
    //执行搜索特定区域
    public void Operation1(int x, int y, Point t1, Point t2, Point t3, Point t4){
        Operation2(x,y);
        Operation4(t1,t2,t3,t4);
    }

    //移动特定位置
    public void Operation2(int x,int y){
        Position position = new Position(x,y);
        String value = JSON.toJSONString(position);

        Message msgClient = new Message();
        msgClient.setOriOrgNode(-1);
        msgClient.setControllerType(Constant.OPERATION2);
        msgClient.setType(Constant.REQUEST);
        //TODO
        msgClient.setToNode(this.getView()% NodeList.size());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setOrgNode(this.getNode());
        msgClient.setNumber(Varible.number++);
        msgClient.setView(this.getView());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setValue(value);
        //只在消息上弄客户端ip，端口
        msgClient.setClientIp(this.getIp());
        msgClient.setClientPort(this.getPort());
        int mainIndex = this.getView()%this.getNodeList().size();
        try {
            sendUtil.sendNode(this.getNodeList().get(mainIndex).getIp(), this.getNodeList().get(mainIndex).getPort(), msgClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //停止搜索
    public void Operation3(){
        String value="停止搜索";
        Message msgClient = new Message();
        msgClient.setOriOrgNode(-1);
        msgClient.setControllerType(Constant.OPERATION3);
        msgClient.setType(Constant.REQUEST);
        //TODO
        msgClient.setToNode(this.getView()% NodeList.size());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setOrgNode(this.getNode());
        msgClient.setNumber(Varible.number++);
        msgClient.setView(this.getView());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setValue(value);
        //只在消息上弄客户端ip，端口
        msgClient.setClientIp(this.getIp());
        msgClient.setClientPort(this.getPort());
        int mainIndex = this.getView()%this.getNodeList().size();
        try {
            sendUtil.sendNode(this.getNodeList().get(mainIndex).getIp(), this.getNodeList().get(mainIndex).getPort(), msgClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //开始搜索
    public void Operation4(Point t1,Point t2,Point t3,Point t4){
        Field field = new Field(t1, t2, t3, t4);
        String value = JSON.toJSONString(field);

        Message msgClient = new Message();
        msgClient.setOriOrgNode(-1);
        msgClient.setControllerType(Constant.OPERATION4);
        msgClient.setType(Constant.REQUEST);
        //TODO
        msgClient.setToNode(this.getView()% NodeList.size());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setOrgNode(this.getNode());
        msgClient.setNumber(Varible.number++);
        msgClient.setView(this.getView());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setValue(value);
        //只在消息上弄客户端ip，端口
        msgClient.setClientIp(this.getIp());
        msgClient.setClientPort(this.getPort());
        int mainIndex = this.getView()%this.getNodeList().size();
        try {
            sendUtil.sendNode(this.getNodeList().get(mainIndex).getIp(), this.getNodeList().get(mainIndex).getPort(), msgClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //启动定时获取位置信息
    public void Operation5(){
        System.out.println("执行5 开始定时任务");
        Timer timer = new Timer();
        ControllerCentre me=this;

        // 定义一个定时任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String value="请求无人机位置信息";
                Message msgClient = new Message();
                msgClient.setOriOrgNode(-1);
                msgClient.setControllerType(Constant.TIMEOPERATION5);
                msgClient.setType(Constant.REQUEST);
                //TODO
                msgClient.setToNode(me.getView()% NodeList.size());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(me.getNode());
                msgClient.setNumber(Varible.number++);
                msgClient.setView(me.getView());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setValue(value);
                //只在消息上弄客户端ip，端口
                msgClient.setClientIp(me.getIp());
                msgClient.setClientPort(me.getPort());
                int mainIndex = me.getView()%me.getNodeList().size();
                try {
                    TestLongSend.startTime= System.nanoTime();
                    sendUtil.sendNode(me.getNodeList().get(mainIndex).getIp(), me.getNodeList().get(mainIndex).getPort(), msgClient);
                } catch (IOException e) {
                    //抛出异常会导致代码执行中断，从而导致定时任务终止
//                    throw new RuntimeException(e);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 定义任务执行的间隔时间（以毫秒为单位）
        long delay = 10; // 初始延迟，即第一次执行的延迟时间
        long interval = 2000; // 间隔时间，每隔5秒执行一次

        // 启动定时任务
        timer.scheduleAtFixedRate(task, delay, interval);
    }
    //开启无人机内部共享位置！！！
    public void Operation6(){

        System.out.println("执行6 开启无人机内部共享位置！！！");
        String value="开启无人机内部共享位置";
        Message msgClient = new Message();
        msgClient.setOriOrgNode(-1);
        msgClient.setControllerType(Constant.OPERATION6);
        msgClient.setType(Constant.REQUEST);
        //TODO
        msgClient.setToNode(this.getView()% NodeList.size());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setOrgNode(this.getNode());
        msgClient.setNumber(Varible.number++);
        msgClient.setView(this.getView());
        msgClient.setValue(value);
        //只在消息上弄客户端ip，端口
        msgClient.setClientIp(this.getIp());
        msgClient.setClientPort(this.getPort());
        int mainIndex = this.getView()%this.getNodeList().size();
        try {
            sendUtil.sendNode(this.getNodeList().get(mainIndex).getIp(), this.getNodeList().get(mainIndex).getPort(), msgClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //关闭无人机内部共享位置！！！
    public void Operation7(){
        System.out.println("执行7 关闭无人机内部共享位置！！！");
        String value="关闭无人机内部共享位置";
        Message msgClient = new Message();
        msgClient.setOriOrgNode(-1);
        msgClient.setControllerType(Constant.OPERATION7);
        msgClient.setType(Constant.REQUEST);
        //TODO
        msgClient.setToNode(this.getView()%this.getNodeList().size());
        msgClient.setTime(LocalDateTime.now());
        msgClient.setOrgNode(this.getNode());
        msgClient.setNumber(Varible.number++);
        msgClient.setView(this.getView());
        msgClient.setValue(value);
        //只在消息上弄客户端ip，端口
        msgClient.setClientIp(this.getIp());
        msgClient.setClientPort(this.getPort());
        int mainIndex = this.getView()%this.getNodeList().size();
        try {
            sendUtil.sendNode(this.getNodeList().get(mainIndex).getIp(), this.getNodeList().get(mainIndex).getPort(), msgClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
//
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode1 = new PbftNode(0, "127.0.0.1", 9001, true);
        pbftNode1.start();
        PbftNode pbftNode2 = new PbftNode(1, "127.0.0.1", 9002, true);
        pbftNode2.start();
        PbftNode pbftNode3 = new PbftNode(2, "127.0.0.1", 9003, true);
        pbftNode3.start();
        PbftNode pbftNode4 = new PbftNode(3, "127.0.0.1", 9004, true);
        pbftNode4.start();
//        Target1 target1 = new Target1(5, 5);
//        target1.start();
        ControllerCentre controllerCentre = new ControllerCentre(-1, "127.0.0.1", 9000);
        controllerCentre.start();

        Scanner scanner = new Scanner(System.in);
        while(true){
            int i = scanner.nextInt();
            switch (i){
                case 1:
                    int t1 = scanner.nextInt();
                    int t2 = scanner.nextInt();
                    int t31 = scanner.nextInt();
                    int t32 = scanner.nextInt();
                    Point t3 = new Point(t31, t32);
                    int t41 = scanner.nextInt();
                    int t42 = scanner.nextInt();
                    Point t4 = new Point(t41, t42);
                    int t51 = scanner.nextInt();
                    int t52 = scanner.nextInt();
                    Point t5 = new Point(t51, t52);
                    int t61 = scanner.nextInt();
                    int t62 = scanner.nextInt();
                    Point t6 = new Point(t61, t62);
                    controllerCentre.Operation1(t1,t2,t3,t4,t5,t6);
                    break;
                case 2:
                    int t7 = scanner.nextInt();
                    int t8 = scanner.nextInt();
                    controllerCentre.Operation2(t7,t8);
                    break;
                case 3:
                    controllerCentre.Operation3();
                    break;
                case 4:
                    int t91 = scanner.nextInt();
                    int t92 = scanner.nextInt();
                    Point t9 = new Point(t91, t92);
                    int t101 = scanner.nextInt();
                    int t102 = scanner.nextInt();
                    Point t10 = new Point(t101, t102);
                    int t111 = scanner.nextInt();
                    int t112 = scanner.nextInt();
                    Point t11 = new Point(t111, t112);
                    int t121 = scanner.nextInt();
                    int t122 = scanner.nextInt();
                    Point t12 = new Point(t121, t122);
                    controllerCentre.Operation4(t9,t10,t11,t12);
                    break;
                case 6:
                    controllerCentre.Operation6();
                    break;
                case 7:
                    controllerCentre.Operation7();
                default:
                    break;
            }
        }
    }



    /**
     * 导入本地节点存储的其他节点信息
     * @throws FileNotFoundException
     */
    public void LoadNodes() throws FileNotFoundException {

        FileReader fileReader   = new FileReader("src/main/resources/Nodes.JSON");
        JSONReader reader = new JSONReader(fileReader);
        JSONObject jsonObject = (JSONObject) reader.readObject();
        List<JSONObject>list = (List<JSONObject>) jsonObject.get("Nodes");
        for (JSONObject object : list) {
            int nodeElse= (int) object.get("node");
            String ipElse=(String)object.get("ip");
            int portElse=(int)object.get("port");
            // 还是将包括本节点放到nodeList里面吧，后面广播时候也会广播到本节点，之后如果有其他要求不需要本节点，加if语句就可以
//            if(nodeElse!=this.node){
            NodeList.add(new Node(nodeElse,ipElse,portElse,0,0));
//            }
        }
    }






}
