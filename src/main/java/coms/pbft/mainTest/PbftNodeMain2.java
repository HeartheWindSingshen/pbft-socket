package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain2 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode3 = new PbftNode(2, "127.0.0.1", 9003, true);
        pbftNode3.start();
    }
}
