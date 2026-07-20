package sh.nfe.neoappspec.ink;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;

public class InkWorkaround {
    // huh?
    public static long addEnergyBugfix(InkStorage storage, InkColor color, long l) {
        return storage instanceof SingleInkStorage && storage.isEmpty()
                ? 0
                : storage.addEnergy(color, l);
    }
}
