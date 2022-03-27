package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.LockSupport;

public class UdpToServer extends Thread{

    String num;
    String name;

    InetAddress serverAddress;
    Integer serverPort;
    Integer localPort;
    final String str_serverIp="42.193.48.88";//"127.0.0.1"---

    DatagramSocket socket;
    boolean flag = true;
    public static UdpToServer Instance;

    HashMap<String,Client> clients;

    public Client client;

    Gson gson;

    Client server;


    static {
        try {
            Instance = new UdpToServer();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public UdpToServer() throws SocketException, UnknownHostException {
        serverAddress=InetAddress.getByName(str_serverIp);
        serverPort=8088;
        socket = new DatagramSocket();
        gson=new Gson();
        server=new Client("0000","server",str_serverIp,serverPort);
    }



    public static UdpToServer getInstance() {
        return Instance;
    }

    public void setNum(String num){
        this.num = num;
    }

    public void setClientName(String clientName){
        this.name=clientName;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("***************Udp心跳已开启***************");
        try {
            Gson gson=new Gson();
            if (num==null||name==null){
                System.out.println("bad input");
                return;
            }
            String string=num+"#"+name;//客户端编号和名称
            Msg msg=new Msg(1,string);//1为心跳事件
            byte[] sendBuf = gson.toJson(msg).getBytes();
            DatagramPacket packet= new DatagramPacket(sendBuf, sendBuf.length,serverAddress,serverPort);
            socket.send(packet);
            System.out.println(socket.getLocalAddress()+"Port:"+socket.getLocalPort());
            while (flag) {
                byte[] recvBuf =new byte[1024];
                DatagramPacket recvPacket=new DatagramPacket(recvBuf,recvBuf.length);
                socket.setSoTimeout(30000);
                try {
                    socket.receive(recvPacket);
                    String res= new String(recvBuf,0,recvBuf.length);
                    String str=new String(res).trim();
                    //System.out.println("Udp-recv: "+str);
                    Msg rmsg=gson.fromJson(str,Msg.class);
                    MsgAnalyze(rmsg);
                    //TO-DO
                }catch (SocketTimeoutException e){//超时发送心跳包
                    socket.send(packet);
                }

            }
        }catch (IOException  e){
            e.printStackTrace();
        }
    }
    private void MsgAnalyze(Msg msg) throws IOException {
        String num;
        String msgbuf;
        switch (msg.getCode()){
            case 0:

                break;
            case 1://心跳

                break;
            case 2://获取用户列表
                System.out.println("code:2");
                Type type= new TypeToken<HashMap<String,Client>>(){}.getType();
                clients=gson.fromJson(msg.data,type);
                LockSupport.unpark(Main.mainThread);
                break;
            case 3://建立连接
            client=gson.fromJson(msg.getData(),Client.class);
            MsgSend(client,gson.toJson(new Msg(4,"successful to connect")));
            LockSupport.unpark(Main.mainThread);
                break;
            case 4://断开连接
                System.out.println(client.getName()+": "+msg.getData());
                break;
            default:

                break;
        }

    }
    public void MsgSend(Client client,String msg) throws IOException {

        InetAddress address=InetAddress.getByName(client.getIp());
        byte[] bufBytes = msg.getBytes();
        DatagramPacket datagramPacket=new DatagramPacket(bufBytes,bufBytes.length,address,client.getPort());
        DatagramSocket socket=new DatagramSocket();
        socket.send(datagramPacket);
    }

}
