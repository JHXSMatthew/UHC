# UHC 极限生存

An UHC game plugin written by JHXSMatthew and widly used in YourCraft mini game server. 
Think it as a PUBG but Minecraft style.


## Dependencies （依赖)
All dependencies are not compulsory. They are for the player control or unified game event for cross-plugins communications.
Dependencies can be removed by removing all relevant codes related to the dependencies.

所有依赖均不是必须，可以通过移除插件内的相关代码去除依赖。
很多是为了维持之前服务器架构使用的一些负责插件间通讯的插件。

- [GameEvent] (https://github.com/JHXSMatthew/GameEvent) 通用游戏事件
- [GameServerStatusSync] (https://github.com/JHXSMatthew/GameServerStatusSync) 控制玩家加入
- [BossBarSender] (https://github.com/JHXSMatthew/BossBarSender) 发送无龙BossBar

## Sql Schema (数据表结构)
The Sql Schema is lost. But it's not complicated since I write different plugins without strong sql coupling.
The Schema should look like this.

数据表结构已经比较久远已经找不到了。
凭着记忆和代码推了一下，应该是这样的：

Format: Column::Type

格式: 列名::类型

- id::long?or int
- Name::varchar(16)
- Games:: int
- Wins::int
- Kills::int
- Deaths::int
- Stacks::int
- Points::int

(id,Name,Games,Wins,Kills,Deaths,Stacks,Points)

# 其他项目
其他项目可以在 www.mcndsj.com/projects 找到。 如您有所需的未开源，请通过任意相关项目的GitHub issue联系我，会将对应项目开源。

# NOTICE

It's really been a while since this project is done.
The code in this project may use untraceable third-party open source codes and may violate the license. 
Please contact me through email 68638023@qq.com or submit an issue in GitHub repo and I will remove any illegal code from this project.
I owe you my apologies.

# 注意

项目内可能含有无法找寻来源的第三方开源代码，如使用有违反开源协议，请您直接通过邮件 68638023@qq.com 或 GitHub 的 issue 联系我。 我将第一时间将违规代码
从项目中删除，深表歉意！

