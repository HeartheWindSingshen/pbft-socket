package coms.pbft.mainTest;

import coms.pbft.ControllerCentre;
import coms.pbft.pojo.Point;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ControllerCentreMain {
    public static void main(String[] args) throws FileNotFoundException {
        ControllerCentre controllerCentre = new ControllerCentre(-1, "127.0.0.1", 9000);
        controllerCentre.start();
        Scanner scanner = new Scanner(System.in);
        while(true){
            int i = scanner.nextInt();
            switch (i){
                case 1:
                    int t1 = scanner.nextInt();
                    int t2 = scanner.nextInt();
                    int t31 = scanner.nextInt();
                    int t32 = scanner.nextInt();
                    Point t3 = new Point(t31, t32);
                    int t41 = scanner.nextInt();
                    int t42 = scanner.nextInt();
                    Point t4 = new Point(t41, t42);
                    int t51 = scanner.nextInt();
                    int t52 = scanner.nextInt();
                    Point t5 = new Point(t51, t52);
                    int t61 = scanner.nextInt();
                    int t62 = scanner.nextInt();
                    Point t6 = new Point(t61, t62);
                    controllerCentre.Operation1(t1,t2,t3,t4,t5,t6);
                    break;
                case 2:
                    int t7 = scanner.nextInt();
                    int t8 = scanner.nextInt();
                    controllerCentre.Operation2(t7,t8);
                    break;
                case 3:
                    controllerCentre.Operation3();
                    break;
                case 4:
                    int t91 = scanner.nextInt();
                    int t92 = scanner.nextInt();
                    Point t9 = new Point(t91, t92);
                    int t101 = scanner.nextInt();
                    int t102 = scanner.nextInt();
                    Point t10 = new Point(t101, t102);
                    int t111 = scanner.nextInt();
                    int t112 = scanner.nextInt();
                    Point t11 = new Point(t111, t112);
                    int t121 = scanner.nextInt();
                    int t122 = scanner.nextInt();
                    Point t12 = new Point(t121, t122);
                    controllerCentre.Operation4(t9,t10,t11,t12);
                    break;
                case 6:
                    controllerCentre.Operation6();
                    break;
                case 7:
                    controllerCentre.Operation7();
                default:
                    break;
            }
        }


    }

}
