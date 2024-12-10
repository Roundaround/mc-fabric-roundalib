package me.roundaround.roundalib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.roundaround.roundalib.util.BuiltinResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.dynamic.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourcePackProfile.class)
public class ResourcePackProfileMixin {
  @WrapOperation(
      method = "loadMetadata", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/resource/ResourcePackProfile;getSupportedFormats(Ljava/lang/String;" +
               "Lnet/minecraft/resource/metadata/PackResourceMetadata;)Lnet/minecraft/util/dynamic/Range;"
  )
  )
  private static Range<Integer> adjustSupportedFormatsRange(
      String packId, PackResourceMetadata metadata, Operation<Range<Integer>> original
  ) {
    if (!BuiltinResourcePack.shouldForceVersionCompat(packId)) {
      return original.call(packId, metadata);
    }

    int packFormat = metadata.packFormat();
    int currentFormat = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);

    return new Range<>(Math.min(packFormat, currentFormat), Math.max(packFormat, currentFormat));
  }
}
