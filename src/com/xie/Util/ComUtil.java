package com.xie.Util;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ComUtil {
    private static SerialPort serialPort = null;
    private static ComUtil comUtil = null;

    private ComUtil(){}

    public static ComUtil getComUtil()
    {
        comUtil = new ComUtil();
        return comUtil;
    }

    public ArrayList<String> getPortNameList()
    {
        SerialPort[] portList = SerialPort.getCommPorts();
        ArrayList<String> portNameList = new ArrayList<>();

        for (SerialPort port:
             portList) {
            portNameList.add(port.getSystemPortName());
        }
        return portNameList;
    }

    public void openPort()
    {
        serialPort = SerialPort.getCommPort("COM2");
        serialPort.setBaudRate(115200);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        if(!serialPort.openPort())
        {
            System.out.println("打开串口失败");
        }else
        {
            System.out.println("打开串口成功:"+serialPort.getSystemPortName());
        }
    }

    public void closePort()
    {
        if(serialPort.closePort())
        {
            System.out.println("关闭串口成功!" + serialPort.getSystemPortName());
        }else
        {
            System.out.println("关闭串口失败!"+serialPort.getSystemPortName());
        }
    }

    public void sendMsg(byte[] msg)
    {
        if(serialPort == null)
        {
            System.out.println("串口未打开!");
            return;
        }
        System.out.println("发送数据中...");
        OutputStream os = null;
        try
        {
            os = serialPort.getOutputStream();
            os.write(msg);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(os!=null)
            {
                try {
                    os.close();
                    os=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] readFromPort()
    {
        InputStream is = null;
        byte[] buffer = null;
        try
        {
            is = serialPort.getInputStream();
            int bufflenth = is.available();
            while(bufflenth!=0)
            {
                buffer = new byte[bufflenth];
                is.read(buffer);
                bufflenth = is.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(buffer==null)
        {
            return "".getBytes();
        }else
        {
            return buffer;
        }
    }

    public SerialPort getSerialPort()
    {
        return serialPort;
    }
}