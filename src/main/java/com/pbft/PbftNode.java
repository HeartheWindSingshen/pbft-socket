package com.pbft;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.pbft.Utils.timeTaskUtil;
import com.pbft.constant.Constant;
import com.pbft.pojo.Node;
import lombok.Data;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;
import com.pbft.Utils.sendUtil;
import com.pbft.constant.Constant;

@Data
public class PbftNode {
    private int node;
    private int view;
    private String ip;
    private int port;
    private boolean isGood;
    //所有集群节点信息
    private List<Node>NodeList=new ArrayList<Node>();
    //prepare记录投票
    private Map<Integer, Map<String,Integer>>prepareVoteList=new HashMap<>();
    //commit记录投票
    private Map<Integer, Map<String,Integer>>commitVoteList=new HashMap<>();
    //reply记录返回
    private Map<Integer,Map<String,Integer>>replyVoteList=new HashMap<>();
    //view-change记录投票
    private Map<Integer,Map<String,Integer>>ViewChangeVoteList=new HashMap<>();
    //view-change-ack记录投票   和reply相似
    private Map<Integer,Map<String,Integer>>ViewChangeAckVoteList=new HashMap<>();
    //client发送各个序列号信息的值，用于进行最后的回复检验
    //主节点作恶 判断client 收到reply消息是否是原来的初始消息  只针对于client使用该成员变量
    private Map<Integer,String>MessageValueCheckList=new HashMap<>();
    //记录在prepare和commit函数内部已经发送的对应COMMIT和REPLY，防止当票数达到界限值就发送，之后的票来，就不能再去操作了，否则会重复发送，故设置这两个变量，控制
    //里面记录 prepare1或者commit1或者reply或者viewChange或者viewChangeAck 请求来当对应函数接收到请求后，直接忽略，因为已经投票成功了
    private Set<String>defendVoteList=new HashSet<>();
    //需要重发的消息队列
    public volatile Queue<Message> queue = new ArrayDeque<>();

    public PbftNode(int node, String ip, int port,boolean isGood) throws FileNotFoundException {
        this.node = node;
        this.ip = ip;
        this.port = port;
        this.isGood=isGood;
        LoadNodes();
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);

