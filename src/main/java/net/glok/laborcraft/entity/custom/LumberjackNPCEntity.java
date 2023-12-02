package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.goals.CollectItemsGoal;
import net.glok.laborcraft.goals.DepositItemsInChestGoal;
import net.glok.laborcraft.goals.LumberGoal;
import net.glok.laborcraft.goals.ManageToolsGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LumberjackNPCEntity extends WorkerNPCEntity {

  public LumberjackNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(
        Laborcraft.MOD_ID,
        "textures/entity/profession/lumberjack.png"
      )
    );
  }

  @Override
  protected void initGoals() {
    super.initGoals();

    this.goalSelector.add(3, new LumberGoal(this));

    this.goalSelector.add(
        0,
        new ManageToolsGoal(
          this,
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
            Items.OAK_LOG,
            Items.SPRUCE_LOG,
            Items.BIRCH_LOG,
            Items.JUNGLE_LOG,
            Items.ACACIA_LOG,
            Items.DARK_OAK_LOG,
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.APPLE,
            Items.STICK,
            Items.CHERRY_LOG,
            Items.CHERRY_SAPLING,
            Items.CRIMSON_STEM,
            Items.WARPED_STEM,
            Items.CRIMSON_STEM,
            Items.WARPED_STEM,
            Items.CRIMSON_FUNGUS,
            Items.WARPED_FUNGUS,
          }
        )
      );
    this.goalSelector.add(
        8,
        new DepositItemsInChestGoal(
          this,
          new Item[] {
            Items.OAK_LOG,
            Items.SPRUCE_LOG,
            Items.BIRCH_LOG,
            Items.JUNGLE_LOG,
            Items.ACACIA_LOG,
            Items.DARK_OAK_LOG,
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.APPLE,
            Items.STICK,
            Items.CHERRY_LOG,
            Items.CHERRY_SAPLING,
            Items.CRIMSON_STEM,
            Items.WARPED_STEM,
            Items.CRIMSON_STEM,
            Items.WARPED_STEM,
            Items.CRIMSON_FUNGUS,
            Items.WARPED_FUNGUS,
          }
        )
      );
  }
}
