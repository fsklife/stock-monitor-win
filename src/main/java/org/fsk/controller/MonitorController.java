package org.fsk.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fsk.Constants;
import org.fsk.Main;
import org.fsk.SettingApplication;
import org.fsk.StageManager;
import org.fsk.pojo.SettingFileDTO;
import org.fsk.pojo.SharesDTO;
import org.fsk.pojo.StockHistoryObject;
import org.fsk.pojo.StockInfoObject;
import org.fsk.pojo.StockMaTable;
import org.fsk.pojo.StockMarketDTO;
import org.fsk.pojo.StockTable;
import org.fsk.pojo.StrategyTable;
import org.fsk.pojo.ZsTable;
import org.fsk.service.MonitorScheduledService;
import org.fsk.utils.CommonUtil;
import org.fsk.utils.JacksonUtil;
import org.fsk.utils.OkHttpUtil;
import org.fsk.utils.TransTimeUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

@Slf4j
public class MonitorController implements Initializable {
    @FXML
    public TableView<ZsTable> zsTable;
    @FXML
    public TableView<StockTable> stockTable;
    @FXML
    public TableView<StockMaTable> stockMaTable;
    @FXML
    public TableView<StrategyTable> strategyTable;
    public TextArea zsRemarkTextArea;
    @FXML
    public Label transTime;
    @FXML
    public Label transDate;
    @FXML
    public Button startBtn;
    @FXML
    public Label quarterVol;
    @FXML
    public Label halfHourVol;

    public static Map<String, Object> map = new HashMap<>();

    private static MonitorScheduledService scheduledService = MonitorScheduledService.getInstance();

