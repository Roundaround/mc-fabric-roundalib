package me.roundaround.roundalib.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
  @Invoker("setScissor")
  void invokeSetScissor(ScreenRect rect);
}
