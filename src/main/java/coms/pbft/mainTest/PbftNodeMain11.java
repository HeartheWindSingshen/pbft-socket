package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain11 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode12 = new PbftNode(11, "127.0.0.1", 9012, true);
        pbftNode12.start();
    }
}
