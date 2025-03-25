package me.roundaround.roundalib.sync.host;

import net.minecraft.block.entity.BlockEntity;

public sealed abstract class HostType<T> permits HostType.BlockEntityHost {
  abstract Class<T> getHostClass();

  @Override
  public int hashCode() {
    return this.getHostClass().hashCode();
  }

  public final static class BlockEntityHost extends HostType<BlockEntity> {
    @Override
    public Class<BlockEntity> getHostClass() {
      return BlockEntity.class;
    }
  }
}
