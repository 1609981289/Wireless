package com.xie.Util;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ComUtil {
    private static SerialPort serialPort = null;
    private static ComUtil comUtil = null;

    private final static String FRAMEHEADER = "A55A";
    private final static String FRAMETAIL = "0D0A";
    private static int frameLength = 0;
    private static int bbc = 0;
    private static int date[] = null;

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

    public StringBuilder readFromPort()
    {
        InputStream is = null;
        byte[] buffer = null;
        StringBuilder result = new StringBuilder();
        result.append("");
        if(!serialPort.isOpen())
        {
            return result;
        }
        try
        {
            is = serialPort.getInputStream();
            int bufflenth = is.available();
            while(bufflenth!=0) {
                buffer = new byte[bufflenth];
                is.read(buffer);
                System.out.println("执行");
                String msg = ByteUtil.byteArrayToHexString(buffer);

                while(true)
                {
                    int i = 0;
                    System.out.println(msg);
                    for(i = 0;i<bufflenth-1;i++) {
                        String fh = msg.substring(i,i+4);
                        System.out.println(fh);
                        if(FRAMEHEADER.equals(fh))
                        {
                            System.out.println("帧头正确");
                            break;
                        }else {
                            System.out.println("帧头错误");
                            return result;
                        }
                    }

                    //执行到这里说明匹配成功了；
                    frameLength = Integer.parseInt(msg.substring(i+4,i+8),16);
                    System.out.println(frameLength);
                    String ft = msg.substring(i+frameLength*2-4,i+frameLength*2);
                    System.out.println(ft);
                    if(FRAMETAIL.equals(ft))
                    {
                        System.out.println("帧尾正确");
                    }else {
                        System.out.println("帧尾错误");
                        return result;
                    }

                    date = new int[frameLength-4];
                    int j = 0;
                    int l = 0;
                    for(j = i+4;j<i+frameLength*2-6;j+=2,l++)
                    {
                        date[l] = Integer.parseInt(msg.substring(j,j+2),16);
                    }

                    int bbc = date[0];
                    for(int k = 1;k< date.length;k++)
                    {
                        bbc ^= date[k];
                    }

                    if(bbc == Integer.parseInt(msg.substring(i+frameLength*2-6,i+frameLength*2-4),16))
                    {
                        System.out.println("BBC正确");
                    }else
                    {
                        System.out.println("BBC错误");
                        return result;
                    }

                    result.append(msg.substring(i,i+frameLength*2));
                    //result.append(msg.substring(i+10,i+frameLength*2-6));
                    result.append("\n");
                    System.out.println(bufflenth * 2 - 1);
                    if(bufflenth*2-i-frameLength*2>14)
                    {
                        msg = msg.substring(i+frameLength*2,bufflenth*2);
                        bufflenth-=frameLength;
                        System.out.println(bufflenth*2);
                        System.out.println(msg);
                    }else
                    {
                        break;
                    }
                }
                bufflenth = is.available();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public SerialPort getSerialPort()
    {
        return serialPort;
    }

    public static String getDate(String msg)
    {
        if("".equals(msg))
        {
            return "";
        }
        int length = Integer.parseInt(msg.substring(4,8),16);
        if(length>8)
        {
            return msg.substring(10,length*2-6);
        }
        return "";
    }
}