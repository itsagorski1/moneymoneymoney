package com.jonah.code.java.mods.moneymoneymoney.network;

import com.jonah.code.java.mods.moneymoneymoney.MoneyMoneyMoneyMod;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MoneyMoneyMoneyMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int packetId;

    private ModMessages() {
    }

    public static void register() {
        INSTANCE.registerMessage(packetId++,
                MoneyActionPacket.class,
                MoneyActionPacket::encode,
                MoneyActionPacket::decode,
                MoneyActionPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}
