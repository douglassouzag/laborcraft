package net.glok.laborcraft.entity.custom;

import net.glok.laborcraft.Laborcraft;
import net.glok.laborcraft.identity.Enums.Gender;
import net.glok.laborcraft.identity.Names;
import net.glok.laborcraft.util.BoxScreenHandler;
import net.glok.laborcraft.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
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
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DefaultWorkerEntity
  extends PathAwareEntity
  implements NamedScreenHandlerFactory, ImplementedInventory {

  public String name;
  public String lastName;
  public ChunkPos workChunk = new ChunkPos(0, 0);
  public boolean isSettingUpWorkArea = true;
  public PlayerEntity boss;
  public boolean isCollectingItems = false;
  public boolean isBreakingBlocks = false;
  public Identifier skin;

  public BlockPos workChest;
  private static final String WORK_CHUNK_KEY = "WorkChunk";
  private static final String NAME_KEY = "Name";
  private static final String LAST_NAME_KEY = "LastName";

  public Names names = new Names();
  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(
    27,
    ItemStack.EMPTY
  );

  public DefaultWorkerEntity(
    EntityType<? extends PathAwareEntity> entityType,
    World world
  ) {
    super(entityType, world);
    this.skin =
      new Identifier(
        Laborcraft.MOD_ID,
        "textures/entity/profession/default.png"
      );
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

    if (!this.getWorld().isClient() && this.isAlive() && !this.isPersistent()) {
      this.setPersistent();
    }

    tickHandSwing();

    if (this.boss != null) {
      this.followPlayer(this.boss);
    }
    if (this.name == null && getCustomName() == null) {
      this.name = names.getRandomName(Gender.MALE);
      this.lastName = names.getRandomLastName();
      this.setCustomName(Text.of(name + " " + lastName));
    }

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
    if (nbt.contains(LAST_NAME_KEY)) {
      this.lastName = nbt.getString(LAST_NAME_KEY);
    }
    Inventories.readNbt(nbt, inventory);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    Inventories.writeNbt(nbt, inventory);

    nbt.putString(WORK_CHUNK_KEY, this.workChunk.toString());
    nbt.putString(NAME_KEY, this.name);
    nbt.putString(LAST_NAME_KEY, this.lastName);

    return super.writeNbt(nbt);
  }

  public BlockPos getClosestChest() {
    BlockPos closestChest = null;
    double closestDistance = Double.MAX_VALUE;

    int searchRadius = 100; // Set your desired search radius
    BlockPos mobPos = this.getBlockPos();

    for (int x = -searchRadius; x <= searchRadius; x++) {
      for (int y = -searchRadius; y <= searchRadius; y++) {
        for (int z = -searchRadius; z <= searchRadius; z++) {
          BlockPos currentPos = mobPos.add(x, y, z);
          BlockState state = this.getWorld().getBlockState(currentPos);

          if (state.getBlock() instanceof ChestBlock) {
            double distance = mobPos.getSquaredDistance(currentPos);

            if (distance < closestDistance) {
              closestDistance = distance;
              closestChest = currentPos;
            }
          }
        }
      }
    }

    return closestChest;
  }

  public BlockPos findClosestChest(int range) {
    BlockPos closestChestPos = null;

    Box area = new Box(this.getBlockPos()).expand(range);

    for (BlockPos blockPos : BlockPos.iterateOutwards(
      this.getBlockPos(),
      range,
      range,
      range
    )) {
      if (area.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
        System.out.println("Closest chest:" + blockPos);
        return blockPos;
      }
    }
    System.out.println("No chest found chest:" + closestChestPos);
    return closestChestPos;
  }

  public ChunkPos getChunkPos() {
    return new ChunkPos(
      this.getBlockPos().getX() >> 4,
      this.getBlockPos().getZ() >> 4
    );
  }

  public void setWorkChunk(ChunkPos chunkPos) {
    this.workChunk = chunkPos;
  }

  public ChunkPos getWorkChunk() {
    return this.workChunk;
  }

  public void setWorkChest(BlockPos chestPos) {
    this.workChest = chestPos;
  }

  public BlockPos getWorkChest() {
    return this.workChest;
  }

  public void setBoss(PlayerEntity boss) {
    this.boss = boss;
  }

  public PlayerEntity getBoss() {
    return this.boss;
  }

  public void setCollectingItems(boolean isCollectingItems) {
    this.isCollectingItems = isCollectingItems;
  }

  public boolean isCollectingItems() {
    return this.isCollectingItems;
  }

  public void setBreakingBlocks(boolean isBreakingBlocks) {
    this.isBreakingBlocks = isBreakingBlocks;
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

  public int getHandSwingDuration() {
    return 7;
  }

  public void tickHandSwing() {
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

  public void workerSwingHand(Hand hand, boolean fromServerPlayer) {
    if (
      !this.handSwinging ||
      this.handSwingTicks >= this.getHandSwingDuration() / 2 ||
      this.handSwingTicks < 0
    ) {
      this.handSwingTicks = -1;
      this.handSwinging = true;
      this.preferredHand = hand;
      if (this.getWorld() instanceof ServerWorld) {
        EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(
          this,
          hand == Hand.MAIN_HAND ? 0 : 3
        );
        ServerChunkManager serverChunkManager =
          ((ServerWorld) this.getWorld()).getChunkManager();
        if (fromServerPlayer) {
          serverChunkManager.sendToNearbyPlayers(
            this,
            entityAnimationS2CPacket
          );
        } else {
          serverChunkManager.sendToOtherNearbyPlayers(
            this,
            entityAnimationS2CPacket
          );
        }
      }
    }
  }
  // public Identifier getTexture() {
  //   return this.npcTexture;
  // }

  // public void setTexture(String texturePath) {
  //   this.npcTexture = new Identifier("minecraft", texturePath);
  // }
  // private void changeNPCTexture() {
  //   MinecraftClient client = MinecraftClient.getInstance();
  //   EntityRenderDispatcher renderDispatcher = client.getEntityRenderDispatcher();
  //   EntityRenderer<? super DefaultWorkerEntity> renderer = renderDispatcher.getRenderer(
  //     this
  //   );

  //   if (renderer instanceof DefaultWorkerRenderer) {
  //     DefaultWorkerRenderer customRenderer = (DefaultWorkerRenderer) renderer;

  //     customRenderer.TEXTURE = this.npcTexture;
  //   }
  // }
}
