package ravenweave.client.module.modules.combat;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.weavemc.api.event.SubscribeEvent;
import org.lwjgl.input.Mouse;
import ravenweave.client.Raven;
import ravenweave.client.event.UpdateEvent;
import ravenweave.client.module.Module;
import ravenweave.client.module.modules.client.Targets;
import ravenweave.client.module.setting.impl.DescriptionSetting;
import ravenweave.client.module.setting.impl.DoubleSliderSetting;
import ravenweave.client.module.setting.impl.TickSetting;
import ravenweave.client.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class AimAssist extends Module {
    public static DoubleSliderSetting speed;
    public static TickSetting clickAim, weaponOnly, breakBlocks, blatantMode;
    @Getter
    public static ArrayList<Entity> friends = new ArrayList<>();

    public AimAssist() {
        super("AimAssist", ModuleCategory.combat);
        this.registerSetting(new DescriptionSetting("Set targets in Client->Targets"));
        this.registerSetting(speed = new DoubleSliderSetting("Speed", 45.0D, 15.0D, 2.0D, 100.0D, 1.0D));
        this.registerSetting(clickAim = new TickSetting("Click aim", true));
        this.registerSetting(breakBlocks = new TickSetting("Break blocks", true));
        this.registerSetting(weaponOnly = new TickSetting("Weapon only", false));
        this.registerSetting(blatantMode = new TickSetting("Blatant Mode", false));
    }

    @SubscribeEvent
    public void onLivingUpdate(UpdateEvent e) {
        if (Minecraft.getMinecraft().currentScreen != null) return;
        if (!Utils.Player.isPlayerInGame()) return;

        if (breakBlocks.isToggled() && mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if (p != null) {
                Block bl = mc.theWorld.getBlockState(p).getBlock();
                if (bl != Blocks.air && !(bl instanceof BlockLiquid)) return;
            }
        }

        if (weaponOnly.isToggled() && !Utils.Player.isPlayerHoldingWeapon()) return;
        Module autoClicker = Raven.moduleManager.getModuleByClazz(LeftClicker.class);
        if ((clickAim.isToggled() && Utils.Client.autoClickerClicking()) || (Mouse.isButtonDown(0) && autoClicker != null && !autoClicker.isEnabled()) || !clickAim.isToggled()) {
            EntityPlayer en = Targets.getTarget();
            if (en == null) return;
            if (Raven.debugger) {
                Utils.Player.sendMessageToSelf(this.getName() + " &e" + en.getName());
            }

            if (blatantMode.isToggled()) {
                //TODO:
                // Use OptimalAim to get rots
            } else {
                double n = Utils.Player.fovFromEntity(en);
                if (n > 1.0D || n < -1.0D) {
                    double complimentSpeed = n * (ThreadLocalRandom.current().nextDouble(speed.getInputMin() - 1.47328, speed.getInputMin() + 2.48293) / 100);
                    float val = (float) (-(complimentSpeed + n / (101.0D - (float) ThreadLocalRandom.current().nextDouble(speed.getInputMax() - 4.723847, speed.getInputMax()))));
                    mc.thePlayer.rotationYaw += val / 2;
                }
            }
        }
    }

    public static void addFriend(Entity entityPlayer) {
        friends.add(entityPlayer);
    }

    public static boolean addFriend(String name) {
        boolean found = false;
        for (Entity entity : mc.theWorld.getLoadedEntityList())
            if (entity.getName().equalsIgnoreCase(name) || entity.getCustomNameTag().equalsIgnoreCase(name))
                if (Targets.isAFriend(entity)) {
                    addFriend(entity);
                    found = true;
                }

        return found;
    }

    public static boolean removeFriend(String name) {
        boolean removed = false;
        boolean found = false;
        for (NetworkPlayerInfo networkPlayerInfo : new ArrayList<>(mc.getNetHandler().getPlayerInfoMap())) {
            Entity entity = mc.theWorld.getPlayerEntityByName(networkPlayerInfo.getDisplayName().getUnformattedText());
            if (entity.getName().equalsIgnoreCase(name) || entity.getCustomNameTag().equalsIgnoreCase(name)) {
                removed = removeFriend(entity);
                found = true;
            }
        }

        return found && removed;
    }

    public static boolean removeFriend(Entity entityPlayer) {
        try {
            friends.remove(entityPlayer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }
}