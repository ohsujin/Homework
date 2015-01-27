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
