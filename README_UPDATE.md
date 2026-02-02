# UHC 极限生存 - 项目更新

## 项目概述
UHC (Ultra Hardcore) 是一个在 Minecraft 中实现的类似 PUBG 的生存竞技游戏模式插件，广泛应用于 YourCraft 游戏服务器。

## 最近更新

### 配置系统改进
- 添加了更全面的配置选项
- 将硬编码值迁移到配置文件
- 支持更灵活的数据库配置
- 添加了游戏设置配置

### 数据库优化
- 改进了数据库初始化过程
- 添加了数据库表自动创建功能
- 改进了错误处理机制

### 性能优化
- 将硬编码的游戏参数迁移到配置文件
- 优化了数据库连接管理
- 提高了插件的可配置性

## 配置说明

### config.yml 配置选项
```yaml
# 大厅世界名称
lobbyWorldName: "lobby"

# 统计系统配置
statistics:
  enable: true
  sql:
    enable: true
    address: "127.0.0.1"
    port: 3306
    database: "minecraft"
    userName: "root"
    password: ""
    table: "UHC"

# 游戏设置
gameSettings:
  startPercentage: 90      # 开始游戏所需玩家比例 (%)
  maxPlayers: 99           # 最大玩家数量
  startCountdown: 300      # 开始倒计时 (秒)
  noPvPDuration: 600       # 无PVP阶段时长 (秒)
  borderShrinkDuration: 3600  # 边界收缩总时长 (秒)
  initialBorderSize: 2000  # 初始边界大小
  chunkPreloadCount: 64    # 预加载区块数量

# 职业系统 (未来功能)
classes:
  enabled: false
```

## 未来发展方向

### 短期目标
- [ ] 添加职业/技能系统
- [ ] 改进游戏平衡性
- [ ] 优化性能表现

### 长期目标
- [ ] 添加新的游戏模式
- [ ] 改进用户界面
- [ ] 增强社交功能

## 依赖
- Spigot API 1.8.8
- Vault (权限和经济系统支持)

## 安装
1. 将 UHC.jar 放入服务器的 plugins 文件夹
2. 启动服务器以生成配置文件
3. 根据需要编辑 config.yml
4. 重启服务器

## 故障排除
- 确保服务器运行 Minecraft 1.8.x 版本
- 确保已安装 Vault 插件
- 检查数据库连接配置