                    while (true) {
                        Socket clientSocket = serverSocket.accept();
//                        System.out.println("获取到socket");
                        try {
                            InputStream inputStream = clientSocket.getInputStream();
                            while(true){
                                byte[] bytes = new byte[Constant.BYTE_LENGTH];
                                int count = inputStream.read(bytes, 0, Constant.BYTE_LENGTH);
                                if(count>0){
                                    Message message=JSON.parseObject(new String(bytes),Message.class);
                                    //用于分行，更好的显示输出
                                    if(message.getType()==Constant.REQUEST){
                                        System.out.println("*****************新消息*****************");
                                    }
                                    if(message.getType()==Constant.PRE_PREPARE){
                                        System.out.println("*****************新消息*****************");
                                    }
                                    System.out.println("节点"+(node)+" 收到来源于"+message.getOrgNode()+"的消息"+message);
                                    doAction(message);
                                }else{
                                    break;
                                }
                            }
                        } catch (SocketException e) {
//                            System.out.println("Client disconnected.");
                        } finally  {
                            clientSocket.close();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();


    }

    private void doAction(Message message) throws IOException {
        if(message!=null){

            switch(message.getType()){
                case Constant.REQUEST:
                    onRequest(message);
                    break;
                case Constant.PRE_PREPARE:
                    onPrePrepare(message);
                    break;
                case Constant.PREPARE:
                    onPrepare(message);
                    break;
                case Constant.COMMIT:
                    onCommit(message);
                    break;
                case Constant.REPLY:
                    onReply(message);
                    break;
                case Constant.CHANGEVIEW:
                    onClientViewChange(message);
                    break;
                case Constant.VIEWCHANGE:
                    onNodeViewChange(message);
                    break;
                case Constant.VIEWCHANGEACK:
                    onViewChangeAck(message);
                    break;
                case Constant.NEWVIEW:
                    onNewView(message);
                    break;
                case Constant.GETVIEW:
                    onGetView(message);
                default:
                    break;
            }
        }

    }




    private void onRequest(Message message) throws IOException {
//        根据朝代计算主节点
        int mainNode=view%(NodeList.size());
        if(message.getToNode()==mainNode){
            //client向主节点发送请求情况
            //发送广播
            sendAllNodes(message,Constant.PRE_PREPARE);
            System.out.println("Pre-prepare阶段主节点广播..............");
            //自己向自己发送，实际是修改prepareVoteList数值
            //这部分相当于主节点在pre-prepare发送时候已经在prepare投票了，所以要都提前添加值,
            //此时是只投了自己,其余人要在他们的节点线程中处理，我放在了pre-prepare函数里面
            int msgNumber = message.getNumber();
            String msgValue = message.getValue();
            requestSendToSelf(msgNumber,msgValue);


        }else{
            //client向非主节点发送请求情况
        }

    }



    private void onPrePrepare(Message message) throws IOException {
        //相当于主节点再pre-prepare阶段就已经投了，prepare的票了
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        /**
         * 如果重发的话，先清除各个节点的上次信息的投票记录
         */
        ClearPrepareCommitReplyVoteDefend(msgNumber);
        requestSendToSelf(msgNumber,msgValue);
        sendAllNodes(message,Constant.PREPARE);
        System.out.println("prepare阶段节点广播..............");
        //自己向自己发送，实际是修改prepareVoteList数值
        msgNumber = message.getNumber();
        msgValue = message.getValue();
        prepareSendToSelf(msgNumber,msgValue);
    }


    private void onPrepare(Message message) throws IOException {
        //已经票数够了，发送了，所以不用操作了
        if(defendVoteList.contains("prepare"+message.getNumber())){
            return;
        }
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        /**
         * prepare阶段投票
         */
        prepareVote(msgNumber,msgValue);
        Map<String, Integer> voteValue = prepareVoteList.get(msgNumber);
        Set<String> voteKeySet = voteValue.keySet();
//        System.out.println(prepareVoteList);
        for (String voteKey : voteKeySet) {
            Integer voteNumber = voteValue.get(voteKey);
            //prepare投票判断
            //2*((NodeList.size()+1)/3)+1
            if(voteNumber>=2*((NodeList.size())/3)+1){
                sendAllNodes(message,Constant.COMMIT);
                System.out.println("commit阶段主节点广播..............");
                ///////////////////////////
                //自己向自己发送，实际是修改commitVoteList数值
                msgNumber = message.getNumber();
                msgValue = message.getValue();
                commitSendToSelf(msgNumber,msgValue);
                //投票成功后，防止之后后面慢的票进来，重复操作
                defendVoteList.add("prepare"+message.getNumber());
            }
        }

    }


    private void onCommit(Message message) throws IOException {
        //已经票数够了，发送了，后面就不要操作了
        if(defendVoteList.contains("commit"+message.getNumber())){
            return;
        }
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        /**
         * commit阶段投票
         */
        commitVote(msgNumber,msgValue);

        Map<String, Integer> voteValue = commitVoteList.get(msgNumber);
        Set<String> voteKeySet = voteValue.keySet();
        for (String voteKey : voteKeySet) {
            Integer voteNumber = voteValue.get(voteKey);
            //2*((NodeList.size()+1)/3)+1
            if(voteNumber>=2*((NodeList.size())/3)+1){
                replyClient(message,Constant.REPLY);
                System.out.println("本节点发送reply");
                defendVoteList.add("commit"+message.getNumber());

            }
        }
    }



    //这部分是客户端独有的经历到
    private void onReply(Message message) {
        //已经票数够了，发送了，后面就不要操作了
        if(defendVoteList.contains("reply"+message.getNumber())){
            return;
        }

        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        replyVote(msgNumber,msgValue);
        Map<String, Integer> voteValue = replyVoteList.get(msgNumber);
        Set<String> voteKeySet = voteValue.keySet();
        for (String voteKey : voteKeySet) {
            Integer count = voteValue.get(voteKey);
            int maxf=(NodeList.size())/3;
            //&&voteKey==this.orgClientMessageValue
            //这里不知道为啥只能用equals  voteKey.equals(this.orgClientMessageValue)
            if(count>=maxf+1&&(voteKey.equals(this.getMessageValueCheckList().get(msgNumber)))){
                timeTaskUtil.cancelTimeTask(msgNumber);
                System.out.println("Client "+node+"接收到共识reply，共识完成！");
                defendVoteList.add("reply"+message.getNumber());
            }
        }

    }
    //这还是view-change阶段之前
    private void onClientViewChange(Message message) throws IOException {
        view=view+1;
        sendAllNodes(message,Constant.VIEWCHANGE);
        //TODO backup节点们广播    client不知道朝代问题，进而无法知道主节点问题
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        ViewChangeToMySelf(msgNumber,msgValue);
    }


    //这才正式是view-change阶段
    private void onNodeViewChange(Message message) throws IOException {
        if(defendVoteList.contains("viewChange"+message.getNumber())){
            return;
        }
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();

        viewChangeVote(msgNumber,msgValue);

        Map<String, Integer> voteValue = ViewChangeVoteList.get(msgNumber);
        Set<String> voteKeySet = voteValue.keySet();
        System.out.println(ViewChangeVoteList);
        for (String voteKey : voteKeySet) {
            Integer voteNumber = voteValue.get(voteKey);
            //2*((NodeList.size()+1)/3)+1
            if(voteNumber>=2*((NodeList.size())/3)+1){
                Node nodekernal = NodeList.get(view % (NodeList.size()));
                //因为onViewChangeAck的voteNumber>=2*((NodeList.size())/3，所以不需要自己给自己发
                if(nodekernal.getNode()!=this.node){
                    Message messageSend = new Message();
                    messageSend.setOrgNode(node);
                    messageSend.setToNode(view % (NodeList.size()));
                    messageSend.setType(Constant.VIEWCHANGEACK);
                    messageSend.setTime(LocalDateTime.now());
                    messageSend.setNumber(msgNumber);
                    messageSend.setValue(msgValue);
                    messageSend.setView(view);
                    messageSend.setClientPort(message.getClientPort());
                    messageSend.setClientIp(message.getClientIp());
                    if(isGood){
                        sendUtil.sendNode(nodekernal.getIp(),nodekernal.getPort(),messageSend);
                    }else{
                        messageSend.setValue("坏节点的错误信息");
                        System.out.println("主节点在view-change-ack作恶，我们模拟就让它随便发了");
                        Random random = new Random();
                        int i = random.nextInt(NodeList.size());
                        Node nodeTo = NodeList.get(i);

                        sendUtil.sendNode(nodeTo.getIp(),nodeTo.getPort(),messageSend);
                    };
                }
                defendVoteList.add("viewChange"+message.getNumber());

            }
        }
    }
    private void onViewChangeAck(Message message) throws IOException {
        if(defendVoteList.contains("viewChangeAck"+message.getNumber())){
            return;
        }
        int msgNumber = message.getNumber();
        String msgValue = message.getValue();
        viewChangeAckVote(msgNumber,msgValue);
        Map<String, Integer> voteValue = ViewChangeAckVoteList.get(msgNumber);
        Set<String> voteKeySet = voteValue.keySet();
        for (String voteKey : voteKeySet) {
            Integer voteNumber = voteValue.get(voteKey);
            //2*((NodeList.size()+1)/3)   //这个前一层没给自己传 自己是主节点
            if(voteNumber>=2*((NodeList.size())/3)){
                message.setValue("新朝代来了！");
                sendAllNodes(message,Constant.NEWVIEW);
                defendVoteList.add("viewChangeAck"+message.getNumber());

            }
        }
    }
    private void onNewView(Message message) {
        this.view=message.getView();
        System.out.println("各节点接收到new view");
    }
    private void onGetView(Message message) throws IOException {
        if(node>=0){
            //集群节点收到client请求信息
            message.setView(this.view);
            message.setTime(LocalDateTime.now());
            //这个很有意思 先得到消息来源client，之后再设置toNode为此
            message.setToNode(message.getOrgNode());
            message.setOrgNode(this.node);
            sendUtil.sendNode(message.getClientIp(),message.getClientPort(),message);
            System.out.println("集群节点返回了view");
        }else{
            //client收到返回的view消息
            this.view=message.getView();
            System.out.println("client收到返回的view："+this.view);
        }
    }



    /**
     * 广播其他节点
     * @param message
     * @param type
     * @throws IOException
     */
    public void sendAllNodes(Message message,int type) throws IOException {
        String msgValue= message.getValue();
        int number = message.getNumber();
        for (Node nodeElse : NodeList) {
            if(nodeElse.getNode()==this.node){
                //跳过自己广播
                System.out.println("跳过自己广播");
                continue;
            }
            Message messageSend = new Message();
            messageSend.setOrgNode(this.node);
            //根据记录表中数据，发送去向节点编号
            messageSend.setToNode(nodeElse.getNode());
            messageSend.setView(this.view);
            messageSend.setTime(LocalDateTime.now());
            messageSend.setValue(msgValue);
            messageSend.setType(type);
            messageSend.setNumber(number);
            messageSend.setClientIp(message.getClientIp());
            messageSend.setClientPort(message.getClientPort());
            String ipSend = nodeElse.getIp();
            int portSend = nodeElse.getPort();
            if(isGood){
                sendUtil.sendNode(ipSend,portSend,messageSend);
            }else{
                messageSend.setValue("坏节点的错误信息");
                sendUtil.sendNode(ipSend,portSend,messageSend);
            }
        }

    }


    /**
     * 回复外部客户端
     * @param message
     * @param type
     * @throws IOException
     */
    private void replyClient(Message message,int type) throws IOException {
        String msgValue= message.getValue();
        int number = message.getNumber();
        Message messageSend = new Message();
        messageSend.setOrgNode(this.node);
        //向客户端发送回复
        messageSend.setToNode(-1);
        messageSend.setView(this.view);
        messageSend.setTime(LocalDateTime.now());
        messageSend.setValue(msgValue);
        messageSend.setType(type);
        messageSend.setNumber(number);
        messageSend.setClientIp(message.getClientIp());
        messageSend.setClientPort(message.getClientPort());
        String ipSend = message.getClientIp();
        int portSend = message.getClientPort();
        if(isGood){
            sendUtil.sendNode(ipSend,portSend,messageSend);
        }else{
            messageSend.setValue("坏节点的错误信息");
            sendUtil.sendNode(ipSend,portSend,messageSend);
        }
    }

    /**
     * prepare阶段模拟自己给自己发送信息
     * @param msgNumber
     * @param msgValue
     */
    //并不是自己给自己发送网络数据包，而是将对应阶段的投票变为1，代表自己给自己投票
    private void prepareSendToSelf(int msgNumber,String msgValue) {
        if(!prepareVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            prepareVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = prepareVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                //理论上给自己投票，只会进入该域，但是保险和懒起见，直接复制整个if语句
                mapValue.put(msgValue,1);
            }else{
                mapValue.put(msgValue,mapValue.get(msgValue)+1);
            }
        }
    }

