package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.client.Minecraft;

public class mod_CAH extends BaseMod {
    @MLProp
    protected static String CAHVer = "1.3.2a";
    protected static String token = "Unknown";
    protected static long lastTexture = 0L;
    protected static long timeTexture = 20000L;
    protected static HashSet texSet = new HashSet();
    @MLProp
    public static String currentMD5 = "cf3486bd444726a9e6476345bb4e898a";
    @MLProp
    public static boolean spoofMAC = false;
    @MLProp
    public static String spoofedMAC = "0f:e4:de:10:ea:d3";

    public String getVersion() {
        return CAHVer;
    }

    public void load() {
        ModLoader.setInGameHook(this, true, true);
        System.out.println("CAH Bypass made by FluR0");
        System.out.println("Made for CAH 1.4.2b (Last modified date of CAH jar: 9/11/12)");
        System.out.println("Path Debug: " + mod_CAH.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public boolean onTickInGame(float var1, Minecraft var2) {
        if (!var2.running) {
            reset();
        } else {
            if (token.equals("Unknown")) {
                return true;
            }

            checkTextures();
        }

        return true;
    }

    public static void reset() {
        token = "Unknown";
        texSet.clear();
        lastTexture = 0L;
    }

    public static void sendToken(String var0) {
        ModLoader.clientSendPacket(new Packet3Chat("@" + token + " " + var0));
        System.out.println("sendToken Method Invoked! Message:" + var0);
    }

    public static void sendRequest() {
        ModLoader.clientSendPacket(new Packet3Chat("/cah Request" + CAHVer));
    }

    public static void sendDebug(String var0) {
        ModLoader.clientSendPacket(new Packet3Chat(var0));
    }

    public static void sendAuthentication(String var0) {
        String[] var1 = var0.split(" ");
        token = var1[var1.length - 1];

        System.out.println("CAH Token: " + token.toString());
        if (!checkHack()) {
            File var2 = new File(getPath());
            String var3 = currentMD5;

            if (var3 != null) {
                sendToken("Auth " + var3 + " " + getIdentity());
                System.out.println("Sent spoofed md5 to CAH");
            } else {
                sendToken("Disconnect");
            }
        }
    }

    public static String getIdentity() {
        if (spoofMAC) {
            return spoofedMAC;
        } else {
            try {
                byte[] var0 = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
                StringBuilder var7 = new StringBuilder(18);
                byte[] var2 = var0;
                int var3 = var0.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    byte var5 = var2[var4];

                    if (var7.length() > 0) {
                        var7.append(':');
                    }

                    var7.append(String.format("%02x", new Object[]{Byte.valueOf(var5)}));
                }

                return var7.toString();
            } catch (Exception var6) {
                String var1 = "?" + mod_CAH.class.getProtectionDomain().getCodeSource().getLocation().getPath();

                if (var1.length() > 15) {
                    var1 = var1.substring(0, 16);
                }

                return var1;
            }
        }
    }

    public static void checkTextures() {
        if (System.currentTimeMillis() - lastTexture >= timeTexture) {
            lastTexture = System.currentTimeMillis();
            File var0 = new File(getHome() + "/texturepacks");

            if (var0.exists() && !var0.isFile() && var0.isDirectory()) {
                File[] var1 = var0.listFiles();
                int var2 = var1.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    File var4 = var1[var3];

                    if (var4.isFile() && !useFile(var4)) {
                        sendToken("TX " + md5(var4));
                    }
                }
            }
        }
    }

    public static boolean checkHack() {
        HashMap var0 = new HashMap();
        var0.put("MyCraft", getHome() + "/bin/mycraft");
        var0.put("MyCraft", getHome() + "/mycraft");
        var0.put("Magic", getHome() + "/bin/magic");
        var0.put("Magic", getHome() + "/magic");
        Iterator var1 = var0.keySet().iterator();
        File var3;

        do {
            if (!var1.hasNext()) {
                return false;
            }

            String var2 = (String) var1.next();
            var3 = new File((String) var0.get(var2));
        }
        while (!var3.exists());

        deleteFile(var3);
        ModLoader.clientSendPacket(new Packet3Chat("/bypass Token: 682234"));
        return true;
    }

    public static String getPath() {
        String var0 = mod_CAH.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String var1 = "";

        for (int var2 = 0; var2 + 2 < var0.length(); ++var2) {
            if (var0.charAt(var2) == 37 && var0.charAt(var2 + 1) == 50 && var0.charAt(var2 + 2) == 48) {
                var1 = var1 + " ";
                var2 += 2;
            } else {
                var1 = var1 + var0.charAt(var2);
            }
        }

        var1 = var1 + var0.charAt(var0.length() - 2);
        var1 = var1 + var0.charAt(var0.length() - 1);
        return var1;
    }

    public static boolean deleteFile(File var0) {
        if (var0.exists()) {
            File[] var1 = var0.listFiles();

            for (int var2 = 0; var2 < var1.length; ++var2) {
                if (var1[var2].isDirectory()) {
                    deleteFile(var1[var2]);
                } else {
                    var1[var2].delete();
                }
            }
        }

        return var0.delete();
    }

    public static String getHome() {
        String var0 = getPath();
        int var1 = var0.length();
        int var2;

        for (var2 = 0; var2 < var0.length(); ++var2) {
            if (var0.charAt(var2) == 92 || var0.charAt(var2) == 47) {
                var1 = var2;
            }
        }

        var0 = var0.substring(0, var1);
        var1 = var0.length();

        for (var2 = 0; var2 < var0.length(); ++var2) {
            if (var0.charAt(var2) == 92 || var0.charAt(var2) == 47) {
                var1 = var2;
            }
        }

        return var0.substring(0, var1);
    }

    public static boolean useFile(File var0) {
        String var1 = var0.getName();
        File var2 = new File(var0.getAbsolutePath() + "/temp");
        return var0.renameTo(var2) ? var2.renameTo(var0) : false;
    }

    public static String md5(File var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            File var2 = new File(getPath());
            FileInputStream var3 = new FileInputStream(var2);
            byte[] var4 = new byte[8192];
            boolean var5 = false;
            int var6;

            while ((var6 = var3.read(var4)) > 0) {
                var1.update(var4, 0, var6);
            }

            byte[] var7 = var1.digest();
            BigInteger var8 = new BigInteger(1, var7);
            String var9 = var8.toString(16);
            var3.close();
            return var9;
        } catch (Exception var10) {
            sendToken("Disconnect");
            sendToken("Report Authentication: Failure");
            return null;
        }
    }
}
