package coms.pbft.mainTest;

import coms.pbft.target.Target1;

import java.io.FileNotFoundException;

public class Target1Main {
    public static void main(String[] args) throws FileNotFoundException {
        Target1 target1 = new Target1(5, 5,"127.0.0.1",8888);
        target1.start();
    }
}
