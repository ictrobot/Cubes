local events = {}

function events.starting(event)
    print(event)
    print(mod.side.getSide() == mod.side.client)
end

-- puts 3x3 square of glass under player's new position
function events.move(event)
    local pos = event.data.newPosition
    local block = pos:sub(0, 2.625, 0)
    local x, y, z = block:blockValues()

    local bedrock = cubes.blocks("core:glass")

    cubes.world().setBlocks(x - 1, y, z - 1, x + 1, y, z + 1, bedrock)
end

return events