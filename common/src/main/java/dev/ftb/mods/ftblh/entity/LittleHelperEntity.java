package dev.ftb.mods.ftblh.entity;

import com.mojang.datafixers.util.Either;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.SyncableSound;
import dev.ftb.mods.ftblh.client.FTBLittleHelperClient;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Function;

public class LittleHelperEntity extends Mob {
    private WeakReference<ServerPlayer> ownerRef;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;
    private final Deque<TimedMessage> messageQueue = new ArrayDeque<>();
    private int messageTimer;
    private boolean staysShown;
    private boolean dancing;
    private int moveCounter;
    private boolean chatMessages = true;

    private static final int MSG_CHECK_NOW = 0;
    private static final int MSG_IDLE = -1;

    public static final Function<Player,Vec3> DEFAULT_POSITIONER = player ->
            player.getEyePosition().add(player.getLookAngle().normalize().scale(1.5)).subtract(0.0, 0.5, 0.0);
    public static final Function<Player, Vec3> IDLE_POSITIONER_RIGHT = player ->
            player.getEyePosition().add(calcViewVector(-20, player.getYHeadRot() + 90).normalize());
    public static final Function<Player, Vec3> IDLE_POSITIONER_LEFT = player ->
            player.getEyePosition().add(calcViewVector(-20, player.getYHeadRot() - 90).normalize());

    public static final int DEFAULT_MSG_DISPLAY_TIME = 70;  // ticks
    private static final int MAX_MSG_QUEUE_SIZE = 16;

    // determine where the helper should hover when idle (default: above and slightly behind player's right shoulder)
    private Function<Player,Vec3> idlePositioner = IDLE_POSITIONER_RIGHT;
    // determine where the helper should hover when it has message (default: in front of and slightly under the eyeline)
    private Function<Player,Vec3> activePositioner = DEFAULT_POSITIONER;

    private static final EntityDataAccessor<TimedMessage> ACTIVE_MSG
            = SynchedEntityData.defineId(LittleHelperEntity.class, TimedMessage.SERIALIZER);

    public LittleHelperEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

