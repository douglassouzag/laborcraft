package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.goals.CollectItemsGoal;
import net.glok.laborcraft.goals.DepositItemsInChestGoal;
import net.glok.laborcraft.goals.FarmGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FarmerNPCEntity extends WorkerNPCEntity {

  public FarmerNPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(
      entityType,
      world,
      new Identifier(Laborcraft.MOD_ID, "textures/entity/profession/farmer.png")
    );
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(3, new FarmGoal(this));
    this.goalSelector.add(
        3,
        new CollectItemsGoal(
          this,
          new Item[] {
            Items.PUMPKIN,
            Items.MELON_SLICE,
            Items.WHEAT_SEEDS,
            Items.WHEAT,
            Items.POISONOUS_POTATO,
            Items.POTATO,
            Items.CARROT,
            Items.BEETROOT_SEEDS,
            Items.COCOA_BEANS,
            Items.BEETROOT,
          }
        )
      );
    this.goalSelector.add(
        8,
        new DepositItemsInChestGoal(
          this,
          new Item[] {
            Items.PUMPKIN,
            Items.MELON_SLICE,
            Items.WHEAT_SEEDS,
            Items.WHEAT,
            Items.POISONOUS_POTATO,
            Items.POTATO,
            Items.CARROT,
            Items.BEETROOT_SEEDS,
            Items.COCOA_BEANS,
            Items.BEETROOT,
          }
        )
      );
  }
}
