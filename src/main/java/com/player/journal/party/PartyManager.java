package com.player.journal.party;

import com.player.journal.network.SyncPartyPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class PartyManager {

    private static final Map<UUID, Set<UUID>> parties = new HashMap<>();
    private static final Map<UUID, UUID> playerToParty = new HashMap<>();
    private static final Map<UUID, UUID> pendingInvites = new HashMap<>();

    public static void invitePlayer(ServerPlayer inviter, ServerPlayer target) {
        UUID inviterId = inviter.getUUID();
        UUID targetId = target.getUUID();

        if (inviterId.equals(targetId)) {
            inviter.displayClientMessage(Component.literal("§cYou cannot invite yourself!"), false);
            return;
        }

        if (playerToParty.containsKey(targetId)) {
            inviter.displayClientMessage(Component.literal("§c" + target.getName().getString() + " is already in a party."), false);
            return;
        }

        UUID partyId = playerToParty.getOrDefault(inviterId, inviterId);
        Set<UUID> members = parties.getOrDefault(partyId, new HashSet<>(Set.of(inviterId)));

        if (members.size() >= 3) {
            inviter.displayClientMessage(Component.literal("§cYour party is full! (Maximum 3 players)"), false);
            return;
        }

        pendingInvites.put(targetId, inviterId);
        inviter.displayClientMessage(Component.literal("§aInvited " + target.getName().getString() + " to the party!"), false);
        target.displayClientMessage(Component.literal("§e" + inviter.getName().getString() + " has invited you to their party! Type §b/party accept§e to join."), false);
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(target, new com.player.journal.network.ReceivePartyInvitePayload(inviter.getName().getString()));

    }

    public static void acceptInvite(ServerPlayer target) {
        UUID targetId = target.getUUID();

        if (!pendingInvites.containsKey(targetId)) {
            target.displayClientMessage(Component.literal("§cYou do not have any pending party invites."), false);
            return;
        }

        UUID inviterId = pendingInvites.remove(targetId);

        UUID partyId = playerToParty.getOrDefault(inviterId, inviterId);
        Set<UUID> members = parties.computeIfAbsent(partyId, k -> new HashSet<>(Set.of(inviterId)));

        if (members.size() >= 3) {
            target.displayClientMessage(Component.literal("§cThat party is already full!"), false);
            return;
        }

        members.add(targetId);
        playerToParty.put(inviterId, partyId);
        playerToParty.put(targetId, partyId);

        broadcastToParty(target.getServer(), partyId, "§a" + target.getName().getString() + " has joined the party!");
        syncParty(target.getServer(), partyId);
    }

    public static void declineInvite(ServerPlayer target) {
        UUID targetId = target.getUUID();

        if (!pendingInvites.containsKey(targetId)) {
            target.displayClientMessage(Component.literal("§cYou do not have any pending party invites."), false);
            return;
        }


        UUID inviterId = pendingInvites.remove(targetId);


        if (target.getServer() != null) {
            ServerPlayer inviter = target.getServer().getPlayerList().getPlayer(inviterId);
            if (inviter != null) {
                inviter.displayClientMessage(Component.literal("§c" + target.getName().getString() + " declined your party invite."), false);
            }
        }

        target.displayClientMessage(Component.literal("§eYou declined the party invite."), false);
    }

    public static void leaveParty(ServerPlayer player) {
        UUID playerId = player.getUUID();

        if (!playerToParty.containsKey(playerId)) {
            player.displayClientMessage(Component.literal("§cYou are not in a party."), false);
            return;
        }

        UUID partyId = playerToParty.remove(playerId);
        Set<UUID> members = parties.get(partyId);

        if (members != null) {
            members.remove(playerId);
            broadcastToParty(player.getServer(), partyId, "§e" + player.getName().getString() + " has left the party.");

            if (members.size() <= 1) {
                for (UUID remainingId : members) {
                    playerToParty.remove(remainingId);
                    ServerPlayer remaining = player.getServer().getPlayerList().getPlayer(remainingId);
                    if (remaining != null) {
                        remaining.displayClientMessage(Component.literal("§cThe party has been disbanded."), false);
                        clearClientParty(remaining);
                    }
                }
                parties.remove(partyId);
            } else {
                syncParty(player.getServer(), partyId);
            }
        }

        clearClientParty(player);
        player.displayClientMessage(Component.literal("§aYou left the party."), false);
    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        UUID playerId = player.getUUID();
        pendingInvites.remove(playerId);

        if (!playerToParty.containsKey(playerId)) {
            return;
        }

        UUID partyId = playerToParty.remove(playerId);
        Set<UUID> members = parties.get(partyId);

        if (members != null) {
            members.remove(playerId);
            broadcastToParty(player.getServer(), partyId, "§e" + player.getName().getString() + " disconnected and left the party.");

            if (members.size() <= 1) {
                for (UUID remainingId : members) {
                    playerToParty.remove(remainingId);
                    ServerPlayer remaining = player.getServer().getPlayerList().getPlayer(remainingId);
                    if (remaining != null) {
                        remaining.displayClientMessage(Component.literal("§cThe party has been disbanded."), false);
                        clearClientParty(remaining);
                    }
                }
                parties.remove(partyId);
            } else {
                syncParty(player.getServer(), partyId);
            }
        }
    }

    public static List<UUID> getPartyMembers(UUID playerId) {
        UUID partyId = playerToParty.get(playerId);
        if (partyId != null && parties.containsKey(partyId)) {
            return new ArrayList<>(parties.get(partyId));
        }
        return new ArrayList<>();
    }

    private static void broadcastToParty(MinecraftServer server, UUID partyId, String message) {
        if (server == null || partyId == null || !parties.containsKey(partyId)) return;
        for (UUID memberId : parties.get(partyId)) {
            ServerPlayer member = server.getPlayerList().getPlayer(memberId);
            if (member != null) {
                member.displayClientMessage(Component.literal(message), false);
            }
        }
    }

    private static void syncParty(MinecraftServer server, UUID partyId) {
        if (server == null || partyId == null || !parties.containsKey(partyId)) return;

        List<SyncPartyPayload.PartyMemberData> roster = new ArrayList<>();
        for (UUID memberId : parties.get(partyId)) {
            ServerPlayer member = server.getPlayerList().getPlayer(memberId);
            if (member != null) {
                roster.add(new SyncPartyPayload.PartyMemberData(member.getUUID(), member.getName().getString(), true));
            }
        }

        SyncPartyPayload payload = new SyncPartyPayload(roster);
        for (UUID memberId : parties.get(partyId)) {
            ServerPlayer member = server.getPlayerList().getPlayer(memberId);
            if (member != null) {
                PacketDistributor.sendToPlayer(member, payload);
            }
        }
    }

    private static void clearClientParty(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SyncPartyPayload(new ArrayList<>()));
    }
    public static void syncPartyForPlayer(ServerPlayer receiver) {
        UUID partyId = playerToParty.get(receiver.getUUID());
        if (partyId == null || !parties.containsKey(partyId)) return;

        List<SyncPartyPayload.PartyMemberData> roster = new ArrayList<>();
        for (UUID memberId : parties.get(partyId)) {
            ServerPlayer member = receiver.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                boolean inRange = true;

                if (!receiver.getUUID().equals(memberId)) {

                    if (receiver.level() != member.level() || receiver.distanceToSqr(member) > 10000) {
                        inRange = false;
                    }
                }
                roster.add(new SyncPartyPayload.PartyMemberData(member.getUUID(), member.getName().getString(), inRange));
            }
        }

        PacketDistributor.sendToPlayer(receiver, new SyncPartyPayload(roster));
    }
}