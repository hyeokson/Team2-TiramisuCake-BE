package com.softeer.backend.fo_domain.comment.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 커서 기반 스크롤 기능을 사용할 수 있는 Util 클래스
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScrollPaginationUtil<T> {

    // 마지막 스크롤인지를 확인하기 위해서 size+1의 개수만큼 item을 저장한다.
    private final List<T> itemsWithNextCursor;

    // 한번 스크롤 할 때의 데이터 개수
    private final int countPerScroll;

    public static <T> ScrollPaginationUtil<T> of(List<T> itemsWithNextCursor, int size) {
        return new ScrollPaginationUtil<>(itemsWithNextCursor, size);
    }

    // 마지막 스크롤인지를 확인하는 메서드
    public boolean isLastScroll() {
        return this.itemsWithNextCursor.size() <= countPerScroll;
    }

    // 마지막 스크롤일 경우, 그대로 데이터를 반환한다.
    // 마지막 스크롤이 아닌 경우, 마지막 데이터를 제외하고 반환한다.
    public List<T> getCurrentScrollItems() {
        if (isLastScroll()) {
            return this.itemsWithNextCursor;
        }
        return this.itemsWithNextCursor.subList(0, countPerScroll);
    }

    // 다음 커서 값을 갖고 있는 데이터를 반환하는 메서드
    public T getNextCursor() {
        return itemsWithNextCursor.get(countPerScroll - 1);
    }

}