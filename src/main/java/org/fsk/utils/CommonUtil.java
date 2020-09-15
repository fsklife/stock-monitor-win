package org.fsk.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fsk.Constants;
import org.fsk.pojo.SettingFileDTO;
import org.fsk.pojo.SharesDTO;
import org.fsk.pojo.StockInfoObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.fsk.Constants.FILE_PATH;

/**
 * @author fanshk
 * @date 2020/9/13 18:29
 */
@Slf4j
public class CommonUtil {

    public static String getPriceRate(String firstPrice, String secondPrice) {
        try {
            BigDecimal firstBig = new BigDecimal(firstPrice);
            BigDecimal secondBig = new BigDecimal(secondPrice);
            BigDecimal rate = (firstBig.subtract(secondBig))
                    .divide(firstBig, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            return rate.doubleValue() + "%";
        } catch (Exception e) {
            log.error("生成价格比例异常：", e);
        }
        return "";
    }

    public static Optional<ButtonType> showAlertConfirmText(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认提示");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(text);

        return alert.showAndWait();
    }

    public static void showAlertErrorText(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误提示");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static void showAlertWarningText(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static void showAlertInfoText(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息提示");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static List<SharesDTO> getSharesList() {
        File dataFile = CommonUtil.getDataFile();
        if (dataFile != null) {
            String data = CommonUtil.txt2String(dataFile);
            data = Base64Util.decodeBase64(data);
            if (StringUtils.isNoneBlank(data)) {
                SettingFileDTO fileDTO = JacksonUtil.parsePojo(data, SettingFileDTO.class);
                List<SharesDTO> sharesList = fileDTO.getShares();
                if (sharesList != null && sharesList.size() > 0) {
                    return sharesList;
                }
            }
        }
        return null;
    }

    public static SettingFileDTO getSettingFile() {
        File dataFile = CommonUtil.getDataFile();
        if (dataFile != null) {
            String data = CommonUtil.txt2String(dataFile);
            data = Base64Util.decodeBase64(data);
            if (StringUtils.isNoneBlank(data)) {
                SettingFileDTO fileDTO = JacksonUtil.parsePojo(data, SettingFileDTO.class);
                return fileDTO;
            }
        }
        return null;
    }

    public static boolean updateDataFile(SettingFileDTO fileDTO) {
        return addDataFile(JacksonUtil.toJson(fileDTO), getDataFile().getPath());
    }

    public static File getDataFile() {
        try {
            File dic = new File(FILE_PATH);
            if (!dic.exists() && !dic.isDirectory()) {
                dic.mkdirs();
            }
            File file = new File(FILE_PATH + "\\data.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            log.error("获取文件异常：", e);
        }
        return null;
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            //构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            //使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            log.error("读取文件异常：", e);
        }
        return result.toString().replace("\r", "").replace("\n", "");
    }

    /**
     * base64编码
     *
     * @param string
     * @param path
     * @return
     */
    private static boolean addDataFile(String string, String path) {
        return addFile(Base64Util.encodeBase64(string), path);
    }

    public static boolean addFile(String string, String path) {

        PrintStream stream = null;
        try {
            //写入的文件path
            stream = new PrintStream(path);
            //写入的字符串
            stream.print(string);
            return true;
        } catch (FileNotFoundException e) {
            log.error("覆盖写入文件异常：", e);
        }
        return false;
    }

    public static String getStockCode(String code) {
        if (code.startsWith("6")) {
            return Constants.SH + code;
        } else if (code.startsWith("0") || code.startsWith("3")) {
            return Constants.SZ + code;
        }
        return null;
    }

    public static Map<String, StockInfoObject> callStockData(Set<String> stockSet) {
        String url = "http://hq.sinajs.cn/list=" + String.join(",", stockSet);
        String result = OkHttpUtil.get(url, null);
        if (result.contains("FAILED")) {
            return null;
        }
        return analyzeResult(result);
    }

    private static Map<String, StockInfoObject> analyzeResult(String result) {
        Map<String, StockInfoObject> map = new HashMap<>();
        String[] split = result.split(";\n");
        for (String str : split) {
            String[] hqArr = str.split("var hq_str_");
            String gpStr = hqArr[1];
            String code = gpStr.substring(0, 8);
            String gpInfo = gpStr.substring(10, gpStr.length() - 1);
            //股票名称、今日开盘价、昨日收盘价、当前价格、今日最高价、今日最低价、竞买价、竞卖价、成交股数、成交金额、买1手、买1报价、买2手、买2报价、…、买5报价、…、卖5报价、日期、时间
            String[] gupInfoArr = gpInfo.split(",");
            StockInfoObject stockInfo = new StockInfoObject();
            stockInfo.setStCode(code);
            stockInfo.setGpName(gupInfoArr[0]);
            stockInfo.setOpen(gupInfoArr[1]);
            String prevClose = gupInfoArr[2];
            stockInfo.setPrevClose(prevClose);
            String currPrice = gupInfoArr[3];
            stockInfo.setCurrentPrice(currPrice);
            stockInfo.setMaxPrice(gupInfoArr[4]);
            stockInfo.setMinPrice(gupInfoArr[5]);
            stockInfo.setDate(gupInfoArr[gupInfoArr.length - 3]);
            stockInfo.setTime(gupInfoArr[gupInfoArr.length - 2]);
            BigDecimal prevCloseBig = new BigDecimal(prevClose);
            BigDecimal currPriceBig = new BigDecimal(currPrice);
            BigDecimal changePriceBig = currPriceBig.subtract(prevCloseBig);
            BigDecimal rateBig = changePriceBig.divide(prevCloseBig, 4, BigDecimal.ROUND_HALF_UP);
            String vol = rateBig.multiply(new BigDecimal("100")).setScale(2).toString() + "%";
            stockInfo.setChgRate(vol);
            stockInfo.setDeltaPrice(changePriceBig.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
            BigDecimal volPriceBig = new BigDecimal(gupInfoArr[9]);
            BigDecimal volPrice = volPriceBig.divide(new BigDecimal("100000000"), 2, BigDecimal.ROUND_HALF_UP);
            stockInfo.setVolPrice(volPrice.floatValue() + "亿");
            map.put(code, stockInfo);
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println(getSettingFile());
    }
}
