# UHC 极限生存

An UHC game plugin wrote by JHXSMatthew and widly used in YourCraft mini game server. 
It is a PUBG like mini-game in Mimecraft

## Dependencies （依赖)
For the latest version (master branch), all the old YourCraft dependencies are removed.
Only Vault is required.

在最新的版本里，已经去除了原YourCraft框架内的依赖，并加入了config文件。

- [Vault] (https://dev.bukkit.org/projects/vault)


## Sql Schema (数据表结构)
The Sql Schema is lost. But it's not complicated since I write different plugins without strong sql coupling.
The Schema should look like this.

数据表结构如下，开启插件会自动创建数据表。 请在配置文件内打开。

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

SQL Statement

    CREATE TABLE UHC (
      id Integer PRIMARY KEY AUTO_INCREMENT ,
      Name Varchar(16),
      Games Integer,
      Wins Integer,
      Kills Integer,
      Deaths Integer,
      Stacks Integer,
      Points Integer
    );
    CREATE INDEX Index_UHC_Name ON UHC (Name);

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

