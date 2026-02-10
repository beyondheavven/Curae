import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '2m',
    thresholds: {
        http_req_duration: ['p(95)<800'],
        http_req_failed: ['rate<0.02'],
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

        if (res.status !== 200) {
            throw new Error(`Login failed for ${user.email}`);
        }

        const token = res.json('data.accessToken');
        if (!token) {
            throw new Error(`No token for ${user.email}`);
        }

        return token;
    });

    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];

    const type = Math.random() < 0.5 ? 'UPCOMING' : 'PAST';

    const res = http.get(
        `${BASE_URL}/visits?type=${type}&page=0&size=10`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    check(res, {
        'visits 200': r => r.status === 200,
    });

    sleep(1);
}

