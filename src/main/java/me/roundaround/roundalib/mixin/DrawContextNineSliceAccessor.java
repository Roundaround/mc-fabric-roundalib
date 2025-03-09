package me.roundaround.roundalib.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextNineSliceAccessor {
  @Invoker
  void invokeDrawSprite(Sprite sprite, Scaling.NineSlice nineSlice, int x, int y, int z, int width, int height);
}
