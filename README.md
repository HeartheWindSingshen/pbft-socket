#### 简单实现pbft算法，应用无人机集群网络通信

建议切换到FlyApplication分支，main分支是之前手生疏简单实现的算法，里面存在不正确的地方。
在FlyApplication分支得到修复。
IDEA导入项目，直接运行coms.pbft.mainTest包下的所有main方法，com.pbft包下是以前的bug代码，所以不用管。

ControllerWeb分支是在FlyApplication分支项目的基础上使用了springboot增加了web服务，将指挥中心控制和展示放在前端。启动springboot指挥中心程序；同时也需使用NodeBashRunJar分支下的jar包来启动集群各个节点，直接点击bat命令即可执行jar包，注意bat命令jar包路径，修改成符合本机的地址。

1. 完成基本pbft算法共识过程   √
2. pbft存在坏节点和故障节点共识过程     √
3. 视图切换      √
4. 基于pbft共识的分布式无人机网络通信系统 (在FlyApplication分支里面) √
5. 测试4节点，7节点无人机集群PBFT达成共识的延迟，吞吐量，可靠性，可用性。  √
6. 指挥中心添加web前端服务，而不是限于IDEA命令行  √

4.18 初稿一版暂时完成，虽然实验做的仓促，但是总算暂时能休息了，累死我了！！！！  -----FlyApplication
5.3 完成指挥中心前端web                                                        -----ControllerWeb && NodeBashRunJar



