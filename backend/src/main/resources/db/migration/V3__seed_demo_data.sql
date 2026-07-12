-- Lưu ý mật khẩu:
-- Password gốc: "Admin@123"     → BCrypt hash bên dưới
-- Password gốc: "Organizer@123" → BCrypt hash bên dưới
-- Password gốc: "Staff@123"     → BCrypt hash bên dưới
-- Password gốc: "Customer@123"  → BCrypt hash bên dưới

INSERT INTO users (email, password_hash, full_name, role_id)
VALUES
    (
        'admin@event.local',
        '$2a$10$JRIlJvhvjtYJENd1ypxY8.oDrMoLAMH0ZBKUkNsc5ChdC0nHo2eFu',
        'Demo Admin',
        (SELECT id FROM roles WHERE name = 'ADMIN')
    ),
    (
        'organizer@event.local',
        '$2a$10$CyOBRINcU5kTBjsKcoZFae3toIGNhARvuGrKloT67oyJeLF8nGGL6',
        'Demo Organizer',
        (SELECT id FROM roles WHERE name = 'ORGANIZER')
    ),
    (
        'staff@event.local',
        '$2a$10$Gjf3peEVHbbaOGijElWAwuaMbohcwI8YaCSgtnh.ud3jxAJq0XBfq',
        'Demo Check-in Staff',
        (SELECT id FROM roles WHERE name = 'CHECKIN_STAFF')
    ),
    (
        'customer@event.local',
        '$2a$10$sM7zWiBcuxrsBl2PaakfVeboW./jLqijmHkopX76NnP2lpOgU0UXS',
        'Demo Customer',
        (SELECT id FROM roles WHERE name = 'CUSTOMER')
    );

INSERT INTO events (
    organizer_id,
    name,
    description,
    banner_url,
    location,
    start_time,
    end_time,
    status
)
VALUES (
    (SELECT id FROM users WHERE email = 'organizer@event.local'),
    'Demo Tech Conference',
    'Demo event for local development and authentication smoke tests.',
    NULL,
    'Hanoi, Vietnam',
    '2026-09-01T09:00:00Z',
    '2026-09-01T18:00:00Z',
    'PUBLISHED'
);

UPDATE users
SET assigned_event_id = (SELECT id FROM events WHERE name = 'Demo Tech Conference')
WHERE email = 'staff@event.local';

INSERT INTO ticket_types (
    event_id,
    name,
    price,
    quantity_total,
    quantity_remaining,
    sales_start_at,
    sales_end_at
)
VALUES
    (
        (SELECT id FROM events WHERE name = 'Demo Tech Conference'),
        'Standard',
        250000,
        100,
        98,
        '2026-07-15T00:00:00Z',
        '2026-08-31T23:59:59Z'
    ),
    (
        (SELECT id FROM events WHERE name = 'Demo Tech Conference'),
        'VIP',
        500000,
        20,
        20,
        '2026-07-15T00:00:00Z',
        '2026-08-31T23:59:59Z'
    );

INSERT INTO gates (event_id, name)
VALUES
    ((SELECT id FROM events WHERE name = 'Demo Tech Conference'), 'Gate A'),
    ((SELECT id FROM events WHERE name = 'Demo Tech Conference'), 'Gate B');

INSERT INTO tickets (
    ticket_type_id,
    customer_id,
    qr_code,
    status,
    idempotency_key,
    reserved_at,
    expires_at,
    confirmed_at,
    checked_in_at
)
VALUES
    (
        (SELECT id FROM ticket_types WHERE name = 'Standard' AND event_id = (SELECT id FROM events WHERE name = 'Demo Tech Conference')),
        (SELECT id FROM users WHERE email = 'customer@event.local'),
        '11111111-1111-4111-8111-111111111111',
        'CONFIRMED',
        'demo-confirmed-ticket',
        '2026-08-20T10:00:00Z',
        '2026-08-20T10:07:00Z',
        '2026-08-20T10:05:00Z',
        NULL
    ),
    (
        (SELECT id FROM ticket_types WHERE name = 'Standard' AND event_id = (SELECT id FROM events WHERE name = 'Demo Tech Conference')),
        (SELECT id FROM users WHERE email = 'customer@event.local'),
        '22222222-2222-4222-8222-222222222222',
        'CHECKED_IN',
        'demo-checked-in-ticket',
        '2026-08-20T10:10:00Z',
        '2026-08-20T10:17:00Z',
        '2026-08-20T10:12:00Z',
        '2026-09-01T09:03:00Z'
    );

INSERT INTO checkin_logs (ticket_id, gate_id, staff_id, result, checked_in_at)
VALUES (
    (SELECT id FROM tickets WHERE qr_code = '22222222-2222-4222-8222-222222222222'),
    (SELECT id FROM gates WHERE name = 'Gate A' AND event_id = (SELECT id FROM events WHERE name = 'Demo Tech Conference')),
    (SELECT id FROM users WHERE email = 'staff@event.local'),
    'SUCCESS',
    '2026-09-01T09:03:00Z'
);
