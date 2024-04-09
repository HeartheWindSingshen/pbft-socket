package com.pbft.constant;

public class Constant {
    public static final int GETVIEW=-5;
    public static final int CHANGEVIEW=-4;
    public static final int VIEWCHANGE=-3;
    public static final int VIEWCHANGEACK=-2;
    public static final int NEWVIEW=-1;
    public static final int  REQUEST=0;
    public static final int  PRE_PREPARE=1;
    public static final int  PREPARE=2;
    public static final int  COMMIT=3;
    public static final int  REPLY=4;
    public static final int REPLYSUCCESSUPDATE=6;
    //主要用于client发送主节点时候
    public static final int CLIENTGETVIEW=-100;
    public static final int CLIENTVIEWCHANGE=-200;

    public static final int BYTE_LENGTH = 1024;
}
