events = require("SampleLua|events")

-- vector demo
local a = vector(10.4, 20.5, 30.6)
local b = vector(1, 2, 3)

print(a)
print(a.blockX, a.blockY, a.blockZ)
print(a.areaX, a.areaY, a.areaZ)

a.areaX = 10;
print(a)
print(a.blockX, a.blockY, a.blockZ)
print(a.areaX, a.areaY, a.areaZ)

a.y = 160.48;
print(a)
print(a.blockX, a.blockY, a.blockZ)
print(a.areaX, a.areaY, a.areaZ)

print(a + b)
print(a - b)
print(a * 10)
print(a:len())
print(a:length())
print(a:nor())
print(a:normalize())

local c = vector(0.4, 0.5, 0.6)
print(c)
print(c:isBlock())
print(c:round())
print(c:round():isBlock())
print(c:block())
print(c:block():isBlock())
print(#c)
print(c:len())
print(c:add(vector(1, 2, 3)))
print(c:add(1, 2, 3))
print(c:add(vector(05, 9, 123)))
print(c:add(05, 9, 123))
print(c:add(vector(05, 9, 123)) == c)
print(c:add(05, 9, 123) == c)

local dotA = vector(1, 2, 3)
local dotB = vector(-1, 2, 5)
print(dotA:dot(dotB))

local crossA = vector(1, 2, 3)
local crossB = vector(2, 1, 2)
print(crossA:cross(crossB))

local x, y, z = crossA:values()
print(crossA)
print(x .. " " .. y .. " " .. z)

-- print statements
print("Hello World from Lua")
-- cubes information
print(cubes.isDedicatedServer())
print(cubes.getApplicationType())
print(cubes.getVersion())
print(cubes.getBuild())
-- add mod event handlers
mod.modEvent(mod.state.startingClient, events.starting)
mod.modEvent(mod.state.startingServer, events.starting)
-- add event handlers to eventbus (events is required at the top)
mod.eventBus("PlayerMovementEvent", events.move, mod.side.server)