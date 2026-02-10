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

const ONLINE_IDS = [
    548008, 548009, 548010, 548011, 548012,
    548013, 548014, 548015, 548016, 548017,
    548018, 548019, 548020, 548021, 548022,
    548023, 548024, 548025, 548026, 548027,
    548028, 548029, 548030, 548031, 548032,
    548033, 548034, 548035, 548036, 548037,
    548038, 548039, 548040, 548041, 548042,
    548043, 548044, 548045, 548046, 548047,
    548048, 548049, 548050, 548051, 548052,
    548053, 548054, 548055, 548056, 548057,
];

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

        return res.json('data.accessToken');
    });

    return { tokens };
}

export default function (data) {
    const token = data.tokens[(__VU - 1) % data.tokens.length];
    const consultationId = ONLINE_IDS[(__ITER) % ONLINE_IDS.length];

    const res = http.get(
        `${BASE_URL}/online-consultation/${consultationId}`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    check(res, {
        'online consultation 200': r => r.status === 200,
    });

    sleep(1);
}
