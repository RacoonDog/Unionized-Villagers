package io.github.racoondog.unionizedvillagers;

public interface IOshaViolationHolder {
    String IDENTIFIER = "vb$osha";

    void villagerBalancing$setOshaViolationStatus(boolean b);

    boolean villagerBalancing$getOshaViolationStatus();

    static void setOshaViolation(IOshaViolationHolder holder, boolean oshaViolationStatus) {
        holder.villagerBalancing$setOshaViolationStatus(oshaViolationStatus);
    }

    static boolean getOshaViolation(IOshaViolationHolder holder) {
        return holder.villagerBalancing$getOshaViolationStatus();
    }
}
