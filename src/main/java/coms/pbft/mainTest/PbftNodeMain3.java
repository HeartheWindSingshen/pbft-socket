package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain3 {
    public static void main(String[] args) throws FileNotFoundException {

        PbftNode pbftNode4 = new PbftNode(3, "127.0.0.1", 9004, true);
        pbftNode4.start();
    }
}
