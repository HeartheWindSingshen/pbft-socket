package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain15 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode16 = new PbftNode(15, "127.0.0.1", 9016, true);
        pbftNode16.start();
    }
}
