# S-FTPClient-
S-FTPClient(Swing,Java,Socket,FTP,加密算法)

##问题描述
   实现一个图形用户界面的FTP客户端,保证文件的安全传输和存储。客户端能够发出各种操作命令；实现conn(连接)、list（列示文件）、retr（下载）、store（上载）的功能；使用一种加密算法，在文件上载前进行加密，文件以密文的形式传输和保存在FTP服务器上,设计客户端的密钥管理机制。

##基本要求
1．实现一个图形用户界面的FTP客户端,保证文件的安全传输和存储。
2．功能：
2.1 配置使用IIS的FTP服务器；
2.2 客户端发出各种操作命令；至少实现conn(连接)、list（列示文件）、retr（下载）、store（上载）的功能；选择你学过的加密算法，在文件上载前进行加密，文件以密文的形式传输和保存在FTP服务器上,设计客户端的密钥管理机制。
2.3 接收服务器的操作结果，如显示连接状态，对下载的文件进行解密等。
3．用户界面：客户端界面用户可以设置远程主机名、用户和密码；显示远程文件列表；显示本地文件的列表；操作命令可以采用菜单、按钮及弹出菜单来实现；显示操作状态（操作是否成功、状态、文件操作的进度等）。

##设计思想
    FTP客户端是建立在Java的Swing技术上，首先设计好大概的FTP客户端的界面模型，然后利用Java的Swing技术绘制好FTP客户端的界面，最后利用Socket技术设计操作FTP客户端的命令等。用户可以通过它把自己机器与世界各地所有运行 FTP协议的服务器相连，访问服务器上的资源和信息。当启动 FTP 从远程计算机拷贝文件时，事实上启动了两个程序：一个本地机器上FTP客户端程序，它向 FTP服务器提出拷贝文件的请求。另一个是启动在远程计算机的上的 FTP服务器程序，它响应请求把你指定的文件传送到你的计算机中。
    在典型的 FTP 会话过程中，用户一般坐在本地主机前进行同远程主机之间的文件传输。为了能够访问远程账户，用户必须提供用户标识和密码。在通过了身份验证之后，用户就可以在本地主机和远程主机之间传输文件了。用户通过 FTP 的用户代理与 FTP 进行交互。用户首先需要远程提供主机名或 IP 地址，以便本地 FTP的客户进程能够同远程主机上的 FTP服务器进程建立连接。然后，用户提供其标识和密码。一旦验证通过，用户即可在两个系统之间传输文件。
    FTP使用两个并行的 TCP协议来传输文件，一个称为控制连接，另一个称为数据连接。控制连接用来在两台主机之间传输控制信息，如用户标识、密码、操作远程主机文件目录的命令、发送和取向文件的命令等。而数据连接则真正用来发送文件。FTP的控制和数据连接如下图所示。
  

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE1.png)


当用户启动一次与远程主机的 FTP会话时，FTP首先建立一个 TCP连接到 FTP服务器的 21号端口。FTP的客户端则通过该连接发送用户标识和密码等，客户端还可以通过该连接发送命令以改变远程系统的当前工作目录。当用户要求传送文件时，FTP服务器则在其 20号端口上建立一个数据连接，FTP在该连接上传送完毕一个文件后会立即断开该连接。如果再一次 FTP会话过程中需要传送另一个文件，FTP服务器则会建立另一个连接。在整个 FTP会话过程中，控制连接是始终保持的，而数据连接则会随着文件的传输不断的打开和关闭。
综上所述需要根据FTP的控制连接和数据连接这整个流程完成对FTP的操作。


##系统结构

经过需求分析后，决定此FTP客户端分本地文件加载系统模块、连接数据模块、断开连接模块、下载模块、刷新模块、删除模块等几项关键的模块。本程序的系统结构图如下：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE2.png)


FTP客户端结构图


	本地文件系统加载模块：
当页面初始化的时候用来加载本地文件系统的，可以让用户更为直观的，方便的，快捷的选择本地相应的文件进行浏览、上传等操作。本地文件系统加载模块如图所示：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE3.png)


本地文件系统加载模块


	连接模块：

连接模块是在图形界面下用户交互与FTP服务器建立连接的一个核心的模块功能。可以使用规定好的IP地址进行连接，IP无论是外网还是内网都可以进行连接。支持不同端口的FTP服务器进行连接，这个设计是为了方便不同端口的FTP。支持匿名用户和FTP普通用户登录客户端。全称由用户决定，随时可以输入的。连接模块如图所示：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE4.png)


连接模块


	断开模块：
断开模块是方便用户断开已有的连接而设计的。此功能图和连接模块的图一样。

	下载模块：
下载模块是为用户下载FTP服务器的文件而设计的。当用户想要下载的时候，必须要选择上想要下载的文件。不然是不能下载的，界面设计如下：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE5.png)


下载模块


刷新、删除模块如上图所示。

##程序流程

本系统主要的流程如下：

从运行程序到登录FTP服务器到拉取FTP服务器目录

上传文件流程图：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE6.png)


下载数据流程图：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE7.png)


加密流程：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE8.png)


解密过程：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE9.png)


##测试数据

1、测试环境
  使用IIS搭建的本地的FTP服务器。
  
  ![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE10.png)

  
2、测试数据
  第一种：
    只输入FTP服务器的连接地址：127.0.0.1
  第二种：
    输入地址：127.0.0.1
    端口：21
    账号：zhenghui
    密码：8042965
然后进行上传本地的文件和下载FTP服务器上的文件

##测试情况

第一种情况：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE11.png)


选择文件上传

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE12.png)


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE13.png)


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE14.png)


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE15.png)
![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE16.png)




下载：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE17.png)

下载到特定位置：


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE18.png)

下载后的文件：

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE19.png)

选择一个进行删除

![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE20.png)

删除后会立马刷新列表


![image](https://github.com/8042965/S-FTPClient-/blob/master/images/%E5%9B%BE21.png)


















