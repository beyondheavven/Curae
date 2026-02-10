import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    vus: 100,
    duration: '2m',

    thresholds: {
        http_req_duration: ['p(95)<1500'],
        http_req_failed: ['rate<0.05'],
    },
};

const BASE_URL = 'http://localhost:8080/api';

const USERS = [
    { email: 'perf1@mail.com', password: 'test123' },
    { email: 'perf2@mail.com', password: 'test123' },
    { email: 'perf3@mail.com', password: 'test123' },
    { email: 'perf4@mail.com', password: 'test123' },
    { email: 'perf5@mail.com', password: 'test123' },
    { email: 'perf6@mail.com', password: 'test123' },
];

export function setup() {
    const tokens = USERS.map(user => {
        const res = http.post(
            `${BASE_URL}/auth/login`,
            JSON.stringify(user),
            { headers: { 'Content-Type': 'application/json' } }
        );

        check(res, { 'login success': r => r.status === 200 });

        return res.json('data.accessToken');
    });

    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];

    const headers = {
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
    };

    const r = Math.random();

    // Visits (~40%)
    if (r < 0.4) {
        const type = Math.random() < 0.5 ? 'UPCOMING' : 'PAST';
        const res = http.get(
            `${BASE_URL}/visits?type=${type}&page=0&size=10`,
            headers
        );
        check(res, { 'visits 200': r => r.status === 200 });
    }

    // Lab history (~25%)
    else if (r < 0.65) {
        const res = http.get(
            `${BASE_URL}/test/history?page=0&size=10`,
            headers
        );
        check(res, { 'lab history 200': r => r.status === 200 });
    }

    // Online consultations (~15%)
    else if (r < 0.8) {
        const consultationId = randomIntBetween(1, 1000);
        const res = http.get(
            `${BASE_URL}/online-consultations/${consultationId}`,
            headers
        );
        check(res, {
            'online consultation response': r =>
                r.status === 200 || r.status === 404,
        });
    }

    // Referrals (~20%)
    else {
        const res = http.get(
            `${BASE_URL}/prescriptions`,
            headers
        );
        check(res, { 'referrals 200': r => r.status === 200 });
    }

    sleep(1);
}
