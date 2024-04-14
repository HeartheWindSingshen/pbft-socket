package coms.pbft.target;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import coms.pbft.Message;
import coms.pbft.constant.Constant;
import coms.pbft.constant.Varible;
import coms.pbft.pojo.Node;
import coms.pbft.pojo.Position;
import coms.pbft.Utils.sendUtil;
import lombok.Data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class Target1 {
    public static List<Node> NodeList=new ArrayList<Node>();
    Position target1Position=new Position();

    public Target1(int x,int y) throws FileNotFoundException {
        target1Position.setX(x);
        target1Position.setY(y);
        this.LoadNodes();
    }
    public void start() throws FileNotFoundException {
        Target1 target1=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("目标开始执行");
                boolean isFind=false;
                while(true&&!isFind){
                    for (Node node : NodeList) {
                        if(node.getX()==target1Position.getX()&&node.getY()== target1Position.getY()){
                            Message msgFind = new Message();
                            Position position = target1.getTarget1Position();
                            String value= JSON.toJSONString(position);
                            msgFind.setOriOrgNode(-2);
                            msgFind.setType(Constant.FIND);
                            //TODO
                            msgFind.setToNode(node.getNode());
                            msgFind.setTime(LocalDateTime.now());
                            msgFind.setOrgNode(-2);
                            msgFind.setNumber(Varible.number++);
                            msgFind.setView(-2);
                            msgFind.setValue(value);
                            try {
                                sendUtil.sendNode(node.getIp(),node.getPort(), msgFind);
                                isFind=true;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
                        }
                    }
                }
            }
        }).start();
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
            System.out.println("执行haha");
            Target1.NodeList.add(new Node(nodeElse,ipElse,portElse,0,0));
//            }
        }
        System.out.println(NodeList);
    }

}
