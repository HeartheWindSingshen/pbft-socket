package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain12 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode13 = new PbftNode(12, "127.0.0.1", 9013, true);
        pbftNode13.start();
    }
}
