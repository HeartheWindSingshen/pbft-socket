package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain10 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode11 = new PbftNode(10, "127.0.0.1", 9011, true);
        pbftNode11.start();
    }
}
