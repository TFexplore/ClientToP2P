package com.company;

public class Client {
    String num;
    String name;
    String ip;
    Integer port;
    Boolean onConnecting;
    public Client(String num, String name, String ip, Integer port) {
        this.num = num;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.onConnecting=false;
    }

    public Boolean isOnConnecting() {
        return onConnecting;
    }

    public void setOnConnecting(Boolean onConnecting) {
        this.onConnecting = onConnecting;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
