package cc.sighs.extremeevasion.network;

import cc.sighs.extremeevasion.BulletTimeController;
import cc.sighs.extremeevasion.ExtremeEvasion;
import cc.sighs.extremeevasion.client.BulletTimeClientState;
import cc.sighs.extremeevasion.client.BulletTimeSoundPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBulletTimePacket(long durationMillis, boolean globalClock) implements CustomPacketPayload {
    public static final Type<SyncBulletTimePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ExtremeEvasion.MODID, "sync_bullet_time")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBulletTimePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            SyncBulletTimePacket::durationMillis,
            ByteBufCodecs.BOOL,
            SyncBulletTimePacket::globalClock,
            SyncBulletTimePacket::new
    );

    public static void handle(SyncBulletTimePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.globalClock) {
                BulletTimeController.triggerGlobal();
            }
            BulletTimeClientState.start(packet.durationMillis);
            BulletTimeSoundPlayer.playTrigger();
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
