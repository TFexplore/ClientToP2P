package com.company;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

public class Main extends Thread {

    public static Thread mainThread;

    public static void main(String[] args) throws InterruptedException, IOException {

                mainThread = Thread.currentThread();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String num = null,name=null;
                System.out.println("Enter your num:");
                num = br.readLine();
                System.out.println("Enter your name:");
                name =br.readLine();
                UdpToServer.getInstance().setClientName(name);
                UdpToServer.getInstance().setNum(num);
                UdpToServer.getInstance().start();
                System.out.println("正在获取用户列表。。。");
                Gson gson=new Gson();
                sleep(1000);
                UdpToServer.getInstance().MsgSend(UdpToServer.getInstance().server,gson.toJson(new Msg(2,num)));
                LockSupport.park();
                UdpToServer.getInstance().clients.forEach((key,client)->{
                    System.out.println(key+": "+client.getName());
                });
                if (UdpToServer.getInstance().clients.size()>1){
                    System.out.println("输入num开始聊天：");
                    String destnum=br.readLine();
                    UdpToServer.getInstance().MsgSend(UdpToServer.getInstance().server,gson.toJson(new Msg(3,destnum+"#"+num)));
                    UdpToServer.getInstance().client=UdpToServer.Instance.clients.get(destnum);
                    while (true){
                        String buf=br.readLine();
                        if (buf.equals("#")){
                            break;
                        }
                        UdpToServer.getInstance().MsgSend(UdpToServer.getInstance().client,gson.toJson(new Msg(4,buf)));
                    }
                    return;
                }
                else {
                    System.out.println("等待连接。。。");
                    LockSupport.park();
                    System.out.println("连接成功："+UdpToServer.getInstance().client.getIp());
                    System.out.println("开始聊天吧：");
                    while (true){
                        String buf=br.readLine();
                        if (buf.equals("#")){
                            break;
                        }
                        UdpToServer.getInstance().MsgSend(UdpToServer.getInstance().client,gson.toJson(new Msg(4,buf)));
                    }
                }




            }
}