        moveControl = new LHMoveControl(this);
        noPhysics = true;
        blocksBuilding = false;
        spinningAnimationTicks = 7f;
        setInvulnerable(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        entityData.define(ACTIVE_MSG, TimedMessage.NONE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (entityDataAccessor.equals(ACTIVE_MSG)) {
            TimedMessage msg = entityData.get(ACTIVE_MSG);
            if (msg.displayTicks > 0) {
                spinningAnimationTicks = 15f;
                dancing = true;
            } else {
                dancing = false;
            }
            msg.onExecute(this, true);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource != damageSources().genericKill();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    public void setActivePositioner(Function<Player, Vec3> activePositioner) {
        this.activePositioner = activePositioner;
    }

    public void setIdlePositioner(Function<Player, Vec3> idlePositioner) {
        this.idlePositioner = idlePositioner;
    }

    public boolean isChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(boolean chatMessages) {
        this.chatMessages = chatMessages;
    }

    public Component getActiveMsg() {
        TimedMessage msg = entityData.get(ACTIVE_MSG);
        return msg.displayTicks == 0 ? Component.empty() : msg.message.left().orElse(Component.empty());
    }

    public void setStaysShown(boolean staysShown) {
        this.staysShown = staysShown;
    }

    public boolean shouldStayShown() {
        return staysShown;
    }

    /**
     * Set the owning player for this helper. Should be called as soon as possible after creation, and definitely
     * before the first tick (entity will be discarded if no owner).
     *
     * @param owner owning player
     * @throws IllegalStateException if called when already has an owner
     */
    public void setOwner(ServerPlayer owner) {
        if (this.ownerRef != null) {
            throw new IllegalStateException("this little helper already has a player owner!");
        }
        this.ownerRef = new WeakReference<>(owner);

        lookAt(EntityAnchorArgument.Anchor.EYES, owner.getEyePosition());
        HelperTracker.INSTANCE.register(owner.getUUID(), getId());
    }

    public Optional<ServerPlayer> getOwner() {
        return ownerRef == null ? Optional.empty() : Optional.ofNullable(ownerRef.get());
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            getOwner().ifPresentOrElse(
                    owner -> {
                        if (owner.isAlive() && HelperTracker.INSTANCE.validateHelper(owner, this)) {
                            updatePosition(owner);
                            processMessageQueue();
                        } else {
                            hideSelf();
                        }
                    },
                    this::hideSelf);
            if (!level().getBlockState(blockPosition()).getCollisionShape(level(), blockPosition()).isEmpty() && !hasEffect(MobEffects.GLOWING)) {
                addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
            }
        } else {
            // client-only stuff?
        }
        spinningAnimationTicks0 = spinningAnimationTicks;
        if (spinningAnimationTicks > 0) {
            spinningAnimationTicks--;
        }

        this.setNoGravity(true);
        super.tick();
    }

    private void hideSelf() {
        discard();
    }

    @Override
    public void onClientRemoval() {
        spawnAnim();
    }

    private void processMessageQueue() {
        if (messageTimer == MSG_CHECK_NOW) {
            if (!messageQueue.isEmpty()) {
                TimedMessage msg = messageQueue.removeFirst();
                entityData.set(ACTIVE_MSG, msg);
                msg.onExecute(this, false);
                messageTimer = msg.displayTicks;
                moveCounter = 0;
            } else {
                entityData.set(ACTIVE_MSG, TimedMessage.NONE);
                messageTimer = MSG_IDLE;
                if (!staysShown) {
                    hideSelf();
                }
            }
        } else if (messageTimer > MSG_CHECK_NOW) {
            // message being displayed
            messageTimer--;
        }
    }

    public boolean addMessage(Component message) {
        return addTimedMessage(TimedMessage.ofComponent(message, DEFAULT_MSG_DISPLAY_TIME), false);
    }

    public boolean addMessage(Component message, int displayTicks) {
        return addTimedMessage(TimedMessage.ofComponent(message, displayTicks), false);
    }

    public boolean addPriorityMessage(Component message) {
        return addTimedMessage(TimedMessage.ofComponent(message, DEFAULT_MSG_DISPLAY_TIME), true);
    }

    public boolean addPriorityMessage(Component message, int displayTicks) {
        return addTimedMessage(TimedMessage.ofComponent(message, displayTicks), true);
    }

    public boolean addSound(SoundEvent sound) {
        return addSound(sound, 1f, 1f, DEFAULT_MSG_DISPLAY_TIME, false);
    }

    public boolean addSound(SoundEvent sound, int waitTicks) {
        return addSound(sound, 1f, 1f, waitTicks, false);
    }

    public boolean addPrioritySound(SoundEvent sound) {
        return addSound(sound, 1f, 1f, DEFAULT_MSG_DISPLAY_TIME, true);
    }

    public boolean addPrioritySound(SoundEvent sound, int waitTicks) {
        return addSound(sound, 1f, 1f, waitTicks, true);
    }

    public boolean addSound(SoundEvent sound, float volume, float pitch, int waitTicks, boolean queueJump) {
        return addTimedMessage(TimedMessage.ofSound(sound, SoundSource.PLAYERS, volume, pitch, waitTicks), queueJump);
    }

    private boolean addTimedMessage(TimedMessage timedMessage, boolean queueJump) {
        if (timedMessage.shouldSquash(this) || messageQueue.size() >= MAX_MSG_QUEUE_SIZE) {
            return false;
        }

        if (queueJump) {
            messageQueue.addFirst(timedMessage);
        } else {
            messageQueue.addLast(timedMessage);
        }

        if (messageTimer == MSG_IDLE || queueJump) {
            messageTimer = MSG_CHECK_NOW;
        }

        return true;
    }

    public float getHoldingItemAnimationProgress(float partialTick) {
        return 0f;
    }

    public boolean isDancing() {
        return dancing;
    }

    public float getSpinningProgress(float partialTick) {
        return Mth.lerp(partialTick, spinningAnimationTicks0, spinningAnimationTicks) / 15f;
    }

    public boolean isSpinning() {
        return spinningAnimationTicks > 0;
    }

    public Vec3 targetPosition(ServerPlayer owner) {
        if (owner == null) return getPosition(0f); // shouldn't happen

        return messageQueue.isEmpty() && messageTimer == MSG_IDLE ? //getActiveMsg().getContents() == ComponentContents.EMPTY ?
                idlePositioner.apply(owner) :
                activePositioner.apply(owner);
    }

    private void updatePosition(ServerPlayer owner) {
        if (tickCount > 15) {
            Vec3 targetPos = targetPosition(owner);

            double distSq = getPosition(0f).distanceToSqr(targetPos);
            moveControl.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, Math.min(3f, distSq));
        }
    }

    @Override
    public void remove(RemovalReason removalReason) {
        getOwner().ifPresent(player -> {
            if (player.isAlive()) {
                // don't unregister if player died; we'll be respawning the helper when the player respawns
                HelperTracker.INSTANCE.unregister(player.getUUID(), this);
            }
        });

        super.remove(removalReason);
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    private static Vec3 calcViewVector(float xRot, float yRot) {
        float xRad = xRot * Mth.DEG_TO_RAD;
        float yRad = -yRot * Mth.DEG_TO_RAD;
        float c = Mth.cos(xRad);
        return new Vec3(Mth.sin(yRad) * c, -Mth.sin(xRad), Mth.cos(yRad) * c);
    }

    private class LHMoveControl extends MoveControl {
        public LHMoveControl(Mob mob) {
            super(mob);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - getX(), this.wantedY - getY(), this.wantedZ - getZ());
                double d0 = vec3.length();
                if (d0 < getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    setDeltaMovement(getDeltaMovement().scale(0.5D));
                    moveCounter = Math.min(20, moveCounter + 1);
                } else {
                    if (moveCounter > 0) {
                        moveCounter = Math.max(0, moveCounter - 1);
                    } else {
                        setDeltaMovement(getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                        if (getDeltaMovement().lengthSqr() > 8) {
                            setDeltaMovement(getDeltaMovement().scale(0.5));
                        }
                        if (getTarget() == null) {
                            Vec3 vec31 = getDeltaMovement();
                            setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float) Math.PI));
                        } else {
                            double d2 = getTarget().getX() - getX();
                            double d1 = getTarget().getZ() - getZ();
                            setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        }
                        yBodyRot = getYRot();
                    }
                }
            }
        }
    }

    public record TimedMessage(Either<Component, SyncableSound> message, int displayTicks) {
        public static final EntityDataSerializer<TimedMessage> SERIALIZER = new EntityDataSerializer.ForValueType<>() {
            @Override
            public void write(FriendlyByteBuf buf, TimedMessage object) {
                object.message.ifLeft(component -> {
                    buf.writeBoolean(true);
                    buf.writeComponent(component);
                }).ifRight(sound -> {
                    buf.writeBoolean(false);
                    sound.toNetwork(buf);
                });
                buf.writeVarInt(object.displayTicks);
            }

            @Override
            public TimedMessage read(FriendlyByteBuf buf) {
                boolean isComponent = buf.readBoolean();
                return isComponent ?
                        new TimedMessage(Either.left(buf.readComponent()), buf.readVarInt()) :
                        new TimedMessage(Either.right(SyncableSound.fromNetwork(buf)), buf.readVarInt());
            }

            @Override
            public TimedMessage copy(TimedMessage object) {
                return object.message.map(
                        c -> new TimedMessage(Either.left(c.copy()), object.displayTicks),
                        s -> new TimedMessage(Either.right(s), object.displayTicks)
                );
            }
        };
        public static final TimedMessage NONE = new TimedMessage(Either.left(Component.empty()), 0);

        public static TimedMessage ofComponent(Component message, int displayTicks) {
            return new TimedMessage(Either.left(message), displayTicks);
        }

        public static TimedMessage ofSound(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, int displayTicks) {
            return new TimedMessage(Either.right(new SyncableSound(soundEvent, soundSource, volume, pitch)), displayTicks);
        }

        public boolean shouldSquash(LittleHelperEntity lh) {
            return message.map(
                    c -> !lh.messageQueue.isEmpty() && lh.messageQueue.peekLast().message.equals(c),
                    s -> false
            );
        }

        public void onExecute(LittleHelperEntity lh, boolean isClient) {
            message.ifLeft(component -> {
                if (!isClient && lh.chatMessages) {
                    lh.getOwner().ifPresent(owner -> owner.displayClientMessage(Component.empty()
                            .append(Component.translatable("ftblh.chat_prefix").withStyle(ChatFormatting.YELLOW))
                            .append(component), false)
                    );
                }
            }).ifRight(sound -> {
                if (isClient) {
                    FTBLittleHelperClient.playSound(sound);
                }
            });
        }
    }
}