    private static boolean initFlag = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StageManager.putMainCtrl(this);
        SettingFileDTO settingFile = CommonUtil.getSettingFile();
        if (settingFile != null) {
            StockMarketDTO stock = settingFile.getStock();
            if (stock != null) {
                zsRemarkTextArea.setText(stock.getRemark());
            }
            List<SharesDTO> sharesList = settingFile.getShares();
            if (sharesList != null) {
                sharesList.forEach(sharesDTO -> {
                    Constants.stockSet.add(sharesDTO.getCode());
                });
            }
        }
        scheduledService.init(zsTable, stockTable, transDate, transTime);
        scheduledService.execute();
    }

    private void handleStockStrategyData(List<SharesDTO> sharesList) {
        if (sharesList == null) {
            return;
        }

        ObservableList<StrategyTable> ls2 = FXCollections.observableArrayList();
        sharesList.forEach(sharesDTO -> {
            Constants.stockSet.add(sharesDTO.getCode());
            StockInfoObject stockInfoObject = Constants.monitorStockMap.get(sharesDTO.getCode());
            String costPrice = sharesDTO.getCostPrice();
            if (StringUtils.isBlank(costPrice)) {
                costPrice = Constants.ZERO_BIG.doubleValue() + "";
            }
            // 操作策略
            StrategyTable strategyTable = StrategyTable.builder().sgStName(sharesDTO.getName())
                    .stCutPrice(sharesDTO.getCutPrice()).stCostPrice(costPrice)
                    .stProfitLossRatio(CommonUtil.getPriceRate(stockInfoObject.getCurrentPrice(), costPrice))
                    .stRemark(sharesDTO.getOptStrategy()).sgStCurrPrice(stockInfoObject.getCurrentPrice())
                    .stCutRate(CommonUtil.getPriceRate(stockInfoObject.getCurrentPrice(), sharesDTO.getCutPrice()))
                    .build();
            ls2.add(strategyTable);
        });
        strategyTable.setItems(ls2);
        ObservableList<TableColumn<StrategyTable, ?>> tableColumns = strategyTable.getColumns();
        for (TableColumn<StrategyTable, ?> tableColumn : tableColumns) {
            String id = tableColumn.getId();
            TableColumn<StrategyTable, String> column = (TableColumn<StrategyTable, String>) tableColumn;
            column.setCellValueFactory(new PropertyValueFactory<>(id));
            column.setCellFactory((tb) -> {
                TableCell<StrategyTable, String> cell = new TableCell<StrategyTable, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            return;
                        }
                        StrategyTable rowMap = (StrategyTable) this.getTableRow().getItem();
                        if (rowMap == null) {
                            return;
                        }
                        Text text = new Text();
                        this.setGraphic(text);
                        this.setPrefHeight(Control.USE_COMPUTED_SIZE);
                        if (rowMap.getStProfitLossRatio().contains("-")) {
                            text.setFill(Color.GREEN);
                        } else {
                            text.setFill(Color.RED);
                        }
                        text.wrappingWidthProperty().bind(column.widthProperty());
                        text.textProperty().bind(this.itemProperty());
                    }
                };
                return cell;
            });
        }
    }

    public void monitorStart(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String id = button.getId();
        map.put("__startBtn", id);
        if (map.containsKey(id)) {
            boolean running = scheduledService.isRunning();
            if (!running) {
                scheduledService.restart();
                button.setDisable(true);
                return;
            }
        }

        if (TransTimeUtil.isTransTime()) {
            if (TransTimeUtil.isLunchBreak()) {
                CommonUtil.showAlertWarningText("午间休市，无法交易，吃饱饭睡会觉^_^");
                return;
            }
            scheduledService.setExecutor(Executors.newFixedThreadPool(2));
            // 延时1s开始
            scheduledService.setDelay(Duration.millis(1000));
            // 间隔2s执行
            scheduledService.setPeriod(Duration.millis(5000));
            scheduledService.start();
            map.put(id, scheduledService);
            button.setDisable(true);
        } else {
            CommonUtil.showAlertWarningText("市场已休市，无法交易，好好上班干活吧^_^");
        }
    }

    public void monitorStop(ActionEvent actionEvent) {
        stopMonitor();
    }

    public void stopMonitor() {
        if (scheduledService.isRunning()) {
            scheduledService.cancel();
            startBtn.setDisable(false);
        }
    }

    public void addStock(ActionEvent actionEvent) {
        SettingApplication stockApp = new SettingApplication();
        try {
            Stage settingStage = new Stage();
            settingStage.initOwner(Main.primaryStage);
            stockApp.start(settingStage);
        } catch (Exception e) {
            log.error("打开添加个股窗口异常：", e);
        }
    }

    public void changeStockMA(Event event) {
        Tab tab = (Tab) event.getSource();
        boolean selected = tab.isSelected();
        if (selected) {
            ObservableList<StockMaTable> ls2 = FXCollections.observableArrayList();
            Map<String, StockInfoObject> monitorStockMap = Constants.monitorStockMap;
            monitorStockMap.forEach((code, stockInfo) -> {
                List<StockHistoryObject> historyList = getHistoryData(code, Constants.DAY_KX);
                StockMaTable stockMaTable = handleStockMaData(stockInfo, historyList);
                ls2.add(stockMaTable);
            });
            stockMaTable.setItems(ls2);
            ObservableList<TableColumn<StockMaTable, ?>> tableColumns = stockMaTable.getColumns();
            for (TableColumn<StockMaTable, ?> tableColumn : tableColumns) {
                String id = tableColumn.getId();
                TableColumn<StockMaTable, String> column = (TableColumn<StockMaTable, String>) tableColumn;
                column.setCellValueFactory(new PropertyValueFactory<>(id));
                column.setCellFactory((tb) -> {
                    TableCell<StockMaTable, String> cell = new TableCell<StockMaTable, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                return;
                            }
                            StockMaTable rowMap = (StockMaTable) this.getTableRow().getItem();
                            if (rowMap == null) {
                                return;
                            }
                            Text text = new Text();
                            this.setGraphic(text);
                            this.setPrefHeight(Control.USE_COMPUTED_SIZE);
                            if (rowMap.getChgRate().contains("-")) {
                                text.setFill(Color.GREEN);
                            } else {
                                text.setFill(Color.RED);
                            }
                            text.wrappingWidthProperty().bind(column.widthProperty());
                            text.textProperty().bind(this.itemProperty());
                        }
                    };
                    return cell;
                });
            }
        }
    }

    private StockMaTable handleStockMaData(StockInfoObject stockInfo, List<StockHistoryObject> historyList) {
        StockMaTable stockMaTable = StockMaTable.builder().build();
        stockMaTable.setMaStName(stockInfo.getGpName());
        stockMaTable.setChgRate(stockInfo.getChgRate());
        int size = historyList.size();
        if (size < 5) {
            stockMaTable.setMaRemark("新股");
        } else {

        }
        BigDecimal currentPrice = new BigDecimal(stockInfo.getCurrentPrice());
        BigDecimal prevClose = new BigDecimal(stockInfo.getPrevClose());


        StockHistoryObject lastRecord = historyList.get(size - 1);
        StockHistoryObject minorRecord = historyList.get(size - 2);
        StockHistoryObject minor2Record = historyList.get(size - 3);

        BigDecimal tempTotal = Constants.ZERO_BIG;
        BigDecimal ma30Big = Constants.ZERO_BIG;
        BigDecimal ma20Big = Constants.ZERO_BIG;
        BigDecimal ma10Big = Constants.ZERO_BIG;
        BigDecimal ma5Big = Constants.ZERO_BIG;
        BigDecimal preMa30Big = Constants.ZERO_BIG;
        BigDecimal preMa20Big = Constants.ZERO_BIG;
        BigDecimal preMa10Big = Constants.ZERO_BIG;
        BigDecimal preMa5Big = Constants.ZERO_BIG;
        BigDecimal pre2Ma30Big = Constants.ZERO_BIG;
        BigDecimal pre2Ma20Big = Constants.ZERO_BIG;
        BigDecimal pre2Ma10Big = Constants.ZERO_BIG;
        BigDecimal pre2Ma5Big = Constants.ZERO_BIG;
        if (TransTimeUtil.isNotTodayTime() || TransTimeUtil.isTodayCloseTime()) {
            // 非当天时间 ，则
            preMa5Big = new BigDecimal(minorRecord.getMa_price5());
            preMa10Big = new BigDecimal(minorRecord.getMa_price10());
            preMa20Big = new BigDecimal(minorRecord.getMa_price20());
            preMa30Big = new BigDecimal(minorRecord.getMa_price30());
            //
            pre2Ma5Big = new BigDecimal(minor2Record.getMa_price5());
            pre2Ma10Big = new BigDecimal(minor2Record.getMa_price10());
            pre2Ma20Big = new BigDecimal(minor2Record.getMa_price20());
            pre2Ma30Big = new BigDecimal(minor2Record.getMa_price30());
            //
            ma5Big = new BigDecimal(lastRecord.getMa_price5()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            ma10Big = new BigDecimal(lastRecord.getMa_price10()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            ma20Big = new BigDecimal(lastRecord.getMa_price20()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            ma30Big = new BigDecimal(lastRecord.getMa_price30()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            setDeltaMa(stockMaTable, lastRecord.getMa_price5(), minorRecord.getMa_price5(),
                    lastRecord.getMa_price10(), minorRecord.getMa_price10());
        } else if (TransTimeUtil.isTransTime()) {
            // 股票为交易时间，则动态计算
            // 股票为当天收盘非交易时间，则当前价格减最后一天的价格
            int days = 0;
            for (int index = size - 1; index >= 0; index--) {
                days++;
                StockHistoryObject historyObject = historyList.get(index);
                String close = historyObject.getClose();
                BigDecimal closeBig = new BigDecimal(close);
                tempTotal = tempTotal.add(closeBig);

                BigDecimal total = (tempTotal.add(currentPrice));
                if (days == 4) {
                    // 当前5均线
                    ma5Big = total.divide(new BigDecimal("5"), 2, BigDecimal.ROUND_HALF_DOWN);
                }
                if (days == 9) {
                    ma10Big = total.divide(new BigDecimal("10"), 2, BigDecimal.ROUND_HALF_DOWN);
                }
                if (days == 19) {
                    ma20Big = total.divide(new BigDecimal("20"), 2, BigDecimal.ROUND_HALF_DOWN);
                }
                if (days == 29) {
                    ma30Big = total.divide(new BigDecimal("30"), 2, BigDecimal.ROUND_HALF_DOWN);
                }
                if (days == size) {
                    preMa5Big = new BigDecimal(lastRecord.getMa_price5());
                    preMa10Big = new BigDecimal(lastRecord.getMa_price10());
                    preMa20Big = new BigDecimal(lastRecord.getMa_price20());
                    preMa30Big = new BigDecimal(lastRecord.getMa_price30());
                    pre2Ma5Big = new BigDecimal(minorRecord.getMa_price5());
                    pre2Ma10Big = new BigDecimal(minorRecord.getMa_price10());
                    pre2Ma20Big = new BigDecimal(minorRecord.getMa_price20());
                    pre2Ma30Big = new BigDecimal(minorRecord.getMa_price30());
                    setDeltaMa(stockMaTable, ma5Big.doubleValue() + "", lastRecord.getMa_price5(), ma10Big.doubleValue() +
                            "", lastRecord.getMa_price10());
                }
            }
        } else {

        }

        stockMaTable.setTodayMA5(ma5Big.doubleValue() + "");
        stockMaTable.setTodayMA10(ma10Big.doubleValue() + "");
        stockMaTable.setTodayMA20(ma20Big.doubleValue() + "");

        // 判断均线形态
        String maShape;
        if (ma5Big.compareTo(ma10Big) > 0 && ma10Big.compareTo(ma20Big) > 0 && ma20Big.compareTo(ma30Big) > 0) {
            // 多头
            maShape = "多头";
        } else if (ma30Big.compareTo(ma20Big) > 0 && ma20Big.compareTo(ma10Big) > 0 && ma10Big.compareTo(ma5Big) > 0) {
            // 空头
            maShape = "空头";
        } else {
            // 缠绕
            maShape = "缠绕";
        }
        stockMaTable.setMaShape(maShape);
        StringBuilder builder = new StringBuilder();
        if (ma5Big.compareTo(preMa5Big) < 0 && preMa5Big.compareTo(pre2Ma5Big) > 0) {
            // 5日均线开始拐头下跌
            builder.append("5均向下拐头;");
        }
        if (ma5Big.compareTo(preMa5Big) > 0 && preMa5Big.compareTo(pre2Ma5Big) < 0) {
            // 5日均线开始拐头
            builder.append("5均向上拐头;");
        }
        if (ma10Big.compareTo(preMa10Big) < 0 && preMa10Big.compareTo(pre2Ma10Big) > 0) {
            // 10日均线开始拐头下跌
            builder.append("10均向下拐头;");
        }
        if (ma10Big.compareTo(preMa10Big) > 0 && preMa10Big.compareTo(pre2Ma10Big) < 0) {
            // 10日均线开始拐头
            builder.append("10均向上拐头;");
        }
        if (ma20Big.compareTo(preMa20Big) < 0 && preMa20Big.compareTo(pre2Ma20Big) > 0) {
            // 20日均线开始拐头下跌
            builder.append("20均向下拐头;");
        }
        if (ma20Big.compareTo(preMa20Big) > 0 && preMa20Big.compareTo(pre2Ma20Big) < 0) {
            // 20日均线开始拐头下跌
            builder.append("20均向上拐头;");
        }
        if (preMa5Big.compareTo(preMa10Big) > 0 && ma5Big.compareTo(ma10Big) < 0) {
            // 5均下穿10均
            builder.append("5均下穿10均;");
        }
        if (preMa5Big.compareTo(preMa10Big) < 0 && ma5Big.compareTo(ma10Big) > 0) {
            // 5均上穿10均
            builder.append("5均上穿10均;");
        }
        if (prevClose.compareTo(preMa5Big) < 0 && currentPrice.compareTo(ma5Big) > 0) {
            // K线上穿5均
            builder.append("K线上穿5均;");
        }
        if (prevClose.compareTo(preMa5Big) > 0 && currentPrice.compareTo(ma5Big) < 0) {
            // K先下穿5均
            builder.append("K线下穿5均;");
        }
        if (prevClose.compareTo(preMa20Big) < 0 && currentPrice.compareTo(ma20Big) > 0) {
            // K线上穿20均
            builder.append("K线上穿20均;");
        }
        if (prevClose.compareTo(preMa20Big) > 0 && currentPrice.compareTo(ma20Big) < 0) {
            // K线下穿20均
            builder.append("K线下穿20均;");
        }
        stockMaTable.setMaRemark(builder.toString());
        return stockMaTable;
    }

    private void setDeltaMa(StockMaTable stockMaTable, String lastMa5, String minorMa5, String lastMa10, String minorMa10) {
        if (StringUtils.isBlank(lastMa5) || lastMa5.equals("0") || StringUtils.isBlank(lastMa10) || lastMa10.equals("0")) {
            return;
        }
        BigDecimal lastRecordMaPrice5 = new BigDecimal(lastMa5);
        BigDecimal minorRecordMaPrice5 = new BigDecimal(minorMa5);
        BigDecimal deltaMa5 = lastRecordMaPrice5.subtract(minorRecordMaPrice5);
        BigDecimal lastRecordMaPrice10 = new BigDecimal(lastMa10);
        BigDecimal minorRecordMaPrice10 = new BigDecimal(minorMa10);
        BigDecimal deltaMa10 = lastRecordMaPrice10.subtract(minorRecordMaPrice10);
        stockMaTable.setTomorrowMA5(lastRecordMaPrice5.add(deltaMa5).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
        stockMaTable.setAfterDayMA5(lastRecordMaPrice5.add(deltaMa5.multiply(new BigDecimal("2"))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
        stockMaTable.setTomorrowMA10(lastRecordMaPrice10.add(deltaMa10).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
        stockMaTable.setAfterDayMA10(lastRecordMaPrice10.add(deltaMa10.multiply(new BigDecimal("2"))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
    }

    private List<StockHistoryObject> getHistoryData(String code, int hour) {
        String url = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData" +
                ".getKLineData?symbol=" + code + "&scale=" + hour + "&ma=5,10,20,30&datalen=30";
        String result = OkHttpUtil.get(url, null);
        List<StockHistoryObject> objectList = JacksonUtil.parseList(result, StockHistoryObject.class);
        return objectList;
    }

    public void changeTransStrategy(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
            List<SharesDTO> sharesList = CommonUtil.getSharesList();
            handleStockStrategyData(sharesList);
        }
    }

    public void changeSharesRealData(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
            if (initFlag) {
                initialize(null, null);
            }
            initFlag = true;
        }
    }
}
