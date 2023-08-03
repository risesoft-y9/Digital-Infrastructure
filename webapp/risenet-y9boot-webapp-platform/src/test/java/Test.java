import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

public class Test {

    @Getter
    class GoodsArrivalBatchExcelProperty {
        private String licensePlateNumber;
        private String billLadingNo;
        private String containerCode;
    }

    public static void main(String[] args) {

    }

    private void markErrorLoop(List<GoodsArrivalBatchExcelProperty> dataList,
        List<GoodsArrivalBatchExcelProperty> errorList, Set<GoodsArrivalBatchExcelProperty> returnList) {
        for (GoodsArrivalBatchExcelProperty goodsArrivalBatchExcelProperty : errorList) {
            if (!returnList.contains(goodsArrivalBatchExcelProperty)) {
                // 相同车牌号
                List<GoodsArrivalBatchExcelProperty> errorList1 = dataList
                    .stream().filter(goodsArrivalBatchExcelProperty1 -> goodsArrivalBatchExcelProperty1
                        .getLicensePlateNumber().equals(goodsArrivalBatchExcelProperty.getLicensePlateNumber()))
                    .collect(Collectors.toList());
                markErrorLoop(dataList, errorList1, returnList);

                // 相同提单号
                List<GoodsArrivalBatchExcelProperty> errorList2 = dataList
                    .stream().filter(goodsArrivalBatchExcelProperty1 -> goodsArrivalBatchExcelProperty1
                        .getBillLadingNo().equals(goodsArrivalBatchExcelProperty.getBillLadingNo()))
                    .collect(Collectors.toList());
                markErrorLoop(dataList, errorList2, returnList);

                returnList.add(goodsArrivalBatchExcelProperty);
            }
        }
    }

    private void markErrorLoop(List<GoodsArrivalBatchExcelProperty> excelList, Map<String, String> billLadingErrorMap,
        Map<String, String> licensePlateNumberErrorMap) {
        for (int i = 0; i < excelList.size(); i++) {
            GoodsArrivalBatchExcelProperty goodsArrivalBatchExcelProperty = excelList.get(i);
            if (licensePlateNumberErrorMap.containsKey(goodsArrivalBatchExcelProperty.getLicensePlateNumber())) {
                licensePlateNumberErrorMap.put(goodsArrivalBatchExcelProperty.getLicensePlateNumber(),
                    licensePlateNumberErrorMap.get(goodsArrivalBatchExcelProperty.getLicensePlateNumber()));
                List<GoodsArrivalBatchExcelProperty> newList = new ArrayList<>(excelList);
                newList.remove(i);
                markErrorLoop(newList, billLadingErrorMap, licensePlateNumberErrorMap);
                if (StringUtils.isBlank(goodsArrivalBatchExcelProperty.getContainerCode())) {
                    billLadingErrorMap.put(goodsArrivalBatchExcelProperty.getBillLadingNo(),
                        billLadingErrorMap.get(goodsArrivalBatchExcelProperty.getBillLadingNo()));
                }
            }
            if (billLadingErrorMap.containsKey(goodsArrivalBatchExcelProperty.getBillLadingNo())) {
                billLadingErrorMap.put(goodsArrivalBatchExcelProperty.getBillLadingNo(),
                    billLadingErrorMap.get(goodsArrivalBatchExcelProperty.getBillLadingNo()));
                List<GoodsArrivalBatchExcelProperty> newList = new ArrayList<>(excelList);
                newList.remove(i);
                markErrorLoop(newList, billLadingErrorMap, licensePlateNumberErrorMap);
            }
        }
    }
}
