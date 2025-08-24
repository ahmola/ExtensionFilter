const BASE = "http://43.200.6.17:8888/api/v1/forbidden/crud";

// GET
export async function fetchForbiddenList() {
  const res = await fetch(BASE, { method: 'GET' })
  if (!res.ok) throw new Error(`GET failed: ${res.status}`)

  console.log("fetchForbiddenList Response : ", res)
  return res.json() // [{ extensionName }]
}

// POST
export async function createForbidden(ext) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ extensionName: ext })
  })

  if (!res.ok) throw new Error(`POST failed: ${res.status}`)
  
  console.log("createForbidden Resposne : ", res);

  return res.json() // { id, extensionName }
}

// DELETE
export async function deleteForbidden(extensionName) {
  const url = `${BASE}?extensionName=${encodeURIComponent(extensionName)}`
  const res = await fetch(url, { method: 'DELETE' })

  if (!res.ok) throw new Error(`DELETE failed: ${res.status}`)
  
  console.log("deleteForbidden Response : ", res);
  
  return res.json() // boolean
}