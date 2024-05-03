package coms.controller;

import coms.StmergeApplication;
import coms.pbft.pojo.Node;
import coms.pbft.pojo.Point;
import coms.pojo.Student;
import coms.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloWorld {
    @Autowired
    private HelloService helloService;

    @GetMapping("/get-sharenode")
    public List<Node> getSharePosition(){
        return StmergeApplication.pbftNode.getNodeList();
    }

    @GetMapping("/get-node-coordinates")
    public List<Node> getPosition(){
        return StmergeApplication.controllerCentre.getNodeList();
    }

    @GetMapping("/executeSearch")
    public Boolean executeSearch(@RequestParam int x1, @RequestParam int y1, @RequestParam int lx, @RequestParam int ly, @RequestParam int rx, @RequestParam int ry){
        Point t1 = new Point(lx, ly);
        Point t2 = new Point(lx, ry);
        Point t3 = new Point(rx, ly);
        Point t4 = new Point(rx, ry);
        StmergeApplication.controllerCentre.Operation1(x1,y1,t1,t2,t3,t4);
        return true;
    }
    @GetMapping("/moveToPosition")
    public Boolean moveToPosition(@RequestParam int x1, @RequestParam int y1){
        StmergeApplication.controllerCentre.Operation2(x1,y1);
        return true;
    }
    @GetMapping("/startSearch")
    public Boolean startSearch(@RequestParam int lx, @RequestParam int ly, @RequestParam int rx, @RequestParam int ry){
        Point t1 = new Point(lx, ly);
        Point t2 = new Point(lx, ry);
        Point t3 = new Point(rx, ly);
        Point t4 = new Point(rx, ry);
        StmergeApplication.controllerCentre.Operation4(t1,t2,t3,t4);
        return true;
    }
    @GetMapping("/stopSearch")
    public Boolean stopSearch(){
        StmergeApplication.controllerCentre.Operation3();
        return true;
    }
    @GetMapping("/startSharing")
    public Boolean startSharing(){
        StmergeApplication.controllerCentre.Operation6();
        return true;
    }
    @GetMapping("/stopSharing")
    public Boolean stopSharing(){
        StmergeApplication.controllerCentre.Operation7();
        return true;
    }
}
