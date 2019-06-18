package ItemSave;

import SILib.InventoryLib.InventoryMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class Main extends JavaPlugin {
    FileConfiguration configuration;
    HashMap<String,ItemStack> itemmap = new HashMap<>();
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"插件已经加载 By si_12");
        saveDefaultConfig();
        reloadConfig();
        configuration = getConfig();
        loaditem();
    }
    private void loaditembybianhao(String bianhao){
        File file = new File(getDataFolder().getPath(),bianhao+".yml");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        }
        catch (Exception e){
            e.printStackTrace();
        }
            Integer id = yamlConfiguration.getInt(bianhao + ".ID");
            String display = yamlConfiguration.getString(bianhao + ".DISPLAY");
            List<String> lore = yamlConfiguration.getStringList(bianhao + ".LORE");
            Map<Enchantment, Integer> enchantment = new HashMap<>();
            for (String en : yamlConfiguration.getStringList(bianhao + ".ENCHANT")) {
                Enchantment enc = Enchantment.getByName(en.substring(0, en.indexOf(":")));

                Integer level = Integer.parseInt(en.substring(en.indexOf(":")+1));
                enchantment.put(enc, level);
            }
            Set<ItemFlag> flag = null;
            for (String f : yamlConfiguration.getStringList(bianhao + ".FLAG")) {
                ItemFlag fg = ItemFlag.valueOf(f);
                flag.add(fg);
            }
            ItemStack item = new ItemStack(Material.getMaterial(id));
            ItemMeta im = item.getItemMeta();
            if (display == null) {
            } else {
                im.setDisplayName(display);
            }
            if (lore.isEmpty()) {
            } else {
                im.setLore(lore);
            }
            if (enchantment == null) {
            } else {
                for (Enchantment enchantment1 : enchantment.keySet()) {
                    im.addEnchant(enchantment1, enchantment.get(enchantment1), true);
                }

            }
            if (flag == null) {
            } else {
                for (ItemFlag f : flag) {
                    im.addItemFlags(f);
                }
            }
            item.setItemMeta(im);
            itemmap.put(bianhao,item);
    }
    private void loaditem(){
        File file = new File(getDataFolder().getPath());
        for(File k : file.listFiles()) {
            if(k.getName().equalsIgnoreCase("config.yml")){
                return;
            }
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(k);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (String bianhao : yamlConfiguration.getKeys(false)) {
                Integer id = yamlConfiguration.getInt(bianhao + ".ID");
                String display = yamlConfiguration.getString(bianhao + ".DISPLAY");
                List<String> lore = yamlConfiguration.getStringList(bianhao + ".LORE");
                Map<Enchantment, Integer> enchantment = new HashMap<>();
                for (String en : yamlConfiguration.getStringList(bianhao + ".ENCHANT")) {
                    Enchantment enc = Enchantment.getByName(en.substring(0, en.indexOf(":")));
                    Integer level = Integer.parseInt(en.substring(en.indexOf(":") + 1));
                    enchantment.put(enc, level);
                }
                Set<ItemFlag> flag = new HashSet<>();
                for (String f : yamlConfiguration.getStringList(bianhao + ".FLAG")) {
                    ItemFlag fg = ItemFlag.valueOf(f);
                    flag.add(fg);
                }
                ItemStack item = new ItemStack(Material.getMaterial(id));
                ItemMeta im = item.getItemMeta();
                if (display == null) {
                } else {
                    im.setDisplayName(display);
                }
                if (lore.isEmpty()) {
                } else {
                    im.setLore(lore);
                }
                if (enchantment == null) {
                } else {
                    for (Enchantment enchantment1 : enchantment.keySet()) {
                        im.addEnchant(enchantment1, enchantment.get(enchantment1), true);
                    }

                }
                if (flag == null) {
                } else {
                    for (ItemFlag f : flag) {
                        im.addItemFlags(f);
                    }
                }
                item.setItemMeta(im);
                itemmap.put(bianhao, item);
            }
        }
    }
    private void getitem(String bianhao,Player p){
        ItemStack item = itemmap.get(bianhao);
        ItemMeta im = item.getItemMeta();
        String display = im.getDisplayName();
        if(display!=null){
            display = display.replaceAll("%p",p.getName());
            im.setDisplayName(display);
        }
        List<String> lore = im.getLore();
        if(lore==null){

        }
        else if(!lore.isEmpty()){
            List<String> newlore = new ArrayList<>();
            for(String k:lore){
                newlore.add(k.replaceAll("%p",p.getName()));
                if(k.indexOf("无法破坏")!=-1){
                    im.setUnbreakable(true);
                }
            }
            im.setLore(newlore);
        }
        item.setItemMeta(im);
        if(InventoryMethod.addItem(item,p)>0){
            p.sendMessage("§c你的背包已满 多余物品已掉落 请迅速拾起");
        }

    }
    private void saveitem(ItemStack i,String bianhao){
        ItemMeta im = i.getItemMeta();
        Set<ItemFlag> flag = im.getItemFlags();
        Map<Enchantment,Integer> enchant = im.getEnchants();
        try {
            YamlConfiguration yamlConfiguration = (YamlConfiguration) getConfig();
            yamlConfiguration.set(bianhao,"");
            saveConfig();

            BufferedWriter writer;
            File item = new File(getDataFolder().getPath(),bianhao+".yml");
            FileOutputStream writerStream = new FileOutputStream(item);
            writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(bianhao+":");
            writer.newLine();
            writer.write(" ID: "+i.getTypeId());
            writer.newLine();
            if(im.hasDisplayName()){writer.write(" DISPLAY: "+im.getDisplayName());}
            else{writer.write(" DISPLAY: ");}
            writer.newLine();
            writer.write(" LORE: ");
            if(im.getLore()==null){}
            else {
                for (String lore : im.getLore()) {
                    writer.newLine();
                    writer.write("  - '" + lore+"'");
                }
            }
            writer.newLine();
            writer.write(" ENCHANT:");
            if(enchant==null){}
            else {
                for (Enchantment enchantment : enchant.keySet()) {
                    writer.newLine();
                    writer.write("  - " + enchantment.getName() + ":" + enchant.get(enchantment));
                }
            }
            writer.newLine();
            writer.write(" FLAG:");
            if(flag==null){}
            else {
                for (ItemFlag k : flag) {
                    writer.newLine();
                    writer.write("  - " + k);
                }
            }

            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(sender.isOp()){

            }
            else{
                return true;
            }
            if(args.length==2) {
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("save")) {
                    ItemStack item = p.getItemInHand();
                    if (item.getType()==Material.AIR) {
                        p.sendMessage("§c你的手上没有物品");
                        return true;
                    }
                    String bianhao = args[1];
                    if (configuration.getKeys(false).contains(bianhao)) {
                        p.sendMessage("§c已经有相同编号的物品存在");
                    } else {
                        saveitem(item,bianhao);
                        loaditembybianhao(bianhao);
                        p.sendMessage("§a物品§e["+bianhao+"]§a保存成功");
                    }
                }
                else if(args[0].equalsIgnoreCase("get")){
                    String bianhao = args[1];
                    if(configuration.getKeys(false).contains(bianhao)){
                        getitem(bianhao,p);
                    }
                    else{
                        p.sendMessage("§c没有找到该编号物品");
                    }
                }
            }
            else if(args.length==1){
                if(args[0].equalsIgnoreCase("reload")){
                    loaditem();
                    sender.sendMessage("§a配置重载成功");
                }
            }
            else{
                sender.sendMessage("§c§l物品存取");
                sender.sendMessage("§c/itemsave get <编号> 获取物品");
                sender.sendMessage("§c/itemsave save <编号> 储存物品");
                sender.sendMessage("§c/itemsave reload 重载配置");
            }
        }
        else{return true;}
        return true;
    }
}
