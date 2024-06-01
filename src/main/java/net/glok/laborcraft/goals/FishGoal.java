package net.glok.laborcraft.goals;

import net.glok.laborcraft.entity.custom.NPCEntity;
import net.glok.laborcraft.helpers.NavigationHelper;
import net.glok.laborcraft.state.StateMachineGoal.StateEnum;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FishGoal extends Goal {

  private final NPCEntity npc;

  private final NavigationHelper navigationHelper = new NavigationHelper();

  public FishGoal(NPCEntity npc) {
    this.npc = npc;
  }

  private void castFishingLine(NPCEntity npc, BlockPos pos) {}

  private BlockPos searchForAPlaceToFish(World world, Box area) {
    int minX = MathHelper.floor(area.minX);
    int minY = MathHelper.floor(area.minY);
    int minZ = MathHelper.floor(area.minZ);
    int maxX = MathHelper.ceil(area.maxX);
    int maxY = MathHelper.ceil(area.maxY);
    int maxZ = MathHelper.ceil(area.maxZ);

    for (int x = minX; x <= maxX - 2; x++) {
      for (int y = minY; y <= maxY - 2; y++) {
        for (int z = minZ; z <= maxZ - 2; z++) {
          BlockPos centerPos = new BlockPos(x + 1, y + 1, z + 1);
          boolean is3x3Water = true;

          for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
              for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = centerPos.add(dx, dy, dz);
                if (!world.getBlockState(pos).isOf(Blocks.WATER)) {
                  is3x3Water = false;
                  break;
                }
              }
              if (!is3x3Water) break;
            }
            if (!is3x3Water) break;
          }

          if (is3x3Water) return centerPos;
        }
      }
    }

    return null;
  }

  @Override
  public boolean canStart() {
    return (
      this.npc.currentState == StateEnum.WORKING && this.npc.isWorkAreaValid()
    );
  }

  @Override
  public void tick() {
    BlockPos placeToFish = searchForAPlaceToFish(
      this.npc.getWorld(),
      this.npc.workArea
    );

    System.out.println(placeToFish);

    if (placeToFish == null) return;

    navigationHelper.navigateTo(npc, placeToFish);

    if (navigationHelper.isBesideBlock(npc, placeToFish)) {
      this.npc.swingHand(Hand.MAIN_HAND);
    }
  }
}
