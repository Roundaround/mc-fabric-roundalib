package me.roundaround.roundalib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
  @Invoker
  void invokeDrawTexturedQuad(
      RenderPipeline pipeline,
      Identifier sprite,
      int x1,
      int x2,
      int y1,
      int y2,
      float u1,
      float u2,
      float v1,
      float v2,
      int color);

  @Invoker
  void invokeDrawSpriteRegion(
      RenderPipeline pipeline,
      Sprite sprite,
      int textureWidth,
      int textureHeight,
      int u,
      int v,
      int x,
      int y,
      int width,
      int height,
      int color);

  @Invoker
  void invokeDrawSpriteNineSliced(
      RenderPipeline pipeline,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      int x,
      int y,
      int width,
      int height,
      int color);
}
