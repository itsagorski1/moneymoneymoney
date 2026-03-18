package com.jonah.code.java.mods.moneymoneymoney.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMoneyCapability {

    // ------------------------
    // The data class for money
    // ------------------------
    public static class Money {
        private double balance = 0;

        public double getBalance() { return balance; }
        public void deposit(double amount) { balance += amount; }
        public boolean withdraw(double amount) {
            if (amount > balance) return false;
            balance -= amount;
            return true;
        }
    }

    // ------------------------
    // Storage for saving/loading
    // ------------------------
    public static class Storage implements Capability.IStorage<Money> {

        @Override
        public @Nullable CompoundTag writeNBT(Capability<Money> capability, Money instance, Direction side) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("Balance", instance.getBalance());
            return tag;
        }

        @Override
        public void readNBT(Capability<Money> capability, Money instance, Direction side, net.minecraft.nbt.Tag nbt) {
            if (nbt instanceof CompoundTag tag) {
                instance.balance = tag.getDouble("Balance");
            }
        }
    }

    // ------------------------
    // Capability reference (injected by Forge)
    // ------------------------
    public static Capability<Money> MONEY = null;
}