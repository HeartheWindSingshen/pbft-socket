package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain0 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode1 = new PbftNode(0, "127.0.0.1", 9001, true);
        pbftNode1.start();
    }


}
