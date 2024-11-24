package me.roundaround.roundalib.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
  @Accessor
  VertexConsumerProvider.Immediate getVertexConsumers();

  @Invoker
  void invokeDrawSpriteRegion(
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      int textureWidth,
      int textureHeight,
      int u,
      int v,
      int x,
      int y,
      int width,
      int height,
      int color
  );

  @Invoker
  void invokeDrawSpriteNineSliced(
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      int x,
      int y,
      int width,
      int height,
      int color
  );
}
