package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain4 {
    public static void main(String[] args) throws FileNotFoundException {

        PbftNode pbftNode = new PbftNode(4, "127.0.0.1", 9005, true);
        pbftNode.start();
    }
}
