package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.goals.FindBedToSleepAtNightGoal;
import net.glok.laborcraft.helpers.PlayerHelper;
import net.glok.laborcraft.util.BoxScreenHandler;
import net.glok.laborcraft.util.ImplementedInventory;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public abstract class NPCEntity
  extends PathAwareEntity
  implements NamedScreenHandlerFactory, ImplementedInventory {

  //Helpers
  PlayerHelper playerHelper = new PlayerHelper();

  //Player Setup
  private BlockPos firstWorkPosition;
  private BlockPos secondWorkPosition;
  public Box workArea;
  public BlockPos bedPosition;
  public BlockPos chestPosition;
  public PlayerEntity owner;

  //Constants
  private final int handSwingDuration = 7;
  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(
    27,
    ItemStack.EMPTY
  );
  public Identifier texture;

  public static final Item followItem = Items.STICK;
  public static Item setupItem = Items.COMPASS;

  public NPCEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world,
    Identifier texture
  ) {
    super(entityType, world);
    this.texture = texture;
    MobNavigation mobNavigation = (MobNavigation) this.getNavigation();
    mobNavigation.setCanPathThroughDoors(true);
    mobNavigation.setCanEnterOpenDoors(true);
    mobNavigation.setCanSwim(true);

    this.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, 1f);
    this.setPathfindingPenalty(PathNodeType.DOOR_OPEN, 1f);
    this.setPathfindingPenalty(PathNodeType.DOOR_WOOD_CLOSED, 0.9f);
    this.setPathfindingPenalty(PathNodeType.DOOR_IRON_CLOSED, 0.9f);

    this.navigation = mobNavigation;
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(1, new LongDoorInteractGoal(this, true));
    this.goalSelector.add(1, new AttackGoal(this));
    this.goalSelector.add(2, new FindBedToSleepAtNightGoal(this));
    this.goalSelector.add(3, new WanderAroundGoal(this, 0.5f));
    this.goalSelector.add(
        4,
        new LookAtEntityGoal(this, PlayerEntity.class, 6.0F)
      );
    this.goalSelector.add(5, new LookAroundGoal(this));
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

  public void setPersistence() {
    if (!this.getWorld().isClient() && this.isAlive() && !this.isPersistent()) {
      this.setPersistent();
    }
  }

  public void setOwner(PlayerEntity player) {
    if (this.owner != null) {
      this.owner = null;
    } else {
      this.owner = player;
    }
  }

  public void followOwner() {
    if (this.owner != null) {
      this.getNavigation().startMovingTo(this.owner, 0.6f);
    }
  }

  public void sendMessageToPlayer(PlayerEntity player, String message) {
    player.sendMessage(Text.of("NPC : " + message));
  }

  public void setWorkPosition(BlockPos blockPos) {
    if (this.firstWorkPosition != null && this.secondWorkPosition != null) {
      this.firstWorkPosition = null;
      this.secondWorkPosition = null;

      if (this.workArea != null) {
        this.workArea = null;
        sendMessageToPlayer(
          this.owner,
          Text
            .translatable("message.laborcraft.npc_work_area_reseted")
            .getString()
        );
        return;
      }
    }

    if (this.firstWorkPosition != null) {
      this.secondWorkPosition = blockPos;
      this.workArea = new Box(this.firstWorkPosition, this.secondWorkPosition);
      sendMessageToPlayer(
        this.owner,
        Text
          .translatable("message.laborcraft.npc_work_area_second_pos_added")
          .getString()
      );
    } else {
      sendMessageToPlayer(
        this.owner,
        Text
          .translatable("message.laborcraft.npc_work_area_first_pos_added")
          .getString()
      );
      this.firstWorkPosition = blockPos;
    }
  }

  public void setBedPosition(BlockPos blockPos) {
    this.bedPosition = blockPos;
  }

  public void setChestPosition(BlockPos blockPos) {
    this.chestPosition = blockPos;
  }

  public void checkForOwnerCommands() {
    if (this.owner != null) {
      if (
        playerHelper.isPlayerHandSwinging(this.owner) &&
        this.owner.getStackInHand(Hand.MAIN_HAND).getItem() == setupItem
      ) {
        BlockPos blockPos = playerHelper.getLookingPosition(this.owner, 5f);
        Block block = this.getWorld().getBlockState(blockPos).getBlock();

        if (block == Blocks.CHEST) {
          sendMessageToPlayer(
            this.owner,
            Text
              .translatable("message.laborcraft.npc_chest_pos_added")
              .getString()
          );
          setChestPosition(blockPos);
        } else if (block instanceof BedBlock) {
          sendMessageToPlayer(
            this.owner,
            Text
              .translatable("message.laborcraft.npc_bed_pos_added")
              .getString()
          );
          setBedPosition(blockPos);
        } else {
          setWorkPosition(blockPos);
        }
      }
    }
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
  public void tick() {
    super.tick();
    setPersistence();

    //For some reason, this is needed to make the NPC's arm swing because the
    //handSwingTicks is always -1 on the super method
    tickHandSwing();

    followOwner();

    checkForOwnerCommands();

    this.setCustomNameVisible(true);
  }
}
