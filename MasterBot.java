//final one please upload this master
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.Random;
import java.net.URLConnection;
public class MasterBot
{
private int Portnum;
public void run(int n)throws Exception
  {
    Portnum=n;
    System.out.println(Portnum);
  }

public static void main(String[] args)throws Exception
{
  int pn=0;
  String commandpromt="";
  MasterBot srv =new MasterBot();
if(args.length==2)
{
  switch(args[0])
  {
    case "p":
    pn=Integer.parseInt(args[1]);
    //srv.run(pn);
    break;
    case "-p":
    pn=Integer.parseInt(args[1]);
    //srv.run(pn);
    break;
    default:
      System.err.println("Enter valid input");
      break;
  }

  Master1 mstr = new Master1(pn);
  mstr.start();
  while(true)
  {
    System.out.print(">");
    Scanner s= new Scanner(System.in);
    commandpromt=s.nextLine();
    String[] command=commandpromt.split(" ");
    int length1=command.length;
    switch(command[0])
    {
      case "list":
      mstr.listSlaves();
      break;
      case "connect":
      int caserandom=0;
      String kal_url="";
      int con=1;
      try
      {
        con=Integer.parseInt(command[4]);
        caserandom=1;
      }
      catch(Exception e)
      {
      }
      if(length1==6)
      {
      if(caserandom==1)
      {
      if(command[5].equals("keepalive"))
      {
        kal_url="active";
      }
      else if(command[5].contains("url="))
      {
      kal_url=command[5];
      }
    }
  }
    else if(length1==5)
    {
      if(command[4].equals("keepalive"))
      {
        kal_url="active";
        con=1;
      }
      else if(command[4].contains("url="))
      {
      kal_url=command[4];
      con=1;
      }
      else
      {
        con=Integer.parseInt(command[4]);
      }
    }
      mstr.connecttoslave(command[1],command[2],Integer.parseInt(command[3]),con,kal_url);
      break;
      case "disconnect":
      int cont=1;
      try
      {
        cont=Integer.parseInt(command[4]);
      }
      catch(Exception e)
      {
      }
      mstr.slavediconnectcommand(command[1],command[2],command[3],cont);
      break;
     case "rise-fake-url":
      {
      int portrise=Integer.parseInt(command[1]);
      mstr.riseurl(portrise,command[2]);
      break;
      }
      case "down-fake-url":
      {
       int porturl=Integer.parseInt(command[1]);
       mstr.downurl(porturl,command[2]);
       break;
     }
      default:
        System.err.println("Enter valid input");
      break;
        }
    }
}
}
}
class Master1 implements Runnable
{
  Thread masterth;
  int portNum;
  ArrayList<String> slaveList;

  public Master1(int portNum) {
    this.portNum = portNum;
    this.slaveList = new ArrayList<String>();
  }

