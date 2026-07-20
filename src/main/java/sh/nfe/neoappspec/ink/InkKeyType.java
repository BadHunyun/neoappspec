package sh.nfe.neoappspec.ink;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import sh.nfe.neoappspec.NeoAppSpec;

public class InkKeyType extends AEKeyType {
    public static final InkKeyType TYPE = new InkKeyType();

    public InkKeyType() {
        super(ResourceLocation.fromNamespaceAndPath(NeoAppSpec.MOD_ID, "ink"), InkKey.class, Component.translatable("gui.neo_app_spec.ink"));
    }


    @Override
    public MapCodec<? extends AEKey> codec() {
        return null;
    }

    @Override
    public @Nullable AEKey readFromPacket(RegistryFriendlyByteBuf input) {
        return new InkKey(SpectrumRegistries.INK_COLOR.get(input.readResourceLocation()));
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(HolderLookup.Provider registries, CompoundTag tag) {
        return new InkKey(SpectrumRegistries.INK_COLOR.get(ResourceLocation.parse(tag.getString("Color"))));
    }

    @Override
    public int getAmountPerByte() {
        return 800;
    }

    @Override
    public int getAmountPerOperation() {
        return 100;
    }
}
