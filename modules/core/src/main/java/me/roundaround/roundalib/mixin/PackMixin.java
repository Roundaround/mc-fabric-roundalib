package me.roundaround.roundalib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.roundalib.util.BuiltinResourcePack;
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.InclusiveRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Comparator;

@Mixin(Pack.class)
public class PackMixin {
  @WrapOperation(
      method = "readPackMetadata", at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/server/packs/metadata/pack/PackMetadataSection;supportedFormats()Lnet/minecraft/util/InclusiveRange;"
  )
  )
  private static InclusiveRange<PackFormat> adjustSupportedFormatsRange(
      PackMetadataSection metadata,
      Operation<InclusiveRange<PackFormat>> original,
      @Local(argsOnly = true) PackLocationInfo info
  ) {
    if (!BuiltinResourcePack.shouldForceVersionCompat(info.id())) {
      return original.call(metadata);
    }

    InclusiveRange<PackFormat> versionRange = metadata.supportedFormats();
    PackFormat currentVersion = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);

    ArrayList<PackFormat> versions = new ArrayList<>();
    versions.add(versionRange.minInclusive());
    versions.add(versionRange.maxInclusive());
    versions.add(currentVersion);
    versions.sort(Comparator.naturalOrder());

    return new InclusiveRange<>(versions.getFirst(), versions.getLast());
  }
}
