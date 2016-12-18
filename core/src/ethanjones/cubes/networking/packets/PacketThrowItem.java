package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.ItemEntity;
import ethanjones.cubes.entity.living.player.PlayerInventory;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_SERVER)
public class PacketThrowItem extends Packet {
  
  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    
  }
  
  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    
  }
  
  @Override
  public void handlePacket() {
    ClientIdentifier client = Cubes.getServer().getClient(getSocketMonitor());
    PlayerInventory inventory = client.getPlayer().getInventory();
    ItemStack itemStack = InventoryHelper.reduceCount(inventory, inventory.hotbarSelected);
    if (itemStack != null) {
      ItemEntity itemEntity = new ItemEntity();
      itemEntity.itemStack = itemStack;
      itemEntity.position.set(client.getPlayer().position);
      itemEntity.motion.set(client.getPlayer().angle);
      itemEntity.age = -3000 / Cubes.tickMS;
      Cubes.getServer().world.addEntity(itemEntity);
    }
  }
}
