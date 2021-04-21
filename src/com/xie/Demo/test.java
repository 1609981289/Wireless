package com.xie.Demo;

import com.fazecast.jSerialComm.SerialPort;
import com.xie.Util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

@SuppressWarnings("all")
public class test extends JFrame {

    public static ComUtil comUtil = null;
    public static ArrayList<String> portNameList = null;
    public static SerialPort serialPort = null;

    public static ArrayList<String> id = null;

    public static JTextArea t_sendData = null;
    public static JTextArea t_getDate = null;

    public static JTextArea idText =null;

    public static JButton b_sendDate = null;
    public static JButton b_openPort = null;
    public static JButton b_closePort = null;

    public static JScrollPane js = null;
    public static JScrollPane idJs = null;

    public static long prevTime = 0;
    public static long currTime = 0;

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

        id = new ArrayList<>();

        t_sendData = new JTextArea();
        t_getDate = new JTextArea();

        idText = new JTextArea();
        idText.setEnabled(false);

        js = new JScrollPane(t_getDate);
        idJs = new JScrollPane(idText);

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

        t_getDate.setLineWrap(true);
        idJs.setBounds(120,20,350,120);
        js.setBounds(0,150,480,120);

        idText.setBounds(120,20,250,120);
        idText.setLineWrap(true);

        b_sendDate.setBounds(230,350,60,60);
        b_openPort.setBounds(0,80,100,60);
        b_closePort.setBounds(0,80,100,60);
        //添加组件
        container.add(idJs);
        container.add(t_sendData);
        container.add(js);

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
                prevTime = System.currentTimeMillis();
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
                currTime = System.currentTimeMillis();
                if(currTime - prevTime > 60*1000)
                {
                    id.clear();
                    idText.setText("");
                    prevTime = currTime;
                }
                String msg = comUtil.readFromPort().toString();
                t_getDate.append(msg);
                String idArray[] = msg.split("\n");
                int i = 0;
                for(i = 0;i<idArray.length;i++)
                {
                    if(!id.contains(comUtil.getDate(idArray[i]))&&!"".equals(idArray[i])&&idArray[i]!=null) {
                        System.out.println("不存在");
                        id.add(comUtil.getDate(idArray[i]));
                        idText.append(comUtil.getDate(idArray[i]));
                        idText.append("\n");
                    }
                }
            }
        }
    };
}
