package com.taiyitistmc.fabric.permission;

import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.ServerOperator;

public class TaiyitistDummyPermissible extends PermissibleBase {

    public static final ServerOperator DUMMY_OPERATOR = new ServerOperator() {
        @Override
        public boolean isOp() {
            return false;
        }

        @Override
        public void setOp(boolean b) {}
    };
    public TaiyitistDummyPermissible() {
        super(DUMMY_OPERATOR);
    }
}