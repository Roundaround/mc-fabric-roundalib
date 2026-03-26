package me.roundaround.roundalib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.Identifier;

@Mixin(GuiGraphics.class)
public interface DrawContextAccessor {
  @Invoker
  void invokeInnerBlit(
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
  void invokeBlitSprite(
      RenderPipeline pipeline,
      TextureAtlasSprite sprite,
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
  void invokeBlitNineSlicedSprite(
      RenderPipeline pipeline,
      TextureAtlasSprite sprite,
      GuiSpriteScaling.NineSlice nineSlice,
      int x,
      int y,
      int width,
      int height,
      int color);
}
