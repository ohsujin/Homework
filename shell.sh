#!/bin/bash

# Variables
JAVA=/home/hadoop/tools/jdk
HD_Home=/home/hadoop/tools/hadoop
tools=/home/hadoop/tools

####### root user 실행 #######
# Install jdk
apt-get install -y openjdk-7-jre-headless
apt-get install -y openjdk-7-jdk
apt-get install -y expect
apt-get install -y git

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

mkdir $tools
cd $tools
pwd

#Maven Download
wget http://mirror.apache-kr.org/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
tar xvf apache-maven-3.2.5-bin.tar.gz
ln -s $tools/apache-maven-3.2.5 $tools/maven

#Hadoop Download
wget http://ftp.daum.net/apache//hadoop/common/hadoop-1.2.1/hadoop-1.2.1.tar.gz
tar xvf hadoop-1.2.1.tar.gz
ln -s $tools/hadoop-1.2.1 $tools/hadoop
ln -s /usr/lib/jvm/java-1.7.0-openjdk-amd64 $tools/jdk


chown -R hadoop:hadoop /home/hadoop

# Setting environment
echo "" >> ~hadoop/.bashrc
echo "export JAVA_HOME=$JAVA" >> /home/hadoop/.bashrc
echo "export HADOOP_HOME=$HD_Home" >> /home/hadoop/.bashrc
echo "export MVN_HOME=$tools/maven" >> /home/hadoop/.bashrc
echo "export PATH=\$PATH:\$JAVA_HOME/bin:\$HADOOP_HOME/bin:\$MVN_HOME/bin" >> /home/hadoop/.bashrc

