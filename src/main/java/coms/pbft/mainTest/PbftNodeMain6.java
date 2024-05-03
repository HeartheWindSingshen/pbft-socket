package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain6 {
    public static void main(String[] args) throws FileNotFoundException {

        PbftNode pbftNode = new PbftNode(6, "127.0.0.1", 9007, true);
        pbftNode.start();
    }
}
