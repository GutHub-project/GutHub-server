package com.guthub.guthubserver.domain.supplement.service;

import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;

public interface SupplementIndexService {

    /** 모든 건기식 데이터를 MySQL에서 ES로 인덱싱 */
    void indexAllSupplements();

    /** 단일 건기식 인덱싱 (추가/수정 시 사용) */
    void indexSupplement(SupplementEntity supplement);

    /** 건기식 삭제 시 인덱스에서도 삭제 */
    void deleteFromIndex(Long supplementId);

    /** 리뷰 통계 필드만 부분 업데이트 */
    void updateRankingFields(SupplementDocument doc);
}
