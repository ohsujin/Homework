# 실습과제

##vagrant 설정

1.Vagrnat box 추가

 프로젝트 폴더를 만들고 해당 폴더에 들어가 다음 명령어를 입력한다.
```
> vagrant box add ubuntu/trusty64
```
2.가상 머신 생성하기 
다음명령어로 box명을 확인한다.
아래 명령어를 입력하면 " ubuntu/trusty64 (virtualbox, 14.04) " 라고 나오는데 앞에  ubuntu/trusty64 이부분이 박스명이다.
```
> vagrant box lis
```
박스명을 확인한뒤 다음 명령어를 입력한다.
```
> vagrant init  ubuntu/trusty64 [자신의 box이름]
```

3.vagrantfile 설정하기
 
 위에서 vagrant init 명령어로 생성된 vagrantfile 파일을 다음과 같이 수정한다.
```
# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://atlas.hashicorp.com/search.
 
 # master node
  config.vm.define "master" do |master|
    master.vm.provider "virtualbox" do |v|
      v.name = "master"
      v.memory = 4096
      v.cpus = 1
    end
    master.vm.box = "ubuntu/trusty64"
    master.vm.hostname = "master"
    master.vm.network "private_network", ip: "192.168.200.2"
    master.vm.network "public_network"
    master.vm.provision "shell", path: "./setup.sh"
  end

  # slave1 node
  config.vm.define "slave1" do |slave1|
    slave1.vm.provider "virtualbox" do |v|
      v.name = "slave1"
      v.memory = 2048
      v.cpus = 1
    end
    slave1.vm.box = "ubuntu/trusty64"
    slave1.vm.hostname = "slave1"
    slave1.vm.network "private_network", ip: "192.168.200.100"
    slave1.vm.network "public_network"
    slave1.vm.provision "shell", path: "./setup.sh"
  end

  config.vm.define "slave2" do |slave2|
    slave2.vm.provider "virtualbox" do |v|
      v.name = "slave2"
      v.memory = 2048
      v.cpus = 1
    end
    slave2.vm.box = "ubuntu/trusty64"
    slave2.vm.hostname = "slave2"
    slave2.vm.network "private_network", ip: "192.168.200.101"
    slave2.vm.network "public_network"
    slave2.vm.provision "shell", path: "./setup.sh"
  end
  
end
```

4.shell.sh 파일 만들기
 
vagrantfile이 생성된 프로젝트 폴더에 들어가 아래 내용을 shell.sh 라는 이름으로 저장해 줍니다.
```
#!/bin/bash

# Variables
tools=/home/hadoop/tools
JH=/home/hadoop/tools/jdk
HH=/home/hadoop/tools/hadoop


####### root user 실행 #######
# Install jdk
apt-get install -y openjdk-7-jre-headless
apt-get install -y openjdk-7-jdk
apt-get install -y expect
apt-get install -y git
apt-get install -y maven2

# Add group and user
addgroup hadoop
useradd -g hadoop -d /home/hadoop/ -s /bin/bash -m hadoop
expect <<EOF
spawn passwd hadoop
expect "Enter new UNIX password:"
        send "hadoop\r"
expect "Retype new UNIX password:"
        send "hadoop\r"
expect eof
EOF

# Setting Hosts
echo -e "192.168.200.2 master\n192.168.200.100 slave1\n192.168.200.101 slave2" > /etc/hosts

whoami
mkdir $tools
cd $tools

wget http://ftp.daum.net/apache//hadoop/common/hadoop-1.2.1/hadoop-1.2.1.tar.gz
tar xvf hadoop-1.2.1.tar.gz
ln -s $tools/hadoop-1.2.1 $tools/hadoop
ln -s /usr/lib/jvm/java-1.7.0-openjdk-amd64 $tools/jdk

chown -R hadoop:hadoop /home/hadoop

# Setting environment
echo "" >> ~hadoop/.bashrc
echo "export JAVA_HOME=$JH" >> ~hadoop/.bashrc
echo "export HADOOP_HOME=$HH" >> /home/hadoop/.bashrc
echo "export PATH=\$PATH:\$JAVA_HOME/bin:\$HADOOP_HOME/bin" >> ~hadoop/.bashrc
```

5.ssh 접속.

127.0.0.1:2222, 2200, 2201 각 포트 별로 접속을 시도합니다.

id : vagrant

pw : vagrant

master, slave1,2에 접속한뒤 > su 명령어를 입력하여 root 계정으로 접근합니다.

root 계정에 로그인 되면 
> su - hadoop 

명령어를 입력하여 hadoop 계정으로 로그인을 합니다.

비밀번호를 물으면 hadoop이라고 치면 됩니다.

master에서 홈 디렉토리 및에 "hadoop_shell.sh" 파일을 만들고 아래 내용을 복사해서 붙여 넣은 다음 명령어를 입력하여 shell script를 실행해줍니다.

> sh hadoop_shell.sh 

