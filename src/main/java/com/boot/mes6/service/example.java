if(해당 수주의 발주일이 12시가 넘는가?){
    찾아올 쿼리의 조건 = 해당 일 12시를 넘거나 다음 일 12시를 넘지 않는 원자재 발주 && 원자재명이 같은 원자재 발주
}else{
    찾아올 쿼리의 조건 = 해당 일 12시를 넘지 않고 이전 일 12시를 넘는 원자재 발주 && 원자재명이 같은 원자재 발주
}

레포지토리에서 조건대로 List<>로 찾아온다.

        for(List의 크기+1 만큼 반복){
            if(List의 첫 번째 요소다){
                총입출고량 변수 = 0;
                총입출량+=List.주문량;
                해당 수주의 발주 접수일 = 발주일.plusDate(2);
                save();
            }else{
                총입출량+=List.주문량;
                if(총입출량>2000){
                    해당 수주의 발주 접수일 = 이전 List.발주 접수일 .plusDate(1);
                    save();
                }else{
                    해당 수주의 발주 접수일 = 이전 List.발주 접수일;
                    save();
                }
            }
        }
