package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain5 {
    public static void main(String[] args) throws FileNotFoundException {

        PbftNode pbftNode = new PbftNode(5, "127.0.0.1", 9006, true);
        pbftNode.start();
    }
}