```
#!/bin/bash
hostname=$(hostname)
####### hadoop user 실행 #######
if [ $hostname = "master" ]
then
        #ssh 접속 key 생성 및 배포
        ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa
expect <<EOF
        spawn ssh-copy-id -i /home/hadoop/.ssh/id_rsa.pub hadoop@slave1
        expect "Are you sure you want to continue connecting (yes/no)?"
                send "yes\r"
        expect "hadoop@slave1's password:"
        send "hadoop\r"
    spawn ssh-copy-id -i /home/hadoop/.ssh/id_rsa.pub hadoop@slave2
     expect "Are you sure you want to continue connecting (yes/no)?"
                send "yes\r"
        expect "hadoop@slave2's password:"
        send "hadoop\r"
        expect eof
EOF
        cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

#hadoop 설정
mkdir -p /home/hadoop/hdfs/name

sed '6,8d' /home/hadoop/tools/hadoop/conf/core-site.xml > /home/hadoop/tools/hadoop/conf/core-site.xml1
sed '6,8d' /home/hadoop/tools/hadoop/conf/mapred-site.xml > /home/hadoop/tools/hadoop/conf/mapred-site.xml1
sed '6,8d' /home/hadoop/tools/hadoop/conf/hdfs-site.xml > /home/hadoop/tools/hadoop/conf/hdfs-site.xml1 

mv /home/hadoop/tools/hadoop/conf/core-site.xml1 /home/hadoop/tools/hadoop/conf/core-site.xml
mv /home/hadoop/tools/hadoop/conf/mapred-site.xml1 /home/hadoop/tools/hadoop/conf/mapred-site.xml
mv /home/hadoop/tools/hadoop/conf/hdfs-site.xml1 /home/hadoop/tools/hadoop/conf/hdfs-site.xml


echo "<configuration>\n <property>\n  <name>fs.default.name</name>\n  <value>hdfs://master:9000</value>\n </property>\n</configuration>" >> /home/hadoop/tools/hadoop/conf/core-site.xml
echo "<configuration>\n <property>\n  <name>dfs.name.dir</name>\n <value>/home/hadoop/hdfs/name</value>\n </property>\n <property>\n  <name>dfs.data.dir</name>\n  <value>/home/hadoop/hdfs/data</value>\n </property>\n <property>\n  <name>dfs.replication</name>\n <value>2</value>\n </property>\n</configuration>" >> /home/hadoop/tools/hadoop/conf/hdfs-site.xml
echo "<configuration>\n <property>\n  <name>mapred.job.tracker</name>\n <value>master:9001</value>\n</property>\n</configuration>" >> /home/hadoop/tools/hadoop/conf/mapred-site.xml

echo "master" > /home/hadoop/tools/hadoop/conf/masters
echo "slave1" > /home/hadoop/tools/hadoop/conf/slaves
echo "slave2" >> /home/hadoop/tools/hadoop/conf/slaves

echo "" >> /home/hadoop/tools/hadoop/conf/hadoop-env.sh
echo "export JAVA_HOME=/home/hadoop/tools/jdk" >> /home/hadoop/tools/hadoop/conf/hadoop-env.sh
echo "export HADOOP_HOME=/home/hadoop/tools/hadoop" >> /home/hadoop/tools/hadoop/conf/hadoop-env.sh
echo "export HADOOP_HOME_WARN_SUPPRESS=“TRUE" >> /home/hadoop/tools/hadoop/conf/hadoop-env.sh
echo "export HADOOP_OPTS=-server" >> /home/hadoop/tools/hadoop/conf/hadoop-env.sh

rsync -av /home/hadoop/tools/hadoop/conf slave1:/home/hadoop/tools/hadoop
rsync -av /home/hadoop/tools/hadoop/conf slave2:/home/hadoop/tools/hadoop

rsync -av /home/hadoop/hadoop_shell.sh slave1:/home/hadoop
rsync -av /home/hadoop/hadoop_shell.sh slave2:/home/hadoop


else
        mkdir -p /home/hadoop/hdfs/data
fi


chmod 755 -R /home/hadoop/hdfs

```
다음으로 slave1 과 slave2의 hadoop 유저로 접속하여 홈 디렉토리로 들어가 다음 명령어를 쳐줍니다.
> sh hadoop_shell.sh

slave1 , slave2의 홈 디렉토리에 hdfs 폴더가 생긴것을 확인한다.

6.namenode 초기화 & hadoop start
master에서 다음 명령어를 입력한다.
> hadoop namenode -format

중간에 
> "Re-format filesystem in /home/hadoop/hdfs/name ? (Y or N) " 

이 나오면 Y 를 입력해줍니다. 포맷이 완료된 후에 다음 명령어를 입력하여 hadoop을 실행합니다.
> start-all.sh

실행중에 
> Are you sure you want to continue connecting (yes/no)?

이라는 신호가 나오면 yes를 입력 해줍니다.

7.hadoop 실행확인

아래 명령어를 입력하여 master에 jobtracker, secondarynamenode,namenode가 실행중인지 확인하고 slave1,2 에는 TaskTracker,DataNode가 실행중인지 확인해줍니다.
> jps 

8.git 저장소 복사

과제를 수행할 폴더를 만들고 원격 저장소를 복사해줍니다.
```
> mkdir 

```
> 
