package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.goals.BreakBlocksGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class LumberjackEntity extends DefaultWorkerEntity {

  public LumberjackEntity(
    EntityType<? extends DefaultWorkerEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.name = "Lumberjack";
    this.goalSelector.add(
        1,
        new BreakBlocksGoal(
          this,
          new Block[] {
            Blocks.OAK_LOG,
            Blocks.SPRUCE_LOG,
            Blocks.BIRCH_LOG,
            Blocks.JUNGLE_LOG,
            Blocks.ACACIA_LOG,
            Blocks.DARK_OAK_LOG,
            Blocks.CHERRY_LOG,
            Blocks.MANGROVE_LOG,
            Blocks.OAK_LEAVES,
            Blocks.OAK_LEAVES,
            Blocks.SPRUCE_LEAVES,
            Blocks.BIRCH_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.DARK_OAK_LEAVES,
            Blocks.CHERRY_LEAVES,
            Blocks.MANGROVE_LEAVES,
          },
          new Item[] {
            Items.WOODEN_AXE,
            Items.STONE_AXE,
            Items.IRON_AXE,
            Items.GOLDEN_AXE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE,
          }
        )
      );
  }
}
