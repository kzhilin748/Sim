package com.use.logwrite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class logwrite {
  public void writelog(int DAGTime) throws IOException{
    File log  =new File("log");
    if(!log.exists()){
    	log.createNewFile();
    }
    FileWriter fw=new FileWriter(log, true);
    fw.write(DAGTime+"\n");
    fw.close();
  }

}
