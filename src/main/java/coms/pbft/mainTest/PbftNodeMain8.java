package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain8 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode9 = new PbftNode(8, "127.0.0.1", 9009, true);
        pbftNode9.start();
    }
}
