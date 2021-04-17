package com.xie.Demo;

import com.fazecast.jSerialComm.SerialPort;
import com.xie.Util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

@SuppressWarnings("all")
public class test extends JFrame {

    public static ComUtil comUtil = null;
    public static ArrayList<String> portNameList = null;
    public static SerialPort serialPort = null;

    public static JTextField t_sendData = null;
    public static JTextField t_getDate = null;


    public static JButton b_sendDate = null;
    public static JButton b_openPort = null;
    public static JButton b_closePort = null;


    public test()
    {
        setTitle("串口通信Demo");
        setBounds(300,200,500,500);
        Init();
        showPanel();
        addlisten();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        test t = new test();
    }

    public void Init()
    {
        comUtil = ComUtil.getComUtil();
        portNameList = comUtil.getPortNameList();

        t_sendData = new JTextField();
        t_getDate = new JTextField();


        b_sendDate = new JButton("发送");
        b_openPort = new JButton("打开串口");
        b_closePort = new JButton("关闭串口");

        b_closePort.setVisible(false);
    }

    public void showPanel()
    {
        Container container = getContentPane();
        container.setLayout(null);

        t_sendData.setBounds(0,300,500,30);
        t_getDate.setBounds(0,150,500,100);

        b_sendDate.setBounds(230,350,60,60);
        b_openPort.setBounds(0,80,100,60);
        b_closePort.setBounds(0,80,100,60);
        //添加组件
        container.add(t_sendData);
        container.add(t_getDate);


        container.add(b_openPort);
        container.add(b_sendDate);
        container.add(b_closePort);
    }

    public void addlisten()
    {
        b_openPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comUtil.openPort();
                serialPort = comUtil.getSerialPort();
                Thread thread = new Thread(runnable);
                thread.start();
                b_openPort.setVisible(false);
                b_closePort.setVisible(true);
            }
        });

        b_closePort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comUtil.closePort();
                b_openPort.setVisible(true);
                b_closePort.setVisible(false);
            }
        });


        b_sendDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = t_sendData.getText();
                comUtil.sendMsg(ByteUtil.hex2byte(msg));
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(serialPort.isOpen())
            {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String msg = ByteUtil.byteArrayToHexString(comUtil.readFromPort());
                t_getDate.setText(msg);
            }
        }
    };
}
