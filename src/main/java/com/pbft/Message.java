package com.pbft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
//    private boolean isOk;
    private int type;
    private int orgNode;
    private int toNode;
    private int number;
    private int view;
    private LocalDateTime time;
    private String value;
    private String clientIp;
    private int clientPort;

}
