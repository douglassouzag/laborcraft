package net.glok.laborcraft.entity.client;

import net.glok.laborcraft.Laborcraft;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {

  public static final EntityModelLayer NPC_ENTITY = new EntityModelLayer(
    new Identifier(Laborcraft.MOD_ID, "npc"),
    "main"
  );
}
