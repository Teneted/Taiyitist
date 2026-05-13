package org.teneted.taiyitist.bukkit;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;

public class DelegatePermissionSet implements PermissionSet {

    private final PermissionSet handle;
    private final CommandSourceStack stack;

    public DelegatePermissionSet(PermissionSet handle, CommandSourceStack stack) {
        this.handle = handle;
        this.stack = stack;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        boolean hasPermission = handle.hasPermission(permission);

        CommandNode currentCommand = stack.bridge$getCurrentCommand();
        if (currentCommand != null) {
            return hasPermission(hasPermission, org.bukkit.craftbukkit.command.VanillaCommandWrapper.getPermission(currentCommand));
        }

        if (permission.equals(Permissions.COMMANDS_ENTITY_SELECTORS)) {
            return hasPermission(hasPermission, "minecraft.command.selector");
        }

        return hasPermission;
    }

    public boolean hasPermission(boolean hasPermission, String bukkitPermission) {
        // World is null when loading functions
        return ((stack.getLevel() == null || !stack.getLevel().getCraftServer().ignoreVanillaPermissions) && hasPermission) || stack.getBukkitSender().hasPermission(bukkitPermission);
    }
    // CraftBukkit end
}
