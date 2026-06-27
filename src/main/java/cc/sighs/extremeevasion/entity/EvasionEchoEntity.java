package cc.sighs.extremeevasion.entity;

import cc.sighs.extremeevasion.ExtremeEvasion;
import cc.sighs.extremeevasion.ExtremeEvasionEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EvasionEchoEntity extends ArmorStand {

    private static final String OWNER_UUID_TAG = "OwnerUUID";

    private UUID ownerUuid;

    public EvasionEchoEntity(EntityType<? extends ArmorStand> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvisible(true);
        this.setSilent(true);
        this.setInvulnerable(false);
        this.setShowArms(false);
        this.setNoBasePlate(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }

        ServerPlayer owner = getOwnerPlayer();
        if (owner == null || !ExtremeEvasionEvents.hasActiveExtremeEvasionWindow(owner)) {
            discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide()) {
            return true;
        }

        ServerPlayer owner = getOwnerPlayer();
        if (owner == null) {
            discard();
            return true;
        }

        if (!isValidTriggerSource(source, owner)) {
            return false;
        }

        boolean triggered = ExtremeEvasionEvents.tryTriggerExtremeEvasion(owner);
        discard();
        return triggered || super.hurt(source, amount);
    }

    @Override
    public boolean skipAttackInteraction(net.minecraft.world.entity.Entity entity) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean canBeSeenByAnyone() {
        return false;
    }

    @Override
    public boolean attackable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    public void setOwner(ServerPlayer owner) {
        this.ownerUuid = owner.getUUID();
    }

    public ServerPlayer getOwnerPlayer() {
        if (ownerUuid == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        return serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUuid != null) {
            tag.putUUID(OWNER_UUID_TAG, ownerUuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(OWNER_UUID_TAG)) {
            ownerUuid = tag.getUUID(OWNER_UUID_TAG);
        }
    }

    private boolean isValidTriggerSource(DamageSource source, ServerPlayer owner) {
        Entity attacker = source.getEntity();
        Entity direct = source.getDirectEntity();
        if (attacker == null && direct == null) {
            return false;
        }

        return attacker != owner && direct != owner;
    }
}
