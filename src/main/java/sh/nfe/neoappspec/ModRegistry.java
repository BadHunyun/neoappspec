package sh.nfe.neoappspec;

import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sh.nfe.neoappspec.ink.InkKeyType;
import sh.nfe.neoappspec.item.PortableInkCellItem;

public class ModRegistry {
    static class Items {
        private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.createItems(NeoAppSpec.MOD_ID);

        public static final DeferredHolder<Item, Item>
                INK_CELL_HOUSING = ITEM_REGISTER.register("ink_cell_housing", () -> new Item(new Item.Properties())),
                INK_CELL_1K = inkCellItem("ink_cell_1k", StorageTier.SIZE_1K),
                INK_CELL_4K = inkCellItem("ink_cell_4k", StorageTier.SIZE_4K),
                INK_CELL_16K = inkCellItem("ink_cell_16k", StorageTier.SIZE_16K),
                INK_CELL_64K = inkCellItem("ink_cell_64k", StorageTier.SIZE_64K),
                INK_CELL_256K = inkCellItem("ink_cell_256k", StorageTier.SIZE_256K),
                PORTABLE_INK_CELL_1K = portableCellItem("portable_cell_1k", StorageTier.SIZE_1K),
                PORTABLE_INK_CELL_4K = portableCellItem("portable_cell_4k", StorageTier.SIZE_4K),
                PORTABLE_INK_CELL_16K = portableCellItem("portable_cell_16k", StorageTier.SIZE_16K),
                PORTABLE_INK_CELL_64K = portableCellItem("portable_cell_64k", StorageTier.SIZE_64K),
                PORTABLE_INK_CELL_256K = portableCellItem("portable_cell_256k", StorageTier.SIZE_256K);

        private static DeferredHolder<Item, Item> inkCellItem(String id, StorageTier tier) {
            return ITEM_REGISTER.register(id, () -> inkCell(tier));
        }

        private static DeferredHolder<Item, Item> portableCellItem(String id, StorageTier tier) {
            return ITEM_REGISTER.register(id, () -> new PortableInkCellItem(tier, new Item.Properties().stacksTo(1)));
        }

        static Item inkCell(StorageTier tier) {
            return new BasicStorageCell(new Item.Properties().stacksTo(1), tier.idleDrain(), tier.bytes() / 1024, tier.bytes() / 128, 16, InkKeyType.TYPE);
        }

        public static void register(IEventBus bus) {
            ITEM_REGISTER.register(bus);
        }
    }
}
