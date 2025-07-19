package com.taiyitistmc;

import com.taiyitistmc.config.TaiyitistConfigUtil;
import com.taiyitistmc.util.EulaUtil;
import com.taiyitistmc.util.I18n;
import com.mohistmc.i18n.i18n;
import java.util.Scanner;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaiyitistMCStart {

    public static i18n I18N;
    public static final Logger LOGGER = LogManager.getLogger("TaiyitistMC");
    public static final float javaVersion = Float.parseFloat(System.getProperty("java.class.version"));

    public static void run() throws Exception {
        TaiyitistConfigUtil.copyTaiyitistConfig();
        TaiyitistConfigUtil.lang();
        TaiyitistConfigUtil.i18n();
        TaiyitistConfigUtil.initAllNeededConfig();
        if (TaiyitistConfigUtil.showLogo()) {
            LOGGER.info(" _____       ___   _  __    __  _   _____   _   _____   _____  ");
            LOGGER.info("|_   _|     /   | | | \\ \\  / / | | |_   _| | | /  ___/ |_   _| ");
            LOGGER.info("  | |      / /| | | |  \\ \\/ /  | |   | |   | | | |___    | |   ");
            LOGGER.info("  | |     / /_| | | |   \\  /   | |   | |   | | \\___  \\   | |   ");
            LOGGER.info("  | |    / ___  | | |   / /    | |   | |   | |  ___| |   | |   ");
            LOGGER.info("  |_|   /_/   |_| |_|  /_/     |_|   |_|   |_| /_____/   |_|   ");
            LOGGER.info("{} {}, Java {}", I18n.as("taiyitist.launch.welcomemessage"), getVersion(), javaVersion);
        }
        if(I18N.isCN()) {
            System.out.printf("官方交流QQ群: 211128424%n");
            System.out.printf("如果控制台出现中文乱码请添加启动参数: -Dfile.encoding=GBK%n");
        }
        if (!EulaUtil.hasAcceptedEULA()) {
            System.out.println(I18n.as("eula"));
            while (!"true".equals(new Scanner(System.in).next()));
            EulaUtil.writeInfos();
        }
    }

    public static String getVersion() {
      return FabricLoader.getInstance().getModContainer("taiyitist").get().getMetadata().getVersion().getFriendlyString();
    }
}
