基于Apache FTPServer和SpringBoot进行的FTPServer封装，目的是开箱即用，不推荐用于生产环境

说明

* application.yml：FTPServer的核心配置
* users：用户信息。若存在多个用户，则创建多个properties文件即可
* ftp-server.jar：应用jar包

启动命令

```sh
java -jar ftp-server.jar
```

