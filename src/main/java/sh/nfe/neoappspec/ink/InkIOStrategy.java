package sh.nfe.neoappspec.ink;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import sh.nfe.neoappspec.NeoAppSpec;

public class InkIOStrategy {
    static class Import implements StackImportStrategy {
        private final ServerLevel level;
        private final BlockPos fromPos;
        private final Direction fromSide;

        public Import(ServerLevel level,
                      BlockPos fromPos,
                      Direction fromSide) {
            this.level = level;
            this.fromPos = fromPos;
            this.fromSide = fromSide;
        }

        @Override
        public boolean transfer(StackTransferContext context) {
            if (!context.isKeyTypeEnabled(InkKeyType.TYPE)) {
                return false;
            }

            long maxTransfer = InkKeyType.TYPE.getAmountPerOperation() * (long) context.getOperationsRemaining();
            long transferred = 0L;
            BlockEntity blockEntity = level.getBlockEntity(fromPos);
            if (!(blockEntity instanceof InkStorageBlockEntity<?>))
                return false;
            InkStorage storage = ((InkStorageBlockEntity<?>) blockEntity).getEnergyStorage();
            for (InkColor inkColor : storage.getEnergy().keySet()) {
                InkKey key = new InkKey(inkColor);
                if (context.isInFilter(key) == context.isInverted()) {
                    continue;
                }

                long extracted = storage.drainEnergy(inkColor, maxTransfer);
                long inserted = context.getInternalStorage().getInventory().insert(key, extracted, Actionable.MODULATE, context.getActionSource());
                long toRefund = extracted - inserted;
                long refunded = toRefund - InkWorkaround.addEnergyBugfix(storage, inkColor, toRefund);
                if (refunded != toRefund) {
                    NeoAppSpec.LOGGER.error("Failed to correctly refund over-extracted ink? (Wanted to refund {}, refunded {})", toRefund, refunded);
                }
                maxTransfer -= inserted;
                transferred += inserted;
            }
            ((InkStorageBlockEntity<?>) blockEntity).setInkDirty();
            blockEntity.setChanged();

            long opsUsed = Math.max(1, transferred / InkKeyType.TYPE.getAmountPerOperation());
            context.reduceOperationsRemaining(opsUsed);
            return true;
        }
    }

    static class Export implements StackExportStrategy {
        private final ServerLevel level;
        private final BlockPos fromPos;
        private final Direction fromSide;

        public Export(ServerLevel level,
                                 BlockPos fromPos,
                                 Direction fromSide) {
            this.level = level;
            this.fromPos = fromPos;
            this.fromSide = fromSide;
        }

        @Override
        public long transfer(StackTransferContext context, AEKey what, long maxAmount) {
            if (!(what instanceof InkKey))
                return 0;

            BlockEntity blockEntity = level.getBlockEntity(fromPos);
            if (!(blockEntity instanceof InkStorageBlockEntity<?>))
                return 0;
            InkStorage storage = ((InkStorageBlockEntity<?>) blockEntity).getEnergyStorage();
            InkColor color = ((InkKey) what).getColor();
            if (!storage.accepts(color))
                return 0;

            long remainingCapacity = storage.getRoom(color);
            long insertable = Math.min(maxAmount, remainingCapacity);
            long extracted = StorageHelper.poweredExtraction(context.getEnergySource(),
                    context.getInternalStorage().getInventory(), what, insertable, context.getActionSource(),
                    Actionable.MODULATE);

            if (extracted == 0) {
                return 0;
            }

            long overflow = InkWorkaround.addEnergyBugfix(storage, color, extracted);
            long inserted = extracted - overflow;
            if (extracted != inserted) {
                NeoAppSpec.LOGGER.error("Ink over-extracted despite checking capacity? (Extracted {}, inserted {})", extracted, inserted);
            }
            ((InkStorageBlockEntity<?>) blockEntity).setInkDirty();
            blockEntity.setChanged();
            return extracted;
        }

        @Override
        public long push(AEKey what, long maxAmount, Actionable mode) {
            if (!(what instanceof InkKey))
                return 0;

            BlockEntity blockEntity = level.getBlockEntity(fromPos);
            if (!(blockEntity instanceof InkStorageBlockEntity<?>))
                return 0;
            InkStorage storage = ((InkStorageBlockEntity<?>) blockEntity).getEnergyStorage();
            InkColor color = ((InkKey) what).getColor();
            if (!storage.accepts(color))
                return 0;

            if (mode == Actionable.SIMULATE) {
                long capacity = storage.getRoom(color);
                return Math.min(maxAmount, capacity);
            }

            long overflow = InkWorkaround.addEnergyBugfix(storage, color, maxAmount);
            long inserted = maxAmount - overflow;
            ((InkStorageBlockEntity<?>) blockEntity).setInkDirty();
            blockEntity.setChanged();
            return inserted;
        }
    }
}
