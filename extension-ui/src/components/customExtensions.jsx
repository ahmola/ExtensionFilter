import React, { useCallback, useMemo, useState } from 'react'
import { createForbidden, deleteForbidden } from '../api/forbidden.js'

export default function CustomExtensions({
  items, onItemsChange, normalize, fixedPoolSet, fixedCheckedSet
}) {
  const [input, setInput] = useState('')

  const canAddMore = items.length <= 200 // 200개 이하만가능

  const onAdd = useCallback(async () => {
    let e = normalize(input)
    if (!e) return alert('유효한 확장자를 입력하세요. (최대 20자, a-z0-9_-)')

    if (fixedPoolSet.has(e)) {
      return alert('해당 확장자는 고정 확장자 목록에 있으므로 커스텀에 추가할 수 없습니다.')
    }
    if (fixedCheckedSet.has(e)) {
      return alert('이미 고정 확장자로 차단되었습니다.')
    }
    if (items.includes(e)) {
      return alert('이미 추가된 커스텀 확장자입니다.')
    }
    if (!canAddMore) {
      return alert('커스텀 확장자는 최대 200개까지 가능합니다.')
    }

    const prev = items
    const next = [...items, e]
    onItemsChange(next)

    try {
      await createForbidden(e)
      setInput('')
    } catch (err) {
      onItemsChange(prev)
      alert(`커스텀 확장자 추가 실패: ${err.message || err}`)
    }
  }, [input, items, onItemsChange, normalize, fixedPoolSet, fixedCheckedSet, canAddMore])

  const onRemove = useCallback(async (ext) => {
    const prev = items
    const next = items.filter(x => x !== ext)
    onItemsChange(next)

    try {
      await deleteForbidden(ext)
    } catch (err) {
      onItemsChange(prev)
      alert(`커스텀 확장자 삭제 실패: ${err.message || err}`)
    }
  }, [items, onItemsChange])

  const sorted = useMemo(() => [...items].sort(), [items])

  return (
    <section>
      <h2>커스텀 확장자</h2>
  
      <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="예: txt"
          maxLength={21}
        />
        <button onClick={onAdd} disabled={!canAddMore}>추가</button>
      </div>
  
      {/* 박스 스타일 추가 */}
      <div
        style={{
          border: '1px solid #ccc',
          borderRadius: 8,
          padding: 16,
          maxHeight: 300,
          overflowY: 'auto',
          background: '#fafafa'
        }}
      >
        <ul
          style={{
            listStyle: 'none',
            padding: 0,
            display: 'grid',
            gap: 8,
            gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))'
          }}
        >
          {sorted.map(ext => (
            <li
              key={ext}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 8,
                border: '1px solid #ddd',
                borderRadius: 4,
                padding: '4px 8px',
                background: 'white'
              }}
            >
              <span style={{ flex: 1 }}>{ext}</span>
              <button
                onClick={() => onRemove(ext)}
                aria-label={`${ext} 삭제`}
                style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: 'red' }}
              >
                x
              </button>
            </li>
          ))}
        </ul>
      </div>
    </section>
  )
}