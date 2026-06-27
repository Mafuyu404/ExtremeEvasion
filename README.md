# ExtremeEvasion 使用说明

ExtremeEvasion 是一个围绕“翻滚闪避”的战斗增强模组。玩家在翻滚期间成功躲过生物攻击时，会触发极限闪避，并获得短时间的极限反击机会。

## 安装需求

- Minecraft: 1.20.1
- Forge: 47.x
- 前置模组：Moves Like Mafuyu

把本模组 jar 放入 `mods` 文件夹即可。

## 核心机制

### 极限闪避

在玩家处于翻滚状态时，如果生物的近战攻击或弹射物攻击命中极限闪避判定，就会触发极限闪避。

触发成功后：

- 本次受到的攻击会被取消。
- 本次翻滚不会再次重复触发极限闪避。
- 玩家获得“极限反击”。
- 单人游玩时可以进入子弹时间。

### 极限反击

极限闪避成功后，玩家会获得一定次数的极限反击。

默认情况下，极限反击会让接下来的攻击：

- 必定暴击
- 无视护甲
- 立刻视为满攻击冷却
- 手持武器出现金色高亮和外圈光晕

极限反击会在成功造成伤害后消耗次数。次数用完或持续时间结束后，效果消失。

### 子弹时间

子弹时间只会在单人游玩中触发。

不会触发子弹时间的情况：

- 多人服务器
- 专用服务器
- 单人世界开放到局域网后

子弹时间期间会出现蓝灰色屏幕滤镜、边缘模糊和触发音效。

## 配置文件

配置文件由 Forge 自动生成，通常位于：

```text
config/extremeevasion-common.toml
```

主要配置项：

```toml
extremeCounterAttackWindowTicks = 30
```

极限反击可用的持续 tick 数。20 tick 约等于 1 秒。

```toml
extremeCounterAttackCharges = 1
```

每次极限闪避成功后，极限反击可强化的攻击次数。

```toml
enableExtremeCounterAttackCritical = true
```

极限反击是否必定暴击。

```toml
enableExtremeCounterAttackArmorPiercing = true
```

极限反击是否无视护甲。

```toml
enableExtremeCounterGoldShader = true
```

是否显示极限反击期间的手持武器金色高亮。

```toml
enableBulletTime = true
bulletTimeSpeed = 0.3
bulletTimeDurationMillis = 3000
```

控制单人游玩中的子弹时间。`bulletTimeSpeed = 0.3` 表示时间流速为 30%。

```toml
bulletTimeVisualFadeMillis = 250
enableBulletTimeScreenShader = true
```

控制子弹时间屏幕滤镜和淡入淡出。

```toml
enableBulletTimeTriggerSound = true
bulletTimeTriggerSoundVolume = 0.65
bulletTimeTriggerSoundPitch = 1.0
```

控制子弹时间触发音效。

## 常见问题

### 为什么多人游戏没有子弹时间？

子弹时间会改变全局时间流速。为了避免影响多人服务器和其他玩家，本模组只允许单人游玩触发子弹时间。

### 为什么触发了极限闪避但没有再次触发？

同一次翻滚内，极限闪避只能成功触发一次。触发后残影和 AI 诱导判定会立刻清除，防止重复触发。

### 为什么武器有金色高亮？

金色高亮表示你当前拥有极限反击。造成伤害并消耗完极限反击次数后，高亮会淡出。

### 可以关闭视觉效果吗？

可以。关闭以下配置项即可：

```toml
enableExtremeCounterGoldShader = false
enableBulletTimeScreenShader = false
```

### 可以只保留极限反击，不要子弹时间吗？

可以：

```toml
enableBulletTime = false
```

极限闪避和极限反击仍会正常工作。
