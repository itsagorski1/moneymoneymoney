package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import net.minecraft.nbt.CompoundTag;

public class PlayerMoneyCapability implements IPlayerMoneyData {
    private static final String WALLET_KEY = "WalletBalance";
    private static final String BANK_KEY = "BankBalance";

    private int walletBalance;
    private int bankBalance;

    @Override
    public int getWalletBalance() {
        return walletBalance;
    }

    @Override
    public int getBankBalance() {
        return bankBalance;
    }

    @Override
    public void setWalletBalance(int amount) {
        walletBalance = Math.max(0, amount);
    }

    @Override
    public void setBankBalance(int amount) {
        bankBalance = Math.max(0, amount);
    }

    @Override
    public boolean addWallet(int amount) {
        if (amount <= 0) {
            return false;
        }

        walletBalance = Math.addExact(walletBalance, amount);
        return true;
    }

    @Override
    public boolean removeWallet(int amount) {
        if (amount <= 0 || walletBalance < amount) {
            return false;
        }

        walletBalance -= amount;
        return true;
    }

    @Override
    public boolean addBank(int amount) {
        if (amount <= 0) {
            return false;
        }

        bankBalance = Math.addExact(bankBalance, amount);
        return true;
    }

    @Override
    public boolean removeBank(int amount) {
        if (amount <= 0 || bankBalance < amount) {
            return false;
        }

        bankBalance -= amount;
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(WALLET_KEY, walletBalance);
        tag.putInt(BANK_KEY, bankBalance);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        walletBalance = Math.max(0, tag.getInt(WALLET_KEY));
        bankBalance = Math.max(0, tag.getInt(BANK_KEY));
    }
}
