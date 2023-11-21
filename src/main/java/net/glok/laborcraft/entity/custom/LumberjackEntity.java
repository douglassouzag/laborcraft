package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.goals.BreakBlocksGoal;
import net.glok.laborcraft.goals.CollectItemsGoal;
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
          },
          new Item[] {
            Items.WOODEN_AXE,
            Items.STONE_AXE,
            Items.IRON_AXE,
            Items.GOLDEN_AXE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE,
          },
          true
        )
      );
    this.goalSelector.add(
        3,
        new CollectItemsGoal(
          this,
          new Item[] {
            Items.STICK,
            Items.APPLE,
            Items.OAK_LOG,
            Items.SPRUCE_LOG,
            Items.BIRCH_LOG,
            Items.JUNGLE_LOG,
            Items.ACACIA_LOG,
            Items.DARK_OAK_LOG,
            Items.CHERRY_LOG,
            Items.MANGROVE_LOG,
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.CHERRY_SAPLING,
          }
        )
      );
  }
}
