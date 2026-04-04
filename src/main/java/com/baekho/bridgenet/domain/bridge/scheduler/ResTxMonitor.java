package com.baekho.bridgenet.domain.bridge.scheduler;

import org.springframework.stereotype.Component;

@Component
public class ResTxMonitor {
    // Worker 1
    // 브릿지 상태가 IN_PROGRESS, 관리자의 승인이 난 요청을 가져온다.
    // 만약 전송 처리 내역(TO, PENDING)이 없다면 자산을 전송하는 컨트랙트 요청을 전송한다. (실패시 다음 워커 작동시 재시도)
    // 결과 DB 저장

    // Worker 2
    // 컨펌 수가 지난 TO, PENDING 트랜잭션을 조회한다.
    // 트랜잭션이 존재한다면 컨펌 처리 후 브릿징 현황값을 COMPLETED로 변경한다.
    // 트랜잭션이 존재하지 않을 경우에 상태를 DROPPED로 변경한다

    // 3번 이상 실패시 FAILED 처리 (관리자 처리 필요)
}
