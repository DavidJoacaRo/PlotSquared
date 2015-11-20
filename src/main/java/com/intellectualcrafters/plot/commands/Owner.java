////////////////////////////////////////////////////////////////////////////////////////////////////
// PlotSquared - A plot manager and world generator for the Bukkit API                             /
// Copyright (c) 2014 IntellectualSites/IntellectualCrafters                                       /
//                                                                                                 /
// This program is free software; you can redistribute it and/or modify                            /
// it under the terms of the GNU General Public License as published by                            /
// the Free Software Foundation; either version 3 of the License, or                               /
// (at your option) any later version.                                                             /
//                                                                                                 /
// This program is distributed in the hope that it will be useful,                                 /
// but WITHOUT ANY WARRANTY; without even the implied warranty of                                  /
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                   /
// GNU General Public License for more details.                                                    /
//                                                                                                 /
// You should have received a copy of the GNU General Public License                               /
// along with this program; if not, write to the Free Software Foundation,                         /
// Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA                               /
//                                                                                                 /
// You can contact us via: support@intellectualsites.com                                           /
////////////////////////////////////////////////////////////////////////////////////////////////////
package com.intellectualcrafters.plot.commands;

import java.util.HashSet;
import java.util.UUID;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
command = "setowner",
permission = "plots.set.owner",
description = "Set the plot owner",
usage = "/plot setowner <player>",
aliases = { "owner", "so", "seto" },
category = CommandCategory.ACTIONS,
requiredType = RequiredType.NONE)
public class Owner extends SetCommand {
    
    @Override
    public boolean set(PlotPlayer plr, Plot plot, String value) {
        HashSet<Plot> plots = MainUtil.getConnectedPlots(plot);
        final PlotPlayer other = UUIDHandler.getPlayer(value);
        UUID uuid = null;
        if (other == null) {
            if (Permissions.hasPermission(plr, "plots.admin.command.setowner")) {
                if ((uuid = UUIDHandler.getUUID(value, null)) == null) {
                    try {
                        uuid = UUID.fromString(value);
                    } catch (Exception e) {}
                }
            }
        }
        else {
            other.getUUID();
        }
        if (uuid == null) {
            MainUtil.sendMessage(plr, C.INVALID_PLAYER, value);
            return false;
        }
        String name = other == null ? MainUtil.getName(uuid) : other.getName();
        if (plot.isOwner(uuid)) {
            C.ALREADY_OWNER.send(plr);
            return false;
        }
        if (other != null && !Permissions.hasPermission(plr, "plots.admin.command.setowner")) {
            final int size = plots.size();
            final int currentPlots = (Settings.GLOBAL_LIMIT ? MainUtil.getPlayerPlotCount(other) : MainUtil.getPlayerPlotCount(plot.world, other)) + size;
            if (currentPlots > MainUtil.getAllowedPlots(other)) {
                sendMessage(plr, C.CANT_TRANSFER_MORE_PLOTS);
                return false;
            }
        }
        plot.setOwner(uuid);
        MainUtil.setSign(name, plot);
        MainUtil.sendMessage(plr, C.SET_OWNER);
        if (other != null) {
            MainUtil.sendMessage(other, C.NOW_OWNER, plot.world + ";" + plot.id);
        }
        return true;
    }
}