    /**
     * commit阶段模拟自己给自己发送信息
     * @param msgNumber
     * @param msgValue
     */
    //并不是自己给自己发送网络数据包，而是将对应阶段的投票变为1，代表自己给自己投票
    private void commitSendToSelf(int msgNumber,String msgValue) {

        if(!commitVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            commitVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = commitVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                //理论上给自己投票，只会进入该域，但是保险和懒起见，直接复制整个if语句
                mapValue.put(msgValue,1);
            }else{
                mapValue.put(msgValue,mapValue.get(msgValue)+1);
            }
        }

    }
    private void requestSendToSelf(int msgNumber, String msgValue) {
        if(!prepareVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            prepareVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = prepareVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                //理论上给自己投票，只会进入该域，但是保险和懒起见，直接复制整个if语句
                mapValue.put(msgValue,1);
            }else{
                mapValue.put(msgValue,mapValue.get(msgValue)+1);
            }
        }
    }
    private void ViewChangeToMySelf(int msgNumber, String msgValue) {
        if(!ViewChangeVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            ViewChangeVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = ViewChangeVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                //理论上给自己投票，只会进入该域，但是保险和懒起见，直接复制整个if语句
                mapValue.put(msgValue,1);
            }else{
                mapValue.put(msgValue,mapValue.get(msgValue)+1);
            }
        }
    }

    /**
     * prepare 投票阶段
     * @param msgNumber
     * @param msgValue
     */
    private void prepareVote(int msgNumber, String msgValue) {
        /**
         * {
         *     "1":{
         *         "yes":2,
         *         "no":1
         *     }
         * }
         * 所以以下if语句是记录投票的逻辑处理
         */
        if(!prepareVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            prepareVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = prepareVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                mapValue.put(msgValue,1);
            }else{
                mapValue.put(msgValue,mapValue.get(msgValue)+1);
            }
        }
    }

    /**
     * commit 投票阶段
     * @param msgNumber
     * @param msgValue
     */
    private void commitVote(int msgNumber, String msgValue) {
        if(!commitVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            commitVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = commitVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                mapValue.put(msgValue,1);
            }else{
                Integer add = mapValue.get(msgValue);
                mapValue.put(msgValue,add+1);
            }
        }
    }

    /**
     * 回复阶段
     * @param msgNumber
     */
    private void replyVote(int msgNumber,String msgValue) {
        if(!replyVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            replyVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = replyVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                mapValue.put(msgValue,1);
            }else{
                Integer add = mapValue.get(msgValue);
                mapValue.put(msgValue,add+1);
            }
        }
    }
    private void viewChangeVote(int msgNumber, String msgValue) {
        if(!ViewChangeVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            ViewChangeVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = ViewChangeVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                mapValue.put(msgValue,1);
            }else{
                Integer add = mapValue.get(msgValue);
                mapValue.put(msgValue,add+1);
            }
        }
    }
    private void viewChangeAckVote(int msgNumber, String msgValue) {
        if(!ViewChangeAckVoteList.containsKey(msgNumber)){
            HashMap<String, Integer> mapValue = new HashMap<>();
            mapValue.put(msgValue,1);
            ViewChangeAckVoteList.put(msgNumber,mapValue);
        }else{
            Map<String, Integer> mapValue = ViewChangeAckVoteList.get(msgNumber);
            if(!mapValue.containsKey(msgValue)){
                mapValue.put(msgValue,1);
            }else{
                Integer add = mapValue.get(msgValue);
                mapValue.put(msgValue,add+1);
            }
        }
    }

    public void ClearPrepareCommitReplyVoteDefend(Integer number){
        prepareVoteList.remove(number);
        commitVoteList.remove(number);
        replyVoteList.remove(number);
        defendVoteList.remove("prepare"+number);
        defendVoteList.remove("commit"+number);
        defendVoteList.remove("reply"+number);
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
                NodeList.add(new Node(nodeElse,ipElse,portElse));
//            }
        }
    }
}
