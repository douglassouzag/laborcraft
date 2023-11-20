package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.util.BoxScreenHandler;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DefaultWorkerEntity
  extends PathAwareEntity
  implements NamedScreenHandlerFactory, ImplementedInventory {

  public String name;
  public ChunkPos workChunk = new ChunkPos(0, 0);
  private boolean isSettingUpWorkArea = true;
  public PlayerEntity boss;
  private static final String WORK_CHUNK_KEY = "WorkChunk";
  private static final String NAME_KEY = "Name";

  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(
    27,
    ItemStack.EMPTY
  );

  public DefaultWorkerEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.name = "Unemployed";
  }

  public void followPlayer(PlayerEntity player) {
    this.getNavigation()
      .startMovingTo(player.getX(), player.getY(), player.getZ(), 0.5f);
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
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5f);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.boss != null) {
      this.followPlayer(this.boss);
    }

    this.setCustomName(Text.of(this.name + " " + this.workChunk.toString()));

    this.setCustomNameVisible(true);
  }

  @Override
  public DefaultedList<ItemStack> getItems() {
    return inventory;
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    if (nbt.contains(WORK_CHUNK_KEY)) {
      String chunkString = nbt.getString(WORK_CHUNK_KEY);
      chunkString = chunkString.substring(1, chunkString.length() - 1);

      int x = Integer.parseInt(chunkString.split(",")[0].strip());
      int z = Integer.parseInt(chunkString.split(",")[1].strip());

      this.workChunk = new ChunkPos(x, z);
    }
    if (nbt.contains(NAME_KEY)) {
      this.name = nbt.getString(NAME_KEY);
    }
    Inventories.readNbt(nbt, inventory);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    Inventories.writeNbt(nbt, inventory);

    nbt.putString(WORK_CHUNK_KEY, this.workChunk.toString());
    nbt.putString(NAME_KEY, this.name);

    return super.writeNbt(nbt);
  }

  @Nullable
  @Override
  public ActionResult interactMob(PlayerEntity player, Hand hand) {
    if (!player.getWorld().isClient()) {
      if (
        player.getStackInHand(hand).getItem() == net.minecraft.item.Items.STICK
      ) {
        if (isSettingUpWorkArea) {
          player.sendMessage(Text.of("Alright, take me to my work chunk"));
          this.boss = player;
        } else {
          player.sendMessage(Text.of("Ok, i will work in this chunk"));

          this.workChunk = this.getChunkPos();
          this.boss = null;
        }
        isSettingUpWorkArea = !isSettingUpWorkArea;
      } else {
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

  @Override
  public ScreenHandler createMenu(
    int syncId,
    PlayerInventory playerInventory,
    PlayerEntity player
  ) {
    return new BoxScreenHandler(syncId, playerInventory, this);
  }
}
