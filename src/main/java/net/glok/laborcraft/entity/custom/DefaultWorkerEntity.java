package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.util.ImplementedInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DefaultWorkerEntity
  extends PathAwareEntity
  implements ImplementedInventory {

  public String occupation;

  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(
    2,
    ItemStack.EMPTY
  );

  public DefaultWorkerEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.occupation = "Unemployed";
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(
        1,
        new LookAtEntityGoal(this, PlayerEntity.class, 4F)
      );
    this.goalSelector.add(2, new LookAroundGoal(this));
    this.goalSelector.add(3, new WanderAroundGoal(this, 0.5f));
  }

  public static DefaultAttributeContainer.Builder createDefaultWorkerAttributes() {
    return MobEntity
      .createMobAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2);
  }

  @Override
  public void tick() {
    if (
      this.getCustomName() == null ||
      this.getCustomName().getString() != this.occupation
    ) {
      this.setCustomName(Text.of(this.occupation));
      this.setCustomNameVisible(true);
    }
    super.tick();
  }

  //Inventory related methods

  @Override
  public DefaultedList<ItemStack> getItems() {
    return inventory;
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    Inventories.readNbt(nbt, inventory);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    Inventories.writeNbt(nbt, inventory);
    return super.writeNbt(nbt);
  }

  @Nullable
  @Override
  public ActionResult interactMob(PlayerEntity player, Hand hand) {
    if (player.getWorld().isClient()) return ActionResult.SUCCESS;

    Inventory mobEntity = (Inventory) this;

    if (!player.getStackInHand(hand).isEmpty()) {
      if (mobEntity.getStack(0).isEmpty()) {
        mobEntity.setStack(0, player.getStackInHand(hand).copy());
        player.getStackInHand(hand).setCount(0);
        this.activeItemStack = player.getStackInHand(hand).copy();
      } else if (mobEntity.getStack(1).isEmpty()) {
        mobEntity.setStack(1, player.getStackInHand(hand).copy());
        player.getStackInHand(hand).setCount(0);
      } else {
        System.out.println(
          "The first slot holds " +
          mobEntity.getStack(0) +
          " and the second slot holds " +
          mobEntity.getStack(1)
        );
      }
    } else {
      if (!mobEntity.getStack(1).isEmpty()) {
        player.getInventory().offerOrDrop(mobEntity.getStack(1));
        mobEntity.removeStack(1);
      } else if (!mobEntity.getStack(0).isEmpty()) {
        player.getInventory().offerOrDrop(mobEntity.getStack(0));
        mobEntity.removeStack(0);
      }
    }
    return ActionResult.SUCCESS;
  }

  @Override
  protected void onKilledBy(LivingEntity adversary) {
    for (int i = 0; i < this.inventory.size(); i++) {
      if (!this.inventory.get(i).isEmpty()) {
        ItemScatterer.spawn(
          getEntityWorld(),
          this.getX(),
          this.getY(),
          this.getZ(),
          this.inventory.get(i)
        );
      }
    }

    super.onKilledBy(adversary);
  }
}
