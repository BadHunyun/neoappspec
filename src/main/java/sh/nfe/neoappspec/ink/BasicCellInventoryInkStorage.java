package sh.nfe.neoappspec.ink;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.me.cells.BasicCellInventory;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sh.nfe.neoappspec.mixin.BasicCellInventoryMixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicCellInventoryInkStorage implements InkStorage {
    private final ItemStack itemStack;
    private final BasicCellInventory inventory;

    public BasicCellInventoryInkStorage(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.inventory = BasicCellInventory.createInventory(itemStack, null);
    }

    @Override
    public boolean accepts(InkColor inkColor) {
        return true;
    }

    @Override
    public long addEnergy(InkColor inkColor, long l) {
        long ins = inventory.insert(new InkKey(inkColor), l, Actionable.MODULATE, IActionSource.empty());
        return l - ins;
    }

    @Override
    public long drainEnergy(InkColor inkColor, long l) {
        return inventory.extract(new InkKey(inkColor), l, Actionable.MODULATE, IActionSource.empty());
    }

    @Override
    public boolean requestEnergy(InkColor inkColor, long l) {
        long extractAmount = inventory.extract(new InkKey(inkColor), l, Actionable.SIMULATE, IActionSource.empty());

        if (extractAmount == l) {
            inventory.extract(new InkKey(inkColor), l, Actionable.MODULATE, IActionSource.empty());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getEnergy(InkColor inkColor) {
        return inventory.getAvailableStacks().get(new InkKey(inkColor));
    }

    @Override
    public Map<InkColor, Long> getEnergy() {
        KeyCounter availableStacks = inventory.getAvailableStacks();
        HashMap<InkColor, Long> result = new HashMap<InkColor, Long>(availableStacks.size());
        availableStacks.forEach(e -> {
            if (e.getKey() instanceof InkKey key) result.put(key.getColor(), e.getLongValue());
        });
        return result;
    }

    @Override
    public void setEnergy(Map<InkColor, Long> map, long l) {
        Object2LongMap<AEKey> cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        map.forEach((key, value) -> cellItems.put(new InkKey(key), value));
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public long getMaxPerColor() {
        return inventory.getTotalBytes() * InkKeyType.TYPE.getAmountPerByte();
    }

    @Override
    public long getMaxTotal() {
        return inventory.getTotalBytes() * InkKeyType.TYPE.getAmountPerByte();
    }

    @Override
    public long getCurrentTotal() {
        return inventory.getStoredItemCount();
    }

    @Override
    public boolean isEmpty() {
        return inventory.getStoredItemCount() == 0;
    }

    @Override
    public boolean isFull() {
        return inventory.getRemainingItemCount() == 0;
    }

    @Override
    public void fillCompletely() {
        Object2LongMap<AEKey> cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        long energyPerColor = inventory.getTotalBytes() - (long) inventory.getBytesPerType() * SpectrumRegistries.INK_COLOR.size() / SpectrumRegistries.INK_COLOR.size();
        InkColors.all().forEach(e -> cellItems.put(new InkKey(e), energyPerColor));
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public void clearContent() {
        Object2LongMap<AEKey> cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public void addTooltip(List<Component> list) {

    }

    @Override
    public long getRoom(InkColor inkColor) {
        return inventory.getRemainingItemCount();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
