package unsafedodo.fabricauctionhouse.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Paths;

public class ConfigManager {

    public static File configFile = new File(Paths.get("", "config").toFile(), "auctionhouse.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ConfigData getConfigData(File configFile) throws FileNotFoundException, UnsupportedEncodingException {

        return configFile.exists() ? GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), ConfigData.class) : new ConfigData();
    }

    public static boolean loadConfig(){
        boolean success;
        try {
            ConfigData configData = getConfigData(configFile);

            {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
                writer.write(GSON.toJson(configData));
                writer.close();
            }

            success = true;

        } catch (IOException e){
            success = false;
        }

        return success;
    }
}
