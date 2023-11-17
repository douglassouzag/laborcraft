package net.glok.laborcraft.entity.client;

import net.glok.laborcraft.Laborcraft;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {

  public static final EntityModelLayer DEFAULT_WORKER = new EntityModelLayer(
    new Identifier(Laborcraft.MOD_ID, "default_worker"),
    "main"
  );
  public static final EntityModelLayer LUMBERJACK = new EntityModelLayer(
    new Identifier(Laborcraft.MOD_ID, "lumberjack"),
    "main"
  );
}
