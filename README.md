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
