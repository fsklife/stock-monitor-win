package org.fsk.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fsk.Constants;
import org.fsk.pojo.SettingFileDTO;
import org.fsk.pojo.SharesDTO;
import org.fsk.pojo.SharesListTable;
import org.fsk.pojo.StockInfoObject;
import org.fsk.pojo.StockMarketDTO;
import org.fsk.utils.CommonUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fanshk
 * @date 2020/9/13 15:37
 */
@Slf4j
public class SettingController implements Initializable {
    public TextArea zsRemarkTextArea;
    public TextField addCodeText;
    public TextField addCutPriceText;
    public TextField addBuyText;
    public TextArea addOptStgText;
    public ChoiceBox<String> updateChoiceBox;
    public TextField updateCutPriceText;
    public TextField updateBuyText;
    public TextArea updateOptStgText;
    public TableView sharesListTable;
    @FXML
    public TableColumn<SharesListTable, String> deleteSharesCol;
    @FXML
    public TableColumn<SharesListTable, String> sharesCodeCol, sharesNameCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setStockRemark(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
            SettingFileDTO settingFile = CommonUtil.getSettingFile();
            if (settingFile != null) {
                StockMarketDTO stock = settingFile.getStock();
                if (stock != null) {
                    zsRemarkTextArea.setText(stock.getRemark());
                }
            }
        }
    }

    public void showSharesList(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
            List<SharesDTO> sharesList = CommonUtil.getSharesList();
            if (sharesList != null) {
                ObservableList<SharesListTable> lists = FXCollections.observableArrayList();
                for (SharesDTO sharesDTO : sharesList) {
                    SharesListTable listTable = new SharesListTable();
                    listTable.setSharesCode(sharesDTO.getCode());
                    listTable.setSharesName(sharesDTO.getName());
                    lists.add(listTable);
                }
                sharesListTable.setItems(lists);
                sharesCodeCol.setCellValueFactory(new PropertyValueFactory<>("sharesCode"));
                sharesNameCol.setCellValueFactory(new PropertyValueFactory<>("sharesName"));
                deleteSharesCol.setCellFactory((col) -> {
                    TableCell<SharesListTable, String> cell = new TableCell<SharesListTable, String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            this.setText(null);
                            this.setGraphic(null);

                            if (!empty) {
                                /*String delPath = getClass().getResourceAsStream("del.png").toString();
                                ImageView delICON = new ImageView(delPath);*/
                                Button delBtn = new Button("删除");
                                this.setGraphic(delBtn);
                                delBtn.setOnMouseClicked((me) -> {
                                    SharesListTable clickedStu = this.getTableView().getItems().get(this.getIndex());
                                    String sharesCode = clickedStu.getSharesCode();
                                    log.info("删除个股：{}", sharesCode);
                                    String hitMsg = "删除个股[" + clickedStu.getSharesName() + "]";
                                    Optional<ButtonType> option = CommonUtil.showAlertConfirmText("确定要" + hitMsg + "吗？");
                                    if (ButtonType.OK.equals(option.get())) {
                                        Constants.stockSet.remove(sharesCode);
                                        Constants.monitorStockMap.remove(sharesCode);
                                        SettingFileDTO settingFile = CommonUtil.getSettingFile();
                                        assert settingFile != null;
                                        List<SharesDTO> shares = settingFile.getShares();
                                        shares.removeIf(next -> next.getCode().equals(sharesCode));
                                        log.info("最新数据：{}", settingFile);
                                        CommonUtil.updateDataFile(settingFile);
                                        CommonUtil.showAlertInfoText(hitMsg + "成功");
                                    }
                                });
                            }
                        }

                    };
                    return cell;
                });

            }
        }
    }

    public void changeSharesData(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected()) {
            List<SharesDTO> sharesList = CommonUtil.getSharesList();
            if (sharesList != null) {
                // 修改个股初始化
                ObservableList<String> sharesChoice = FXCollections.observableArrayList();
                Map<String, SharesDTO> sharesDTOMap = new HashMap<>(sharesList.size());
                sharesList.forEach(sharesDTO -> {
                    String key = sharesDTO.getName() + Constants.LEFT_ZKH + sharesDTO.getCode() + Constants.RIGHT_ZKH;
                    sharesChoice.add(key);
                    sharesDTOMap.put(key, sharesDTO);
                });
                updateChoiceBox.setItems(sharesChoice);
               /* updateChoiceBox.converterProperty().set(new StringConverter<SharesDTO>() {
                    @Override
                    public String toString(SharesDTO object) {
                        return object.getName();
                    }

                    @Override
                    public SharesDTO fromString(String string) {
                        return null;
                    }
                });*/
                ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.contains(Constants.LEFT_ZKH)) {
                        SharesDTO sharesDTO = sharesDTOMap.get(newValue);
                        updateCutPriceText.setText(sharesDTO.getCutPrice());
                        updateBuyText.setText(sharesDTO.getBuyCondition());
                        updateOptStgText.setText(sharesDTO.getOptStrategy());
                    }
                };
                SingleSelectionModel<String> selectionModel = updateChoiceBox.getSelectionModel();
                selectionModel.selectedItemProperty().addListener(changeListener);
                selectionModel.selectFirst();
            }
        }
    }


    public void updateZsRemark(ActionEvent actionEvent) {
        String text = zsRemarkTextArea.getText();

        SettingFileDTO fileDTO = CommonUtil.getSettingFile();
        if (fileDTO == null) {
            fileDTO = new SettingFileDTO();
            StockMarketDTO stockMarketDTO = new StockMarketDTO();
            stockMarketDTO.setRemark(text);
            fileDTO.setStock(stockMarketDTO);
        } else {
            StockMarketDTO stock = fileDTO.getStock();
            if (stock == null) {
                stock = new StockMarketDTO();
            }
            stock.setRemark(text);
            fileDTO.setStock(stock);
        }
        boolean res = CommonUtil.updateDataFile(fileDTO);
        if (res) {
            log.info("大盘备注更新，文件更新成功");
            CommonUtil.showAlertInfoText("大盘备注更新成功！");
        }

    }

    public void resetZsRemark(ActionEvent actionEvent) {
    }

    public void addNewShares(ActionEvent actionEvent) {
        String code = addCodeText.getText();
        int codeLength = code.length();
        int baseLength = 6;
        if (codeLength < baseLength) {
            CommonUtil.showAlertErrorText("股票代码错误");
            return;
        }
        String cutPriceText = addCutPriceText.getText();
        if (StringUtils.isBlank(cutPriceText)) {
            CommonUtil.showAlertErrorText("未设置止损");
            return;
        }
        cutPriceText = cutPriceText.trim();
        Set<String> set = new HashSet<>();
        if (codeLength == baseLength) {
            code = CommonUtil.getStockCode(code);
        }
        set.add(code);

        Map<String, StockInfoObject> stockData = CommonUtil.callStockData(set);
        if (stockData == null || stockData.isEmpty()) {
            CommonUtil.showAlertErrorText("该股票[" + code + "]不存在");
            return;
        }
        StockInfoObject stockInfoObject = stockData.get(code);
        SharesDTO sharesDTO = new SharesDTO();
        sharesDTO.setCode(code);
        sharesDTO.setName(stockInfoObject.getGpName());
        BigDecimal cutPriceBig = new BigDecimal(cutPriceText).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        sharesDTO.setCutPrice(cutPriceBig.doubleValue() + "");
        sharesDTO.setBuyCondition(addBuyText.getText());
        sharesDTO.setOptStrategy(addOptStgText.getText());
        SettingFileDTO settingFile = CommonUtil.getSettingFile();
        if (settingFile == null) {
            settingFile = new SettingFileDTO();
        }
        List<SharesDTO> shares = settingFile.getShares();
        if (shares == null) {
            shares = new ArrayList<>();
            shares.add(sharesDTO);
            Constants.stockSet.add(code);
        } else {
            Map<String, SharesDTO> sharesDTOMap = shares.stream().collect(Collectors.toMap(SharesDTO::getCode, Function.identity()));
            if (sharesDTOMap.containsKey(code)) {
                SharesDTO hasShares = sharesDTOMap.get(code);
                hasShares.setCutPrice(cutPriceText);
                hasShares.setBuyCondition(addBuyText.getText());
                hasShares.setOptStrategy(addOptStgText.getText());
            } else {
                shares.add(sharesDTO);
                Constants.stockSet.add(code);
            }
        }
        settingFile.setShares(shares);
        CommonUtil.updateDataFile(settingFile);
        Constants.monitorStockMap.put(code, stockInfoObject);
        CommonUtil.showAlertInfoText("添加个股成功");

    }

    public void updateSharesCommit(ActionEvent actionEvent) {
        String codeName = updateChoiceBox.getValue();
        String code = codeName.substring(codeName.indexOf(Constants.LEFT_ZKH) + 1, codeName.indexOf(Constants.RIGHT_ZKH));
        String cutPriceText = updateCutPriceText.getText();
        if (StringUtils.isBlank(cutPriceText)) {
            CommonUtil.showAlertErrorText("未设置止损");
            return;
        }
        cutPriceText = cutPriceText.trim();
        StockInfoObject stockInfoObject = Constants.monitorStockMap.get(code);
        SharesDTO sharesDTO = new SharesDTO();
        sharesDTO.setCode(code);
        sharesDTO.setName(stockInfoObject.getGpName());
        BigDecimal cutPriceBig = new BigDecimal(cutPriceText).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        sharesDTO.setCutPrice(cutPriceBig.doubleValue() + "");
        sharesDTO.setBuyCondition(updateBuyText.getText());
        sharesDTO.setOptStrategy(updateOptStgText.getText());
        SettingFileDTO settingFile = CommonUtil.getSettingFile();
        if (settingFile == null) {
            settingFile = new SettingFileDTO();
        }
        List<SharesDTO> shares = settingFile.getShares();
        Map<String, SharesDTO> sharesDTOMap = shares.stream().collect(Collectors.toMap(SharesDTO::getCode, Function.identity()));
        if (sharesDTOMap.containsKey(code)) {
            SharesDTO hasShares = sharesDTOMap.get(code);
            hasShares.setCutPrice(cutPriceText);
            hasShares.setBuyCondition(updateBuyText.getText());
            hasShares.setOptStrategy(updateOptStgText.getText());
        }

        settingFile.setShares(shares);
        CommonUtil.updateDataFile(settingFile);
        Constants.monitorStockMap.put(code, stockInfoObject);
        CommonUtil.showAlertInfoText("修改个股成功");
    }

}
