import http from 'k6/http';
import { sleep, check } from 'k6';
import { randomItem, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

// 설정
export const options = {
  vus: 1, // 다중 사용자 시나리오
  iterations: 1, // 1회만 실행
};

const BASE_URL = 'http://localhost:8080/api/v1/forbidden/crud';
const EXTENSIONS = ['bat', 'cmd', 'com', 'cpl', 'exe', 'scr', 'js'];
let createdList = new Set();

export default function () {
    // POST 요청 150개를 랜덤한 숫자와 고정 확장자의 조합으로 만듦
    while (createdList.size < 150) {
        const ext = randomItem(EXTENSIONS);
        const id = Math.floor(Math.random() * 100000);
        const useRandom = Math.random() < 0.5;

        const name = useRandom ? `${id}` : ext;
        if (createdList.has(name)) continue; // 중복 방지
        createdList.add(name);

        const payload = JSON.stringify({ extensionName: name });
        const res = http.post(BASE_URL, payload, {
            headers: { 'Content-Type': 'application/json' },
        });

        check(res, {
            'POST status is 201': (r) => r.status === 201,
        });

        if (res.status === 200) {
            createdList.push(name);
        }

        sleep(0.01); // 너무 과도한 요청 방지
    }

    // GET 목록 전체 조회
    const getRes = http.get(BASE_URL);
    check(getRes, {
        'GET status is 200': (r) => r.status === 200,
    });

    // DELETE: createdList에서 무작위로 절반을 골라 삭제
    const listArray = Array.from(createdList);
    const deleteCount = Math.floor(listArray.length / 2);

    // 인덱스를 무작위로 셔플
    const shuffled = listArray.sort(() => 0.5 - Math.random());

    // 앞 절반만 추출
    const toDelete = shuffled.slice(0, deleteCount);

    for (const name of toDelete) {
      const deleteUrl = `${BASE_URL}?extensionName=${name}`;
      const delRes = http.del(deleteUrl);

      check(delRes, {
        'DELETE status is 200': (r) => r.status === 200,
      });

      sleep(0.01);
    }
}