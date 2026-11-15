package com.player.journal.events;

import com.player.journal.api.PlayerJournalAPI;
import com.player.journal.config.JournalConfig;
import com.player.journal.data.JournalProgressionData;
import com.player.journal.data.RestrictionDataLoader;
import com.player.journal.network.InspectJournalPayload;
import com.player.journal.network.SyncJournalConfigPayload;
import com.player.journal.network.SyncJournalDataPayload;
import com.player.journal.party.PartyManager;
import com.player.journal.registry.ModAttachments;
import com.player.journal.registry.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.*;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class JournalServerEvents {

    private static final Map<UUID, Boolean> lastDamageWasEnvironmental = new HashMap<>();
    private static final Map<UUID, Float> envXpAllowance = new HashMap<>();
    private static final Map<UUID, Long> envDamageCooldown = new HashMap<>();
    private static final Map<UUID, Vec3> lastPlayerPositions = new HashMap<>();
    private static final Map<UUID, Float> pendingAgilityXp = new HashMap<>();

    public static final Map<UUID, Long> pvpDamageTimer = new HashMap<>();
    public static final Map<UUID, Boolean> lastDamageWasPvP = new HashMap<>();
    public static final Map<UUID, Float> pvpXpAllowance = new HashMap<>();

    public static final Map<UUID, Boolean> lastDamageWasPvE = new HashMap<>();
    public static final Map<UUID, Float> pveXpAllowance = new HashMap<>();

    private static final Map<UUID, Map<String, Float>> xpCombos = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> lastXpTicks = new HashMap<>();

    private static final Map<UUID, Set<UUID>> MOB_ATTACKERS = new HashMap<>();
    private static final Map<UUID, Long> xpPageCooldown = new HashMap<>();

    private static int globalBuffTimer = 0;

    public static void register() {

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new RestrictionDataLoader());

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    syncConfigToPlayer(player);
                }
            }
        });


        ServerTickEvents.END_SERVER_TICK.register(server -> {
            onServerTick(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                onPlayerTick(player);
            }
        });


        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerLogin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerLogout(handler.getPlayer()));


        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> onPlayerClone(newPlayer, !alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> onPlayerRespawn(newPlayer, alive));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> onPlayerJoinWorld(player));


        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof Mob mob) {
                MOB_ATTACKERS.remove(mob.getUUID());
            }
        });

        // Living Entity Death & Totem
        ServerLivingEntityEvents.AFTER_DEATH.register(JournalServerEvents::onMobKill);
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                return handleAllowDeathTotemCheck(serverPlayer, damageSource);
            }
            return true;
        });

        // Block Left-Click, Break, Right-Click
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (onLeftClickBlock(serverPlayer, world, pos, player.getItemInHand(hand))) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                return !onBlockBreak(serverPlayer, state);
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (onBlockInteract(serverPlayer, world, hitResult.getBlockPos(), player.getItemInHand(hand))) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // Item Right-Click (Book consume + item restriction)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (onBookConsumeForMagic(serverPlayer, stack)) {
                    return InteractionResultHolder.success(stack);
                }
                if (onItemInteract(serverPlayer, stack)) {
                    return InteractionResultHolder.fail(stack);
                }
            }
            return InteractionResultHolder.pass(stack);
        });

        // Entity Attack & Interact
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (onPlayerAttack(serverPlayer)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                if (onWanderingTraderInteract(serverPlayer, entity)) {
                    return InteractionResult.FAIL;
                }
                if (onEntityInteract(serverPlayer, entity, player.getItemInHand(hand))) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static void sendSyncData(ServerPlayer player, JournalProgressionData data) {
        player.setAttached(ModAttachments.JOURNAL_DATA_TYPE, data);
        SyncJournalDataPayload payload = new SyncJournalDataPayload(
                data.getVitalityLevel(), data.getVitalityXP(),
                data.getCombatLevel(), data.getCombatXP(),
                data.getDefenseLevel(), data.getDefenseXP(),
                data.getMiningLevel(), data.getMiningXP(),
                data.getFarmingLevel(), data.getFarmingXP(),
                data.getSmithingLevel(), data.getSmithingXP(),
                data.getArcheryLevel(), data.getArcheryXP(),
                data.getFishingLevel(), data.getFishingXP(),
                data.getAgilityLevel(), data.getAgilityXP(),
                data.getAlchemyLevel(), data.getAlchemyXP(),
                data.getTornPages(),
                data.getResearchedItems()
        );
        ServerPlayNetworking.send(player, payload);
    }

    public static float getXpMultiplier(ServerPlayer player) {
        float multiplier = 1.0f;
        try {
            if (player.hasEffect(ModEffects.PERSONAL_XP_BUFF)) {
                multiplier += 0.50f;
            }
            if (player.hasEffect(ModEffects.GLOBAL_XP_BUFF)) {
                multiplier += 0.50f;
            }
            if (player.hasEffect(ModEffects.PARTY_XP_BUFF)) {
                multiplier += 0.50f;
            } else {
                List<UUID> partyUUIDs = PartyManager.getPartyMembers(player.getUUID());
                if (partyUUIDs != null) {
                    for (UUID memberId : partyUUIDs) {
                        if (!memberId.equals(player.getUUID())) {
                            ServerPlayer member = player.getServer().getPlayerList().getPlayer(memberId);
                            if (member != null && member.hasEffect(ModEffects.PARTY_XP_BUFF)) {
                                if (player.distanceToSqr(member) <= 10000) {
                                    multiplier += 0.10f;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return multiplier;
    }

    public static void onServerTick(MinecraftServer server) {
        globalBuffTimer++;

        if (globalBuffTimer >= 72000) {
            globalBuffTimer = 0;

            if (server.overworld().random.nextFloat() < 0.20f) {
                int durationTicks = 20 * 60 * 15;
                globalBuffTimer = -durationTicks;

                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    try {
                        p.addEffect(new MobEffectInstance(
                                ModEffects.GLOBAL_XP_BUFF, durationTicks, 0, false, false, true
                        ));
                    } catch (Exception ignored) {}
                }
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("§6[Server] §e Global XP Buff activated for 15 minutes!"), false
                );
            }
        }
    }

    private static void checkAndBroadcastMilestone(ServerPlayer player, String skill, int level) {
        if (level > 0 && level % 5 == 0) {
            if (player.getServer() != null) {
                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                Component broadcastMsg = Component.literal("§b[Server] §e" + player.getName().getString() + " has reached Level " + level + " in " + displaySkill + "!");
                player.getServer().getPlayerList().broadcastSystemMessage(broadcastMsg, false);
            }
        }
    }

    private static void displayComboXp(ServerPlayer player, String skill, float xpGained, ChatFormatting color) {
        UUID playerId = player.getUUID();
        long currentTick = player.level().getGameTime();

        xpCombos.putIfAbsent(playerId, new HashMap<>());
        lastXpTicks.putIfAbsent(playerId, new HashMap<>());

        Map<String, Float> combos = xpCombos.get(playerId);
        Map<String, Long> ticks = lastXpTicks.get(playerId);

        long lastTick = ticks.getOrDefault(skill, 0L);
        float oldComboXp = combos.getOrDefault(skill, 0f);

        if (currentTick - lastTick > 60) {
            oldComboXp = 0f;
        }

        float newComboXp = oldComboXp + xpGained;

        combos.put(skill, newComboXp);
        ticks.put(skill, currentTick);

        MutableComponent combinedMessage = Component.empty();
        boolean first = true;
        boolean hasActiveBuff = getXpMultiplier(player) > 1.0f;

        for (Map.Entry<String, Float> entry : combos.entrySet()) {
            String activeSkill = entry.getKey();
            float activeXp = entry.getValue();
            long activeLastTick = ticks.getOrDefault(activeSkill, 0L);

            if (currentTick - activeLastTick <= 60 && activeXp >= 1) {
                if (!first) {
                    combinedMessage.append(Component.literal("  |  ").withStyle(ChatFormatting.DARK_GRAY));
                }

                String skillDisplay = activeSkill.substring(0, 1).toUpperCase() + activeSkill.substring(1);
                ChatFormatting displayColor;

                if (hasActiveBuff) {
                    displayColor = ChatFormatting.GOLD;
                } else {
                    displayColor = switch (activeSkill) {
                        case "vitality" -> ChatFormatting.RED;
                        case "combat" -> ChatFormatting.DARK_RED;
                        case "defense" -> ChatFormatting.GOLD;
                        case "archery" -> ChatFormatting.AQUA;
                        case "mining" -> ChatFormatting.GRAY;
                        case "farming" -> ChatFormatting.GREEN;
                        case "smithing" -> ChatFormatting.DARK_GRAY;
                        case "fishing" -> ChatFormatting.BLUE;
                        case "alchemy" -> ChatFormatting.LIGHT_PURPLE;
                        case "agility" -> ChatFormatting.DARK_GREEN;
                        default -> ChatFormatting.WHITE;
                    };
                }

                combinedMessage.append(Component.literal("+ " + (int) activeXp + " " + skillDisplay + " XP").withStyle(displayColor));
                first = false;
            }
        }

        if (!first) {
            player.displayClientMessage(combinedMessage, true);
        }
    }

    private static void clearCombo(ServerPlayer player, String skill) {
        xpCombos.putIfAbsent(player.getUUID(), new HashMap<>());
        xpCombos.get(player.getUUID()).put(skill, 0f);
    }

    private static boolean isHarvestable(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (state.hasProperty(BlockStateProperties.AGE_3)) {
            return state.getValue(BlockStateProperties.AGE_3) >= 3;
        }
        if (state.hasProperty(BlockStateProperties.AGE_2)) {
            return state.getValue(BlockStateProperties.AGE_2) >= 2;
        }
        if (state.hasProperty(BlockStateProperties.BERRIES)) {
            return state.getValue(BlockStateProperties.BERRIES);
        }
        return true;
    }

    public static void onGameModeChange(ServerPlayer player, GameType newMode) {
        boolean willBeDisabled = (newMode == GameType.CREATIVE || newMode == GameType.SPECTATOR);
        boolean wasDisabled = (player.isCreative() || player.isSpectator());

        if (willBeDisabled && !wasDisabled) {
            player.displayClientMessage(Component.literal("Journal disabled").withStyle(ChatFormatting.RED), true);
        } else if (!willBeDisabled && wasDisabled) {
            player.displayClientMessage(Component.literal("Journal enabled").withStyle(ChatFormatting.GREEN), true);
        }
    }

    public static boolean onEntityMount(ServerPlayer player, Entity entityBeingMounted) {
        if (player.isCreative() || player.isSpectator()) return false;

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        String targetId = BuiltInRegistries.ENTITY_TYPE.getKey(entityBeingMounted.getType()).toString();

        if (data.hasResearched(targetId)) {
            return false;
        }

        String apiFailMessage = getInteractRestrictionError(player, targetId);
        if (apiFailMessage != null) {
            player.displayClientMessage(Component.literal(apiFailMessage).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
            return true;
        }

        try {
            for (String restriction : (List<String>) JournalConfig.AGILITY_MOUNTS.get()) {
                String[] parts = restriction.split(";");
                if (parts.length >= 2) {
                    String[] groupedAnimals = parts[0].split(",");

                    for (String restrictedEntity : groupedAnimals) {
                        if (restrictedEntity.trim().equals(targetId)) {
                            String[] reqs = parts[1].split(":");
                            if (reqs.length == 2 && reqs[0].equalsIgnoreCase("agility")) {
                                int reqLevel = Integer.parseInt(reqs[1].trim());

                                if (data.getAgilityLevel() < reqLevel) {
                                    player.displayClientMessage(Component.literal(getFriendlyName(targetId) + " requires Agility Level " + reqLevel + " to ride!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static void onPlayerLogin(ServerPlayer serverPlayer) {
        JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        data.syncPlayerHealth(serverPlayer);
        data.syncPlayerAgility(serverPlayer);

        sendSyncData(serverPlayer, data);
        syncConfigToPlayer(serverPlayer);
        PartyManager.onPlayerLoggedIn(serverPlayer);
    }

    public static void onPlayerLogout(ServerPlayer serverPlayer) {
        PartyManager.onPlayerLoggedOut(serverPlayer);
    }

    public static void onPlayerRespawn(ServerPlayer player, boolean endConquered) {
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        data.syncPlayerHealth(player);
        data.syncPlayerAgility(player);
        sendSyncData(player, data);

        xpPageCooldown.put(player.getUUID(), System.currentTimeMillis() + 300000L);

        if (endConquered) return;

        List<UUID> partyMembers = PartyManager.getPartyMembers(player.getUUID());
        if (partyMembers.size() <= 1) return;

        Optional<GlobalPos> deathPosOpt = player.getLastDeathLocation();
        if (deathPosOpt.isEmpty()) return;

        GlobalPos deathPos = deathPosOpt.get();
        ServerLevel deathLevel = player.getServer().getLevel(deathPos.dimension());
        if (deathLevel == null) return;

        ServerPlayer targetMember = null;

        for (UUID memberId : partyMembers) {
            if (memberId.equals(player.getUUID())) continue;

            ServerPlayer member = player.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && member.isAlive() && member.level() == deathLevel) {
                if (member.blockPosition().distSqr(deathPos.pos()) <= 10000) {
                    targetMember = member;
                    break;
                }
            }
        }

        if (targetMember != null) {
            BlockPos spawnPos = findSafeSpawnNear(targetMember.serverLevel(), targetMember.blockPosition(), 5);
            player.teleportTo(targetMember.serverLevel(), spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        }
    }

    private static BlockPos findSafeSpawnNear(ServerLevel level, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 3; y >= -3; y--) {
                    BlockPos check = center.offset(x, y, z);
                    BlockState floor = level.getBlockState(check.below());

                    if (!floor.isAir() && floor.getFluidState().isEmpty() && !floor.is(Blocks.MAGMA_BLOCK)
                            && level.isEmptyBlock(check) && level.isEmptyBlock(check.above())) {
                        return check;
                    }
                }
            }
        }
        return center;
    }

    public static void onShieldBlock(ServerPlayer player, DamageSource source, float blockedDamage) {
        if (player.isCreative() || player.isSpectator()) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof Mob) && !(attacker instanceof Player)) return;
        if (attacker == player) return;

        ItemStack shieldStack = player.getUseItem();
        if (shieldStack.isEmpty()) {
            if (player.getOffhandItem().getItem() instanceof ShieldItem) {
                shieldStack = player.getOffhandItem();
            } else if (player.getMainHandItem().getItem() instanceof ShieldItem) {
                shieldStack = player.getMainHandItem();
            }
        }

        if (!shieldStack.isEmpty()) {
            String shieldId = BuiltInRegistries.ITEM.getKey(shieldStack.getItem()).toString();
            String failMessage = getFailedRequirementFromMap(player, shieldId);

            if (failMessage == null && blockedDamage > 0) {
                JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                if (data.getDefenseLevel() >= 1) {
                    float xpMultiplier = JournalConfig.DEFENSE_SHIELD_XP_PER_DAMAGE.get().floatValue();
                    float baseXpGained = Math.max(1.0f, (blockedDamage * xpMultiplier));
                    float finalXp = Math.min(10.0f, baseXpGained) * getXpMultiplier(player);

                    boolean leveledUp = data.addDefenseXP(finalXp, player);
                    sendSyncData(player, data);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§6§lDefense Level Up! §eYou are now level " + data.getDefenseLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "defense");
                        checkAndBroadcastMilestone(player, "defense", data.getDefenseLevel());
                    } else {
                        displayComboXp(player, "defense", finalXp, ChatFormatting.GOLD);
                    }
                    sharePartyXP(player, "defense", finalXp);
                }
            }
        }
    }

    public static void onIncomingDamage(ServerPlayer player, DamageSource source, float incomingDamage) {
        if (player.isCreative() || player.isSpectator()) return;

        Entity attacker = source.getEntity();
        if (attacker == null || attacker == player) return;

        if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && incomingDamage >= 1.0f) {
            int physicalArmorValue = 0;
            for (ItemStack armorPiece : player.getInventory().armor) {
                if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof ArmorItem armorItem) {
                    physicalArmorValue += armorItem.getDefense();
                }
            }

            if (physicalArmorValue > 0) {
                float mitigationPercentage = Math.min(0.8f, physicalArmorValue / 25.0f);
                float blockedDamage = incomingDamage * mitigationPercentage;

                if (blockedDamage > 0) {
                    JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                    if (data.getDefenseLevel() >= 1) {
                        float xpMultiplier = JournalConfig.DEFENSE_ARMOR_XP_PER_DAMAGE.get().floatValue();
                        float finalXp = Math.max(1.0f, (blockedDamage * xpMultiplier)) * getXpMultiplier(player);

                        boolean leveledUp = data.addDefenseXP(finalXp, player);
                        sendSyncData(player, data);

                        if (leveledUp) {
                            player.displayClientMessage(Component.literal("§6§lDefense Level Up! §eYou are now level " + data.getDefenseLevel() + "!"), false);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            clearCombo(player, "defense");
                            checkAndBroadcastMilestone(player, "defense", data.getDefenseLevel());
                        } else {
                            displayComboXp(player, "defense", finalXp, ChatFormatting.GOLD);
                        }
                        sharePartyXP(player, "defense", finalXp);
                    }
                }
            }
        }
    }

    public static float onPlayerDamagePre(LivingEntity target, DamageSource source, float amount) {
        if (source.getEntity() instanceof ServerPlayer attacker && !attacker.level().isClientSide) {
            ItemStack mainHand = attacker.getMainHandItem();
            if (!mainHand.isEmpty()) {
                String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(attacker, itemIdentifier);
                if (failMessage != null) {
                    return 0f;
                }
            }
        }

        if (!target.level().isClientSide && target instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return amount;

            UUID playerId = player.getUUID();
            Entity damageAttacker = source.getEntity();
            long currentTick = player.level().getGameTime();

            if (damageAttacker instanceof Player && damageAttacker != player) {
                lastDamageWasPvP.put(playerId, true);
                lastDamageWasEnvironmental.put(playerId, false);
                lastDamageWasPvE.put(playerId, false);

                long currentTime = System.currentTimeMillis();
                long lastTime = pvpDamageTimer.getOrDefault(playerId, 0L);
                long cooldownMs = JournalConfig.ENV_DAMAGE_COOLDOWN.get() * 1000L;

                if (currentTime - lastTime >= cooldownMs) {
                    pvpXpAllowance.put(playerId, amount);
                    pvpDamageTimer.put(playerId, currentTime);
                }
            } else if (damageAttacker instanceof Mob) {
                lastDamageWasPvE.put(playerId, true);
                lastDamageWasPvP.put(playerId, false);
                lastDamageWasEnvironmental.put(playerId, false);

                float currentPve = pveXpAllowance.getOrDefault(playerId, 0f);
                pveXpAllowance.put(playerId, currentPve + amount);
            } else if (damageAttacker == null) {
                String dmgType = source.getMsgId();

                boolean isValidEnv = dmgType.equals("fall") ||
                        dmgType.equals("lava") ||
                        dmgType.equals("inFire") ||
                        dmgType.equals("onFire") ||
                        dmgType.equals("cactus") ||
                        dmgType.equals("hotFloor") ||
                        dmgType.equals("sweetBerryBush") ||
                        dmgType.equals("magic") ||
                        dmgType.equals("indirectMagic") ||
                        dmgType.equals("freeze") ||
                        dmgType.equals("fallingBlock") ||
                        dmgType.equals("stalagmite");

                if (!isValidEnv) {
                    lastDamageWasEnvironmental.put(playerId, false);
                    lastDamageWasPvP.put(playerId, false);
                    lastDamageWasPvE.put(playerId, false);

                    envXpAllowance.put(playerId, 0f);
                    pvpXpAllowance.put(playerId, 0f);
                    pveXpAllowance.put(playerId, 0f);
                    return amount;
                }

                lastDamageWasEnvironmental.put(playerId, true);
                lastDamageWasPvP.put(playerId, false);
                lastDamageWasPvE.put(playerId, false);

                long cooldownEnd = envDamageCooldown.getOrDefault(playerId, 0L);
                if (currentTick >= cooldownEnd) {
                    float currentAllowance = envXpAllowance.getOrDefault(playerId, 0f);
                    envXpAllowance.put(playerId, currentAllowance + amount);

                    int cooldownSeconds = JournalConfig.ENV_DAMAGE_COOLDOWN.get();
                    envDamageCooldown.put(playerId, currentTick + (cooldownSeconds * 20L));
                }
            }
        }
        return amount;
    }

    public static void onPlayerHeal(ServerPlayer serverPlayer, float healAmount) {
        if (serverPlayer.isCreative() || serverPlayer.isSpectator()) return;

        UUID playerId = serverPlayer.getUUID();

        boolean isEnv = lastDamageWasEnvironmental.getOrDefault(playerId, false);
        boolean isPvP = lastDamageWasPvP.getOrDefault(playerId, false);
        boolean isPvE = lastDamageWasPvE.getOrDefault(playerId, false);

        float validHealAmount = 0f;

        if (isEnv) {
            float allowance = envXpAllowance.getOrDefault(playerId, 0f);
            if (allowance > 0) {
                validHealAmount = Math.min(healAmount, allowance);
                envXpAllowance.put(playerId, allowance - validHealAmount);
            }
        } else if (isPvP) {
            float allowance = pvpXpAllowance.getOrDefault(playerId, 0f);
            if (allowance > 0) {
                validHealAmount = Math.min(healAmount, allowance);
                pvpXpAllowance.put(playerId, allowance - validHealAmount);
            }
        } else if (isPvE) {
            float allowance = pveXpAllowance.getOrDefault(playerId, 0f);
            if (allowance > 0) {
                validHealAmount = Math.min(healAmount, allowance);
                pveXpAllowance.put(playerId, allowance - validHealAmount);
            }
        }

        if (validHealAmount <= 0) return;

        JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        if (data.getVitalityLevel() >= 1) {
            double xpMultiplier = JournalConfig.XP_PER_HEART_HEALED.get();
            float finalXp = (float) (validHealAmount * xpMultiplier) * getXpMultiplier(serverPlayer);

            if (finalXp <= 0) return;

            boolean leveledUp = data.addVitalityXP(finalXp, serverPlayer);
            sendSyncData(serverPlayer, data);

            if (leveledUp) {
                serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                serverPlayer.displayClientMessage(Component.literal("§6§lVitality Level Up! §eYou are now level " + data.getVitalityLevel() + "!"), false);
                clearCombo(serverPlayer, "vitality");
                checkAndBroadcastMilestone(serverPlayer, "vitality", data.getVitalityLevel());
            } else {
                displayComboXp(serverPlayer, "vitality", finalXp, ChatFormatting.GREEN);
            }
            sharePartyXP(serverPlayer, "vitality", finalXp);
        }
    }

    public static void onDamagePost(LivingEntity target, DamageSource source, float damageDealt) {
        if (target.level().isClientSide()) return;

        // Melee Hit XP
        if (source.getEntity() instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator()) {
            if (target instanceof Mob mob && !source.is(DamageTypeTags.IS_PROJECTILE) && damageDealt > 0) {
                MOB_ATTACKERS.computeIfAbsent(mob.getUUID(), k -> new HashSet<>()).add(player.getUUID());
                JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                if (data.getCombatLevel() >= 1) {
                    float finalXp = damageDealt * getXpMultiplier(player);
                    boolean leveledUp = data.addCombatXP(finalXp, player);
                    sendSyncData(player, data);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§6§lCombat Level Up! §eYou are now level " + data.getCombatLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "combat");
                        checkAndBroadcastMilestone(player, "combat", data.getCombatLevel());
                    } else {
                        displayComboXp(player, "combat", finalXp, ChatFormatting.RED);
                    }
                    sharePartyXP(player, "combat", finalXp);
                }
            }
        }

        // Projectile Hit XP
        if (source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof ServerPlayer player) {
            if (!player.isCreative() && !player.isSpectator()) {
                float xpReward = JournalConfig.ARCHERY_XP_PER_HIT.get().floatValue();
                JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                if (data.getArcheryLevel() >= 1) {
                    float finalXp = xpReward * getXpMultiplier(player);
                    boolean leveledUp = data.addArcheryXP(finalXp, player);
                    sendSyncData(player, data);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§6§lArchery Level Up! §eYou are now level " + data.getArcheryLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "archery");
                        checkAndBroadcastMilestone(player, "archery", data.getArcheryLevel());
                    } else {
                        displayComboXp(player, "archery", finalXp, ChatFormatting.AQUA);
                    }
                    sharePartyXP(player, "archery", finalXp);
                }
            }
        }
    }

    public static void onMobKill(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide()) return;

        if (entity instanceof Mob mob) {
            Set<UUID> attackerUUIDs = MOB_ATTACKERS.remove(mob.getUUID());
            if (attackerUUIDs == null) {
                attackerUUIDs = new HashSet<>();
            }
            if (source.getEntity() instanceof ServerPlayer directKiller) {
                attackerUUIDs.add(directKiller.getUUID());
            }

            if (!attackerUUIDs.isEmpty()) {
                float xpAmount = 0f;

                if (JournalConfig.ENABLE_CUSTOM_MOB_XP.get()) {
                    String entityIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
                    List<String> mobXpRegistry = JournalConfig.getAllMobXpValues();

                    for (String entry : mobXpRegistry) {
                        if (entry.contains(";")) {
                            String[] parts = entry.split(";");
                            String configEntityId = parts[0].trim();

                            if (configEntityId.equals(entityIdentifier)) {
                                try {
                                    xpAmount = Float.parseFloat(parts[1].trim());
                                } catch (NumberFormatException ignored) {}
                                break;
                            }
                        }
                    }
                } else {
                    xpAmount = mob.getExperienceReward((ServerLevel) mob.level(), source.getEntity());
                }

                if (xpAmount > 0) {
                    for (UUID playerUUID : attackerUUIDs) {
                        ServerPlayer player = (ServerPlayer) mob.level().getPlayerByUUID(playerUUID);

                        if (player != null && !player.isCreative() && !player.isSpectator()) {
                            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                            if (data.getCombatLevel() >= 1) {
                                float finalXp = xpAmount * getXpMultiplier(player);

                                if (data.addCombatXP(finalXp, player)) {
                                    player.displayClientMessage(Component.literal("§6§lCombat Level Up! §eYou are now level " + data.getCombatLevel() + "!"), false);
                                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    clearCombo(player, "combat");
                                    checkAndBroadcastMilestone(player, "combat", data.getCombatLevel());
                                } else {
                                    displayComboXp(player, "combat", finalXp, ChatFormatting.RED);
                                }

                                sendSyncData(player, data);
                                sharePartyXP(player, "combat", finalXp);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void onBabySpawn(ServerPlayer player, Animal parentA) {
        if (player == null || player.isCreative() || player.isSpectator()) return;

        String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(parentA.getType()).toString();
        List<String> breedingConfig = (List<String>) JournalConfig.FARMING_BREEDING.get();

        for (String entry : breedingConfig) {
            String[] parts = entry.split(";");
            if (parts.length == 3) {
                String[] groupedAnimals = parts[0].split(",");
                for (String a : groupedAnimals) {
                    if (a.trim().equals(animalId)) {
                        float xpReward = Float.parseFloat(parts[2]);
                        if (xpReward > 0) {
                            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                            if (data.getFarmingLevel() >= 1) {
                                float finalXp = xpReward * getXpMultiplier(player);
                                boolean leveledUp = data.addFarmingXP(finalXp, player);
                                sendSyncData(player, data);

                                if (leveledUp) {
                                    player.displayClientMessage(Component.literal("§6§lFarming Level Up! §eYou are now level " + data.getFarmingLevel() + "!"), false);
                                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    clearCombo(player, "farming");
                                    checkAndBroadcastMilestone(player, "farming", data.getFarmingLevel());
                                } else {
                                    displayComboXp(player, "farming", finalXp, ChatFormatting.YELLOW);
                                }
                                sharePartyXP(player, "farming", finalXp);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    private static String getFailedRequirement(Player player, String itemIdentifier, List<String> configList) {
        if (player.isCreative() || player.isSpectator()) {
            return null;
        }

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        if (data.hasResearched(itemIdentifier)) {
            return null;
        }

        for (String restriction : configList) {
            String[] parts = restriction.split(";");
            if (parts.length < 2) continue;

            String[] groupedIds = parts[0].split(",");
            boolean matchFound = false;

            for (String id : groupedIds) {
                if (id.trim().equals(itemIdentifier)) {
                    matchFound = true;
                    break;
                }
            }

            if (matchFound) {
                List<String> failedSkills = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length != 2) continue;

                    String skill = skillReq[0].toLowerCase().trim();
                    int requiredLevel = Integer.parseInt(skillReq[1].trim());
                    int playerLevel = switch (skill) {
                        case "vitality" -> data.getVitalityLevel();
                        case "agility" -> data.getAgilityLevel();
                        case "combat" -> data.getCombatLevel();
                        case "defense" -> data.getDefenseLevel();
                        case "mining" -> data.getMiningLevel();
                        case "farming" -> data.getFarmingLevel();
                        case "smithing" -> data.getSmithingLevel();
                        case "fishing" -> data.getFishingLevel();
                        case "archery" -> data.getArcheryLevel();
                        case "alchemy" -> data.getAlchemyLevel();
                        default -> 0;
                    };

                    if (playerLevel < requiredLevel) {
                        String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                        if (requiredLevel == 1 && !skill.equals("vitality") && !skill.equals("agility") && !skill.equals("combat") && !skill.equals("defense") && !skill.equals("mining") && !skill.equals("farming") && !skill.equals("smithing")) {
                            failedSkills.add(displaySkill + " Class");
                        } else {
                            failedSkills.add(displaySkill + " " + requiredLevel);
                        }
                    }
                }

                if (!failedSkills.isEmpty()) {
                    return getFriendlyName(itemIdentifier) + " requires " + String.join(" & ", failedSkills) + "!";
                }
                return null;
            }
        }
        return null;
    }

    public static String getFailedRequirementFromMap(Player player, String itemIdentifier) {
        if (player.isCreative() || player.isSpectator()) return null;

        Map<String, Integer> requirements = PlayerJournalAPI.getCustomUsage(itemIdentifier);
        if (requirements.isEmpty()) {
            return getFailedRequirement(player, itemIdentifier, getAllConfigUsageRestrictions());
        }

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        if (data.hasResearched(itemIdentifier)) {
            return null;
        }

        List<String> failedSkills = new ArrayList<>();

        for (Map.Entry<String, Integer> req : requirements.entrySet()) {
            String skill = req.getKey().toLowerCase();
            int requiredLevel = req.getValue();
            int playerLevel = switch (skill) {
                case "vitality" -> data.getVitalityLevel();
                case "agility" -> data.getAgilityLevel();
                case "combat" -> data.getCombatLevel();
                case "defense" -> data.getDefenseLevel();
                case "mining" -> data.getMiningLevel();
                case "farming" -> data.getFarmingLevel();
                case "smithing" -> data.getSmithingLevel();
                case "fishing" -> data.getFishingLevel();
                case "archery" -> data.getArcheryLevel();
                case "alchemy" -> data.getAlchemyLevel();
                default -> 0;
            };

            if (playerLevel < requiredLevel) {
                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                if (requiredLevel == 1 && !skill.equals("vitality") && !skill.equals("agility") && !skill.equals("combat") && !skill.equals("defense") && !skill.equals("mining") && !skill.equals("farming") && !skill.equals("smithing")) {
                    failedSkills.add(displaySkill + " Class");
                } else {
                    failedSkills.add(displaySkill + " " + requiredLevel);
                }
            }
        }

        if (!failedSkills.isEmpty()) {
            return getFriendlyName(itemIdentifier) + " requires " + String.join(" & ", failedSkills) + "!";
        }
        return null;
    }

    public static String getInteractRestrictionError(ServerPlayer player, String itemIdentifier) {
        if (player.isCreative() || player.isSpectator()) return null;

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        if (data.hasResearched(itemIdentifier)) {
            return null;
        }

        Map<String, Integer> interactReqs = PlayerJournalAPI.getCustomInteract(itemIdentifier);
        if (interactReqs.isEmpty()) return null;

        List<String> failedSkills = new ArrayList<>();

        for (Map.Entry<String, Integer> req : interactReqs.entrySet()) {
            String skill = req.getKey().toLowerCase();
            int requiredLevel = req.getValue();
            int playerLevel = switch (skill) {
                case "vitality" -> data.getVitalityLevel();
                case "agility" -> data.getAgilityLevel();
                case "combat" -> data.getCombatLevel();
                case "defense" -> data.getDefenseLevel();
                case "mining" -> data.getMiningLevel();
                case "farming" -> data.getFarmingLevel();
                case "smithing" -> data.getSmithingLevel();
                case "fishing" -> data.getFishingLevel();
                case "archery" -> data.getArcheryLevel();
                case "alchemy" -> data.getAlchemyLevel();
                default -> 0;
            };

            if (playerLevel < requiredLevel) {
                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                failedSkills.add(displaySkill + " " + requiredLevel);
            }
        }

        if (!failedSkills.isEmpty()) {
            return getFriendlyName(itemIdentifier) + " requires " + String.join(" & ", failedSkills) + "!";
        }
        return null;
    }

    public static void onItemCrafted(ServerPlayer player, ItemStack craftedItem) {
        if (craftedItem.isEmpty()) return;

        String itemId = BuiltInRegistries.ITEM.getKey(craftedItem.getItem()).toString();
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        if (data.hasResearched(itemId)) {
            return;
        }

        Map<String, Integer> craftReqs = RestrictionDataLoader.getCraftingRestrictions(itemId);
        List<String> failedSkills = new ArrayList<>();

        if (!craftReqs.isEmpty()) {
            for (Map.Entry<String, Integer> req : craftReqs.entrySet()) {
                String skill = req.getKey().toLowerCase();
                int requiredLevel = req.getValue();
                int playerLevel = switch (skill) {
                    case "vitality" -> data.getVitalityLevel();
                    case "agility" -> data.getAgilityLevel();
                    case "combat" -> data.getCombatLevel();
                    case "defense" -> data.getDefenseLevel();
                    case "mining" -> data.getMiningLevel();
                    case "farming" -> data.getFarmingLevel();
                    case "smithing" -> data.getSmithingLevel();
                    case "fishing" -> data.getFishingLevel();
                    case "archery" -> data.getArcheryLevel();
                    case "alchemy" -> data.getAlchemyLevel();
                    default -> 0;
                };

                if (playerLevel < requiredLevel) {
                    failedSkills.add(skill.substring(0, 1).toUpperCase() + skill.substring(1) + " " + requiredLevel);
                }
            }
        }

        String failMessage = null;
        if (!failedSkills.isEmpty()) {
            failMessage = getFriendlyName(itemId) + " requires " + String.join(" & ", failedSkills) + " to craft!";
        } else if (craftReqs.isEmpty()) {
            failMessage = getFailedRequirement(player, itemId, JournalConfig.getAllCraftingRestrictions());
        }

        if (failMessage != null) {
            Item itemToRemove = craftedItem.getItem();
            int amountToRemove = craftedItem.getCount();

            // Clear the crafted stack directly and any matching items moved to cursor/inventory
            craftedItem.setCount(0);
            player.getServer().execute(() -> {
                int remaining = amountToRemove;
                ItemStack cursorStack = player.containerMenu.getCarried();
                if (cursorStack.is(itemToRemove)) {
                    int shrink = Math.min(remaining, cursorStack.getCount());
                    cursorStack.shrink(shrink);
                    remaining -= shrink;
                }
                for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                    ItemStack invStack = player.getInventory().getItem(i);
                    if (invStack.is(itemToRemove)) {
                        int shrinkAmount = Math.min(remaining, invStack.getCount());
                        invStack.shrink(shrinkAmount);
                        remaining -= shrinkAmount;
                    }
                }
                player.inventoryMenu.sendAllDataToRemote();
                player.containerMenu.sendAllDataToRemote();
            });

            player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.displayClientMessage(Component.literal("You lack the knowledge and ruined the materials!")
                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), true);
        } else {
            if (player.isCreative() || player.isSpectator()) return;

            float xpReward = (float) RestrictionDataLoader.getCraftingXp(itemId) * craftedItem.getCount();

            if (xpReward <= 0) {
                for (String restriction : JournalConfig.getAllCraftingRestrictions()) {
                    String[] parts = restriction.split(";");
                    if (parts.length == 3) {
                        String[] groupedIds = parts[0].split(",");
                        for (String id : groupedIds) {
                            if (id.trim().equals(itemId)) {
                                xpReward = Float.parseFloat(parts[2].trim()) * craftedItem.getCount();
                                break;
                            }
                        }
                    }
                }
            }

            if (xpReward > 0 && data.getSmithingLevel() >= 1) {
                float finalXp = xpReward * getXpMultiplier(player);
                boolean leveledUp = data.addSmithingXP(finalXp, player);
                sendSyncData(player, data);

                if (leveledUp) {
                    player.displayClientMessage(Component.literal("§6§lSmithing Level Up! §eYou are now level " + data.getSmithingLevel() + "!"), false);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(player, "smithing");
                    checkAndBroadcastMilestone(player, "smithing", data.getSmithingLevel());
                } else {
                    displayComboXp(player, "smithing", finalXp, ChatFormatting.GRAY);
                }
                sharePartyXP(player, "smithing", finalXp);
            }
        }
    }

    public static void onItemSmelted(ServerPlayer player, ItemStack smeltedItem) {
        if (player.isCreative() || player.isSpectator() || smeltedItem.isEmpty()) return;

        String itemId = BuiltInRegistries.ITEM.getKey(smeltedItem.getItem()).toString();
        float xpReward = (float) RestrictionDataLoader.getCraftingXp(itemId) * smeltedItem.getCount();

        if (xpReward <= 0) {
            List<String> smeltingXpList = (List<String>) JournalConfig.SMITHING_SMELTING.get();
            for (String entry : smeltingXpList) {
                String[] parts = entry.split(";");
                if (parts.length >= 2) {
                    String[] groupedIds = parts[0].split(",");
                    for (String id : groupedIds) {
                        if (id.trim().equals(itemId)) {
                            xpReward = Float.parseFloat(parts[1].trim()) * smeltedItem.getCount();
                            break;
                        }
                    }
                }
            }
        }

        if (xpReward > 0) {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

            if (data.getSmithingLevel() >= 1) {
                float finalXp = xpReward * getXpMultiplier(player);
                boolean leveledUp = data.addSmithingXP(finalXp, player);
                sendSyncData(player, data);

                if (leveledUp) {
                    player.displayClientMessage(Component.literal("§6§lSmithing Level Up! §eYou are now level " + data.getSmithingLevel() + "!"), false);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(player, "smithing");
                    checkAndBroadcastMilestone(player, "smithing", data.getSmithingLevel());
                } else {
                    displayComboXp(player, "smithing", finalXp, ChatFormatting.GRAY);
                }
                sharePartyXP(player, "smithing", finalXp);
            }
        }
    }

    public static boolean onItemInteract(ServerPlayer player, ItemStack handStack) {
        if (!handStack.isEmpty()) {
            String itemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();

            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
            if (data.hasResearched(itemId)) {
                return false;
            }

            String failMessage = getInteractRestrictionError(player, itemId);
            if (failMessage == null) {
                failMessage = getFailedRequirementFromMap(player, itemId);
            }

            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage).withStyle(ChatFormatting.RED), true);
                player.getServer().execute(() -> {
                    player.inventoryMenu.sendAllDataToRemote();
                    player.containerMenu.sendAllDataToRemote();
                });
                return true;
            }
        }
        return false;
    }

    public static boolean onLeftClickBlock(ServerPlayer player, Level level, BlockPos pos, ItemStack handStack) {
        if (player.isCreative() || player.isSpectator()) return false;

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        String blockIdentifier = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
        String itemId = handStack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();

        if (data.hasResearched(blockIdentifier) || (!itemId.isEmpty() && data.hasResearched(itemId))) {
            return false;
        }

        String toolError = null;
        if (!handStack.isEmpty()) {
            String toolFailMessage = getFailedRequirementFromMap(player, itemId);
            if (toolFailMessage != null) {
                toolError = toolFailMessage;
            }
        }

        String blockError = null;
        boolean handledByApi = false;

        Map<String, Integer> apiBlockReqs = PlayerJournalAPI.getCustomBlockUsage(blockIdentifier);

        if (!apiBlockReqs.isEmpty()) {
            handledByApi = true;
            for (Map.Entry<String, Integer> req : apiBlockReqs.entrySet()) {
                String skill = req.getKey().toLowerCase();
                int requiredLevel = req.getValue();

                int playerLevel = switch (skill) {
                    case "mining" -> data.getMiningLevel();
                    case "farming" -> data.getFarmingLevel();
                    case "smithing" -> data.getSmithingLevel();
                    default -> 0;
                };

                if (playerLevel < requiredLevel) {
                    String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                    blockError = getFriendlyName(blockIdentifier) + " requires " + displaySkill + " " + requiredLevel + "!";
                    break;
                }
            }
        }

        if (!handledByApi) {
            List<String> blockRestrictions = JournalConfig.getAllBlockRestrictions();
            for (String restriction : blockRestrictions) {
                String[] parts = restriction.split(";");
                if (parts.length >= 2) {
                    String[] groupedBlocks = parts[0].split(",");
                    boolean blockMatches = false;
                    for (String b : groupedBlocks) {
                        if (b.trim().equals(blockIdentifier)) {
                            blockMatches = true;
                            break;
                        }
                    }

                    if (blockMatches) {
                        String[] skillReq = parts[1].split(":");
                        if (skillReq.length == 2) {
                            String skill = skillReq[0].toLowerCase();
                            int requiredLevel = Integer.parseInt(skillReq[1]);

                            int playerLevel = switch (skill) {
                                case "mining" -> data.getMiningLevel();
                                case "farming" -> data.getFarmingLevel();
                                case "smithing" -> data.getSmithingLevel();
                                default -> 0;
                            };

                            if (playerLevel < requiredLevel) {
                                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                                blockError = getFriendlyName(blockIdentifier) + " requires " + displaySkill + " " + requiredLevel + "!";
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (toolError != null || blockError != null) {
            String combinedMessage;
            if (toolError != null && blockError != null) {
                combinedMessage = blockError + " & " + toolError;
            } else if (blockError != null) {
                combinedMessage = blockError;
            } else {
                combinedMessage = toolError;
            }

            player.displayClientMessage(Component.literal(combinedMessage).withStyle(ChatFormatting.RED), true);
            return true;
        }
        return false;
    }

    public static boolean onPlayerAttack(ServerPlayer serverPlayer) {
        ItemStack mainHand = serverPlayer.getMainHandItem();
        if (!mainHand.isEmpty()) {
            String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
            String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

            if (failMessage != null) {
                serverPlayer.displayClientMessage(Component.literal(failMessage), true);
                return true;
            }
        }
        return false;
    }

    public static boolean onBlockInteract(ServerPlayer player, Level level, BlockPos pos, ItemStack handStack) {
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        String blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
        String itemId = handStack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();

        if (data.hasResearched(blockId) || (!itemId.isEmpty() && data.hasResearched(itemId))) {
            return false;
        }

        boolean activatingBlock = !player.isCrouching() && isInteractiveBlock(level.getBlockState(pos), level, pos);

        if (!activatingBlock && !handStack.isEmpty()) {
            String failMessage = getInteractRestrictionError(player, itemId);
            if (failMessage == null) {
                failMessage = getFailedRequirementFromMap(player, itemId);
            }

            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage).withStyle(ChatFormatting.RED), true);
                player.getServer().execute(() -> {
                    player.inventoryMenu.sendAllDataToRemote();
                    player.containerMenu.sendAllDataToRemote();
                });
                return true;
            }
        }

        String apiFailMessage = getInteractRestrictionError(player, blockId);
        if (apiFailMessage != null) {
            player.displayClientMessage(Component.literal(apiFailMessage).withStyle(ChatFormatting.RED), true);
            return true;
        }

        List<String> utilityRestrictions = JournalConfig.getUtilityBlockRestrictions();
        String failMessage = getFailedRequirement(player, blockId, utilityRestrictions);
        if (failMessage != null) {
            player.displayClientMessage(Component.literal(failMessage).withStyle(ChatFormatting.RED), true);
            return true;
        }
        return false;
    }

    private static boolean isInteractiveBlock(BlockState state, Level level, BlockPos pos) {
        if (state.getMenuProvider(level, pos) != null) return true;

        Block block = state.getBlock();
        return block instanceof DoorBlock ||
                block instanceof TrapDoorBlock ||
                block instanceof FenceGateBlock ||
                block instanceof LeverBlock ||
                block instanceof ButtonBlock ||
                block instanceof BedBlock ||
                block instanceof AnvilBlock ||
                block instanceof CraftingTableBlock ||
                block instanceof EnchantingTableBlock ||
                block instanceof EnderChestBlock ||
                block instanceof BellBlock ||
                block instanceof DiodeBlock;
    }

    public static boolean onEntityInteract(ServerPlayer player, Entity target, ItemStack handStack) {
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        String targetId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString();
        String itemId = handStack.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();

        if (data.hasResearched(targetId) || (!itemId.isEmpty() && data.hasResearched(itemId))) {
            return false;
        }

        if (player.isCrouching() && target instanceof ServerPlayer targetPlayer) {
            if (player.getMainHandItem().isEmpty()) {
                JournalProgressionData targetData = targetPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                InspectJournalPayload payload = new InspectJournalPayload(
                        targetPlayer.getName().getString(),
                        targetData.getVitalityLevel(),
                        targetData.getAgilityLevel(),
                        targetData.getCombatLevel(),
                        targetData.getDefenseLevel(),
                        targetData.getFarmingLevel(),
                        targetData.getMiningLevel(),
                        targetData.getSmithingLevel(),
                        targetData.getArcheryLevel(),
                        targetData.getFishingLevel(),
                        targetData.getAlchemyLevel()
                );
                ServerPlayNetworking.send(player, payload);
                return true;
            }
        }

        String entityFailMessage = getInteractRestrictionError(player, targetId);
        if (entityFailMessage != null) {
            player.displayClientMessage(Component.literal(entityFailMessage).withStyle(ChatFormatting.RED), true);
            return true;
        }

        boolean isDisplayOrMerchant = target instanceof ItemFrame ||
                target instanceof ArmorStand ||
                target instanceof Villager ||
                target instanceof WanderingTrader;

        if (!isDisplayOrMerchant && !handStack.isEmpty()) {
            String failMessage = getInteractRestrictionError(player, itemId);
            if (failMessage == null) {
                failMessage = getFailedRequirementFromMap(player, itemId);
            }

            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage).withStyle(ChatFormatting.RED), true);
                return true;
            }
        }

        if (target instanceof Villager villager) {
            String professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString();
            List<String> villagerRestrictions = (List<String>) JournalConfig.VILLAGER_RESTRICTIONS.get();

            String failMessage = getFailedRequirement(player, professionId, villagerRestrictions);
            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage), true);
                return true;
            }
        }

        if (target instanceof Animal animal && animal.isFood(handStack)) {
            String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).toString();
            List<String> breedingConfig = (List<String>) JournalConfig.FARMING_BREEDING.get();

            String failMessage = getFailedRequirement(player, animalId, breedingConfig);
            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage), true);
                return true;
            }
        }
        return false;
    }

    public static boolean onItemUseStart(ServerPlayer serverPlayer, ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemIdentifier = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        if (data.hasResearched(itemIdentifier)) {
            return false;
        }

        String potionIdentifier = "";
        if (stack.getItem() instanceof PotionItem) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                potionIdentifier = "potion:" + contents.potion().get().unwrapKey().get().location().toString();
                if (data.hasResearched(potionIdentifier)) {
                    return false;
                }
            }
        }

        String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

        if (failMessage == null && !potionIdentifier.isEmpty()) {
            List<String> useRestrictions = new ArrayList<>((List<String>) JournalConfig.POTION_RESTRICTIONS.get());
            failMessage = getFailedRequirement(serverPlayer, potionIdentifier, useRestrictions);
        }

        if (failMessage != null) {
            serverPlayer.displayClientMessage(Component.literal(failMessage), true);
            return true;
        }
        return false;
    }

    public static void onItemUsed(ServerPlayer player, ItemStack stack) {
        if (player.isCreative() || player.isSpectator()) return;

        if (stack.getItem() instanceof PotionItem) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                String potionId = contents.potion().get().unwrapKey().get().location().toString();

                if (potionId.equals("minecraft:water") ||
                        potionId.equals("minecraft:mundane") ||
                        potionId.equals("minecraft:thick") ||
                        potionId.equals("minecraft:awkward")) {
                    return;
                }
            }

            float xpGained = JournalConfig.ALCHEMY_DRINK_XP.get().floatValue();

            if (xpGained > 0) {
                JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                if (data.getAlchemyLevel() >= 1) {
                    float finalXp = xpGained * getXpMultiplier(player);
                    boolean leveledUp = data.addAlchemyXP(finalXp, player);
                    sendSyncData(player, data);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "alchemy");
                        checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                    } else {
                        displayComboXp(player, "alchemy", finalXp, ChatFormatting.LIGHT_PURPLE);
                    }
                    sharePartyXP(player, "alchemy", finalXp);
                }
            }
        }
    }

    public static void onPotionBrewed(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) return;

        float xpGained = JournalConfig.ALCHEMY_BREW_XP.get().floatValue();

        if (xpGained > 0) {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

            if (data.getAlchemyLevel() >= 1) {
                float finalXp = xpGained * getXpMultiplier(player);
                boolean leveledUp = data.addAlchemyXP(finalXp, player);
                sendSyncData(player, data);

                if (leveledUp) {
                    player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(player, "alchemy");
                    checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                } else {
                    displayComboXp(player, "alchemy", finalXp, ChatFormatting.LIGHT_PURPLE);
                }
                sharePartyXP(player, "alchemy", finalXp);
            }
        }
    }

    private static boolean handleAllowDeathTotemCheck(ServerPlayer serverPlayer, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = serverPlayer.getItemInHand(hand);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                String itemIdentifier = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
                if (!data.hasResearched(itemIdentifier)) {
                    String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);
                    if (failMessage != null) {
                        serverPlayer.displayClientMessage(Component.literal(failMessage), true);
                        return false; // Disallow totem usage via Mixin check below
                    }
                }
            }
        }
        return true;
    }

    public static boolean canUseTotem(ServerPlayer serverPlayer, ItemStack totemStack) {
        if (totemStack.isEmpty()) return true;
        String itemIdentifier = BuiltInRegistries.ITEM.getKey(totemStack.getItem()).toString();
        JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        if (data.hasResearched(itemIdentifier)) return true;

        String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);
        if (failMessage != null) {
            serverPlayer.displayClientMessage(Component.literal(failMessage), true);
            return false;
        }
        return true;
    }

    public static boolean onBlockBreak(ServerPlayer serverPlayer, BlockState state) {
        if (serverPlayer.isCreative() || serverPlayer.isSpectator()) return false;

        JournalProgressionData data = serverPlayer.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        String blockIdentifier = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();


        boolean isResearched = data != null && data.hasResearched(blockIdentifier);


        String toolError = null;
        ItemStack mainHand = serverPlayer.getMainHandItem();
        if (!mainHand.isEmpty()) {
            String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
            String toolFailMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);
            if (toolFailMessage != null) {
                toolError = toolFailMessage;
            }
        }

        // 2. Evaluate Block
        String blockError = null;
        float pendingXpReward = 0f;
        String pendingXpSkill = null;

        boolean handledByApi = false;

        Map<String, Integer> apiBlockReqs = PlayerJournalAPI.getCustomBlockUsage(blockIdentifier);

        if (!apiBlockReqs.isEmpty()) {
            handledByApi = true;
            for (Map.Entry<String, Integer> req : apiBlockReqs.entrySet()) {
                String skill = req.getKey().toLowerCase();
                int requiredLevel = req.getValue();

                int playerLevel = switch (skill) {
                    case "mining" -> data.getMiningLevel();
                    case "farming" -> data.getFarmingLevel();
                    case "smithing" -> data.getSmithingLevel();
                    default -> 0;
                };

                // --- NEW: Bypass the level restriction if researched ---
                if (playerLevel < requiredLevel && !isResearched) {
                    String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                    blockError = getFriendlyName(blockIdentifier) + " requires " + displaySkill + " " + requiredLevel + "!";
                    break;
                } else {
                    // This block executes if they have the level OR if they researched it
                    float xpReward = PlayerJournalAPI.getCustomBlockXp(blockIdentifier);
                    if (xpReward > 0) {
                        if (!skill.equals("farming") || isHarvestable(state)) {
                            pendingXpReward = xpReward;
                            pendingXpSkill = skill;
                        }
                    }
                }
            }
        }

        // 3. Fallback to Config Restrictions if API didn't handle it
        if (!handledByApi) {
            List<String> blockRestrictions = JournalConfig.getAllBlockRestrictions();
            for (String restriction : blockRestrictions) {
                String[] parts = restriction.split(";");
                if (parts.length >= 2) {
                    String[] groupedBlocks = parts[0].split(",");
                    boolean blockMatches = false;
                    for (String b : groupedBlocks) {
                        if (b.trim().equals(blockIdentifier)) {
                            blockMatches = true;
                            break;
                        }
                    }

                    if (blockMatches) {
                        String[] skillReq = parts[1].split(":");
                        if (skillReq.length == 2) {
                            String skill = skillReq[0].toLowerCase();
                            int requiredLevel = Integer.parseInt(skillReq[1]);

                            int playerLevel = switch (skill) {
                                case "mining" -> data.getMiningLevel();
                                case "farming" -> data.getFarmingLevel();
                                case "smithing" -> data.getSmithingLevel();
                                default -> 0;
                            };

                            // --- NEW: Bypass the level restriction if researched ---
                            if (playerLevel < requiredLevel && !isResearched) {
                                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                                blockError = getFriendlyName(blockIdentifier) + " requires " + displaySkill + " " + requiredLevel + "!";
                                break;
                            } else if (parts.length == 3) {
                                // This block executes if they have the level OR if they researched it
                                float xpReward = Float.parseFloat(parts[2]);
                                if (xpReward > 0) {
                                    if (!skill.equals("farming") || isHarvestable(state)) {
                                        pendingXpReward = xpReward;
                                        pendingXpSkill = skill;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        // 4. DISPLAY ERRORS (If Tool OR Block failed)
        if (toolError != null || blockError != null) {
            String combinedMessage;

            if (toolError != null && blockError != null) {
                combinedMessage = blockError + " & " + toolError;
            } else if (blockError != null) {
                combinedMessage = blockError;
            } else {
                combinedMessage = toolError;
            }

            serverPlayer.displayClientMessage(Component.literal(combinedMessage).withStyle(ChatFormatting.RED), true);
            return true; // Returning true cancels the break in PlayerBlockBreakEvents.BEFORE
        }

        // 5. GRANT XP (Only runs if BOTH the tool and block are valid)
        if (pendingXpReward > 0 && pendingXpSkill != null) {
            float finalXp = pendingXpReward * getXpMultiplier(serverPlayer);
            boolean leveledUp = false;
            boolean canEarnXp = false;

            if (pendingXpSkill.equals("mining") && data.getMiningLevel() >= 1) {
                leveledUp = data.addMiningXP(finalXp, serverPlayer);
                canEarnXp = true;
            } else if (pendingXpSkill.equals("farming") && data.getFarmingLevel() >= 1) {
                leveledUp = data.addFarmingXP(finalXp, serverPlayer);
                canEarnXp = true;
            } else if (pendingXpSkill.equals("smithing") && data.getSmithingLevel() >= 1) {
                leveledUp = data.addSmithingXP(finalXp, serverPlayer);
                canEarnXp = true;
            }

            if (canEarnXp) {
                sendSyncData(serverPlayer, data);

                if (leveledUp) {
                    String displaySkill = pendingXpSkill.substring(0, 1).toUpperCase() + pendingXpSkill.substring(1);
                    int levelDisplay = switch (pendingXpSkill) {
                        case "mining" -> data.getMiningLevel();
                        case "farming" -> data.getFarmingLevel();
                        case "smithing" -> data.getSmithingLevel();
                        default -> 0;
                    };
                    serverPlayer.displayClientMessage(Component.literal("§6§l" + displaySkill + " Level Up! §eYou are now level " + levelDisplay + "!"), false);
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(serverPlayer, pendingXpSkill);
                    checkAndBroadcastMilestone(serverPlayer, pendingXpSkill, levelDisplay);
                } else {
                    displayComboXp(serverPlayer, pendingXpSkill, finalXp, pendingXpSkill.equals("mining") ? ChatFormatting.AQUA : (pendingXpSkill.equals("farming") ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
                }
                sharePartyXP(serverPlayer, pendingXpSkill, finalXp);
            }
        }
        return false; // Allow the block break to proceed
    }

    public static boolean onWanderingTraderInteract(ServerPlayer player, Entity target) {
        if (player.isCreative() || player.isSpectator()) return false;

        if (target instanceof WanderingTrader) {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
            boolean allUnlocked = data.getFarmingLevel() >= 1 &&
                    data.getMiningLevel() >= 1 &&
                    data.getSmithingLevel() >= 1 &&
                    data.hasArchery() &&
                    data.hasFishing() &&
                    data.hasAlchemy();

            if (!allUnlocked) {
                player.displayClientMessage(Component.literal("Requires All Classes Unlocked!").withStyle(ChatFormatting.RED), true);
                return true;
            }
        }
        return false;
    }

    public static void onPlayerJoinWorld(ServerPlayer player) {
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        data.syncPlayerHealth(player);

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    public static void onPlayerClone(ServerPlayer player, boolean wasDeath) {
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        data.syncPlayerHealth(player);

        if (wasDeath) {
            player.setHealth(player.getMaxHealth());
        }
    }

    public static void onPlayerFish(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) return;

        float xpReward = JournalConfig.FISHING_XP_PER_CATCH.get().floatValue();
        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        if (data.getFishingLevel() >= 1) {
            float finalXp = xpReward * getXpMultiplier(player);
            boolean leveledUp = data.addFishingXP(finalXp, player);
            sendSyncData(player, data);

            if (leveledUp) {
                player.displayClientMessage(Component.literal("§9§lFishing Level Up! §bYou are now level " + data.getFishingLevel() + "!"), false);
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                clearCombo(player, "fishing");
                checkAndBroadcastMilestone(player, "fishing", data.getFishingLevel());
            } else {
                displayComboXp(player, "fishing", finalXp, ChatFormatting.BLUE);
            }
            sharePartyXP(player, "fishing", finalXp);
        }
    }

    public static boolean onAnvilUpdate(Player player, ItemStack right) {
        if (player == null || player.isCreative() || player.isSpectator() || right.isEmpty()) return false;

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
        var enchantReg = player.level().registryAccess().registry(Registries.ENCHANTMENT).orElse(null);
        if (enchantReg == null) return false;

        ItemEnchantments rightEnchants = right.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments rightStored = right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

        try {
            List<String> enchantmentRestrictions = (List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get();

            for (String restriction : enchantmentRestrictions) {
                String[] parts = restriction.split(";");
                if (parts.length >= 2) {
                    ResourceLocation loc = ResourceLocation.parse(parts[0].trim());
                    var holderOpt = enchantReg.getHolder(loc);

                    if (holderOpt.isPresent()) {
                        var holder = holderOpt.get();
                        int rightLevel = Math.max(rightEnchants.getLevel(holder), rightStored.getLevel(holder));

                        if (rightLevel > 0) {
                            String[] reqs = parts[1].split(":");
                            if (reqs.length == 2 && reqs[0].equalsIgnoreCase("alchemy")) {
                                int reqLevel = Integer.parseInt(reqs[1].trim());
                                if (data.getAlchemyLevel() < reqLevel) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static void onPlayerTick(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator()) return;

        // 1. Party Sync
        if (player.tickCount % 20 == 0) {
            PartyManager.syncPartyForPlayer(player);
        }

        // 2. Vanilla Armor Enforcer
        for (EquipmentSlot equipSlot : EquipmentSlot.values()) {
            if (equipSlot.isArmor()) {
                ItemStack armorStack = player.getItemBySlot(equipSlot);
                if (!armorStack.isEmpty()) {
                    String id = BuiltInRegistries.ITEM.getKey(armorStack.getItem()).toString();
                    String failMsg = getFailedRequirementFromMap(player, id);
                    if (failMsg != null) {
                        player.setItemSlot(equipSlot, ItemStack.EMPTY);
                        if (!player.getInventory().add(armorStack.copy())) {
                            player.drop(armorStack.copy(), false);
                        }
                        player.inventoryMenu.sendAllDataToRemote();
                        player.displayClientMessage(Component.literal(failMsg), true);
                    }
                }
            }
        }

        // 3. Accessories Enforcer
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            AccessoriesCompat.enforceTick(player);
        }

        // 4. Agility XP Tracker
        if (player.tickCount % 5 == 0) {
            UUID id = player.getUUID();
            Vec3 currentPos = player.position();

            boolean isInBoat = player.getVehicle() instanceof Boat;
            if ((player.isPassenger() && !isInBoat) || player.getAbilities().flying || player.isFallFlying()) {
                lastPlayerPositions.put(id, currentPos);
                return;
            }

            if (lastPlayerPositions.containsKey(id)) {
                Vec3 lastPos = lastPlayerPositions.get(id);

                double dx = currentPos.x - lastPos.x;
                double dz = currentPos.z - lastPos.z;
                double distanceMoved = Math.sqrt(dx * dx + dz * dz);

                if (distanceMoved > 0.1 && distanceMoved < 20.0) {
                    float xpGained = (float) (distanceMoved * JournalConfig.AGILITY_XP_PER_BLOCK.get());
                    float totalPending = pendingAgilityXp.getOrDefault(id, 0f) + xpGained;

                    if (totalPending >= 10.0f) {
                        float finalXp = totalPending * getXpMultiplier(player);
                        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

                        if (data.getAgilityLevel() >= 1) {
                            boolean leveledUp = data.addAgilityXP(finalXp, player);
                            sendSyncData(player, data);

                            if (leveledUp) {
                                player.displayClientMessage(Component.literal("§a§lAgility Level Up! §2You are now level " + data.getAgilityLevel() + "!"), false);
                                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                clearCombo(player, "agility");
                                checkAndBroadcastMilestone(player, "agility", data.getAgilityLevel());
                            } else {
                                displayComboXp(player, "agility", finalXp, ChatFormatting.GREEN);
                            }
                            sharePartyXP(player, "agility", finalXp);
                        }
                        pendingAgilityXp.put(id, 0f);
                    } else {
                        pendingAgilityXp.put(id, totalPending);
                    }
                }
            }
            lastPlayerPositions.put(id, currentPos);
        }
    }

    public static void onPlayerXpLevelChange(ServerPlayer player, int levelsGained) {
        if (player.isCreative() || player.isSpectator()) return;

        if (levelsGained > 0) {
            if (System.currentTimeMillis() < xpPageCooldown.getOrDefault(player.getUUID(), 0L)) {
                return;
            }

            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
            boolean gotPage = false;
            int pagesFound = 0;

            for (int i = 0; i < levelsGained; i++) {
                if (player.getRandom().nextFloat() <= 0.30f) {
                    pagesFound++;
                    gotPage = true;
                }
            }

            if (gotPage) {
                data.setTornPages(data.getTornPages() + pagesFound);

                String msg = pagesFound > 1 ? "§e You discovered " + pagesFound + " Pages from your experiences!" : "§e You discovered a Page from your experiences!";
                player.displayClientMessage(Component.literal(msg), false);
                player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

                sendSyncData(player, data);
            }
        }
    }

    public static void syncConfigToPlayer(ServerPlayer serverPlayer) {
        SyncJournalConfigPayload configPayload = new SyncJournalConfigPayload(
                (List<String>) JournalConfig.ARMOR_RESTRICTIONS.get(),
                (List<String>) JournalConfig.POTION_RESTRICTIONS.get(),
                RestrictionDataLoader.exportDatapackAndConfig(),
                (List<String>) JournalConfig.TIDE_ITEMS.get(),
                (List<String>) JournalConfig.GLIDERS_ITEMS.get(),
                (List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get(),
                (List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get(),
                (List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get(),
                (List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get(),
                (List<String>) JournalConfig.ALCHEMY_UTILITIES.get(),
                (List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get(),
                (List<String>) JournalConfig.AGILITY_MOUNTS.get(),
                RestrictionDataLoader.exportCraftingDatapackAndConfig(),
                JournalConfig.XP_BASE_REQUIREMENT.get(), JournalConfig.XP_MULTIPLIER.get(),
                JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get(), JournalConfig.AGILITY_XP_MULTIPLIER.get(),
                JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get(), JournalConfig.COMBAT_XP_MULTIPLIER.get(),
                JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get(), JournalConfig.DEFENSE_XP_MULTIPLIER.get(),
                JournalConfig.FARMING_XP_BASE_REQUIREMENT.get(), JournalConfig.FARMING_XP_MULTIPLIER.get(),
                JournalConfig.MINING_XP_BASE_REQUIREMENT.get(), JournalConfig.MINING_XP_MULTIPLIER.get(),
                JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get(), JournalConfig.SMITHING_XP_MULTIPLIER.get(),
                JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get(), JournalConfig.ARCHERY_XP_MULTIPLIER.get(),
                JournalConfig.FISHING_XP_BASE_REQUIREMENT.get(), JournalConfig.FISHING_XP_MULTIPLIER.get(),
                JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get(), JournalConfig.ALCHEMY_XP_MULTIPLIER.get()
        );
        ServerPlayNetworking.send(serverPlayer, configPayload);
    }

    public static boolean onBookConsumeForMagic(ServerPlayer player, ItemStack handStack) {
        if (player.isCreative() || player.isSpectator() || !player.isCrouching()) return false;

        boolean isNormalBook = handStack.is(Items.BOOK);
        boolean isEnchantedBook = handStack.is(Items.ENCHANTED_BOOK);

        if (isNormalBook || isEnchantedBook) {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

            if (data.getAlchemyLevel() >= 1) {
                float xpGained = 0f;

                if (isNormalBook) {
                    xpGained = JournalConfig.ALCHEMY_BOOK_XP.get().floatValue();
                } else {
                    xpGained = JournalConfig.ALCHEMY_ENCHANTED_BOOK_BASE_XP.get().floatValue();
                    float levelMultiplier = JournalConfig.ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER.get().floatValue();

                    ItemEnchantments storedEnchants = handStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                    for (var holder : storedEnchants.keySet()) {
                        int enchantLevel = storedEnchants.getLevel(holder);
                        xpGained += (enchantLevel * levelMultiplier);
                    }
                }

                if (xpGained <= 0) return false;

                float finalXp = xpGained * getXpMultiplier(player);
                handStack.shrink(1);

                player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                boolean leveledUp = data.addAlchemyXP(finalXp, player);
                sendSyncData(player, data);

                if (leveledUp) {
                    player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(player, "alchemy");
                    checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                } else {
                    displayComboXp(player, "alchemy", finalXp, ChatFormatting.LIGHT_PURPLE);
                }
                sharePartyXP(player, "alchemy", finalXp);
                return true;
            }
        }
        return false;
    }

    public static void sharePartyXP(ServerPlayer earner, String skill, float totalXp) {
        float sharedXp = totalXp * 0.10f;
        if (sharedXp <= 0) return;

        List<UUID> partyUUIDs = PartyManager.getPartyMembers(earner.getUUID());
        if (partyUUIDs == null || partyUUIDs.size() <= 1) return;

        for (UUID memberId : partyUUIDs) {
            if (memberId.equals(earner.getUUID())) continue;

            ServerPlayer member = earner.getServer().getPlayerList().getPlayer(memberId);
            if (member != null && !member.isCreative() && !member.isSpectator()) {
                if (earner.distanceToSqr(member) > 10000) continue;

                float buffedSharedXp = sharedXp * getXpMultiplier(member);
                JournalProgressionData data = member.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
                boolean leveledUp = false;
                boolean canEarnXp = false;

                switch (skill) {
                    case "vitality" -> { if (data.getVitalityLevel() >= 1) { leveledUp = data.addVitalityXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "agility" -> { if (data.getAgilityLevel() >= 1) { leveledUp = data.addAgilityXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "combat" -> { if (data.getCombatLevel() >= 1) { leveledUp = data.addCombatXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "defense" -> { if (data.getDefenseLevel() >= 1) { leveledUp = data.addDefenseXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "mining" -> { if (data.getMiningLevel() >= 1) { leveledUp = data.addMiningXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "farming" -> { if (data.getFarmingLevel() >= 1) { leveledUp = data.addFarmingXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "smithing" -> { if (data.getSmithingLevel() >= 1) { leveledUp = data.addSmithingXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "archery" -> { if (data.getArcheryLevel() >= 1) { leveledUp = data.addArcheryXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "fishing" -> { if (data.getFishingLevel() >= 1) { leveledUp = data.addFishingXP(buffedSharedXp, member); canEarnXp = true; } }
                    case "alchemy" -> { if (data.getAlchemyLevel() >= 1) { leveledUp = data.addAlchemyXP(buffedSharedXp, member); canEarnXp = true; } }
                }

                if (canEarnXp) {
                    sendSyncData(member, data);

                    if (leveledUp) {
                        member.level().playSound(null, member.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                        int levelDisplay = switch (skill) {
                            case "vitality" -> data.getVitalityLevel();
                            case "agility" -> data.getAgilityLevel();
                            case "combat" -> data.getCombatLevel();
                            case "defense" -> data.getDefenseLevel();
                            case "mining" -> data.getMiningLevel();
                            case "farming" -> data.getFarmingLevel();
                            case "smithing" -> data.getSmithingLevel();
                            case "archery" -> data.getArcheryLevel();
                            case "fishing" -> data.getFishingLevel();
                            case "alchemy" -> data.getAlchemyLevel();
                            default -> 0;
                        };

                        ChatFormatting color = switch (skill) {
                            case "vitality" -> ChatFormatting.RED;
                            case "combat" -> ChatFormatting.DARK_RED;
                            case "defense" -> ChatFormatting.GOLD;
                            case "archery" -> ChatFormatting.AQUA;
                            case "mining" -> ChatFormatting.GRAY;
                            case "farming" -> ChatFormatting.GREEN;
                            case "smithing" -> ChatFormatting.DARK_GRAY;
                            case "fishing" -> ChatFormatting.BLUE;
                            case "alchemy" -> ChatFormatting.LIGHT_PURPLE;
                            case "agility" -> ChatFormatting.DARK_GREEN;
                            default -> ChatFormatting.WHITE;
                        };

                        member.displayClientMessage(Component.literal("§l" + displaySkill + " Level Up! §rYou are now level " + levelDisplay + "!").withStyle(color), false);
                        clearCombo(member, skill);
                        checkAndBroadcastMilestone(member, skill, levelDisplay);
                    } else {
                        displayComboXp(member, skill, buffedSharedXp, ChatFormatting.WHITE);
                    }
                }
            }
        }
    }

    public static List<String> getAllConfigUsageRestrictions() {
        List<String> all = new ArrayList<>();
        all.addAll((List<String>) JournalConfig.ARMOR_RESTRICTIONS.get());
        all.addAll((List<String>) JournalConfig.POTION_RESTRICTIONS.get());
        all.addAll(JournalConfig.getAllItemRestrictions());
        all.addAll((List<String>) JournalConfig.TIDE_ITEMS.get());
        all.addAll((List<String>) JournalConfig.GLIDERS_ITEMS.get());
        all.addAll((List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get());
        all.addAll((List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get());
        all.addAll((List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get());
        all.addAll((List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get());
        all.addAll((List<String>) JournalConfig.ALCHEMY_UTILITIES.get());
        all.addAll((List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get());
        return all;
    }

    private static String getFriendlyName(String id) {
        try {
            ResourceLocation loc = ResourceLocation.parse(id.trim());

            if (BuiltInRegistries.ITEM.containsKey(loc) && BuiltInRegistries.ITEM.get(loc) != Items.AIR) {
                return BuiltInRegistries.ITEM.get(loc).getDescription().getString();
            }
            if (BuiltInRegistries.BLOCK.containsKey(loc) && BuiltInRegistries.BLOCK.get(loc) != Blocks.AIR) {
                return BuiltInRegistries.BLOCK.get(loc).getName().getString();
            }
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(loc)) {
                return BuiltInRegistries.ENTITY_TYPE.get(loc).getDescription().getString();
            }
        } catch (Exception ignored) {}

        String[] parts = id.split(":");
        String namePart = parts.length > 1 ? parts[1] : id;
        String[] words = namePart.split("_");
        StringBuilder friendlyName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                friendlyName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
            }
        }
        return friendlyName.toString().trim();
    }
}