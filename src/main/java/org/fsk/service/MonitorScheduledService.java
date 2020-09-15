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
import org.fsk.Constants;
import org.fsk.StageManager;
import org.fsk.pojo.StockInfoObject;
import org.fsk.pojo.StockTable;
import org.fsk.pojo.ZsTable;
import org.fsk.utils.CommonUtil;
import org.fsk.utils.TransTimeUtil;

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
                if (!TransTimeUtil.isTransTime() || TransTimeUtil.isLunchBreak()) {
                    StageManager.getMainCtrl().stopMonitor();
                }
            }
        });
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
