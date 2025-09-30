package me.roundaround.roundalib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.roundalib.util.BuiltinResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.dynamic.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Comparator;

@Mixin(ResourcePackProfile.class)
public class ResourcePackProfileMixin {
  @WrapOperation(
      method = "loadMetadata", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/resource/metadata/PackResourceMetadata;supportedFormats()" +
               "Lnet/minecraft/util/dynamic/Range;"
  )
  )
  private static Range<PackVersion> adjustSupportedFormatsRange(
      PackResourceMetadata metadata,
      Operation<Range<PackVersion>> original,
      @Local(argsOnly = true) ResourcePackInfo info
  ) {
    if (!BuiltinResourcePack.shouldForceVersionCompat(info.id())) {
      return original.call(metadata);
    }

    Range<PackVersion> versionRange = metadata.supportedFormats();
    PackVersion currentVersion = SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES);

    ArrayList<PackVersion> versions = new ArrayList<>();
    versions.add(versionRange.minInclusive());
    versions.add(versionRange.maxInclusive());
    versions.add(currentVersion);
    versions.sort(Comparator.naturalOrder());

    return new Range<>(versions.getFirst(), versions.getLast());
  }
}
