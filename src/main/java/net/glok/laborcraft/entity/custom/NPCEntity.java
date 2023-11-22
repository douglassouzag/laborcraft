package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.util.BoxScreenHandler;
import net.glok.laborcraft.util.ImplementedInventory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class NPCEntity
  extends PathAwareEntity
  implements NamedScreenHandlerFactory, ImplementedInventory {

  private final int handSwingDuration = 7;
  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(
    27,
    ItemStack.EMPTY
  );

  public NPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
  }

  public static DefaultAttributeContainer.Builder createDefaultAttributes() {
    return MobEntity
      .createMobAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5f);
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

  @Override
  public DefaultedList<ItemStack> getItems() {
    return inventory;
  }

  @Override
  public ScreenHandler createMenu(
    int syncId,
    PlayerInventory playerInventory,
    PlayerEntity player
  ) {
    return new BoxScreenHandler(syncId, playerInventory, this);
  }

  public void openNPCInventory(PlayerEntity player) {
    if (!player.getWorld().isClient()) {
      SimpleNamedScreenHandlerFactory screenHandlerFactory = new SimpleNamedScreenHandlerFactory(
        (syncId, inventory, playerx) ->
          new BoxScreenHandler(syncId, inventory, this),
        this.getDisplayName()
      );

      if (screenHandlerFactory != null) {
        player.openHandledScreen(screenHandlerFactory);
      }
    }
  }

  @Nullable
  @Override
  public ActionResult interactMob(PlayerEntity player, Hand hand) {
    openNPCInventory(player);

    return ActionResult.SUCCESS;
  }

  private int getHandSwingDuration() {
    return handSwingDuration;
  }

  @Override
  protected void tickHandSwing() {
    int i = this.getHandSwingDuration();
    if (this.handSwinging) {
      ++this.handSwingTicks;
      if (this.handSwingTicks >= i) {
        this.handSwingTicks = 0;
        this.handSwinging = false;
      }
    } else {
      this.handSwingTicks = 0;
    }

    this.handSwingProgress = (float) this.handSwingTicks / (float) i;
  }

  @Override
  public void tick() {
    super.tick();

    //Setting persistence
    if (!this.getWorld().isClient() && this.isAlive() && !this.isPersistent()) {
      this.setPersistent();
    }

    //For some reason, this is needed to make the NPC's arm swing because the
    //handSwingTicks is always -1 on the super method
    tickHandSwing();

    this.setCustomNameVisible(true);
  }
}
