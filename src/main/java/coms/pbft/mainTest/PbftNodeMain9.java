package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain9 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode10 = new PbftNode(9, "127.0.0.1", 9010, true);
        pbftNode10.start();
    }
}
