package com.optimus.rb80SMP.aztec;

import com.optimus.rb80SMP.SMP;
import com.optimus.rb80SMP.util.ItemBuilder;
import enchantmentapi.enchantmentapi.EnchantmentAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@AllArgsConstructor
public enum AztecItem {

    XIUHCOATL_SWORD(EnchantmentAPI.addEnchantment(new ItemBuilder(ChatColor.YELLOW + "Xiuhcoatl Sword", Material.DIAMOND_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 3).addEnchantment(Enchantment.FIRE_ASPECT, 2).toItemStack(), SMP.getPlugin().chimali, 3)),
    FEATHERED_BOW(new ItemBuilder(ChatColor.YELLOW + "Feathered Serpent Bow", Material.BOW).addEnchantment(Enchantment.ARROW_DAMAGE, 3).addEnchantment(Enchantment.ARROW_FIRE, 1).addEnchantment(Enchantment.ARROW_INFINITE, 1).toItemStack()),
    OBSIDIAN_PLATING(new ItemBuilder(ChatColor.YELLOW + "Obsidian Plating", Material.OBSIDIAN).addEnchantmentGlint().addLore("&7Combine with diamond", "&7armor to make", "&7obsidian armor.").toItemStack()),
    BLOOD(new ItemBuilder(ChatColor.RED + "Blood", Material.REDSTONE).addLore("&7Summoning Item").toItemStack()),
    HEART(new ItemBuilder(ChatColor.RED + "Heart", Material.BEETROOT).addLore("&7Summoning Item").toItemStack()),
    SKULL(new ItemBuilder(ChatColor.RED + "Skull", Material.SKELETON_SKULL).addLore("&7Summoning Item").toItemStack()),
    TISSUE(new ItemBuilder(ChatColor.RED + "Tissue", Material.BEEF).addLore("&7Summoning Item").toItemStack()),
    CODEX_STAND(new ItemBuilder(ChatColor.YELLOW + "Codex Table", Material.RESPAWN_ANCHOR).addLore("&7Villager Profession").toItemStack()),
    OBSIDIAN_SWORD(new ItemBuilder(ChatColor.WHITE + "Obsidian Sword", Material.NETHERITE_SWORD).addEnchantmentGlint().setAttribute(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)).toItemStack()),
    JAGUAR_TOTEM(new ItemBuilder(ChatColor.YELLOW + "Jaguar Totem", Material.TOTEM_OF_UNDYING).toItemStack()),
    EAGLE_TOTEM(new ItemBuilder(ChatColor.GREEN + "Eagle Totem", Material.TOTEM_OF_UNDYING).toItemStack()),
    ARROW_TOTEM(new ItemBuilder(ChatColor.RED + "Arrow Totem", Material.TOTEM_OF_UNDYING).toItemStack()),
    TEPULI_BOOK(EnchantmentAPI.addEnchantment(new ItemStack(Material.ENCHANTED_BOOK), SMP.getPlugin().tepuli, 1)),
    CHIMALI_BOOK(EnchantmentAPI.addEnchantment(new ItemStack(Material.ENCHANTED_BOOK), SMP.getPlugin().chimali, 1)),
    SPEAR(new ItemBuilder(ChatColor.YELLOW + "Spear", Material.TRIDENT).addEnchantmentGlint().addLore("&7one use trident").toItemStack());

    private final ItemStack item;

}
