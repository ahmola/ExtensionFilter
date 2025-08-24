import React, { useEffect, useMemo, useState } from 'react'
import { fetchForbiddenList } from './api/forbidden.js'
import FixedExtensions from './components/FixedExtensions.jsx'
import CustomExtensions from './components/CustomExtensions.jsx'

// 고정 확장자 풀 (기본은 전부 unchecked)
const FIXED_POOL = [
  'bat', 'cmd', 'com', 'cpl', 'exe', 'scr', 'js'
]
const FIXED_POOL_SET = new Set(FIXED_POOL)

function normalize(ext) {
  if (!ext) return ''
  const e = ext.trim().toLowerCase().replace(/^\./, '') // 공백 제거, 모두 소문자로 치환
  if (!e) return ''
  if (e.length > 20) return '' // 2-1. 최대 20자
  if (!/^[a-z0-9_-]+$/.test(e)) return '' // 필요시 규칙 보강
  return e
}

export default function App() {
  const [fixed, setFixed] = useState(
    FIXED_POOL.map((ext) => ({ ext, defaultChecked: false }))
  )
  const [custom, setCustom] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // 새로고침 시에 서버 목록 GET 요청 → fixed/custom 동기화 (1-2, 새로고침 유지)
  useEffect(() => {
    (async () => {
      try {
        setLoading(true)
        const response = await fetchForbiddenList()
        const list = response.extensions || []
        console.log(list)
        const fromServer = new Set(list.map(d => (d.extensionName || '').toLowerCase()))

        // 1) fixed는 서버 목록 포함 여부로 체크 세팅
        const nextFixed = fixed.map(item => ({
          ...item,
          defaultChecked: fromServer.has(item.ext)
        }))

        // 2) custom은 서버 목록 중 fixed 풀에 없는 것만
        const nextCustom = Array.from(fromServer).filter(e => !FIXED_POOL_SET.has(e))

        setFixed(nextFixed)
        setCustom(nextCustom)
        setError(null)
      } catch (e) {
        setError(e?.message || '초기화 실패')
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  // fixed에서 체크되는 순간, 동일 확장자가 custom에 있으면 제거(일관성)
  useEffect(() => {
    const checkedSet = new Set(fixed.filter(f => f.defaultChecked).map(f => f.ext))
    if (custom.some(c => checkedSet.has(c))) {
      setCustom(prev => prev.filter(c => !checkedSet.has(c)))
    }
  }, [fixed]) // custom을 직접 수정하므로 별도 의존 불필요

  const fixedCheckedSet = useMemo(
    () => new Set(fixed.filter(f => f.defaultChecked).map(f => f.ext)),
    [fixed]
  )

  if (loading) return <div style={{ padding: 24 }}>로딩 중…</div>
  if (error) return <div style={{ padding: 24, color: 'crimson' }}>{error}</div>

  return (
    <div style={{ maxWidth: 720, margin: '0 auto', padding: 24 }}>
      <h1>파일 확장자 차단</h1>
      <p style={{ color: '#666' }}>
        고정 확장자는 체크/해제 시 DB에 저장/삭제되며, 새로고침 시 유지됩니다. 커스텀 확장자는 최대 200개까지 추가/삭제할 수 있습니다.
      </p>

      <FixedExtensions
        items={fixed}
        onItemsChange={setFixed}
      />

      <div style={{ height: 24 }} />

      <CustomExtensions
        items={custom}
        onItemsChange={setCustom}
        normalize={normalize}
        fixedPoolSet={FIXED_POOL_SET}
        fixedCheckedSet={fixedCheckedSet}
      />

      <div style={{ marginTop: 12, color: '#888' }}>
        커스텀 개수: {custom.length} / 200
      </div>
    </div>
  )
}