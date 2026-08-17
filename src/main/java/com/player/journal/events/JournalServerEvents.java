package com.player.journal.events;

import com.player.journal.config.JournalConfig;
import com.player.journal.data.JournalProgressionData;
import com.player.journal.data.RestrictionDataLoader;
import com.player.journal.network.SyncJournalConfigPayload;
import com.player.journal.network.SyncJournalDataPayload;
import com.player.journal.party.PartyManager;
import com.player.journal.registry.ModAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import com.player.journal.network.InspectJournalPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = "playerjournal")
public class JournalServerEvents {

    private static final Map<UUID, Boolean> lastDamageWasEnvironmental = new HashMap<>();
    private static final Map<UUID, Float> envXpAllowance = new HashMap<>();
    private static final Map<UUID, Long> envDamageCooldown = new HashMap<>();
    private static final Map<UUID, net.minecraft.world.phys.Vec3> lastPlayerPositions = new HashMap<>();
    private static final Map<UUID, Float> pendingAgilityXp = new HashMap<>();

    public static final java.util.Map<java.util.UUID, Long> pvpDamageTimer = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, Boolean> lastDamageWasPvP = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, Float> pvpXpAllowance = new java.util.HashMap<>();

    public static final java.util.Map<java.util.UUID, Boolean> lastDamageWasPvE = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, Float> pveXpAllowance = new java.util.HashMap<>();

    private static final Map<UUID, Map<String, Float>> xpCombos = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> lastXpTicks = new HashMap<>();

    private static final Map<UUID, Set<UUID>> MOB_ATTACKERS = new HashMap<>();


    private static int globalBuffTimer = 0;


