package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import net.minecraft.nbt.CompoundTag;

public interface IPlayerMoneyData {
    int getWalletBalance();

    int getBankBalance();

    void setWalletBalance(int amount);

    void setBankBalance(int amount);

    boolean addWallet(int amount);

    boolean removeWallet(int amount);

    boolean addBank(int amount);

    boolean removeBank(int amount);

    default boolean depositToBank(int amount) {
        return removeWallet(amount) && addBank(amount);
    }

    default boolean withdrawFromBank(int amount) {
        return removeBank(amount) && addWallet(amount);
    }

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);
}
