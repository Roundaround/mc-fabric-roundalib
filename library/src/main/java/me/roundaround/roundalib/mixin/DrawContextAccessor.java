package me.roundaround.roundalib.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
  @Invoker
  void invokeDrawSprite(Sprite sprite, int i, int j, int k, int l, int x, int y, int z, int width, int height);
}
