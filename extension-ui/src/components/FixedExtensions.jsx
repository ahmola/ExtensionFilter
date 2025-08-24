import debounce from 'lodash.debounce'
import React, { useCallback, useRef } from 'react'
import { createForbidden, deleteForbidden } from '../api/forbidden.js'

export default function FixedExtensions({ items, onItemsChange }) {
    // 확장자별 디바운스 함수 캐시
    const debouncersRef = useRef(new Map())

    const getDebouncer = (ext) => {
        if (!debouncersRef.current.has(ext)) {
            debouncersRef.current.set(ext, debounce(async (finalChecked) => {
                const prev = items
                try {
                if (finalChecked) {
                    await createForbidden(ext)   // POST
                } else {
                    console.log(ext)
                    await deleteForbidden(ext)   // DELETE
                }
                } catch (e) {
                    // 실패 시 롤백 UI 처리 등을 여기에 (상태 되돌리기 등)
                    onItemsChange(prev)
                    alert(`고정 확장자 ${finalChecked ? '등록' : '삭제'} 실패: ${e.message || e}`)
                    console.error(e)
                }
            }, 2000)) // 2초 내의 변동은 마지막 값만 전송
        }
        return debouncersRef.current.get(ext)
    }

    const toggle = useCallback(async (ext, checked) => {
        
        const next = items.map(i => i.ext === ext ? { ...i, defaultChecked: checked } : i)
        onItemsChange(next)
        
        // 서버 호출은 디바운서에 위임
        getDebouncer(ext)(checked)
    }, [items, onItemsChange])

    return (
        <section>
        <h2>고정 확장자</h2>
        <ul style={{
            listStyle: 'none', padding: 0,
            display: 'grid', gap: 8, gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))'
        }}>
            {items.map(item => (
            <li key={item.ext} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <input
                type="checkbox"
                checked={item.defaultChecked}
                onChange={(e) => toggle(item.ext, e.target.checked)}
                />
                <label>{item.ext}</label>
            </li>
            ))}
        </ul>
        </section>
    )
}