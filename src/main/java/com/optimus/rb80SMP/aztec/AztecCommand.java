package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.aztec.entity.npc.LoneWanderer;
import com.optimus.rb80SMP.aztec.entity.npc.Purempecha;
import com.optimus.rb80SMP.aztec.entity.npc.Tezcatlipoca;
import com.optimus.rb80SMP.aztec.entity.npc.TribeMember;
import com.optimus.rb80SMP.aztec.gui.CodexTable;
import com.optimus.rb80SMP.aztec.structure.Generator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AztecCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (args[0].equals("item")) {
            p.getInventory().addItem(AztecItem.valueOf(args[1].toUpperCase()).getItem());
        } else if (args[0].equals("mob")) {
            if (args[1].equals("purempecha")) {
                new Purempecha().spawn(p.getLocation());
            } else {
                new Tezcatlipoca().spawn(p.getLocation());
            }
        } else if (args[0].equals("generatestructures")) {
            for (int i = 0; i < 15; i++) {
                Generator.generateStructure(Generator.Structure.TEMPLE);
                Generator.generateStructure(Generator.Structure.FORTRESS);
            }

            for (int i = 0; i < 8; i++) {
                Generator.generateStructure(Generator.Structure.PYRAMID);
            }

            for (int i = 0; i < 40; i++) {
                Generator.generateStructure(Generator.Structure.TOMB);
            }
        } else if (args[0].equals("gui")) {
            p.openInventory(new CodexTable());
        }
        return false;
    }
}
