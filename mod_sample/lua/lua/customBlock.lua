block = {}

function block:getLightLevel(this, meta, super)
    return 15
end

function block:renderType(this, meta)
    local renderType = import('ethanjones.cubes.block.BlockRenderType')
    return renderType.CROSS
end

return block
