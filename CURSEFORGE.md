# ExtremeEvasion

ExtremeEvasion adds a timing-based evasive combat system to Minecraft. Roll away at the right moment, bait an enemy attack, and turn that perfect dodge into a short burst of counterattack power.

## Features

- Extreme Evasion: trigger a perfect dodge when a hostile melee attack lands during your roll window.
- Extreme Counter: after a successful evasion, your next empowered attacks can become guaranteed critical hits and ignore armor.
- Bullet Time: in singleplayer, successful evasion can slow the world for a few seconds.
- Roll compatibility: works with the vanilla-style `roll` player tag and supported roll/action mods.
- Evasion echo: leaves a short-lived afterimage during the evasion window.
- Visual feedback: blue-gray bullet-time screen filter and golden weapon glow for Extreme Counter.
- Configurable behavior: tune timing windows, counterattack charges, bullet-time speed, invulnerability, shaders, and trigger sound.

## How It Works

Start a roll just before a monster attack connects. ExtremeEvasion opens a short detection window when the roll begins. If a valid mob melee attack happens during that window, the attack is canceled and Extreme Evasion triggers.

After a successful evasion, you gain Extreme Counter for a short time. By default, the next attack is empowered with a guaranteed critical hit and armor piercing. The attack cooldown is also completed instantly so the counter can be used immediately.

In singleplayer, successful evasion can also trigger Bullet Time. During Bullet Time, the world runs slower, a blue-gray focus filter appears, and the player can be made temporarily immune to attacks.

## Supported Versions

- Minecraft: 1.21.1
- Loader: NeoForge

## Roll Compatibility

ExtremeEvasion detects rolling through the shared `roll` player tag and optional integrations.

Currently supported integrations:

- Moves Like Mafuyu, through the `roll` tag
- Combat Roll
- Epic Fight dodge skills
- ParCool roll and dodge actions

You need at least one supported roll source for the main mechanic to work.

## Configuration

The config file is generated after the game starts:

```text
config/extremeevasion-common.toml
```

Important options include:

```toml
minimumExtremeEvasionWindowTicks = 15
maximumExtremeEvasionWindowTicks = 20

extremeCounterAttackWindowTicks = 30
extremeCounterAttackCharges = 1
enableExtremeCounterAttackCritical = true
enableExtremeCounterAttackArmorPiercing = true
enableExtremeCounterGoldShader = true

enableBulletTime = true
enableBulletTimeInvulnerability = true
bulletTimeSpeed = 0.3
bulletTimeDurationMillis = 3000
bulletTimeVisualFadeMillis = 250
enableBulletTimeScreenShader = true
enableBulletTimeTriggerSound = true
```

Bullet Time only triggers in singleplayer worlds that are not opened to LAN. This is intentional, because the effect changes world time flow.

## Notes

- The same roll can only trigger Extreme Evasion once.
- Bullet Time is singleplayer-only.
- The blue-gray screen shader and golden counterattack shader can be disabled separately.
- Extreme Counter consumes attack attempts, not damage ticks.
- If the maximum evasion window is set below the minimum window, the mod clamps it so the window remains valid.

## Recommended Playstyle

This mod is built around aggressive timing. Roll too early and the window expires. Roll too late and the hit may land before the evasion window opens. The intended rhythm is to leave the enemy's melee range during its attack commitment, trigger Extreme Evasion, then immediately punish with Extreme Counter.
