# 실습과제

###git으로 부터 clone 받기.
git으로 부터 프로젝트를 다운로드 받습니다.
> git clone https://github.com/ohsujin/Homework.git
> cd ./Homework

###vagrant up 하기
git에서 다운로드 받은 Homework폴더에 있는 vagrantfile을 이용하여 vagrant를 up시킨다.
> vagrant up

###master node로 접속.
vagrant로 띄운 가상리눅스에 접속하여 작업을 수행한다.
```
ssh 를 이용하여 ip : 127.0.0.1  | port : 2222( or 2200,2201) 으로 접속하여 master node를 찾습니다.
```

###hadoop_shell.sh 파일 실행
master 접속한뒤 > su 명령어를 입력하여 root 계정으로 접근합니다.

root 계정에 로그인 되면 
> su - hadoop 

비밀번호를 물으면 hadoop 을 입력하면 됩니다.

###생성된 가상 리눅스환경에서 다시한번 git으로 부터 clone 받기.
git으로 부터 프로젝트를 다운로드 받습니다.
> git clone https://github.com/ohsujin/Homework.git

> cd ./Homework

###master에서 git으로부터 clone한 Homework 및에있는 "hadoop_shell.sh" 파일을 실행 합니다.
> sh hadoop_shell.sh 

마찬가지로 slave1 과 slave2의 hadoop 유저로 접속하여 $홈 디렉토리로 들어가 다음 명령어를 쳐줍니다.
> sh hadoop_shell.sh

slave1 , slave2의 홈 디렉토리에 hdfs 폴더가 생긴것을 확인한다.

#### 6.namenode 초기화 & hadoop start
master에서 다음 명령어를 입력한다.
> hadoop namenode -format

중간에 
> "Re-format filesystem in /home/hadoop/hdfs/name ? (Y or N) " 

이 나오면 Y 를 입력해줍니다. 포맷이 완료된 후에 다음 명령어를 입력하여 hadoop을 실행합니다.
> start-all.sh

실행중에 
> Are you sure you want to continue connecting (yes/no)?

이라는 신호가 나오면 yes를 입력 해줍니다.

#### 7.hadoop 실행확인

아래 명령어를 입력하여 master에 jobtracker, secondarynamenode,namenode가 실행중인지 확인하고 slave1,2 에는 TaskTracker,DataNode가 실행중인지 확인해줍니다.
> jps 

#### 8.git 저장소 복사

과제를 수행할 폴더를 만들고 원격 저장소를 복사해줍니다.
```
> mkdir workspace
> cd workspace
> git clone https://github.com/ohsujin/Homework.git
```
> ls -l

명령어를 입력하여 clone이 잘 됬는지 확인을 해줍니다.

#### 9.maven 프로젝트 생성
홈 디렉토리로 들어가 아래 명령어를 입력합니다.
> mvn archetype:generate

```
입력창이 나오면 [enter]를 누르다가
다음 입력값을 요구하면
Define value for property 'groupId': : Frequence_Class
Define value for property 'artifactId': : Frequence_Class
Define value for property 'version': 1.0-SNAPSHOT:[enter]
Define value for property 'package': TF_IDF:[enter]

위와 같이 입력해줍니다.
```

#### 10.Freq_TF_IDF.java maven build하기
maven프로젝트로 생성된 폴더로 들어가 아래 명령을 수행합니다.
 > cd ~/Frequence_Class 
 
 > rm -rf src/main/java/Frequence_Class/App.java
 
 > cp ~/workspace/Homework/TF_IDF/src/WordFrequenceInDocument.java ./src/main/java/Frequence_Class/
 
 > cp ~/workspace/Homework/Freq_TF_IDF_pom.xml ./pom.xml
 
 > mvn package
 
```
Frequence_Class/target
```
 
 페키징 과정까지 수행이 완료되면 로컬 git 저장소로 이동하여 분석할 파일을 압축 해제 합니다.
 > cd workspace/Homework/document
 
 > tar xvf bible.tar
 
 > tar xvf shakespeare.tar

#### 11.Freq_TF_IDF.jar 파일 hadoop으로 실행 시켜 보기
hdfs에 input 폴더를 생성하고 분석할 파일을 put 해줍니다.
> hadoop dfs -mkdir input 

> hadoop dfs -put ~/workspace/Homework/document/shakespeare input
> 




 
 



