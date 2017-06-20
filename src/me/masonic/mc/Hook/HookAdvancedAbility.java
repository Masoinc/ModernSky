package me.masonic.mc.Hook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Mason Project
 * 2017-6-20-0020
 */
public class HookAdvancedAbility {
    public void updateConfig() {
        File src = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\ModernSky\\MenuSettings.yml");
        File des = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\AdvancedAbilities\\MenuSettings.yml");
        try {
            nioTransferCopy(src, des);
        } catch (IOException e) {
            e.printStackTrace();
        }

        src = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\ModernSky\\Lang.yml");
        des = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\AdvancedAbilities\\Lang.yml");
        try {
            nioTransferCopy(src, des);
        } catch (IOException e) {
            e.printStackTrace();
        }

        src = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\ModernSky\\DEFAULT.yml");
        des = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\AdvancedAbilities\\menus\\DEFAULT.yml");
        try {
            nioTransferCopy(src, des);
        } catch (IOException e) {
            e.printStackTrace();
        }

        src = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\ModernSky\\ICONS.yml");
        des = new File("E:\\Minecraft\\现代空岛v2.0\\plugins\\AdvancedAbilities\\menus\\ICONS.yml");
        try {
            nioTransferCopy(src, des);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void nioTransferCopy(File source, File target) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert inStream != null;
            inStream.close();
            assert in != null;
            in.close();
            outStream.close();
            assert out != null;
            out.close();
        }
    }
}


