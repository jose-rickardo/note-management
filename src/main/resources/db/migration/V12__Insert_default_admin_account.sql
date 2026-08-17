insert into account (id, email, password, role)
values (
    'a0000000-0000-0000-0000-000000000001',
    'admin@hei.school',
    '$2b$10$7lXJ/pfW50pJOQRhCxH4ae8xyPoO0RiLq9HVM4YtQg94ea56U141C',
    'ADMIN'
)
on conflict do nothing;
