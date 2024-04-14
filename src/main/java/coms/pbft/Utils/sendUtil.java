package coms.pbft.Utils;

import com.alibaba.fastjson.JSON;
import coms.pbft.Message;
import coms.pbft.constant.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class sendUtil {
    //这就是工具，阶段最好不要直接调用它，发送函数调用它
    public static void sendNode(String ip, int port, Message messageSend) throws IOException {
        Socket socket = new Socket(ip,port);

        String msg  = JSON.toJSONString(messageSend);

        OutputStream outputStream = socket.getOutputStream();
        byte[] bytes = new byte[Constant.BYTE_LENGTH];
        int idx=0;
        for (byte b : msg.getBytes()) {
            bytes[idx++]=b;
        }
        outputStream.write(bytes,0,Constant.BYTE_LENGTH);
        socket.close();
    }
}