    public static float getXpMultiplier(ServerPlayer player) {
        float multiplier = 1.0f;
        try {

            if (player.hasEffect(com.player.journal.registry.ModEffects.PERSONAL_XP_BUFF)) {
                multiplier += 0.50f;
            }

            if (player.hasEffect(com.player.journal.registry.ModEffects.GLOBAL_XP_BUFF)) {
                multiplier += 0.50f;
            }

            if (player.hasEffect(com.player.journal.registry.ModEffects.PARTY_XP_BUFF)) {
                multiplier += 0.50f;
            } else {
                List<UUID> partyUUIDs = PartyManager.getPartyMembers(player.getUUID());
                if (partyUUIDs != null) {
                    for (UUID memberId : partyUUIDs) {
                        if (!memberId.equals(player.getUUID())) {
                            ServerPlayer member = player.getServer().getPlayerList().getPlayer(memberId);
                            if (member != null && member.hasEffect(com.player.journal.registry.ModEffects.PARTY_XP_BUFF)) {
                                if (player.distanceToSqr(member) <= 10000) { // 100 block radius limit
                                    multiplier += 0.10f;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return multiplier;
    }


    @SubscribeEvent
    public static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        globalBuffTimer++;


        if (globalBuffTimer >= 72000) {
            globalBuffTimer = 0;


            if (event.getServer().overworld().random.nextFloat() < 0.20f) {
                int durationTicks = 20 * 60 * 15;


                globalBuffTimer = -durationTicks;

                for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                    try {
                        p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                com.player.journal.registry.ModEffects.GLOBAL_XP_BUFF, durationTicks, 0, false, false, true
                        ));
                    } catch (Exception e) { }
                }
                event.getServer().getPlayerList().broadcastSystemMessage(
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

    private static void displayComboXp(ServerPlayer player, String skill, float xpGained, net.minecraft.ChatFormatting color) {
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


        net.minecraft.network.chat.MutableComponent combinedMessage = Component.empty();
        boolean first = true;


        boolean hasActiveBuff = getXpMultiplier(player) > 1.0f;

        for (Map.Entry<String, Float> entry : combos.entrySet()) {
            String activeSkill = entry.getKey();
            float activeXp = entry.getValue();
            long activeLastTick = ticks.getOrDefault(activeSkill, 0L);


            if (currentTick - activeLastTick <= 60 && activeXp >= 1) {
                if (!first) {
                    combinedMessage.append(Component.literal("  |  ").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                }

                String skillDisplay = activeSkill.substring(0, 1).toUpperCase() + activeSkill.substring(1);

                net.minecraft.ChatFormatting displayColor;


                if (hasActiveBuff) {
                    displayColor = net.minecraft.ChatFormatting.GOLD;
                } else {
                    displayColor = switch (activeSkill) {
                        case "vitality" -> net.minecraft.ChatFormatting.RED;
                        case "combat" -> net.minecraft.ChatFormatting.DARK_RED;
                        case "defense" -> net.minecraft.ChatFormatting.GOLD;
                        case "archery" -> net.minecraft.ChatFormatting.AQUA;
                        case "mining" -> net.minecraft.ChatFormatting.GRAY;
                        case "farming" -> net.minecraft.ChatFormatting.GREEN;
                        case "smithing" -> net.minecraft.ChatFormatting.DARK_GRAY;
                        case "fishing" -> net.minecraft.ChatFormatting.BLUE;
                        case "alchemy" -> net.minecraft.ChatFormatting.LIGHT_PURPLE;
                        case "agility" -> net.minecraft.ChatFormatting.DARK_GREEN;
                        default -> net.minecraft.ChatFormatting.WHITE;
                    };
                }

                combinedMessage.append(Component.literal("+ " + (int)activeXp + " " + skillDisplay + " XP").withStyle(displayColor));
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

    private static boolean isHarvestable(net.minecraft.world.level.block.state.BlockState state) {
        net.minecraft.world.level.block.Block block = state.getBlock();
        if (block instanceof net.minecraft.world.level.block.CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3) >= 3;
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2) >= 2;
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.BERRIES)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.BERRIES);
        }
        return true;
    }

    @SubscribeEvent
    public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            net.minecraft.world.level.GameType newMode = event.getNewGameMode();
            boolean willBeDisabled = (newMode == net.minecraft.world.level.GameType.CREATIVE || newMode == net.minecraft.world.level.GameType.SPECTATOR);
            boolean wasDisabled = (player.isCreative() || player.isSpectator());

            if (willBeDisabled && !wasDisabled) {
                player.displayClientMessage(Component.literal("Journal disabled").withStyle(net.minecraft.ChatFormatting.RED), true);
            } else if (!willBeDisabled && wasDisabled) {
                player.displayClientMessage(Component.literal("Journal enabled").withStyle(net.minecraft.ChatFormatting.GREEN), true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMount(net.neoforged.neoforge.event.entity.EntityMountEvent event) {
        if (event.isMounting() && event.getEntityMounting() instanceof ServerPlayer player && !player.level().isClientSide) {
            if (player.isCreative() || player.isSpectator()) return;

            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
            String targetId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntityBeingMounted().getType()).toString();

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
                                        event.setCanceled(true);
                                        player.displayClientMessage(Component.literal("Requires Agility Level " + reqLevel + " to ride this!").withStyle(net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD), true);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {}
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            JournalProgressionData data = serverPlayer.getData(ModAttachments.JOURNAL_DATA);

            data.syncPlayerHealth(serverPlayer);
            data.syncPlayerAgility(serverPlayer);

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
                    data.getTornPages()
            );
            PacketDistributor.sendToPlayer(serverPlayer, payload);

            // Use the new helper method!
            syncConfigToPlayer(serverPlayer);

            PacketDistributor.sendToPlayer(serverPlayer, new com.player.journal.network.SyncPartyPayload(new java.util.ArrayList<>()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PartyManager.onPlayerLoggedOut(serverPlayer);
        }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {

            // 1. Maintain existing functionality: Sync attributes on respawn
            com.player.journal.data.JournalProgressionData data = player.getData(com.player.journal.registry.ModAttachments.JOURNAL_DATA);
            data.syncPlayerHealth(player);
            data.syncPlayerAgility(player);

            // 2. New Party Respawn Logic
            // If they are just returning from The End portal, don't teleport them to a party member
            if (event.isEndConquered()) return;

            java.util.List<java.util.UUID> partyMembers = com.player.journal.party.PartyManager.getPartyMembers(player.getUUID());

            // If they aren't in a party (or are the only one), do nothing and respawn normally at bed
            if (partyMembers.size() <= 1) return;

            // Get the exact dimension and coordinates where the player just died
            java.util.Optional<net.minecraft.core.GlobalPos> deathPosOpt = player.getLastDeathLocation();
            if (deathPosOpt.isEmpty()) return;

            net.minecraft.core.GlobalPos deathPos = deathPosOpt.get();
            net.minecraft.server.level.ServerLevel deathLevel = player.getServer().getLevel(deathPos.dimension());

            if (deathLevel == null) return;

            net.minecraft.server.level.ServerPlayer targetMember = null;

            for (java.util.UUID memberId : partyMembers) {
                if (memberId.equals(player.getUUID())) continue;

                net.minecraft.server.level.ServerPlayer member = player.getServer().getPlayerList().getPlayer(memberId);

                // Ensure the party member is online, alive, and in the exact same dimension the player died in
                if (member != null && member.isAlive() && member.level() == deathLevel) {
                    // Check if the death location was within 100 blocks (10000 sqr) of the party member
                    if (member.blockPosition().distSqr(deathPos.pos()) <= 10000) {
                        targetMember = member;
                        break;
                    }
                }
            }

            // If they died near a valid party member, teleport them!
            if (targetMember != null) {
                net.minecraft.core.BlockPos spawnPos = findSafeSpawnNear(targetMember.serverLevel(), targetMember.blockPosition(), 5);

                // Teleport the player to the safe spot in the party member's dimension
                player.teleportTo(targetMember.serverLevel(), spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            }
        }
    }

    // Helper method to find a safe block to stand on so they don't spawn inside a wall or in lava
    private static net.minecraft.core.BlockPos findSafeSpawnNear(net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 3; y >= -3; y--) { // Check slightly above and below the target's Y level
                    net.minecraft.core.BlockPos check = center.offset(x, y, z);
                    net.minecraft.world.level.block.state.BlockState floor = level.getBlockState(check.below());

                    // The block below must not be air/liquid, and the 2 blocks for the player's body must be empty air
                    if (!floor.isAir() && floor.getFluidState().isEmpty() && !floor.is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK)
                            && level.isEmptyBlock(check) && level.isEmptyBlock(check.above())) {
                        return check;
                    }
                }
            }
        }
        return center; // Fallback directly to the party member's exact coordinates if no space is found
    }

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            net.minecraft.world.damagesource.DamageSource source = event.getDamageSource();
            net.minecraft.world.entity.Entity attacker = source.getEntity();

            if (!(attacker instanceof net.minecraft.world.entity.Mob) && !(attacker instanceof net.minecraft.world.entity.player.Player)) {
                return;
            }

            if (attacker == player) {
                return;
            }

            ItemStack shieldStack = player.getUseItem();

            if (shieldStack.isEmpty()) {
                if (player.getOffhandItem().getItem() instanceof net.minecraft.world.item.ShieldItem) {
                    shieldStack = player.getOffhandItem();
                } else if (player.getMainHandItem().getItem() instanceof net.minecraft.world.item.ShieldItem) {
                    shieldStack = player.getMainHandItem();
                }
            }

            if (!shieldStack.isEmpty()) {
                String shieldId = BuiltInRegistries.ITEM.getKey(shieldStack.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(player, shieldId);

                if (failMessage == null) {
                    float blockedDamage = event.getBlockedDamage();

                    if (blockedDamage > 0) {
                        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                        if (data.getDefenseLevel() >= 1) {
                            float xpMultiplier = JournalConfig.DEFENSE_SHIELD_XP_PER_DAMAGE.get().floatValue();
                            float baseXpGained = Math.max(1.0f, (blockedDamage * xpMultiplier));

                            // Apply XP Buff!
                            float finalXp = Math.min(10.0f, baseXpGained) * getXpMultiplier(player);

                            boolean leveledUp = data.addDefenseXP(finalXp, player);

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
                                    data.getTornPages()
                            );
                            PacketDistributor.sendToPlayer(player, payload);

                            if (leveledUp) {
                                player.displayClientMessage(Component.literal("§6§lDefense Level Up! §eYou are now level " + data.getDefenseLevel() + "!"), false);
                                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                clearCombo(player, "defense");
                                checkAndBroadcastMilestone(player, "defense", data.getDefenseLevel());
                            } else {
                                displayComboXp(player, "defense", finalXp, net.minecraft.ChatFormatting.GOLD);
                            }
                            sharePartyXP(player, "defense", finalXp);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            net.minecraft.world.entity.Entity attacker = event.getSource().getEntity();
            if (attacker == null || attacker == player) {
                return;
            }

            if (!event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
                float incomingDamage = event.getAmount();

                if (incomingDamage >= 1.0f) {
                    int physicalArmorValue = 0;
                    for (ItemStack armorPiece : player.getInventory().armor) {
                        if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof net.minecraft.world.item.ArmorItem armorItem) {
                            physicalArmorValue += armorItem.getDefense();
                        }
                    }

                    if (physicalArmorValue > 0) {
                        float mitigationPercentage = Math.min(0.8f, physicalArmorValue / 25.0f);
                        float blockedDamage = incomingDamage * mitigationPercentage;

                        if (blockedDamage > 0) {
                            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                            if (data.getDefenseLevel() >= 1) {
                                float xpMultiplier = JournalConfig.DEFENSE_ARMOR_XP_PER_DAMAGE.get().floatValue();

                                // Apply XP Buff!
                                float finalXp = Math.max(1.0f, (blockedDamage * xpMultiplier)) * getXpMultiplier(player);

                                boolean leveledUp = data.addDefenseXP(finalXp, player);

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
                                        data.getTornPages()
                                );
                                PacketDistributor.sendToPlayer(player, payload);

                                if (leveledUp) {
                                    player.displayClientMessage(Component.literal("§6§lDefense Level Up! §eYou are now level " + data.getDefenseLevel() + "!"), false);
                                    player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    clearCombo(player, "defense");
                                    checkAndBroadcastMilestone(player, "defense", data.getDefenseLevel());
                                } else {
                                    displayComboXp(player, "defense", finalXp, net.minecraft.ChatFormatting.GOLD);
                                }
                                sharePartyXP(player, "defense", finalXp);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof ServerPlayer attacker && !attacker.level().isClientSide) {
            ItemStack mainHand = attacker.getMainHandItem();
            if (!mainHand.isEmpty()) {
                String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(attacker, itemIdentifier);
                if (failMessage != null) {
                    event.setNewDamage(0);
                    return;
                }
            }
        }

        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            UUID playerId = player.getUUID();
            net.minecraft.world.damagesource.DamageSource source = event.getSource();
            net.minecraft.world.entity.Entity damageAttacker = source.getEntity();
            long currentTick = player.level().getGameTime();
            float amount = event.getNewDamage();

            if (damageAttacker instanceof net.minecraft.world.entity.player.Player && damageAttacker != player) {
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
            }
            else if (damageAttacker instanceof net.minecraft.world.entity.Mob) {
                lastDamageWasPvE.put(playerId, true);
                lastDamageWasPvP.put(playerId, false);
                lastDamageWasEnvironmental.put(playerId, false);

                float currentPve = pveXpAllowance.getOrDefault(playerId, 0f);
                pveXpAllowance.put(playerId, currentPve + amount);
            }
            else if (damageAttacker == null) {
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
                    return;
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
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.isCreative() || serverPlayer.isSpectator()) return;

            UUID playerId = serverPlayer.getUUID();
            float healAmount = event.getAmount();

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
            }
            else if (isPvP) {
                float allowance = pvpXpAllowance.getOrDefault(playerId, 0f);
                if (allowance > 0) {
                    validHealAmount = Math.min(healAmount, allowance);
                    pvpXpAllowance.put(playerId, allowance - validHealAmount);
                }
            }
            else if (isPvE) {
                float allowance = pveXpAllowance.getOrDefault(playerId, 0f);
                if (allowance > 0) {
                    validHealAmount = Math.min(healAmount, allowance);
                    pveXpAllowance.put(playerId, allowance - validHealAmount);
                }
            }

            if (validHealAmount <= 0) return;

            JournalProgressionData data = serverPlayer.getData(ModAttachments.JOURNAL_DATA);

            if (data.getVitalityLevel() >= 1) {
                double xpMultiplier = JournalConfig.XP_PER_HEART_HEALED.get();

                // Apply XP Buff!
                float finalXp = (float) (validHealAmount * xpMultiplier) * getXpMultiplier(serverPlayer);

                if (finalXp <= 0) return;

                boolean leveledUp = data.addVitalityXP(finalXp, serverPlayer);

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
                        data.getTornPages()
                );
                PacketDistributor.sendToPlayer(serverPlayer, payload);

                if (leveledUp) {
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                    serverPlayer.displayClientMessage(Component.literal("§6§lVitality Level Up! §eYou are now level " + data.getVitalityLevel() + "!"), false);
                    clearCombo(serverPlayer, "vitality");
                    checkAndBroadcastMilestone(serverPlayer, "vitality", data.getVitalityLevel());
                } else {
                    displayComboXp(serverPlayer, "vitality", finalXp, net.minecraft.ChatFormatting.GREEN);
                }
                sharePartyXP(serverPlayer, "vitality", finalXp);
            }
        }
    }


    @SubscribeEvent
    public static void onMeleeHitPost(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            if (event.getEntity() instanceof net.minecraft.world.entity.Mob mob) {
                if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;

                float damageDealt = event.getNewDamage();

                if (damageDealt > 0) {
                    MOB_ATTACKERS.computeIfAbsent(mob.getUUID(), k -> new HashSet<>()).add(player.getUUID());

                    JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                    if (data.getCombatLevel() >= 1) {
                        // Apply XP Buff!
                        float finalXp = damageDealt * getXpMultiplier(player);

                        boolean leveledUp = data.addCombatXP(finalXp, player);

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
                                data.getTornPages()
                        );
                        PacketDistributor.sendToPlayer(player, payload);

                        if (leveledUp) {
                            player.displayClientMessage(Component.literal("§6§lCombat Level Up! §eYou are now level " + data.getCombatLevel() + "!"), false);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            clearCombo(player, "combat");
                            checkAndBroadcastMilestone(player, "combat", data.getCombatLevel());
                        } else {
                            displayComboXp(player, "combat", finalXp, net.minecraft.ChatFormatting.RED);
                        }

                        sharePartyXP(player, "combat", finalXp);
                    }
                }
            }
        }
    }


    @net.neoforged.bus.api.SubscribeEvent
    public static void onMobKill(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getEntity() instanceof net.minecraft.world.entity.Mob mob) {
            java.util.Set<java.util.UUID> attackerUUIDs = MOB_ATTACKERS.remove(mob.getUUID());

            if (attackerUUIDs == null) {
                attackerUUIDs = new java.util.HashSet<>();
            }
            if (event.getSource().getEntity() instanceof net.minecraft.server.level.ServerPlayer directKiller) {
                attackerUUIDs.add(directKiller.getUUID());
            }

            if (!attackerUUIDs.isEmpty()) {
                float xpAmount = 0f;

                // --- NEW: TOGGLE CHECK ---
                if (com.player.journal.config.JournalConfig.ENABLE_CUSTOM_MOB_XP.get()) {

                    // Toggle is ON: Use your custom config list
                    String entityIdentifier = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
                    java.util.List<String> mobXpRegistry = com.player.journal.config.JournalConfig.getAllMobXpValues();

                    for (String entry : mobXpRegistry) {
                        if (entry.contains(";")) {
                            String[] parts = entry.split(";");
                            String configEntityId = parts[0].trim();

                            if (configEntityId.equals(entityIdentifier)) {
                                try {
                                    xpAmount = Float.parseFloat(parts[1].trim());
                                } catch (NumberFormatException e) { }
                                break;
                            }
                        }
                    }
                } else {
                    // Toggle is OFF: Fall back to the mob's vanilla experience drops so Combat leveling still works!
                    xpAmount = mob.getExperienceReward((net.minecraft.server.level.ServerLevel) mob.level(), event.getSource().getEntity());
                }

                // --- CONTINUE WITH EXISTING XP LOGIC ---
                if (xpAmount > 0) {
                    for (java.util.UUID playerUUID : attackerUUIDs) {
                        net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) mob.level().getPlayerByUUID(playerUUID);

                        if (player != null && !player.isCreative() && !player.isSpectator()) {
                            com.player.journal.data.JournalProgressionData data = player.getData(com.player.journal.registry.ModAttachments.JOURNAL_DATA);

                            if (data.getCombatLevel() >= 1) {
                                // Apply XP Buff!
                                float finalXp = xpAmount * getXpMultiplier(player);

                                if (data.addCombatXP(finalXp, player)) {
                                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§6§lCombat Level Up! §eYou are now level " + data.getCombatLevel() + "!"), false);
                                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                                    clearCombo(player, "combat");
                                    checkAndBroadcastMilestone(player, "combat", data.getCombatLevel());
                                } else {
                                    displayComboXp(player, "combat", finalXp, net.minecraft.ChatFormatting.RED);
                                }

                                com.player.journal.network.SyncJournalDataPayload payload = new com.player.journal.network.SyncJournalDataPayload(
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
                                        data.getTornPages()
                                );
                                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
                                sharePartyXP(player, "combat", finalXp);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof net.minecraft.world.entity.Mob mob) {
            MOB_ATTACKERS.remove(mob.getUUID());
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onBabySpawn(net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getParentA().getType()).toString();
            List<String> breedingConfig = (List<String>) JournalConfig.FARMING_BREEDING.get();

            for (String entry : breedingConfig) {
                String[] parts = entry.split(";");
                if (parts.length == 3) {
                    String[] groupedAnimals = parts[0].split(",");
                    for (String a : groupedAnimals) {
                        if (a.trim().equals(animalId)) {
                            float xpReward = Float.parseFloat(parts[2]);
                            if (xpReward > 0) {
                                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                                if (data.getFarmingLevel() >= 1) {
                                    // Apply XP Buff!
                                    float finalXp = xpReward * getXpMultiplier(player);

                                    boolean leveledUp = data.addFarmingXP(finalXp, player);

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
                                            data.getTornPages()
                                    );
                                    PacketDistributor.sendToPlayer(player, payload);

                                    if (leveledUp) {
                                        player.displayClientMessage(Component.literal("§6§lFarming Level Up! §eYou are now level " + data.getFarmingLevel() + "!"), false);
                                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                        clearCombo(player, "farming");
                                        checkAndBroadcastMilestone(player, "farming", data.getFarmingLevel());
                                    } else {
                                        displayComboXp(player, "farming", finalXp, net.minecraft.ChatFormatting.YELLOW);
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
    }

    private static String getFailedRequirement(ServerPlayer player, String itemIdentifier, List<String> configList) {
        if (player.isCreative() || player.isSpectator()) {
            return null;
        }

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

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
                    int playerLevel = 0;

                    switch (skill) {
                        case "vitality" -> playerLevel = data.getVitalityLevel();
                        case "agility" -> playerLevel = data.getAgilityLevel();
                        case "combat" -> playerLevel = data.getCombatLevel();
                        case "defense" -> playerLevel = data.getDefenseLevel();
                        case "mining" -> playerLevel = data.getMiningLevel();
                        case "farming" -> playerLevel = data.getFarmingLevel();
                        case "smithing" -> playerLevel = data.getSmithingLevel();
                        case "fishing" -> playerLevel = data.getFishingLevel();
                        case "archery" -> playerLevel = data.getArcheryLevel();
                        case "alchemy" -> playerLevel = data.getAlchemyLevel();
                    }

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
                    return "Requires " + String.join(" & ", failedSkills) + "!";
                }

                return null;
            }
        }
        return null;
    }

    public static String getFailedRequirementFromMap(ServerPlayer player, String itemIdentifier) {
        if (player.isCreative() || player.isSpectator()) return null;

        Map<String, Integer> requirements = RestrictionDataLoader.getItemRestrictions(itemIdentifier);

        // --- THE FIX: If the item isn't in a Datapack, fall back and check the TOML Config! ---
        if (requirements.isEmpty()) {
            return getFailedRequirement(player, itemIdentifier, getAllConfigUsageRestrictions());
        }

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        List<String> failedSkills = new ArrayList<>();

        for (Map.Entry<String, Integer> req : requirements.entrySet()) {
            String skill = req.getKey().toLowerCase();
            int requiredLevel = req.getValue();
            int playerLevel = 0;

            switch (skill) {
                case "vitality" -> playerLevel = data.getVitalityLevel();
                case "agility" -> playerLevel = data.getAgilityLevel();
                case "combat" -> playerLevel = data.getCombatLevel();
                case "defense" -> playerLevel = data.getDefenseLevel();
                case "mining" -> playerLevel = data.getMiningLevel();
                case "farming" -> playerLevel = data.getFarmingLevel();
                case "smithing" -> playerLevel = data.getSmithingLevel();
                case "fishing" -> playerLevel = data.getFishingLevel();
                case "archery" -> playerLevel = data.getArcheryLevel();
                case "alchemy" -> playerLevel = data.getAlchemyLevel();
            }

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
            return "Requires " + String.join(" & ", failedSkills) + "!";
        }
        return null;
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            ItemStack craftedItem = event.getCrafting();
            if (craftedItem.isEmpty()) return;

            String itemId = BuiltInRegistries.ITEM.getKey(craftedItem.getItem()).toString();
            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

            // 1. Check Datapack Requirements
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
                failMessage = "Requires " + String.join(" & ", failedSkills) + " to craft!";
            } else if (craftReqs.isEmpty()) {
                // Fallback to old config if no datapack entry exists
                failMessage = getFailedRequirement(player, itemId, JournalConfig.getAllCraftingRestrictions());
            }

            if (failMessage != null) {
                net.minecraft.world.item.Item itemToRemove = craftedItem.getItem();
                int amountToRemove = craftedItem.getCount();

                ItemStack cursorStack = player.containerMenu.getCarried();
                if (cursorStack.is(itemToRemove)) {
                    cursorStack.shrink(amountToRemove);
                } else {
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(itemToRemove)) {
                            int shrinkAmount = Math.min(amountToRemove, invStack.getCount());
                            invStack.shrink(shrinkAmount);
                            amountToRemove -= shrinkAmount;
                            if (amountToRemove <= 0) break;
                        }
                    }
                }
                player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.literal("You lack the knowledge and ruined the materials!")
                        .withStyle(net.minecraft.ChatFormatting.DARK_RED, net.minecraft.ChatFormatting.BOLD), true);
            } else {
                if (player.isCreative() || player.isSpectator()) return;

                // 2. Grant XP from Datapack
                float xpReward = (float) RestrictionDataLoader.getCraftingXp(itemId) * craftedItem.getCount();

                // Fallback to Config XP if Datapack has none
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

                if (xpReward > 0) {
                    if (data.getSmithingLevel() >= 1) {
                        float finalXp = xpReward * getXpMultiplier(player);

                        boolean leveledUp = data.addSmithingXP(finalXp, player);

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
                                data.getTornPages()
                        );
                        PacketDistributor.sendToPlayer(player, payload);

                        if (leveledUp) {
                            player.displayClientMessage(Component.literal("§6§lSmithing Level Up! §eYou are now level " + data.getSmithingLevel() + "!"), false);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            clearCombo(player, "smithing");
                            checkAndBroadcastMilestone(player, "smithing", data.getSmithingLevel());
                        } else {
                            displayComboXp(player, "smithing", finalXp, net.minecraft.ChatFormatting.GRAY);
                        }
                        sharePartyXP(player, "smithing", finalXp);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            ItemStack smeltedItem = event.getSmelting();
            if (smeltedItem.isEmpty()) return;

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
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                if (data.getSmithingLevel() >= 1) {
                    float finalXp = xpReward * getXpMultiplier(player);

                    boolean leveledUp = data.addSmithingXP(finalXp, player);

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
                            data.getTornPages()
                    );
                    PacketDistributor.sendToPlayer(player, payload);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§6§lSmithing Level Up! §eYou are now level " + data.getSmithingLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "smithing");
                        checkAndBroadcastMilestone(player, "smithing", data.getSmithingLevel());
                    } else {
                        displayComboXp(player, "smithing", finalXp, net.minecraft.ChatFormatting.GRAY);
                    }
                    sharePartyXP(player, "smithing", finalXp);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onArmorEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == net.minecraft.world.entity.EquipmentSlot.MAINHAND ||
                event.getSlot() == net.minecraft.world.entity.EquipmentSlot.OFFHAND) {
            return;
        }

        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack stack = event.getTo();
            if (stack.isEmpty()) return;

            String itemIdentifier = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

            if (failMessage != null) {
                ItemStack restrictedItem = stack.copy();

                serverPlayer.getServer().execute(() -> {
                    serverPlayer.setItemSlot(event.getSlot(), ItemStack.EMPTY);

                    if (!serverPlayer.getInventory().add(restrictedItem)) {
                        serverPlayer.drop(restrictedItem, false);
                    }

                    serverPlayer.inventoryMenu.sendAllDataToRemote();
                    serverPlayer.containerMenu.sendAllDataToRemote();
                });

                serverPlayer.displayClientMessage(Component.literal(failMessage), true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onItemInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            ItemStack handStack = event.getItemStack();
            if (!handStack.isEmpty()) {
                String itemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(player, itemId);

                if (failMessage != null) {
                    event.setCanceled(true);
                    player.displayClientMessage(Component.literal(failMessage), true);

                    player.getServer().execute(() -> {
                        player.inventoryMenu.sendAllDataToRemote();
                        player.containerMenu.sendAllDataToRemote();
                    });
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onLeftClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            ItemStack handStack = event.getItemStack();
            if (!handStack.isEmpty()) {
                String itemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(player, itemId);

                if (failMessage != null) {
                    event.setCanceled(true);
                    player.displayClientMessage(Component.literal(failMessage), true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(net.neoforged.neoforge.event.entity.player.AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack mainHand = serverPlayer.getMainHandItem();
            if (!mainHand.isEmpty()) {
                String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
                String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

                if (failMessage != null) {
                    event.setCanceled(true);
                    serverPlayer.displayClientMessage(Component.literal(failMessage), true);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onBlockInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            boolean activatingBlock = !player.isCrouching() && isInteractiveBlock(event.getLevel().getBlockState(event.getPos()), event.getLevel(), event.getPos());

            if (!activatingBlock) {
                ItemStack handStack = event.getItemStack();
                if (!handStack.isEmpty()) {
                    String itemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
                    String failMessage = getFailedRequirementFromMap(player, itemId);

                    if (failMessage != null) {
                        event.setCanceled(true);
                        player.displayClientMessage(Component.literal(failMessage), true);

                        player.getServer().execute(() -> {
                            player.inventoryMenu.sendAllDataToRemote();
                            player.containerMenu.sendAllDataToRemote();
                        });
                        return;
                    }
                }
            }

            String blockId = BuiltInRegistries.BLOCK.getKey(event.getLevel().getBlockState(event.getPos()).getBlock()).toString();
            List<String> utilityRestrictions = JournalConfig.getUtilityBlockRestrictions();

            String failMessage = getFailedRequirement(player, blockId, utilityRestrictions);
            if (failMessage != null) {
                event.setCanceled(true);
                player.displayClientMessage(Component.literal(failMessage), true);
            }
        }
    }

    private static boolean isInteractiveBlock(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        if (state.getMenuProvider(level, pos) != null) return true;

        net.minecraft.world.level.block.Block block = state.getBlock();
        return block instanceof net.minecraft.world.level.block.DoorBlock ||
                block instanceof net.minecraft.world.level.block.TrapDoorBlock ||
                block instanceof net.minecraft.world.level.block.FenceGateBlock ||
                block instanceof net.minecraft.world.level.block.LeverBlock ||
                block instanceof net.minecraft.world.level.block.ButtonBlock ||
                block instanceof net.minecraft.world.level.block.BedBlock ||
                block instanceof net.minecraft.world.level.block.AnvilBlock ||
                block instanceof net.minecraft.world.level.block.CraftingTableBlock ||
                block instanceof net.minecraft.world.level.block.EnchantingTableBlock ||
                block instanceof net.minecraft.world.level.block.EnderChestBlock ||
                block instanceof net.minecraft.world.level.block.BellBlock ||
                block instanceof net.minecraft.world.level.block.DiodeBlock;
    }


    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onEntityInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player) {

            if (player.isCrouching() && event.getTarget() instanceof ServerPlayer targetPlayer) {
                if (player.getMainHandItem().isEmpty()) {
                    JournalProgressionData targetData = targetPlayer.getData(ModAttachments.JOURNAL_DATA);

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
                    PacketDistributor.sendToPlayer(player, payload);

                    event.setCanceled(true);
                    return;
                }
            }

            boolean isDisplayOrMerchant = event.getTarget() instanceof net.minecraft.world.entity.decoration.ItemFrame ||
                    event.getTarget() instanceof net.minecraft.world.entity.decoration.ArmorStand ||
                    event.getTarget() instanceof net.minecraft.world.entity.npc.Villager ||
                    event.getTarget() instanceof net.minecraft.world.entity.npc.WanderingTrader;

            if (!isDisplayOrMerchant) {
                ItemStack handStack = event.getItemStack();
                if (!handStack.isEmpty()) {
                    String itemId = BuiltInRegistries.ITEM.getKey(handStack.getItem()).toString();
                    String failMessage = getFailedRequirementFromMap(player, itemId);
                    if (failMessage != null) {
                        event.setCanceled(true);
                        player.displayClientMessage(Component.literal(failMessage), true);
                        return;
                    }
                }
            }

            if (event.getTarget() instanceof net.minecraft.world.entity.npc.Villager villager) {
                String professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString();
                List<String> villagerRestrictions = (List<String>) JournalConfig.VILLAGER_RESTRICTIONS.get();

                String failMessage = getFailedRequirement(player, professionId, villagerRestrictions);
                if (failMessage != null) {
                    event.setCanceled(true);
                    player.displayClientMessage(Component.literal(failMessage), true);
                    return;
                }
            }

            if (event.getTarget() instanceof net.minecraft.world.entity.animal.Animal animal) {
                if (animal.isFood(event.getItemStack())) {
                    String animalId = BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).toString();
                    List<String> breedingConfig = (List<String>) JournalConfig.FARMING_BREEDING.get();

                    String failMessage = getFailedRequirement(player, animalId, breedingConfig);
                    if (failMessage != null) {
                        event.setCanceled(true);
                        player.displayClientMessage(Component.literal(failMessage), true);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack stack = event.getItem();
            String itemIdentifier = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

            String potionIdentifier = "";
            if (stack.getItem() instanceof PotionItem) {
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null && contents.potion().isPresent()) {
                    potionIdentifier = "potion:" + contents.potion().get().unwrapKey().get().location().toString();
                }
            }

            String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

            // Only check potion-specific logic if the base item check passes
            if (failMessage == null && !potionIdentifier.isEmpty()) {
                List<String> useRestrictions = new ArrayList<>((List<String>) JournalConfig.POTION_RESTRICTIONS.get());
                failMessage = getFailedRequirement(serverPlayer, potionIdentifier, useRestrictions);
            }

            if (failMessage != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal(failMessage), true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUsed(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            ItemStack stack = event.getItem();

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
                    JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                    if (data.getAlchemyLevel() >= 1) {
                        // Apply XP Buff!
                        float finalXp = xpGained * getXpMultiplier(player);

                        boolean leveledUp = data.addAlchemyXP(finalXp, player);

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
                                data.getTornPages()
                        );
                        PacketDistributor.sendToPlayer(player, payload);

                        if (leveledUp) {
                            player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                            clearCombo(player, "alchemy");
                            checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                        } else {
                            displayComboXp(player, "alchemy", finalXp, net.minecraft.ChatFormatting.LIGHT_PURPLE);
                        }
                        sharePartyXP(player, "alchemy", finalXp);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPotionBrewed(PlayerBrewedPotionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            float xpGained = JournalConfig.ALCHEMY_BREW_XP.get().floatValue();

            if (xpGained > 0) {
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                if (data.getAlchemyLevel() >= 1) {
                    // Apply XP Buff!
                    float finalXp = xpGained * getXpMultiplier(player);

                    boolean leveledUp = data.addAlchemyXP(finalXp, player);

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
                            data.getTornPages()
                    );
                    PacketDistributor.sendToPlayer(player, payload);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "alchemy");
                        checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                    } else {
                        displayComboXp(player, "alchemy", finalXp, net.minecraft.ChatFormatting.LIGHT_PURPLE);
                    }
                    sharePartyXP(player, "alchemy", finalXp);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTotemUse(LivingUseTotemEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack totemStack = event.getTotem();
            if (totemStack.isEmpty()) return;

            String itemIdentifier = BuiltInRegistries.ITEM.getKey(totemStack.getItem()).toString();
            String failMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

            if (failMessage != null) {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(Component.literal(failMessage), true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(net.neoforged.neoforge.event.level.BlockEvent.BreakEvent event) {
        if (!event.getPlayer().level().isClientSide && event.getPlayer() instanceof ServerPlayer serverPlayer) {

            if (serverPlayer.isCreative() || serverPlayer.isSpectator()) return;

            JournalProgressionData data = serverPlayer.getData(ModAttachments.JOURNAL_DATA);

            ItemStack mainHand = serverPlayer.getMainHandItem();
            if (!mainHand.isEmpty()) {
                String itemIdentifier = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).toString();
                String toolFailMessage = getFailedRequirementFromMap(serverPlayer, itemIdentifier);

                if (toolFailMessage != null) {
                    event.setCanceled(true);
                    serverPlayer.displayClientMessage(Component.literal(toolFailMessage), true);
                    return;
                }
            }

            String blockIdentifier = BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString();
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
                                event.setCanceled(true);
                                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                                serverPlayer.displayClientMessage(Component.literal("Requires " + displaySkill + " " + requiredLevel + "!").withStyle(net.minecraft.ChatFormatting.RED), true);
                                return;
                            } else if (parts.length == 3) {
                                float xpReward = Float.parseFloat(parts[2]);
                                if (xpReward > 0) {
                                    if (skill.equals("farming") && !isHarvestable(event.getState())) {
                                        return;
                                    }

                                    // Apply XP Buff!
                                    float finalXp = xpReward * getXpMultiplier(serverPlayer);

                                    boolean leveledUp = false;
                                    boolean canEarnXp = false;

                                    if (skill.equals("mining") && data.getMiningLevel() >= 1) {
                                        leveledUp = data.addMiningXP(finalXp, serverPlayer);
                                        canEarnXp = true;
                                    } else if (skill.equals("farming") && data.getFarmingLevel() >= 1) {
                                        leveledUp = data.addFarmingXP(finalXp, serverPlayer);
                                        canEarnXp = true;
                                    } else if (skill.equals("smithing") && data.getSmithingLevel() >= 1) {
                                        leveledUp = data.addSmithingXP(finalXp, serverPlayer);
                                        canEarnXp = true;
                                    }

                                    if (canEarnXp) {
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
                                                data.getTornPages()
                                        );
                                        PacketDistributor.sendToPlayer(serverPlayer, payload);

                                        if (leveledUp) {
                                            String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                                            int levelDisplay = switch (skill) {
                                                case "mining" -> data.getMiningLevel();
                                                case "farming" -> data.getFarmingLevel();
                                                case "smithing" -> data.getSmithingLevel();
                                                default -> 0;
                                            };
                                            serverPlayer.displayClientMessage(Component.literal("§6§l" + displaySkill + " Level Up! §eYou are now level " + levelDisplay + "!"), false);
                                            serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                            clearCombo(serverPlayer, skill);
                                            checkAndBroadcastMilestone(serverPlayer, skill, levelDisplay);
                                        } else {
                                            displayComboXp(serverPlayer, skill, finalXp, skill.equals("mining") ? net.minecraft.ChatFormatting.AQUA : (skill.equals("farming") ? net.minecraft.ChatFormatting.YELLOW : net.minecraft.ChatFormatting.GRAY));
                                        }
                                        sharePartyXP(serverPlayer, skill, finalXp);
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWanderingTraderInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            if (event.getTarget() instanceof net.minecraft.world.entity.npc.WanderingTrader) {
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
                boolean allUnlocked = data.getFarmingLevel() >= 1 &&
                        data.getMiningLevel() >= 1 &&
                        data.getSmithingLevel() >= 1 &&
                        data.hasArchery() &&
                        data.hasFishing() &&
                        data.hasAlchemy();

                if (!allUnlocked) {
                    event.setCanceled(true);
                    player.displayClientMessage(Component.literal("Requires All Classes Unlocked!").withStyle(net.minecraft.ChatFormatting.RED), true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
            data.syncPlayerHealth(player);

            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
            data.syncPlayerHealth(player);

            if (event.isWasDeath()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileHit(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
        if (!event.getEntity().level().isClientSide) {
            if (event.getSource().getDirectEntity() instanceof net.minecraft.world.entity.projectile.Projectile projectile) {
                if (projectile.getOwner() instanceof net.minecraft.server.level.ServerPlayer player) {
                    if (player.isCreative() || player.isSpectator()) return;

                    float xpReward = JournalConfig.ARCHERY_XP_PER_HIT.get().floatValue();

                    JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                    if (data.getArcheryLevel() >= 1) {
                        // Apply XP Buff!
                        float finalXp = xpReward * getXpMultiplier(player);

                        boolean leveledUp = data.addArcheryXP(finalXp, player);

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
                                data.getTornPages()
                        );
                        PacketDistributor.sendToPlayer(player, payload);

                        if (leveledUp) {
                            player.displayClientMessage(Component.literal("§6§lArchery Level Up! §eYou are now level " + data.getArcheryLevel() + "!"), false);
                            player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                            clearCombo(player, "archery");
                            checkAndBroadcastMilestone(player, "archery", data.getArcheryLevel());
                        } else {
                            displayComboXp(player, "archery", finalXp, net.minecraft.ChatFormatting.AQUA);
                        }
                        sharePartyXP(player, "archery", finalXp);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFish(net.neoforged.neoforge.event.entity.player.ItemFishedEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            float xpReward = JournalConfig.FISHING_XP_PER_CATCH.get().floatValue();

            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

            if (data.getFishingLevel() >= 1) {
                // Apply XP Buff!
                float finalXp = xpReward * getXpMultiplier(player);

                boolean leveledUp = data.addFishingXP(finalXp, player);

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
                        data.getTornPages()
                );
                PacketDistributor.sendToPlayer(player, payload);

                if (leveledUp) {
                    player.displayClientMessage(Component.literal("§9§lFishing Level Up! §bYou are now level " + data.getFishingLevel() + "!"), false);
                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                    clearCombo(player, "fishing");
                    checkAndBroadcastMilestone(player, "fishing", data.getFishingLevel());
                } else {
                    displayComboXp(player, "fishing", finalXp, net.minecraft.ChatFormatting.BLUE);
                }
                sharePartyXP(player, "fishing", finalXp);
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(net.neoforged.neoforge.event.AnvilUpdateEvent event) {
        net.minecraft.world.entity.player.Player player = event.getPlayer();
        if (player == null) return;
        if (player.isCreative() || player.isSpectator()) return;

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        var enchantReg = player.level().registryAccess().registry(net.minecraft.core.registries.Registries.ENCHANTMENT).orElse(null);
        if (enchantReg == null) return;

        ItemStack right = event.getRight();
        if (right.isEmpty()) return;

        net.minecraft.world.item.enchantment.ItemEnchantments rightEnchants = right.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        net.minecraft.world.item.enchantment.ItemEnchantments rightStored = right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);

        try {
            @SuppressWarnings("unchecked")
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
                                    event.setCanceled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            if (player.tickCount % 20 == 0) {
                PartyManager.syncPartyForPlayer(player);
            }

            // --- NEW: Passive Random Torn Page Reward ---
            // 144,000 ticks = roughly once every 2 hours.
            if (player.getRandom().nextInt(144000) == 0) {
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
                data.setTornPages(data.getTornPages() + 1);

                player.displayClientMessage(Component.literal("§d§lLucky! §eYou discovered a hidden Torn Page!"), false);
                player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

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
                        data.getTornPages()
                );
                PacketDistributor.sendToPlayer(player, payload);
            }
            // --------------------------------------------

            if (player.tickCount % 5 == 0) {
                UUID id = player.getUUID();
                net.minecraft.world.phys.Vec3 currentPos = player.position();

                boolean isInBoat = player.getVehicle() instanceof net.minecraft.world.entity.vehicle.Boat;
                if ((player.isPassenger() && !isInBoat) || player.getAbilities().flying || player.isFallFlying()) {
                    lastPlayerPositions.put(id, currentPos);
                    return;
                }

                if (lastPlayerPositions.containsKey(id)) {
                    net.minecraft.world.phys.Vec3 lastPos = lastPlayerPositions.get(id);

                    double dx = currentPos.x - lastPos.x;
                    double dz = currentPos.z - lastPos.z;
                    double distanceMoved = Math.sqrt(dx * dx + dz * dz);

                    if (distanceMoved > 0.1 && distanceMoved < 20.0) {
                        // 1. Accumulate the UNBUFFED raw distance XP
                        float xpGained = (float) (distanceMoved * JournalConfig.AGILITY_XP_PER_BLOCK.get());
                        float totalPending = pendingAgilityXp.getOrDefault(id, 0f) + xpGained;

                        if (totalPending >= 10.0f) {
                            // 2. NOW apply the XP buff to the big chunk so the player actually sees +15 pop up!
                            float finalXp = totalPending * getXpMultiplier(player);

                            JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                            if (data.getAgilityLevel() >= 1) {
                                boolean leveledUp = data.addAgilityXP(finalXp, player);

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
                                        data.getTornPages()
                                );
                                PacketDistributor.sendToPlayer(player, payload);

                                if (leveledUp) {
                                    player.displayClientMessage(Component.literal("§a§lAgility Level Up! §2You are now level " + data.getAgilityLevel() + "!"), false);
                                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                                    clearCombo(player, "agility");
                                    checkAndBroadcastMilestone(player, "agility", data.getAgilityLevel());
                                } else {
                                    displayComboXp(player, "agility", finalXp, net.minecraft.ChatFormatting.GREEN);
                                }
                                sharePartyXP(player, "agility", finalXp);
                            }
                            pendingAgilityXp.put(id, 0f); // Reset bucket
                        } else {
                            pendingAgilityXp.put(id, totalPending); // Keep filling bucket
                        }
                    }
                }
                lastPlayerPositions.put(id, currentPos);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        com.player.journal.command.ScanModsCommand.register(event.getDispatcher());
        com.player.journal.command.PartyCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAddReloadListeners(net.neoforged.neoforge.event.AddReloadListenerEvent event) {
        event.addListener(new com.player.journal.data.RestrictionDataLoader());

        // If the server is running, broadcast the updated payload to all online players after a reload!
        if (event.getServerResources() != null) {
            net.minecraft.server.MinecraftServer server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    // Re-send the updated config payload with fresh datapack data
                    syncConfigToPlayer(player);
                }
            }
        }
    }

    public static void syncConfigToPlayer(ServerPlayer serverPlayer) {
        @SuppressWarnings("unchecked")
        SyncJournalConfigPayload configPayload = new SyncJournalConfigPayload(
                (List<String>) JournalConfig.ARMOR_RESTRICTIONS.get(),
                (List<String>) JournalConfig.POTION_RESTRICTIONS.get(),
                com.player.journal.data.RestrictionDataLoader.exportDatapackAndConfig(),
                (List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get(),
                (List<String>) JournalConfig.FARMERS_DELIGHT_RESTRICTIONS.get(),
                (List<String>) JournalConfig.PALADINS_PRIESTS_ARMORS.get(),
                (List<String>) JournalConfig.PALADINS_PRIESTS_WEAPONS.get(),
                (List<String>) JournalConfig.PALADINS_PRIESTS_SHIELDS.get(),
                (List<String>) JournalConfig.ROGUES_WARRIORS_ARMORS.get(),
                (List<String>) JournalConfig.ROGUES_WARRIORS_WEAPONS.get(),
                (List<String>) JournalConfig.ARCHERS_ARMORS.get(),
                (List<String>) JournalConfig.ARCHERS_WEAPONS.get(),
                (List<String>) JournalConfig.WIZARDS_ARMORS.get(),
                (List<String>) JournalConfig.WIZARDS_WEAPONS.get(),
                (List<String>) JournalConfig.ARSENAL_WEAPONS.get(),
                (List<String>) JournalConfig.ARTIFACTS_ITEMS.get(),
                (List<String>) JournalConfig.TIDE_ITEMS.get(),
                (List<String>) JournalConfig.GLIDERS_ITEMS.get(),
                (List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get(),
                (List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get(),
                (List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get(),
                (List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get(),
                (List<String>) JournalConfig.ALCHEMY_UTILITIES.get(),
                (List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get(),
                (List<String>) JournalConfig.AGILITY_MOUNTS.get(),
                com.player.journal.data.RestrictionDataLoader.exportCraftingDatapackAndConfig(),
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
        PacketDistributor.sendToPlayer(serverPlayer, configPayload);
    }

    @SubscribeEvent
    public static void onBookConsumeForMagic(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.isSpectator()) return;

            if (!player.isCrouching()) return;

            ItemStack handStack = event.getItemStack();

            boolean isNormalBook = handStack.is(net.minecraft.world.item.Items.BOOK);
            boolean isEnchantedBook = handStack.is(net.minecraft.world.item.Items.ENCHANTED_BOOK);

            if (isNormalBook || isEnchantedBook) {
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

                if (data.getAlchemyLevel() >= 1) {
                    float xpGained = 0f;

                    if (isNormalBook) {
                        xpGained = JournalConfig.ALCHEMY_BOOK_XP.get().floatValue();
                    } else if (isEnchantedBook) {
                        xpGained = JournalConfig.ALCHEMY_ENCHANTED_BOOK_BASE_XP.get().floatValue();
                        float levelMultiplier = JournalConfig.ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER.get().floatValue();

                        net.minecraft.world.item.enchantment.ItemEnchantments storedEnchants = handStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
                        for (var holder : storedEnchants.keySet()) {
                            int enchantLevel = storedEnchants.getLevel(holder);
                            xpGained += (enchantLevel * levelMultiplier);
                        }
                    }

                    if (xpGained <= 0) return;

                    // Apply XP Buff!
                    float finalXp = xpGained * getXpMultiplier(player);

                    handStack.shrink(1);

                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

                    boolean leveledUp = data.addAlchemyXP(finalXp, player);

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
                            data.getTornPages()
                    );
                    PacketDistributor.sendToPlayer(player, payload);

                    if (leveledUp) {
                        player.displayClientMessage(Component.literal("§d§lAlchemy Level Up! §5You are now level " + data.getAlchemyLevel() + "!"), false);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                        clearCombo(player, "alchemy");
                        checkAndBroadcastMilestone(player, "alchemy", data.getAlchemyLevel());
                    } else {
                        displayComboXp(player, "alchemy", finalXp, net.minecraft.ChatFormatting.LIGHT_PURPLE);
                    }
                    sharePartyXP(player, "alchemy", finalXp);

                    event.setCanceled(true);
                }
            }
        }
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

                // Make sure party members also benefit from their own XP buffs!
                float buffedSharedXp = sharedXp * getXpMultiplier(member);

                JournalProgressionData data = member.getData(ModAttachments.JOURNAL_DATA);
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
                            data.getTornPages()
                    );
                    PacketDistributor.sendToPlayer(member, payload);

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

                        net.minecraft.ChatFormatting color = switch (skill) {
                            case "vitality" -> net.minecraft.ChatFormatting.RED;
                            case "combat" -> net.minecraft.ChatFormatting.DARK_RED;
                            case "defense" -> net.minecraft.ChatFormatting.GOLD;
                            case "archery" -> net.minecraft.ChatFormatting.AQUA;
                            case "mining" -> net.minecraft.ChatFormatting.GRAY;
                            case "farming" -> net.minecraft.ChatFormatting.GREEN;
                            case "smithing" -> net.minecraft.ChatFormatting.DARK_GRAY;
                            case "fishing" -> net.minecraft.ChatFormatting.BLUE;
                            case "alchemy" -> net.minecraft.ChatFormatting.LIGHT_PURPLE;
                            case "agility" -> net.minecraft.ChatFormatting.DARK_GREEN;
                            default -> net.minecraft.ChatFormatting.WHITE;
                        };

                        member.displayClientMessage(Component.literal("§l" + displaySkill + " Level Up! §rYou are now level " + levelDisplay + "!").withStyle(color), false);
                        clearCombo(member, skill);
                        checkAndBroadcastMilestone(member, skill, levelDisplay);
                    } else {
                        displayComboXp(member, skill, buffedSharedXp, net.minecraft.ChatFormatting.WHITE);
                    }
                }
            }
        }
    }
    @SuppressWarnings("unchecked")
    public static List<String> getAllConfigUsageRestrictions() {
        List<String> all = new ArrayList<>();
        all.addAll((List<String>) JournalConfig.ARMOR_RESTRICTIONS.get());
        all.addAll((List<String>) JournalConfig.POTION_RESTRICTIONS.get());
        all.addAll(JournalConfig.getAllItemRestrictions());
        all.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
        all.addAll((List<String>) JournalConfig.FARMERS_DELIGHT_RESTRICTIONS.get());
        all.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_ARMORS.get());
        all.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_WEAPONS.get());
        all.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_SHIELDS.get());
        all.addAll((List<String>) JournalConfig.ROGUES_WARRIORS_ARMORS.get());
        all.addAll((List<String>) JournalConfig.ROGUES_WARRIORS_WEAPONS.get());
        all.addAll((List<String>) JournalConfig.ARCHERS_ARMORS.get());
        all.addAll((List<String>) JournalConfig.ARCHERS_WEAPONS.get());
        all.addAll((List<String>) JournalConfig.WIZARDS_ARMORS.get());
        all.addAll((List<String>) JournalConfig.WIZARDS_WEAPONS.get());
        all.addAll((List<String>) JournalConfig.ARSENAL_WEAPONS.get());
        all.addAll((List<String>) JournalConfig.ARTIFACTS_ITEMS.get());
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
}