  public void listSlaves()
  {
      for (String slave : this.slaveList)
      {
        System.out.println(slave);
      }
  }
 public void connecttoslave(String Hostip , String Targethostname, int targetport_no,int connections, String kalive)
 {
   int flag1=0;
if(Hostip.equals("all"))
{
  //for(int z=1;z<connections+1;z++)
  //{
  for(int i = 0; i < this.slaveList.size(); i++)
  {
    String[] info = this.slaveList.get(i).split(" ");
    try
    {
    Socket slavereciever = new Socket(info[1],Integer.parseInt(info[2]));
    PrintStream ps = new PrintStream(slavereciever.getOutputStream());
    PrintWriter ps1 = new PrintWriter(slavereciever.getOutputStream(),true);
    if((kalive.contains("url=")))
    {
      flag1=1;
    }
    ps1.println("connect "+Targethostname+" "+targetport_no+" "+connections+" "+kalive+" "+flag1);
    if(!(kalive.equals("keepalive")))
    {
    slavereciever.close();
  }
  }
  catch (Exception e)
  {
    System.err.println("Exception in connections to slave");
  }
}
//}
}
else
{
  //for(int z=1;z<connections+1;z++)
  //{
  for(int i = 0; i < this.slaveList.size(); i++)
  {
    String[] info = this.slaveList.get(i).split(" ");
    if(info[0].equals(Hostip) || info[1].equals(Hostip))
    {
      portNum=Integer.parseInt(info[2]);
    }
}
   try
   {
   Socket slavereciever = new Socket(Hostip,portNum);
   PrintStream ps = new PrintStream(slavereciever.getOutputStream());
   PrintWriter ps1 = new PrintWriter(slavereciever.getOutputStream(),true);
   ps1.println("connect "+Targethostname+" "+targetport_no+" "+connections+" "+kalive+" "+flag1);
   if(!(kalive.equals("keepalive")))
   {
   slavereciever.close();
 }
 }
 catch (Exception e)
 {
 //System.out.println("problem here");
 }
//  }
}
//}
}
public void slavediconnectcommand(String hostip,String Targethostname,String targetport_no,int connections)
{
  int targetport_no1=0;
  if(hostip.equals("all"))
  {
    if(targetport_no.equals("all"))
    {
       targetport_no1=-1;
    }
    else
    {
      targetport_no1=Integer.parseInt(targetport_no);
    }
    for(int i = 0; i < this.slaveList.size(); i++)
    {
      String[] info = this.slaveList.get(i).split(" ");
      try
      {
      Socket disconnectcontrol = new Socket(info[1],Integer.parseInt(info[2]));
      PrintStream ps = new PrintStream(disconnectcontrol.getOutputStream());
      PrintWriter ps4 = new PrintWriter(disconnectcontrol.getOutputStream(),true);
      ps4.println("disconnect "+Targethostname+" "+targetport_no1);
      disconnectcontrol.close();
    }
    catch (Exception e)
    {
      System.err.println("Exception in disconnect to slave");
    }
  }
}
  else
  {
  if(targetport_no.equals("all"))
  {
     targetport_no1=-1;
  }
  else
  {
    targetport_no1=Integer.parseInt(targetport_no);
  }
  for(int i = 0; i < this.slaveList.size(); i++)
  {
    String[] info = this.slaveList.get(i).split(" ");
    if(info[0].equals(hostip) || info[1].equals(hostip))
    {
      portNum=Integer.parseInt(info[2]);
    }
}
try{
  Socket disconnectcontrol=new Socket(hostip,portNum);
  PrintStream ps = new PrintStream(disconnectcontrol.getOutputStream());
  PrintWriter ps4 = new PrintWriter(disconnectcontrol.getOutputStream(),true);
  ps4.println("disconnect "+Targethostname+" "+targetport_no1);
  disconnectcontrol.close();
}
catch(Exception e)
{
}
}
}
public void riseurl(int portnum,String url)
{
  for(int i = 0; i < this.slaveList.size(); i++)
  {
    String[] info = this.slaveList.get(i).split(" ");
    try
    {
    Socket risefake = new Socket(info[1],Integer.parseInt(info[2]));
    PrintStream ps = new PrintStream(risefake.getOutputStream());
    PrintStream ps7 = new PrintStream(risefake.getOutputStream());
    ps7.println("rise "+portnum+" "+url);
    risefake.close();
  }
  catch(Exception e)
  {

  }
System.out.println(portnum);
System.out.println(url);
}
}
public void downurl(int portnum,String url)
{
  for(int i = 0; i < this.slaveList.size(); i++)
  {
    String[] info = this.slaveList.get(i).split(" ");
    try
    {
    Socket downfake = new Socket(info[1],Integer.parseInt(info[2]));
    PrintStream ps = new PrintStream(downfake.getOutputStream());
    PrintStream ps8 = new PrintStream(downfake.getOutputStream());
    ps8.println("down "+portnum+" "+url);
    downfake.close();
  }
  catch(Exception e)
  {

  }
  System.out.println(portnum);
  System.out.println(url);
  }
}
  @Override
  public void run()
  {
    while(true)
    {
    try {
    ServerSocket socketmaster=new ServerSocket(portNum);
    Socket client_slave = socketmaster.accept();
    InputStreamReader IR = new InputStreamReader(client_slave.getInputStream());
    BufferedReader bf = new BufferedReader(IR);
    String message = bf.readLine();
    this.slaveList.add(message);
//    if(message != null)
//    {
//    PrintStream ps = new PrintStream(client.getOutputStream());
//    ps.println("message recieved");
//    }
    socketmaster.close();
  } catch(Exception e) {

  }
    }
  }
  public void start()
  {
    if (masterth==null)
    {
      masterth = new Thread(this,"Master");
      masterth.start();
    }
  }//end of start

}
