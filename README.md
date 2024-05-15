# 用户管理系统
## 初始化
### 后端项目初始化
springboot官网/IDEA生成初始代码

使用依赖：java web、spring、spring boot、mysql driver、lombok、mybatis、mybatis plus、junit

[MyBatis-Plus (baomidou.com)](https://baomidou.com/ "MyBatis-Plus (baomidou.com)")

[Maven Repository: Search/Browse/Explore (mvnrepository.com)](https://mvnrepository.com/ "Maven Repository: Search/Browse/Explore (mvnrepository.com)")

::: tip 此处报错与依赖版本有关，以后添加依赖先去mvn repository查一下！
报错时mybatis-plus-spring-boot3-starter用的是3.5.4版本，换成最多人用的3.5.5就正常运行了
![](/image/image_Oozv6Ek8gz.png)
:::

整理目录结构

```bash
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─baiye959
│  │  │          └─usercenter
│  │  │              ├─controller # 请求层/控制层：专门处理请求
│  │  │              ├─mapper # 数据访问层：专门从数据库中查询数据
│  │  │              ├─model # 定义数据表对应模型/用到的封装类
│  │  │              ├─service # 编写应用逻辑
│  │  │              └─utils # 放工具类
│  │  └─resources
│  └─test
│      └─java
│          └─com
│              └─baiye959
│                  └─usercenter # 放测试类
└─target

```

### 数据库表设计
```
id(主键) bigint
username 昵称 varchar
userAccount 登录账号
avatarUrl 头像 varchar
gender 性别 tinyint
userPassword 密码 varchar
phone 电话 varchar
email 邮箱 varchar
userStatus 用户状态 int 0-正常
createTime 创建时间（数据插入时间） datetime
updateTime 更新时间（数据更新时间） datetime
isDelete 是否删除01（逻辑删除） tinyint
userRole 用户角色 0-普通用户1-管理员
```

```sql
create table user
(
    id bigint auto_increment primary key comment 'id',
    username varchar(256) null comment '用户昵称',
    userAccount varchar(256) null comment '账号',
    avatarUrl varchar(1024) null comment '用户头像',
    gender tinyint null comment '性别',
    userPassword varchar(512) not null comment '密码',
    phone varchar(128) null comment '电话',
    email varchar(512) null comment '邮箱',
    userStatus int default 0 not null comment '状态 0 - 正常',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete tinyint default 0 not null comment '是否删除',
    userRole int default 0 null comment '用户角色 0-默认用户 1-管理员'
) comment '用户';
```

### 自动生成器的使用
MyBatisX插件，自动根据数据库生成：
- domain：实体对象
- mapper：操作数据库的对象
- mapper.xml：定义了mapper对象和数据库的关联，可以在里面自己写SQL
- service：包含常用的增删改查
- serviceImpl：具体实现service

GenerateAllSetter插件，自动生成所有get方法

## 开发功能
### 用户注册
#### 注册逻辑
1. 用户在前端输入账户和密码、校验密码
2. 校验用户输入的信息是否符合要求
    1. 非空
    2. 账户长度至少4位，不包含特殊字符，不能重复
    3. 密码长度至少8位
    4. 校验密码和密码相同
3. 对密码进行加密
4. 向数据库插入用户数据

- 引入依赖"Apache Commons Lang"，便于校验
- 特殊字符匹配，[java正则表达式处理特殊字符](http://t.csdnimg.cn/qz0Ot "java正则表达式处理特殊字符")
- 这里检查账户是否重复放到最后，省去不必要的查表
- Java常用正则表达式，[http://t.csdnimg.cn/3AM03](http://t.csdnimg.cn/3AM03 "http://t.csdnimg.cn/3AM03")

#### 单元测试
debug（F7进入函数/循环的下一步，F8下一步，F9下一个断点）
![](/image/image_M5ALcpJgKQ.png)

此处发现是正则表达式匹配的使用不正确
![](/image/image_xApVrsqdA6.png)

### 用户登录（单机登录）
#### 登录逻辑
- 接受参数：用户账户、密码
- 请求类型：POST
  > 请求参数很长时不建议用GET，会限制长度
- 请求体：JSON格式的数据（较轻量）
- 返回值：用户信息（**脱敏**）


1. 校验用户账户和密码是否合法
    1. 账户长度至少4位，不包含特殊字符
    2. 密码长度至少8位
2. 密码是否正确
3. 用户信息脱敏，隐藏敏感信息，防止数据库中的字段泄露
4. 记录用户的登录态（session），存到服务器上（用Spring Boot框架封装的服务器tomcat记录），cookie
5. 返回脱敏后的用户信息

#### 登录态管理
1. 连接服务器后，得到一个session1状态，返回给前端
2. 登录成功后，得到了登录成功的session，返回给前端一个设置cookie的命令

   session → cookie
3. 前端接收到命令，设置cookie，保存到浏览器中
4. 前端再次请求后端时（相同的域名），在请求头中带上cookie去请求
5. 后端拿到前端传来的cookie，找到对应的session
6. 后端从session中可以取出基于该session存储的变量（用户的登录信息、用户名）

#### 逻辑删除
[**逻辑删除 | MyBatis-Plus**](https://baomidou.com/pages/6b03c5 "逻辑删除 | MyBatis-Plus")

### 用户注销
删除登录态

### 接口开发
控制层Controller封装请求
> @RestController适用于编写 restful 风格的api，返回值默认为json类型

controller层倾向于对请求参数本身的校验，不涉及业务逻辑本身（越少越好）

service层是对业务逻辑的校验（有可能被controller之外的类调用）

#### 序列化
**设置序列化ID的原因：**

简单点理解，序列化ID就是对象的身份证号。对于一些对象被序列之后，类的成员发生了改变，反序列之后无法被识别为此类的实例。所以需要序列ID来校验对象，可以在反序列化之后正常的识别使用。

#### 测试
IDEA自带的，"Tools"-"HTTP Client"-"Create Request in HTTP Client"

![](/image/image_XB0yOYZj2f.png)
![](/image/image_HWPiiymzkU.png)
![](/image/image_EJfVWNUJu0.png)

#### 用户管理接口（鉴权）
在user表中加一个userRole（角色）字段
- 查询用户
    - 允许根据用户名查询
- 删除用户

#### 获取当前登录用户信息接口
1. 通过session获得当前登录用户id
2. 用id查询数据库，及时更新用户信息
3. 返回更新并脱敏后的用户信息

### 代理
- 正向代理：替客户端向服务器发送请求
  ![](/image/image_bZPzEM0Klu.png)
- 反向代理：替服务器接收请求。（有多台服务器分担压力，代理服务器转发请求）
  ![](/image/image_vL6eK6TfCf.png)


前端使用代理，带/api的路径会自动转到localhost:8080，后端接口要加个/api

`application.yml`指定接口全局api

```yaml
servlet:
  context-path: /api
```

## 后端代码优化
### 通用反馈对象
目的：给对象补充一些信息，告诉前端这个请求在业务层面上是成功还是失败

```javascript
{
    name: "baiye959"
}

↓

// 成功
{
    code: 0, // 错误码
    data: {
        name: "baiye959"
    },
    message: "success"
}

// 失败
{
    code: 50001, // 错误码
    data: null,
    message: "用户操作异常，xxx"
}

```

⾃定义统⼀的错误码
```java
  SUCCESS(0, "success", ""),
  // 400开头：用户的问题
  PARAMS_ERROR(40000, "请求参数错误", ""),
  NULL_ERROR(40001, "请求数据为空", ""),
  NO_LOGIN(40100, "未登录", ""),
  NO_AUTH(40101, "无权限", ""),
  // 500开头：系统的问题
  SYSTEM_ERROR(50000, "系统内部异常", "");

```

返回类支持返回正常和错误
```java
  public static <T> BaseResponse<T> success(T data) {
      return new BaseResponse<T>(0, data, "success");
  }
  public static BaseResponse error(ErrorCode errorCode) {
      return new BaseResponse<>(errorCode);
  }
```

```java
  private int code;
  private T data;
  private String message;
  private String description;

  public BaseResponse(int code, T data, String message, String description) {
      this.code = code;
      this.data = data;
      this.message = message;
      this.description = description;
  }
  public BaseResponse(ErrorCode errorCode) {
      this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
  }
```

### 全局异常处理器
#### 作用
1. 捕获代码中所有的异常，内部消化，让前端得到更详细的业务报错/信息
2. 同时屏蔽掉项目框架本身的异常（不暴露服务器内部状态）
3. 集中处理，比如记录日志

#### 实现
1. 定义业务异常类
    1. 相对于Java的异常类，支持更多字段
    2. 自定义构造函数，更灵活/快捷的设置字段
2. 编写全局异常处理器（利用Spring AOP，在调用方法前后进行额外的处理）

## 部署
### 多环境
#### 理论
[http://t.csdnimg.cn/bPWnF](http://t.csdnimg.cn/bPWnF "http://t.csdnimg.cn/bPWnF")

本地开发：localhost(127.0.0.1)

多环境：指同一套项目代码在不同的阶段需要根据实际情况来调整配置并且部署到不同的机器上。

为什么需要？
1. 每个环境互不影响
2. 区分不同的阶段：开发/测试/生产
3. 对项目进行优化：
    1. 本地日志级别
    2. 精简依赖，节省项目体积
    3. 项目的环境/参数可以调整，比如JVM参数

针对不同环境做不同的事情。

多环境分类：
1. 本地环境（自己的电脑）localhost
2. 开发环境（远程开发）大家连同一台机器，为了大家开发方便
3. 测试环境（测试）开发/测试/产品，单元测试/性能测试/功能测试/系统集成测试，独立的数据库、独立的服务器
4. 预发布环境（体验服）：和正式环境一致，正式数据库，更严谨，查出更多问题
5. 正式环境（线上，公开对外访问的项目）：尽量不要改动，保证上线前的代码是“完美”运行
6. 沙箱环境（实验环境）：为了做实验

#### 实现
SpringBoot项目，通过application.yml添加不同的后缀来区分配置文件

可以在启动项目时传入环境变量：
```bash
java -jar \user-center-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

需要修改：
- 依赖的环境地址
    - 数据库地址
    - 缓存地址
    - 消息队列地址
    - 项目端口号
- 服务器配置

### Docker部署
#### 理论
docker是容器，可以理解为软件安装包，可以将项目的环境（比如java、nginx）和项目的代码一起打包成镜像，所有人都能下载镜像，更容易分发和移植。再启动项目时，不需要敲一大堆命令，而是直接下载镜像、启动镜像就可以了。

Docker安装：https://www.docker.com/get-started/ 或者宝塔安装


Dockerfile用于指定构建Docker镜像的方法。<br/>
Dockerfile一般情况下不需要完全从0自己写，可以去github、gitee等托管平台参考同类项目（比如
springboot）<br/>
[官方文档](https://docs.docker.com/reference/dockerfile/)<br/>
[Docker Dockerfile | 菜鸟教程](https://www.runoob.com/docker/docker-dockerfile.html)

Dockerfile编写：
- `FROM`依赖的基础镜像（在[DockerHub官网](https://hub.docker.com/)上找）
- `NORKDIR`工作目录
- `COPY`从本机复制文件
- `RUN`执行命令
- `CMD/ENTRYPOINT`(附加额外参数)指定运行容器时默认执行的命令


#### 实现
根据Dockerfile构建镜像（[Docker build 命令 | 菜鸟教程](https://www.runoob.com/docker/docker-build-command.html)）：
```bash
# 后端
docker build -t user-center-backend:v0.0.1.
# 前端
docker build -t user-center-front:v0.0.1.
```

docker run启动：
```bash
#前端
docker run -p 80:80 -d user-center-frontend:V0.0.1
#后端
docker run -p 8080:8080 user-center-backend:v0.0.1
```

虚拟化
1. 端口映射：把本机的资源（实际访问地址）和容器内部的资源（应用启动端口）进行关联
2. 目录映射：把本机的端口和容器应用的端口进行关联

常用命令
```bash
# 进入容器
docker exec -i -t fee2bbb7c9ee /bin/bash

# 查看进程
docker ps

# 查看日志
docker logs -f [container-id]

# 杀死容器
docker kill

# 强制删除镜像
docker rmi -f
```

### 跨域问题解决（添加跨域头）
浏览器为了用户的安全，仅允许向同域名、同端口的服务器发送请求。

这里添加跨域头解决该问题，让服务器告诉浏览器：允许跨域（返回cross-origin-allow响应头）&#x20;

[Springboot解决跨域问题方案总结](http://t.csdnimg.cn/UdvZ4 "Springboot解决跨域问题方案总结")

#### 可用方法
1. Spring Boot 中解决跨域
    1. 通过注解跨域
    2. 通过配置文件跨域
    3. 通过 CorsFilter 跨域
    4. 通过 Response 跨域
    5. 通过 ResponseBodyAdvice 跨域
2. Nginx 中解决跨域
3. 网关中解决跨域
    1. 配置文件中设置跨域
    2. 添加 CorsWebFilter 来解决跨域问题

#### 本项目选用的方法：网关支持（Nginx）
```nginx
#跨域配置
location ^~ /api/{
    proxy_pass http://127.0.0.1:8080/api/;
    add_header 'Access-Control-Allow-origin' $http_origin;
    add_header 'Access-Control-Allow-Credentials' 'true';
    add_header Access-Control-Allow-Methods 'GET,POST,OPTIONS';
    add_header Access-Control-Allow-Headers '*';
    if ($request_method 'OPTIONS'){
        add_header 'Access-Control-Allow-Credentials' 'true';
        add_header 'Access-Control-Allow-Origin'$http_origin;
        add_header 'Access-Control-Allow-Methods' 'GET,POST,OPTIONS';
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-with,If-Modified-since,Cache-Control,Content-Type,Range';
        add_header 'Access-Control-Max-Age' 1728000;
        add_header 'Content-Type' 'text/plain;charset=utf-8';
        add_header 'Content-Length' 0;
        return 204;
    }
}
```
