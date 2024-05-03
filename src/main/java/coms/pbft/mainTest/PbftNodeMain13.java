package coms.pbft.mainTest;

import coms.pbft.PbftNode;

import java.io.FileNotFoundException;

public class PbftNodeMain13 {
    public static void main(String[] args) throws FileNotFoundException {
        PbftNode pbftNode14 = new PbftNode(13, "127.0.0.1", 9014, true);
        pbftNode14.start();
    }
}
