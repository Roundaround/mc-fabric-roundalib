package me.roundaround.roundalib.mixin;

import net.minecraft.client.gui.ScreenRect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(targets = "net.minecraft.client.gui.DrawContext$ScissorStack")
public interface ScissorStackAccessor {
  @Accessor("stack")
  Deque<ScreenRect> getStack();
}
