Migration
=========

__REQUIRES Spigot 1.8.8+ and Java 7+__

Bukkit plugin gamemode intended for UHC

Whilst Migration is running players are given an amount of time to reach a specific area. When this time is up the players
outside the area take damage over time for a short while and then a new area is picked randomly and then game loops.

# Configuration:

```yaml
seconds for warning: 600
seconds for damaging: 60
seconds for half heart: 10
damage sender: SUBTITLE+CHAT
phase change sender: SUBTITLE+CHAT
timer update sender: ACTIONBAR
notification seconds: [1,2,3,4,5,6,7,8,9,10,15,30,60,90,120,180,240,300,360,420,480,540,600,660,720,780,840,900]
areas:
  example area 1:
    x: "[0..+Inf)"
    z: "(-Inf..+Inf)"
    announce: Positive X coord
    weight: 0
  example area 2:
    x: "[0..64]"
    z: "[0..64]"
    announce: 0,0 -> 64,64
    weight: 0
messages:
  GET TO AREA: "&3Get to `&6%1$s&3` within &6%2$d &3seconds"
  DAMAGE NOTIFICATION: "&3You took &4%1$d&3HP damage for being outside of the area"
  DAMAGE PHASE START: "&3Now damaging players not within the area"
  DAMAGE TICK: "&3Damaging players not in `&6%1$s&3` for the next &6%2$d&3 seconds"
```

#### seconds for warning

This is the amount of seconds that the warning phase goes on for.

#### seconds of damaging

This is the amount of seconds players outside the chosen area should be damaged for

#### seconds for half heart

Every second outside of the chosen area the player will accumulate 1 point. This setting is how many of those points
will cause the player to take half a heart and reset the count

#### damage sender

Where to send the notification that the player took damage. Sends `messages.DAMAGE NOTIFICATION`.

Allowed senders:

- `TITLE` - sends a title to the player
- `SUBTITLE` - sends a subtitle to the player
- `CHAT` - sends a chat message to the player
- `ACTIONBAR` - sends an actionbar message to the player

Senders can be combined with a '+' character: `SUBTITLE+CHAT`, `ACTIONBAR+CHAT` e.t.c.

#### phase change sender

See `damage sender`. Sends `messages.DAMAGE PHASE START` or `messages.GET TO AREA` depending on phase switch

#### timer update sender

See `damage sender`. Sends `messages.GET TO AREA` or `messages.DAMAGE TICK` depending on phase

#### notification ticks

A list of seconds that notification updates should be sent on. Defaults try to reduce spam but update a lot when the timer
is about to run out

#### areas

A list of areas that are to be randomly chosen from

An area is formatted as follows:

```yaml
example area 1:
    x: "[0..+Inf)"
    z: "(-Inf..+Inf)"
    announce: Positive X coord
    weight: 0
```

`example area 1` - name of the area, not used but should be kept unique  
`x` - Range to define inside of the area on the x axis. This is allowing any positive X coordinate  
`z` - Range to define inside of the area on the z axis. This allows any z coordinate  
`announce` - the name of the region when announced in-game, used in `messages.GET TO AREA` and `messages.DAMAGE TICK`  
`weight` - optional (default 1). The weighting of this region in random selection, a weight of 0 will not be included. Heigher weights = higher probability (weight = 2 is twice as likely)  

#### messages

A list of messages that are sent in-game to players. Colour codes like `&c` are allowed. Positional parameters (e.g. `%1$s`) are used to denote a variables placement in the message.
