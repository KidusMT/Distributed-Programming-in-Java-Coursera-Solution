package edu.coursera.distributed;

import java.lang.reflect.Array;

public class Test {
    public static void main(String[] args) {
        final PCDPFolder root = new PCDPFolder("static");
        PCDPFilesystem fs = new PCDPFilesystem();
        String line = "GET /path/to/file HTTP/1.1;\n";
        assert line != null;
        assert line.startsWith("GET");
        final String path = line.split(" ")[1];
        PCDPPath Ppath = new PCDPPath(path);
        if(fs.readFile(Ppath)!=null){
            System.out.println("working");
        }else{
            System.out.println("null returned");
        }
//        File f = new File(fs.readFile(Ppath));
//        System.out.println(fs.readFile(Ppath));
//        System.out.println(Ppath.toString().substring(1).split("/"));//.getComponent(0).equals(root.getName()));
//        path.getComponent(0).equals(root.getName())
//        path.substring(1).split("/")

    }
}
