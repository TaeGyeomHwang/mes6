package com.boot.mes6.service;

import com.boot.mes6.constant.MaterialName;
import com.boot.mes6.constant.MaterialStatus;
import com.boot.mes6.constant.MaterialSupplierName;
import com.boot.mes6.constant.ProductName;
import com.boot.mes6.entity.MaterialInOut;
import com.boot.mes6.entity.Order;
import com.boot.mes6.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;

    // 원자재 입출고 이력 가져오기
    public List<MaterialInOut> getAllMaterial() {
        return materialRepository.findAll();
    }

    // 원자재 입출고 이력에 추가하기
    public void addMaterial(MaterialInOut materialInOut) {
        materialRepository.save(materialInOut);
    }

    // 자동 원자재 발주
    // 수주 추가가 되면 추가된 수주의 정보를 Order로 받아옴
    public void autoAddMaterial(Order order) {
        //order에서 orderNo, orderAmount, orderProductType, orderDate 가져온다.
        //초기값들 설정해야함
        Long orderNo;
        Long orderAmount;
        ProductName orderProductType;
        LocalDateTime materialOrderDate = orderDate;
        Long materialAmount = 0L;

        -----------------------------------------------------------------------------;
        //orderProductType이 즙인지 젤리인지
        //즙이라면 양배추 혹은 흑마늘과 벌꿀의 양을 계산, 발주처 업체명 설정
        materialAmount, subMaterialAmount 설정;
        //젤리라면 석류, 매실, 콜라겐 양 계산, 발주처 업체명 설정
        materialAmount, subMaterialAmount 설정;

        -----------------------------------------------------------------------------;
        //수주 정보에서 수주일(발주일)에 따라 기본 발주접수일(materialReceiptDate) 설정
        LocalDateTime materialReceiptDate = null; //기본값

        //orderDate가 금요일 오후, 토요일, 일요일이면 발주접수일을 월요일 12시로 설정
        if (orderDate가 금요일 오후?)
            materialReceiptDate = orderDate.plusDay(3).withHour(12시);
        if(orderDate가 토요일?)
            materialReceiptDate = orderDate.plusDay(2).withHour(12시);
        if(orderDate가 일요일)
            materialReceiptDate = orderDate.plusDay(1).withHour(12시);

        //orderDate가 금요일 오후, 토요일, 일요일도 아니면 당일 12시로 설정
        if(금요일 오후, 토요일, 일요일도 아니면)
            materialReceiptDate = orderDate.withHour(12시);

        -----------------------------------------------------------------------------;
        //지금부터 양배추즙 수주가 들어오고 원자재 주문을 양배추만 주문한다고 가정

        //원자재 입출고 관리 테이블에서 '같은 원자재'를 가진 애들 중 이력ID가 가장 큰(마지막)인 발주 접수일을 가져온다.;
        LocalDateTime lastMaterialReceiptDate = materialRepository.find;
        //발주 접수일이 토요일 or 일요일일 수가 있나? 밑에서 계산 잘했다면 없음
        //가장 마지막에 가져온 발주 접수일을 가진 애들을 다시 가져옴, 이력ID가 가장 크고(마지막) 그 가장 큰 애의 발주일이 같은 애들을 가져올 수 있나?;
        List<MaterialInOut> materialInOuts = materialRepository.find(lastMaterialReceiptDate);

        //list가 isEmpty
        if(materialInOuts.isEmpty()){
            //발주할 원자재가 최대 2000을 넘지 않으면 바로 발주
            if (materialAmount < 2000) {
                원자재명, 발주량, 수주번호, 발주일(수주일), 발주 접수일(materialReceiptDate), 상태 set하고 save;
            } else {
                //발주해야할 원자재가 2000이 넘었다면 반복해서 발주
                for(materialAmount >= 0) {
                    //ex)5000kg을 발주해야한다면 2000을 2번해야함
                    if(materialAmount >= 2000) {
                        원자재명, 발주량 = 2000, 수주번호, 발주일(수주일), 발주 접수일(materialReceiptDate), 상태 set하고 save;
                    }

                    //2000을 계속 빼다가 2000미만이 되면 남은 만큼 발주
                    원자재명, 발주량 = materialAmount, 수주번호, 발주일(수주일), 발주 접수일(materialReceiptDate), 상태 set하고 save;

                    materialAmount -= 2000;
                    //하루에 2000개니까 + 1일
                    materialReceiptDate.plusDay(1일);

                    //발주를 계속하다가 금요일이 되었다면 토,일에는 발주접수가 불가능하니 다시 월요일 12시로 조정
                    if(materialReceiptDate가 금요일이 되었나)
                        materialReceiptDate.plusDay(3).withHour(12시);
                }
            }
        }

        //가져온 list가 안 비었다면
        else {
            //그 애들의 발주량의 합이 2000과 같은가?
            if(MaterialOrderAmountSum == 2000) {
                //2000과 같다면 그 날 발주할 수 있는 양을 가득 채웠기 때문에 다음날에 발주접수가 됨
                //근데 금요일이다? 월요일에 발주접수가 됨.
                if(그 날이 금요일?)
                    materialReceiptDate.plusDays(3);

                materialReceiptDate.plusDays(1);
            }

            //발주량의 합이 2000이 안넘는다.
            //일단 가져온 list의 발주량의 합을 구한다.
            Long materialAmountSum = 0L;
            for (MaterialInOut materialInOut : materialInOuts) {
                materialAmountSum += materialInOut.getMaterialInoutAmount();
            }

            //발주할 수 있는 남은 양 계산
            Long maxMaterialOrderAmount = 2000L;
            maxMaterialOrderAmount -= materialAmountSum;

            //

        }
    }






    //지금은 안씀 위에서 의사코드 다 작성하고 필요하면 사용
    //old code
    private long calculateMaterialAmount(Order order) {
        long productionAmount = (long) (order.getOrderAmount() + Math.ceil(order.getOrderAmount() * 0.03));
        switch (order.getOrderProductType()) {
            case CABBAGE_JUICE:
                return (long) Math.ceil(productionAmount * 30 * 10 / 0.5 / 0.2 / 0.75 / 1000);
            case GARLIC_JUICE:
                return (long) Math.ceil(productionAmount * 30 * 10 / 0.5 / 0.2 / 0.75 / 1000);
            case POMEGRANATE_JELLY:
            case PLUM_JELLY:
                return (long) Math.ceil(productionAmount * 25 * 5 / 1000.0);
            default:
                throw new IllegalArgumentException("Invalid product type: " + order.getOrderProductType());
        }
    }

    private MaterialName getMaterialName(ProductName productName) {
        switch (productName) {
            case CABBAGE_JUICE:
                return MaterialName.CABBAGE;
            case GARLIC_JUICE:
                return MaterialName.GARLIC;
            case POMEGRANATE_JELLY:
                return MaterialName.POMEGRANATE;
            case PLUM_JELLY:
                return MaterialName.PLUM;
            default:
                throw new IllegalArgumentException("Invalid product type: " + productName);
        }
    }

    private MaterialSupplierName getSupplierName(ProductName productName) {
        switch (productName) {
            case CABBAGE_JUICE:
            case GARLIC_JUICE:
                return MaterialSupplierName.A_FARM;
            case POMEGRANATE_JELLY:
            case PLUM_JELLY:
                return MaterialSupplierName.OO_NH;
            default:
                throw new IllegalArgumentException("Invalid product type: " + productName);
        }
    }

    private MaterialInOut createMaterialOrder(Order order, MaterialName materialName, MaterialSupplierName materialSupplierName, LocalDateTime materialOrderDate, long orderAmount) {
        MaterialInOut materialInOut = new MaterialInOut();
        materialInOut.setOrder(order);
        materialInOut.setMaterialName(materialName);
        materialInOut.setMaterialInoutAmount(orderAmount);
        materialInOut.setMaterialSupplierName(materialSupplierName);
        materialInOut.setMaterialOrderDate(materialOrderDate);
        materialInOut.setMaterialReceiptDate(getReceiptDate(materialOrderDate));
        materialInOut.setMaterialStatus(MaterialStatus.PREPARING_SHIP);
        return materialInOut;
    }

    private LocalDateTime getReceiptDate(LocalDateTime orderDate) {
        if (orderDate.getDayOfWeek() == DayOfWeek.FRIDAY && orderDate.getHour() >= 12) {
            return orderDate.plusDays(3).withHour(12).withMinute(0).withSecond(0).withNano(0);
        } else if (orderDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return orderDate.plusDays(2).withHour(12).withMinute(0).withSecond(0).withNano(0);
        } else if (orderDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return orderDate.plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
        } else if (orderDate.getHour() >= 12) {
            return orderDate.plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
        } else {
            return orderDate.withHour(12).withMinute(0).withSecond(0).withNano(0);
        }
    }

    public List<MaterialInOut> getMaterialList(LocalDateTime orderDate, MaterialName materialName) {
        LocalDateTime startTime, endTime;

        if (orderDate.getHour() >= 12) {
            startTime = orderDate.withHour(12).withMinute(0).withSecond(0).withNano(0);
            endTime = startTime.plusDays(1);
        } else {
            startTime = orderDate.minusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
            endTime = startTime.plusDays(1);
        }

        return materialRepository.findByMaterialNameAndMaterialOrderDateBetweenOrderByMaterialNo(materialName, startTime, endTime);
    }
}
