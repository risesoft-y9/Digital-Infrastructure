## 使用统一码组件步骤（maven）

### 一.注册成为SP

**使用该组件的前提**：必须注册成为统一标识代码注册管理中心的服务提供商（Service Provider简称SP）

注册地址：**<a href="https://work.idcode.org.cn/SPAuthen/SpandUserReg" target="_blank">SP服务商用户注册</a>**

### 二.查看单位根节点IDcode码 

登录【**统一标识代码注册管理中心**】后，【我的主页】-->【查看MA标识代码】

<div><img src="https://vue.youshengyun.com/files/idcode/idcode1.png"><div/>
<div><img src="https://vue.youshengyun.com/files/idcode/idcode2.png"><div/>

### 三.获取授权key

<div><img src="https://vue.youshengyun.com/files/idcode/idcode3.png"><div/>

### 四.添加数字底座中央仓库地址

pom.xml中添加数字底座中央仓库地址

```
<repositories>
        <repository>
            <id>nexus</id>
            <name>local private nexus</name>
            <url>https://svn.youshengyun.com:9900/nexus/repository/maven-public/</url>
            <snapshots>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>nexus</id>
            <name>local private nexus</name>
            <url>https://svn.youshengyun.com:9900/nexus/repository/maven-public/</url>
        </pluginRepository>
    </pluginRepositories>
```

pom.xml中添加统一码组件依赖

```
<dependency>
     <groupId>net.risesoft</groupId>
     <artifactId>risenet-y9boot-idcode</artifactId>
     <version>v9.6.6-SNAPSHOT</version>
</dependency>
```

### 五.配置文件添加配置信息

```
#统一码接口地址
idCode_url: https://api.idcode.org.cn
#统一码解析地址
analyze_url: http://s.idcode.org.cn/
#单位主码
main_code: MA.156.1003.16291
#系统授权key
api_key: 9XRMKN90QA8O90SDAAAAAKJ
#系统授权码
api_code: 09DN09QA8X08XRNN9DRMXRXM9DNAK09Q
```

### 六.统一码接口调用

```
以【获取单位基本信息接口】为例
Result result = Three.m306(ConfigReader.MAIN_CODE);
System.out.println("结果信息:" + result);
```

## 怎么为一个物品生成一个码

### 一.确定物品所属品类信息

通过【202接口】查看【品类用途】获取品类信息

这里以注册一个有形资产为示例。得到一下信息：

用途：60

品类ID :10166

品类码号：10000000



<div><img src="https://vue.youshengyun.com/files/idcode/idcode4.png"><div/>

### 二.注册物品获得一个idcode

>  406接口：注册/备案***产品***品类IDcode码接口 

> 407接口： 注册/备案***非产品***品类IDcode码接口 

有形资产属于非产品类，所以调用407接口，得到物品的idcode码：**MA.156.1003.1629/60.10000000.2/**

注意：一个modelNumber只能注册一次，注册成功后，再使用相同的modelNumber会提示“重复申请”。

```
/**
* 有形资产用途ID
* codeUseId: 60 {@link net.risesoft.interfaces.TwoTest#testM201()}
*/
String codeUseId = "60";
Integer codePayType = CodePayTypeEnum.REGISTER.getValue();
/**
* 有形资产
* 品类ID-industryCategoryId: 10166
* 品类码号-categoryCode: 10000000
*/
Integer industryCategoryId = 10166;
String categoryCode = "10000000";
String modelNumber = "电脑桌-A002";
String modelNumberEn = "desktop-A002";
String introduction = "所属南方第六事业部";
String gotoUrl = "http://example.com/goto";
String sampleUrl = "http://example.com/goto";
IdcodeRegResult result = Four.m407(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode, modelNumber, modelNumberEn, introduction, codePayType, gotoUrl, sampleUrl);
System.out.println("注册/备案结果:" + result);
```

### 三.为idcode生成一个码

>  603：生成码图接口 
>

>  604：生成带边框的码图  

>  605：生成质量认证二维码图  

这里以603为例：

```
String code = "MA.156.1003.1629/60.10000000.2/";
Integer picSize = 300;
Integer codeType = CodeTypeEnum.QR.getValue();
CodeAddress result = Six.m603(code, picSize, codeType);
System.out.println("码图地址:" + result.getAddress());
```

请求接口后会得到一个码图地址：http://api.idcode.org.cn/Photo/20240603155634578985.jpg