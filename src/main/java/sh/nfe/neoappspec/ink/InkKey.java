package sh.nfe.neoappspec.ink;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class InkKey extends AEKey {
    private final InkColor color;

    public InkKey(InkColor color) {
        this.color = color;
    }

    @Override
    public AEKeyType getType() {
        return InkKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Color", color.getID().toString());
        return tag;
    }

    @Override
    public Object getPrimaryKey() {
        return color;
    }

    @Override
    public ResourceLocation getId() {
        return color.getID();
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        data.writeResourceLocation(color.getID());
    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("ink.suffix", color.getName());
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
    }

    @Override
    public boolean hasComponents() {
        return false;
    }

    public InkColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        else {
            var key = (InkKey) obj;
            return Objects.equals(color, key.color);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(color);
    }
}
