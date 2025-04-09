//package asia.virtualmc.vLibrary.core.cryptocurrency;
//
//import asia.virtualmc.vLibrary.VLibrary;
//import asia.virtualmc.vLibrary.utilities.miscellaneous.ChartUtils;
//import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
//import asia.virtualmc.vLibrary.utilities.file.JSONUtils;
//import asia.virtualmc.vLibrary.utilities.file.YAMLUtils;
//import asia.virtualmc.vLibrary.utilities.messages.ConsoleMessageUtils;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import org.jetbrains.annotations.NotNull;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BitcoinManager {
//    private final VLibrary vlib;
//    private static String KEY;
//
//    public BitcoinManager(@NotNull VLibrary vlib) {
//        this.vlib = vlib;
//        initialize();
//        List<Double> numbers = new ArrayList<>();
//
//        for (int i = 1; i < 11; i++) {
//            numbers.add((double) i);
//        }
//
//        ChartUtils.createGraph(vlib, numbers, "cryptocurrency/image", 640, 480);
//    }
//
//    private void initialize() {
//        KEY = YAMLUtils.getYamlDocument(vlib, "cryptocurrency/settings.yml").getString("KEY");
//
//        if (KEY == null) {
//            vlib.getLogger().severe("Couldn't find KEY from cryptocurrency/settings.yml!");
//            return;
//        }
//
//        ConsoleMessageUtils.sendConsoleMessage("Successfully added KEY for cryptocurrency feature.");
//    }
//
//    private void getBitcoinPrice() {
//        Double price = getCryptoPrice("bitcoin", KEY);
//
//        if (price == null) {
//            vlib.getLogger().severe("Unable to fetch bitcoin's current price.");
//            return;
//        }
//
//        storeCurrentPrice(DigitUtils.getPreciseValue(price, 2));
//    }
//
//    private void storeCurrentPrice(double price) {
//        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Singapore"));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmm");
//        String dateTime = now.format(formatter);
//
//        JSONUtils.addKeyValue(vlib, "cryptocurrency/storage.json", dateTime, price);
//    }
//
//    public Double getCryptoPrice(String CRYPTO_NAME, String API_KEY) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//        try {
//            String urlString = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" + CRYPTO_NAME.toUpperCase();
//            URL url = new URL(urlString);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("X-CMC_PRO_API_KEY", API_KEY);
//            connection.setRequestProperty("Accept", "application/json");
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode != 200) return null;
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder responseBody = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                responseBody.append(line);
//            }
//            reader.close();
//
//            JsonObject json = gson.fromJson(responseBody.toString(), JsonObject.class);
//            JsonObject data = json.getAsJsonObject("data");
//            JsonObject crypto = data.getAsJsonObject(CRYPTO_NAME.toUpperCase());
//            JsonObject quote = crypto.getAsJsonObject("quote").getAsJsonObject("USD");
//
//            return quote.get("price").getAsDouble();
//
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
