package cc.sighs.extremeevasion.network;

import cc.sighs.extremeevasion.ExtremeEvasion;
import cc.sighs.extremeevasion.client.CounterAttackClientState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCounterAttackPacket(boolean active) implements CustomPacketPayload {
    public static final Type<SyncCounterAttackPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ExtremeEvasion.MODID, "sync_counter_attack")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCounterAttackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SyncCounterAttackPacket::active,
            SyncCounterAttackPacket::new
    );

    public static void handle(SyncCounterAttackPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> CounterAttackClientState.setActive(packet.active));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
