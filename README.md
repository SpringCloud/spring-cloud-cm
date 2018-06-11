# spring-cloud-cm
主要实现基于容器化环境下springcloud融合存在的通讯问题与watch机制的建立。

目前已有基础方案和初始java版本，整理后提交issue，需要参与评估讨论完善，确定最终方案后拟定开发计划。

# 适用场景
1、springCloud微服务容器化迁移场景，k8s集群内的微服务与集群外注册于eureka或zk的微服务并行。

2、部分k8s集群外未容器化的中间件组件。

3、其他服务注册与发现场景。

# 术语定义
CM ：Center Manager 管理连接器

Studio：Management Studio管理控制器

Proxy：Service Proxy 服务代理

Agent：Manage Agent 管理代理

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

# 通信组件原理
1、通信互通

2、watch机制

3、心跳保持

# 优势与不足
1、优势：对集群并行独立，最大程度保持新老环境独立运行，通过中间件衔接，降低迁移分险。
2、不足：对业务有简单侵入，无法做到服务网格的零依赖。
