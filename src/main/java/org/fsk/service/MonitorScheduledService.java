package org.fsk.service;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.fsk.Constants;
import org.fsk.StageManager;
import org.fsk.pojo.EstimateShVolDTO;
import org.fsk.pojo.StockInfoObject;
import org.fsk.pojo.StockTable;
import org.fsk.pojo.ZsTable;
import org.fsk.utils.CommonUtil;
import org.fsk.utils.DateUtil;
import org.fsk.utils.TransTimeUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author fanshk
 * @date 2020/9/10 19:18
 */
@Data
public class MonitorScheduledService extends ScheduledService<Void> {

    private TableView<ZsTable> zsTable;
    private TableView<StockTable> stockTable;
    private Label transTime;
    private Label transDate;

    private static MonitorScheduledService instance = null;

    private MonitorScheduledService() {

    }

    public static synchronized MonitorScheduledService getInstance() {
        if (instance == null) {
            instance = new MonitorScheduledService();
        }
        return instance;
    }

    public void init(TableView<ZsTable> zsTable, TableView<StockTable> stockTable, Label transDate, Label transTime) {
        this.zsTable = zsTable;
        this.stockTable = stockTable;
        this.transTime = transTime;
        this.transDate = transDate;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                execute();
                return null;
            }

        };
    }

    public void runLate() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                transDate.setText(Constants.monitorSh.getDate());
                transTime.setText(Constants.monitorSh.getTime());
                boolean transTime = TransTimeUtil.isTransTime();
                boolean resetVol = true;
                EstimateShVolDTO estimateShVol = CommonUtil.getEstimateShVol();
                if (estimateShVol != null) {
                    String quarterVol = estimateShVol.getQuarterVol();
                    // 存在数据
                    String day = estimateShVol.getDay();
                    // 是当前时间的
                    if (Constants.monitorSh.getDate().equals(day)
                            && StringUtils.isNoneBlank(estimateShVol.getQuarterVol())
                            && StringUtils.isNoneBlank(estimateShVol.getHalfHourVol())) {
                        resetVol = false;
                    }
                }
                if (resetVol && transTime) {
                    estimateShVol = calculateVol(estimateShVol);
                    CommonUtil.updateShVolFile(estimateShVol);
                }
                if (estimateShVol != null) {
                    StageManager.getMainCtrl().quarterVol.setText(estimateShVol.getQuarterVol());
                    StageManager.getMainCtrl().halfHourVol.setText(estimateShVol.getHalfHourVol());
                }
                if (!transTime || TransTimeUtil.isLunchBreak()) {
                    StageManager.getMainCtrl().stopMonitor();
                }
            }
        });
    }

    private EstimateShVolDTO calculateVol(EstimateShVolDTO estimateShVol) {
        if (estimateShVol == null) {
            estimateShVol = EstimateShVolDTO.init();
        }
        estimateShVol.setDay(Constants.monitorSh.getDate());
        // 偏离不会超过当天总成交的8%，准确率90%
        String time = DateUtil.getHHmmss();
        int timeInt = Integer.parseInt(time);
        String volPrice = Constants.monitorSh.getVolPrice();
        BigDecimal volBig = new BigDecimal(volPrice.replace("亿", ""));

        if (timeInt > 94455 && timeInt < 94501) {
            // 计算45分钟
            BigDecimal quarter = calculateVolByQuarter(volBig);
            estimateShVol.setQuarterVol(quarter.doubleValue() + "亿");
        }

        if (timeInt > 95955 && timeInt < 100001) {
            BigDecimal halfHour = calculateVolByHalfHour(volBig);
            estimateShVol.setHalfHourVol(halfHour.doubleValue() + "亿");
        }

        return estimateShVol;
    }

    private BigDecimal calculateVolByQuarter(BigDecimal volBig) {
        BigDecimal times = new BigDecimal("1");
        // 第一个15分钟
        if (volBig.doubleValue() < 400) {
            // *7
            times = new BigDecimal("7");
        } else if (volBig.doubleValue() >= 400 && volBig.doubleValue() < 600) {
            // *6
            times = new BigDecimal("6");
        } else if (volBig.doubleValue() >= 600 && volBig.doubleValue() < 900) {
            // *5.5
            times = new BigDecimal("5.5");
        } else if (volBig.doubleValue() >= 900) {
            // *5
            times = new BigDecimal("5");
        }
        return volBig.multiply(times).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 偏离不会超过当天总成交的8%，准确率90%
     *
     * @param volBig
     * @return
     */
    private BigDecimal calculateVolByHalfHour(BigDecimal volBig) {
        BigDecimal times = new BigDecimal("1");
        // 第一个30分钟
        if (volBig.doubleValue() < 600) {
            // *4
            times = new BigDecimal("4");
        } else if (volBig.doubleValue() >= 600 && volBig.doubleValue() < 900) {
            // *3.8
            times = new BigDecimal("3.8");
        } else if (volBig.doubleValue() >= 900) {
            // *3.5
            times = new BigDecimal("3.5");
        }
        return volBig.multiply(times).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void execute() {
        Map<String, StockInfoObject> stockData = callStockData(Constants.stockSet);
        // 处理指数
        handleZsData(stockData, zsTable);
        // 处理个股
        handleStockData(stockData, stockTable);
        runLate();
    }

    private void handleStockData(Map<String, StockInfoObject> stockData,
                                 TableView<StockTable> stockTable) {
        ObservableList<StockTable> ls2 = FXCollections.observableArrayList();
        for (String code : Constants.stockSet) {
            StockInfoObject stockInfo = stockData.get(code);
            StockTable table =
                    StockTable.builder().stName(stockInfo.getGpName()).stCurrPrice(stockInfo.getCurrentPrice())
                            .stChg(stockInfo.getChgRate()).stVol(stockInfo.getVolPrice()).stCode(stockInfo.getStCode())
                            .stMinPrice(stockInfo.getMinPrice()).stMaxPrice(stockInfo.getMaxPrice())
                            .currOpen(stockInfo.getOpen()).prevClose(stockInfo.getPrevClose())
                            .stDeltaPrice(stockInfo.getDeltaPrice()).build();
            ls2.add(table);
            Constants.monitorStockMap.put(code, stockInfo);
        }
        stockTable.setItems(ls2);
        ObservableList<TableColumn<StockTable, ?>> tableColumns = stockTable.getColumns();
        for (TableColumn<StockTable, ?> tableColumn : tableColumns) {
            String id = tableColumn.getId();
            TableColumn<StockTable, String> column = (TableColumn<StockTable, String>) tableColumn;
            column.setCellValueFactory(new PropertyValueFactory<>(id));
            column.setCellFactory((tb) -> {
                TableCell<StockTable, String> row = new TableCell<StockTable, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            return;
                        }
                        StockTable rowMap = (StockTable) this.getTableRow().getItem();
                        if (rowMap == null) {
                            return;
                        }
                        if (rowMap.getStChg().contains("-")) {
                            this.setTextFill(Color.GREEN);
                        } else {
                            this.setTextFill(Color.RED);
                        }
                        setText(item);
                    }
                };
                return row;
            });
        }
    }

    private Map<String, StockInfoObject> callStockData(Set<String> stockSet) {
        Set<String> set = new TreeSet<>();
        set.add(Constants.SH_CODE);
        set.add(Constants.SZ_CODE);
        set.add(Constants.CY_CODE);
        set.addAll(stockSet);
        return CommonUtil.callStockData(set);
    }

    private void handleZsData(Map<String, StockInfoObject> analyzeResult, TableView<ZsTable> zsTable) {

        StockInfoObject shStock = analyzeResult.get(Constants.SH_CODE);
        Constants.monitorSh = shStock;

        ZsTable shTable = ZsTable.builder().name(shStock.getGpName()).currentPrice(shStock.getCurrentPrice())
                .zsChg(shStock.getChgRate()).zsVol(shStock.getVolPrice()).zsPrevClose(shStock.getPrevClose())
                .zsDeltaPrice(shStock.getDeltaPrice()).build();
        StockInfoObject szStock = analyzeResult.get(Constants.SZ_CODE);
        ZsTable szTable = ZsTable.builder().name(szStock.getGpName()).currentPrice(szStock.getCurrentPrice())
                .zsChg(szStock.getChgRate()).zsVol(szStock.getVolPrice()).zsPrevClose(szStock.getPrevClose())
                .zsDeltaPrice(szStock.getDeltaPrice()).build();
        StockInfoObject cyStock = analyzeResult.get(Constants.CY_CODE);
        ZsTable cyTable = ZsTable.builder().name(cyStock.getGpName()).currentPrice(cyStock.getCurrentPrice())
                .zsChg(cyStock.getChgRate()).zsVol(cyStock.getVolPrice()).zsPrevClose(cyStock.getPrevClose())
                .zsDeltaPrice(cyStock.getDeltaPrice()).build();
        ObservableList<ZsTable> ls2 = FXCollections.observableArrayList();
        ls2.addAll(shTable, szTable, cyTable);
        zsTable.setItems(ls2);
        ObservableList<TableColumn<ZsTable, ?>> tableColumns = zsTable.getColumns();
        for (TableColumn<ZsTable, ?> tableColumn : tableColumns) {
            String id = tableColumn.getId();
            TableColumn<ZsTable, String> column = (TableColumn<ZsTable, String>) tableColumn;
            column.setCellValueFactory(new PropertyValueFactory<>(id));
            column.setCellFactory((tb) -> {
                TableCell<ZsTable, String> row = new TableCell<ZsTable, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            return;
                        }
                        ZsTable rowMap = (ZsTable) this.getTableRow().getItem();
                        if (rowMap == null) {
                            return;
                        }
                        if (rowMap.getZsChg().contains("-")) {
                            this.setTextFill(Color.GREEN);
                        } else {
                            this.setTextFill(Color.RED);
                        }
                        setText(item);
                    }
                };
                return row;
            });
        }
    }


}
