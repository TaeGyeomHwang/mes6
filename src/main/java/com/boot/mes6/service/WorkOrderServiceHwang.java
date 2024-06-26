package com.boot.mes6.service;

import com.boot.mes6.constant.*;
import com.boot.mes6.entity.Order;
import com.boot.mes6.entity.OrderPlanMap;
import com.boot.mes6.entity.Plan;
import com.boot.mes6.entity.WorkOrder;
import com.boot.mes6.repository.OrderPlanMapRepositoryHwang;
import com.boot.mes6.repository.OrderRepositoryHwang;
import com.boot.mes6.repository.PlanRepositoryHwang;
import com.boot.mes6.repository.WorkOrderRepositoryHwang;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkOrderServiceHwang {

    private final CurrentProductServiceHwang currentProductServiceHwang;
    private final MaterialService materialService;
    private final ProductInOutServiceHwang productInOutServiceHwang;

    private final PlanRepositoryHwang planRepository;
    private final WorkOrderRepositoryHwang workOrderRepositoryHwang;
    private final OrderPlanMapRepositoryHwang orderPlanMapRepositoryHwang;
    private final OrderRepositoryHwang orderRepositoryHwang;

    //  작업지시 생성
    public List<Long> createNewWorkOrder(Long planNo, ProductType productType) {
        Plan plan = planRepository.findById(planNo)
                .orElseThrow(EntityNotFoundException::new);

        LocalDateTime planStartDate = plan.getPlanStartDate();
        Long workOrderInput = plan.getPlanProductionAmount();

        List<Long> workOrderNoList = new ArrayList<>();

        if (productType != ProductType.JELLY) { //  즙 작업지시 생성
            for (int i = 0; i < 7; i++) {
                WorkOrder workOrder = new WorkOrder();
                workOrder.setPlan(plan);
                workOrder.setWorkOrderType(ProductType.JUICE);
                switch (i) {
                    case 0: //  세척
                        workOrder.setWorkOrderProcessName(ProcessCode.A1);
                        workOrder.setWorkOrderFacilityName(FacilityName.NONE);
                        workOrder.setWorkOrderInput((long) Math.ceil(workOrderInput * 30 * 10 / 0.5 / 0.2 / 0.75 / 1000));
                        workOrder.setWorkOrderOutput((long) Math.ceil(workOrder.getWorkOrderInput() * 0.75));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(2));
                        break;
                    case 1: //  추출
                        workOrder.setWorkOrderProcessName(ProcessCode.A2);
                        workOrder.setWorkOrderFacilityName(FacilityName.EXTRACTOR_1);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput((long) Math.ceil(workOrderInput * 0.2));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(24));
                        break;
                    case 2: //  여과
                        workOrder.setWorkOrderProcessName(ProcessCode.A3);
                        workOrder.setWorkOrderFacilityName(FacilityName.FILTER);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput((long) Math.ceil(workOrderInput * 0.5));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(4));
                        break;
                    case 3: //  살균
                        workOrder.setWorkOrderProcessName(ProcessCode.A4);
                        workOrder.setWorkOrderFacilityName(FacilityName.STERILIZER_1);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput((long) Math.ceil(workOrderInput));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(2));
                        break;
                    case 4: //  충진
                        workOrder.setWorkOrderProcessName(ProcessCode.A5);
                        workOrder.setWorkOrderFacilityName(FacilityName.JUICE_WRAPPER_1);
                        //  투입량 계산
                        long wrapProductionAmount = plan.getPlanProductionAmount() * 30;
                        workOrder.setWorkOrderInput(wrapProductionAmount * 80);
                        workOrder.setWorkOrderOutput(wrapProductionAmount);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  충진에 필요한 시간 계산 (시간)
                        long productionTimeInHours = (long) Math.ceil((double) wrapProductionAmount / 1250);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(productionTimeInHours));
                        break;
                    case 5: //  검사
                        workOrder.setWorkOrderProcessName(ProcessCode.A6);
                        workOrder.setWorkOrderFacilityName(FacilityName.DETECTOR);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(workOrderInput - (long) Math.ceil(workOrderInput * 0.03));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  검사에 필요한 시간 계산 (시간)
                        long detectProductionTimeInHours = (long) Math.ceil((double) workOrderInput / 5000);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(detectProductionTimeInHours));
                        break;
                    case 6: //  포장
                        workOrder.setWorkOrderProcessName(ProcessCode.A7);
                        workOrder.setWorkOrderFacilityName(FacilityName.BOX_WRAPPER);
                        //  투입량 계산
                        long boxProductionAmount = plan.getPlanProductionAmount() - (long) Math.ceil(plan.getPlanProductionAmount() * 0.03);
                        workOrder.setWorkOrderInput(boxProductionAmount * 30);
                        workOrder.setWorkOrderOutput(boxProductionAmount);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  포장에 필요한 시간 계산 (시간)
                        long boxProductionTimeInHours = (long) Math.ceil((double) boxProductionAmount / 160);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(boxProductionTimeInHours));
                        plan.setPlanExpectFinishDate(workOrder.getWorkOrderExpectDate());
                        break;
                }
                workOrderInput = workOrder.getWorkOrderOutput();
                planStartDate = workOrder.getWorkOrderExpectDate();
                workOrder.setWorkOrderStatus(WorkOrderStatus.WAITING);

                workOrderRepositoryHwang.save(workOrder);
                workOrderNoList.add(workOrder.getWorkOrderNo());
            }
            planRepository.save(plan);
        } else {    //  젤리스틱 작업지시 생성
            for (int i = 0; i < 6; i++) {
                WorkOrder workOrder = new WorkOrder();
                workOrder.setPlan(plan);
                workOrder.setWorkOrderType(ProductType.JELLY);
                switch (i) {
                    case 0: //  혼합
                        workOrder.setWorkOrderProcessName(ProcessCode.B1);
                        workOrder.setWorkOrderFacilityName(FacilityName.MIXER);
                        workOrder.setWorkOrderInput(workOrderInput * 25 * 15);
                        workOrder.setWorkOrderOutput(workOrderInput * 25 * 15);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(8));
                        break;
                    case 1: //  살균
                        workOrder.setWorkOrderProcessName(ProcessCode.B2);
                        workOrder.setWorkOrderFacilityName(FacilityName.STERILIZER_2);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(workOrderInput);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(2));
                        break;
                    case 2: //  충진
                        workOrder.setWorkOrderProcessName(ProcessCode.B3);
                        workOrder.setWorkOrderFacilityName(FacilityName.JELLY_WRAPPER_1);
                        //  투입량 계산
                        long wrapProductionAmount = plan.getPlanProductionAmount() * 25;
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(wrapProductionAmount);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  충진에 필요한 시간 계산 (시간)
                        long wrapProductionTimeInHours = (long) Math.ceil((double) wrapProductionAmount / 2000);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(wrapProductionTimeInHours));
                        break;
                    case 3: //  냉각
                        workOrder.setWorkOrderProcessName(ProcessCode.B4);
                        workOrder.setWorkOrderFacilityName(FacilityName.NONE);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(workOrderInput);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(8));
                        break;
                    case 4: //  검사
                        workOrder.setWorkOrderProcessName(ProcessCode.B5);
                        workOrder.setWorkOrderFacilityName(FacilityName.DETECTOR);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(workOrderInput - (long) Math.ceil(workOrderInput * 0.03));
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  검사에 필요한 시간 계산 (시간)
                        long detectProductionTimeInHours = (long) Math.ceil((double) workOrderInput / 5000);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(detectProductionTimeInHours));
                        break;
                    case 5: //  포장
                        workOrder.setWorkOrderProcessName(ProcessCode.B6);
                        workOrder.setWorkOrderFacilityName(FacilityName.BOX_WRAPPER);
                        //  투입량 계산
                        long boxProductionAmount = plan.getPlanProductionAmount() - (long) Math.ceil(plan.getPlanProductionAmount() * 0.03);
                        workOrder.setWorkOrderInput(workOrderInput);
                        workOrder.setWorkOrderOutput(boxProductionAmount);
                        workOrder.setWorkOrderStartDate(planStartDate);
                        //  포장에 필요한 시간 계산 (시간)
                        long boxProductionTimeInHours = (long) Math.ceil((double) boxProductionAmount / 160);
                        workOrder.setWorkOrderExpectDate(planStartDate.plusHours(boxProductionTimeInHours));
                        plan.setPlanExpectFinishDate(workOrder.getWorkOrderExpectDate());
                        break;
                }
                workOrderInput = workOrder.getWorkOrderOutput();
                planStartDate = workOrder.getWorkOrderExpectDate();
                workOrder.setWorkOrderStatus(WorkOrderStatus.WAITING);

                workOrderRepositoryHwang.save(workOrder);
                workOrderNoList.add(workOrder.getWorkOrderNo());
            }
            planRepository.save(plan);
        }

        return workOrderNoList;
    }

    //  해당 생산계획ID를 가지는 모든 workOrder 삭제
    public List<Long> deleteWorkOrderWithPlanNo(Long planNo) {
        List<WorkOrder> workOrderList = workOrderRepositoryHwang.findAllByPlanNo(planNo);
        List<Long> deletedWorkOrderNo = new ArrayList<>();
        for (WorkOrder workOrder : workOrderList) {
            deletedWorkOrderNo.add(workOrder.getWorkOrderNo());
            workOrderRepositoryHwang.delete(workOrder);
        }
        return deletedWorkOrderNo;
    }

    //  작업지시 진행중으로 변경
    public List<Long> setWorkOrderStatusProcessing() {
        List<Long> workOrderNoList = new ArrayList<>();
        List<WorkOrder> workOrderWaiting = workOrderRepositoryHwang.findWaitingWorkOrdersWithCurrentTimeInRange();
        for (WorkOrder workOrder : workOrderWaiting) {
            //  만약 전처리 또는 혼합 공정이라면
            if (workOrder.getWorkOrderProcessName() == ProcessCode.A1 | workOrder.getWorkOrderProcessName() == ProcessCode.B1) {
                List<OrderPlanMap> orderPlanMapList = orderPlanMapRepositoryHwang.findListByPlanNo(workOrder.getPlan().getPlanNo());
                for (OrderPlanMap orderPlanMap : orderPlanMapList) {
                    Long orderNo = orderPlanMap.getOrder().getOrderNo();
                    Order foundOrder = orderRepositoryHwang.findById(orderNo)
                            .orElseThrow(EntityNotFoundException::new);
                    if (foundOrder.getOrderStatus() != OrderStatus.SHIPPED) {
                        foundOrder.setOrderStatus(OrderStatus.IN_PRODUCTION);
                        orderRepositoryHwang.save(foundOrder);
                    }
                }
                /*  여기에 원자재 자동 출고 메소드 삽입*/
                materialService.autoOutMaterial(orderPlanMapList.get(0).getOrder().getOrderNo(), workOrder.getPlan().getPlanProductionAmount(), workOrder.getWorkOrderStartDate());
            }
            workOrder.setWorkOrderStatus(WorkOrderStatus.PROCESSING);
            workOrderRepositoryHwang.save(workOrder);
            workOrderNoList.add(workOrder.getWorkOrderNo());
        }
        return workOrderNoList;
    }

    //  작업지시 완료로 변경 & 전처리 및 혼합 작업지시가 바로 완료될 경우 수주 상태 처리
    public List<Long> setWorkOrderStatusComplete() {
        List<Long> workOrderNoList = new ArrayList<>();
        List<WorkOrder> workOrderWaiting = workOrderRepositoryHwang.findProcessingOrWaitingWorkOrdersWithCurrentTimeAfterExpectDate();

        for (WorkOrder workOrder : workOrderWaiting) {
            //  해당 작업지시가 진행중이 아니고 전처리 또는 혼합 공정이라면
            if ((workOrder.getWorkOrderProcessName() == ProcessCode.A1 | workOrder.getWorkOrderProcessName() == ProcessCode.B1) && workOrder.getWorkOrderStatus()!=WorkOrderStatus.PROCESSING) {
                List<OrderPlanMap> orderPlanMapList = orderPlanMapRepositoryHwang.findListByPlanNo(workOrder.getPlan().getPlanNo());
                for (OrderPlanMap orderPlanMap : orderPlanMapList) {
                    Long orderNo = orderPlanMap.getOrder().getOrderNo();
                    Order foundOrder = orderRepositoryHwang.findById(orderNo)
                            .orElseThrow(EntityNotFoundException::new);
                    if (foundOrder.getOrderStatus() != OrderStatus.SHIPPED) {
                        foundOrder.setOrderStatus(OrderStatus.IN_PRODUCTION);
                        orderRepositoryHwang.save(foundOrder);
                    }
                }
                /*  여기에 원자재 자동 출고 메소드 삽입  */
                materialService.autoOutMaterial(orderPlanMapList.get(0).getOrder().getOrderNo(), workOrder.getPlan().getPlanProductionAmount(), workOrder.getWorkOrderStartDate());
            }
            //  해당 작업지시가 포장 공정이라면
            if (workOrder.getWorkOrderProcessName() == ProcessCode.A7 | workOrder.getWorkOrderProcessName() == ProcessCode.B6) {
                List<OrderPlanMap> orderPlanMapList = orderPlanMapRepositoryHwang.findFilteredByPlanNo(workOrder.getPlan().getPlanNo());
                if (orderPlanMapList.size() != 0) {
                    Long producedAmount = workOrder.getWorkOrderOutput();
                    for (int i=0; i<orderPlanMapList.size(); i++){
                        System.out.println("producedAmount의 수: "+producedAmount);
                        OrderPlanMap orderPlanMap = orderPlanMapList.get(i);
                        Order foundOrder = orderPlanMap.getOrder();
                        System.out.println("foundOrder의 번호: "+foundOrder.getOrderNo());
                        //  이미 출고된 수주인지 확인
                        if (foundOrder.getOrderStatus() != OrderStatus.SHIPPED) {
                            foundOrder.setOrderStatus(OrderStatus.SHIPPED);
                            foundOrder.setOrderOutDate(foundOrder.getOrderExpectShipDate());
                            orderRepositoryHwang.save(foundOrder);
                            /*  여기에 완제품 입고 및 출고 메소드 삽입  */
                            if (i!=orderPlanMapList.size()-1){
                                productInOutServiceHwang.productIn(foundOrder, foundOrder.getOrderAmount(), workOrder.getWorkOrderExpectDate());
                                productInOutServiceHwang.productOut(foundOrder, foundOrder.getOrderAmount(), workOrder.getWorkOrderExpectDate());
                                producedAmount -= foundOrder.getOrderAmount();
                            }else {
                                productInOutServiceHwang.productIn(foundOrder, producedAmount, workOrder.getWorkOrderExpectDate());
                                productInOutServiceHwang.productOut(foundOrder, foundOrder.getOrderAmount(), workOrder.getWorkOrderExpectDate());
                            }
                        } else {
                            /*  여기에 완제품 입고 메소드 삽입   */
                            productInOutServiceHwang.productIn(foundOrder, workOrder.getWorkOrderOutput(), workOrder.getWorkOrderFinishDate());

                        }
                    }
                }
                Plan plan = planRepository.findById(workOrder.getPlan().getPlanNo())
                        .orElseThrow(EntityNotFoundException::new);
                plan.setPlanFinishDate(plan.getPlanExpectFinishDate());
                planRepository.save(plan);
            }
            workOrder.setWorkOrderStatus(WorkOrderStatus.COMPLETE);
            workOrder.setWorkOrderFinishDate(workOrder.getWorkOrderExpectDate());
            workOrderRepositoryHwang.save(workOrder);
            workOrderNoList.add(workOrder.getWorkOrderNo());
        }
        return workOrderNoList;
    }

    public List<WorkOrder> getWrapperWorkOrders() {
        return workOrderRepositoryHwang.findWrapperWorkOrders();
    }

    public List<WorkOrder> getFacilityWorkOrders() {
        return workOrderRepositoryHwang.findTop12ByWorkOrderFacilityNameInOrderByWorkOrderNoAsc(
                FacilityName.JUICE_WRAPPER_1,
                FacilityName.JUICE_WRAPPER_2,
                FacilityName.JELLY_WRAPPER_1,
                FacilityName.JELLY_WRAPPER_2,
                FacilityName.EXTRACTOR_1,
                FacilityName.EXTRACTOR_2,
                FacilityName.STERILIZER_1,
                FacilityName.STERILIZER_2,
                FacilityName.MIXER,
                FacilityName.FILTER,
                FacilityName.BOX_WRAPPER,
                FacilityName.DETECTOR
        );
    }

    public List<WorkOrder> getCapaWorkOrders() {
        return workOrderRepositoryHwang.findByWorkOrderFacilityNameInOrderByWorkOrderNoAsc(
                FacilityName.EXTRACTOR_1,
                FacilityName.EXTRACTOR_2,
                FacilityName.STERILIZER_1,
                FacilityName.STERILIZER_2,
                FacilityName.MIXER,
                FacilityName.FILTER
        );
    }
}
