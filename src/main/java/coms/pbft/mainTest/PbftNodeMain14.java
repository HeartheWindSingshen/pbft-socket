package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain14 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode15 = new PbftNode(14, "127.0.0.1", 9015, true);
        pbftNode15.start();
    }
}
