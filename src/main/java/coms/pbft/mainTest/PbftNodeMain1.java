package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain1 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode2 = new PbftNode(1, "127.0.0.1", 9002, false);
        pbftNode2.start();

    }

}
