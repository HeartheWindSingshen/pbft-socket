package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain7 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode8 = new PbftNode(7, "127.0.0.1", 9008, true);
        pbftNode8.start();
    }
}
