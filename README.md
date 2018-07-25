# spring-cloud-cm
主要实现基于容器化环境下springcloud融合存在的通讯问题与watch机制的建立。

# 适用场景
1、springCloud微服务容器化迁移场景，k8s集群内的微服务与集群外注册于eureka或zk的微服务并行。

2、传统服务容器化迁移场景，保持传统服务与已迁移至容器的依赖关系衔接。

3、部分k8s集群外未容器化的中间件组件与k8s服务通信连接。

4、其他服务注册与发现场景，如ZK、consful等。

# 术语定义
CM ：Center Manager 管理连接器

Studio：Management Studio管理控制器

Proxy：Service Proxy 服务代理

Agent：Manage Agent 管理代理

# 通信组件原理
1、通信互通

2、watch机制

3、心跳保持

# 优势
1、优势：对集群并行独立，最大程度保持新老环境独立运行，通过中间件衔接，降低迁移分险。

# 方案对比
第一种迁移方案:service注册，否定。

![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/one-Architecture.png)

第二种迁移方案：podIp注册，否定。

![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/two-Architecture.png)

第三种迁移方案：增加通信组件。

![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/three-Architecture.png)

# 通信组件 Architecture
![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/architecture.png)
![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/agent-base.png)
![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/cm-base.png)
![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/agent-uml.png)
![image](https://github.com/SpringCloud/spring-cloud-cm/blob/master/page-resources/img/app-uml.png)


