package com.optimus.rb80SMP.aztec.structure;

import com.optimus.rb80SMP.util.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class ChestDrop {

    private double chance;
    private ItemStack item;

    public ChestDrop(double chance, Material mat) {
        this(chance, new ItemStack(mat));
    }

    public boolean test() {
        return Util.random(0, 100) <= (chance * 100);
    }
}
