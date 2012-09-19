package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Command_lockup extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length != 2)
        {
            return false;
        }

        final Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);

        if (args[1].equalsIgnoreCase("on"))
        {
            cancelLockup(playerdata);

            playerdata.setLockupScheduleID(server.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
            {
                private Random random = new Random();

                @Override
                public void run()
                {
                    p.openWorkbench(null, true);

                    Location l = p.getLocation().clone();
                    l.setPitch(random.nextFloat() * 360.0f);
                    l.setYaw(random.nextFloat() * 360.0f);
                    p.teleport(l);
                }
            }, 0L, 5L));

            sender.sendMessage(ChatColor.GRAY + "Locking up " + p.getName());
        }
        else if (args[1].equalsIgnoreCase("off"))
        {
            cancelLockup(playerdata);

            sender.sendMessage(ChatColor.GRAY + "Not locking up " + p.getName() + " anymore.");
        }
        else
        {
            return false;
        }

        return true;
    }

    private void cancelLockup(TFM_UserInfo playerdata)
    {
        BukkitScheduler scheduler = server.getScheduler();
        int lockupScheduleID = playerdata.getLockupScheduleID();
        if (lockupScheduleID != -1)
        {
            scheduler.cancelTask(lockupScheduleID);
            playerdata.setLockupScheduleID(-1);
        }
    }
}
