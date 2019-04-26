# 背景

在开发过程中，经常需要用到开关，对一个功能做到动态启停，传统的做法是在Diamond上配置一个dataid和groupid，在代码中注册订阅，接收diamond的推送后，在listener中转化文本到对象，再修改目标属性的值，这一套流程还是有一定开发和测试成本的，如何透明化这个过程，正是本文所阐述的。

# 工具介绍
## 用法有三种如下
1. 监控关键字触发方法  
 key1 or key2 or ... 触发 method1  
 key1 or key3 or ... 触发 method2  
 比如在一次变更中key1、key2，key3都发生变化，但method1、method2都仅触发执行一次（内部做了标记，在一次diamond变更过程中，不重复触发不同的method）

2. Annotation自动更新Field
例如下面使用，可以在开关变更时，直接更改field属性的值
```
@AdSwitch
private volatile String sss;
```
3. 最基本的触发机制
如下代码，前面config是变量全集，diffConfig是本次变更的变量

```
   public void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        System.out.println("config:"+config);
        System.out.println("diff-config:"+diffConfig);
    }
```    

下面仔细讲一下第二种用法，如下面代码，在类中需要动态生效的属性上加一个annotation，然后在配置文件中，配置一下property即可，整个过程不需要开发一行代码，还能在diamond发生变更时，通过钉钉机器人通知到群组。

```
public class MergeAndEmitPipeline extends AbstractPipeline {
    private final static Logger LOG = Logger.getLogger(MergeAndEmitPipeline.class);
    private MsgDOMergeContainer msgDOMergeContainer;
    private Thread submitTaskTimer;

    //***************************需要配置的参数,Diamond动态生效********
    @AdStlSwitchDesc
    private volatile long msForMerging = 50;
    @AdStlSwitchDesc
    private volatile boolean mergeEnable = false;
    //***END*********************需要配置的参数***********************
```
```
<bean class="com.nickcao.MergeAndEmitPipeline">
    <!--开关的设置,yaml配置文件默认值-->
    <property name="msForMerging" value = "${cpc.pv.MergeAndEmitPipeline.msForMerging}"/>
    <property name="mergeEnable" value = "${cpc.pv.MergeAndEmitPipeline.mergeEnable}"/>
    <!--开关的设置,Diamond生效-->
    <property name="fieldName2ConfigKeyMap">
        <map>
            <entry key="msForMerging" value="cpc.pv.MergeAndEmitPipeline.msForMerging"/>
            <entry key="mergeEnable" value="cpc.pv.MergeAndEmitPipeline.mergeEnable"/>
        </map>
    </property>
</bean>
```

# 4种使用方式的demo
4.1. annotation使用方式
```
public class AnnotationSwitchDemo {

    public static final String DATA_ID = "caoning.dataid";
    public static final String GROUP_ID = "caoning.groupid";

    static class MyClass extends AnnotationSwitchFieldSetter4KvConfig {
        @AdStlSwitch(configKey = "haha")
        private int value = 111;

        @Override
        public String toString() {
            return "{\"MyClass\":"
                + super.toString()
                + ", \"value\":\"" + value + "\""
                + "}";
        }
    }

    public static void main(String[] args) {
        MyClass myClass =  new MyClass();
        DiamondDynamicKvKeyListener listener = new DiamondDynamicKvKeyListener(DATA_ID,GROUP_ID);


        Set<String> listenerKeySet1 = new HashSet<>();
        listenerKeySet1.add("haha");

        listener.setDingdingURL("http://创建一个机器人回调url");
        listener.rigisterKeys2Callback(listenerKeySet1,myClass);
        listener.rigister2Diamond();

        new Thread(){
            public void run(){
                while(true) {
                    System.out.println(myClass);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
```
4.2. key触发method的使用方式
```
public class DynamicKVKeyDemo {
    public static final String DATA_ID = "key-callback";
    public static final String GROUP_ID = "caoning.groupid";



    public static void main(String[] args) {
        //DynamicKeyDemo demo = new DynamicKeyDemo();
        DiamondDynamicKvKeyListener listener = new DiamondDynamicKvKeyListener(DATA_ID,GROUP_ID) ;


        Set<String> listenerKeySet1 = new HashSet<>();
        listenerKeySet1.add("key1");
        listenerKeySet1.add("key2");
        listenerKeySet1.add("key3");

        listener.rigisterKeys2Callback(listenerKeySet1,
                (config, diffConfig) -> System.out.println("callback1----diffConfig:"+diffConfig));

        Set<String> listenerKeySet2 = new HashSet<>();
        listenerKeySet2.add("key2");
        listenerKeySet2.add("key3");
        listenerKeySet2.add("key4");

        listener.rigisterKeys2Callback(listenerKeySet2,
                (config, diffConfig) -> System.out.println("callback2------diffConfig:"+diffConfig));


        listener.rigister2Diamond();


        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

```
4.3. 最原始的获取变更key的使用方式
```
public class DynamicKVConfigDemo implements IDynamicKvConfig {
    public static final String DATA_ID = "caoning.dataid";
    public static final String GROUP_ID = "caoning.groupid";


    @Override
    public void reload(Map<String, String> config, Map<String, DiffValue> diffConfig) {
        System.out.println("config:"+config);
        System.out.println("diff-config:"+diffConfig);
    }


    public static void main(String[] args) {
        DynamicKVConfigDemo demo = new DynamicKVConfigDemo();
        DiamondDynamicKvConfigListener listener = new DiamondDynamicKvConfigListener(DATA_ID,GROUP_ID);
        listener.rigisterIDynamicKvConfig(demo);
        listener.rigister2Diamond();
        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
```
4.4. 静态拉取配置的方式
```
public class StaticKVDemo {
    static final String DATA_ID = "caoning.dataid";
    static final String GROUP_ID = "caoning.groupid";

    public static void main(String[] args) {
        DiamondKvStaticReader demo = new DiamondKvStaticReader(DATA_ID,GROUP_ID);
        Map<String, String> result = demo.load();
        System.out.println(result);
    }
}
```



# 与阿里集团中间件Switch的对比
1. Switch与AppName绑定，满足大多数场景但分组部署方案不太方面
有的项目可能存在一个AppName主备部署、HSF分组部署等，基于Switch不太好做，本方案可以和HSF分组配置、或者主备集群配置、放入相应Diamond的dataid配置即可满足不同分组使用不同开关的方案
以我们项目举例，主集群的dataid是engine-0，备用集群的dataid是engine-1，主备开关互不影响
2. 本文方案可以一次性装载所有开关，开关可当配置使用
3. 本文方案可以获取变更前的old value，也可以基于Diamond拿到一定时间内的变更记录
