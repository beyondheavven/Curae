create table email_verification_token (
                                          id bigserial primary key,

                                          user_id bigint not null,
                                          type varchar(25) not null,
                                          status varchar(25) not null,

                                          code integer,
                                          token_hash varchar(64),

                                          expires_at timestamp not null,
                                          created_at timestamp not null,

                                          constraint fk_email_token_user
                                              foreign key (user_id)
                                                  references users (id)
                                                  on delete cascade
);

create index idx_email_token_user_type_status
    on email_verification_token (user_id, type, status);

create index idx_email_token_token_hash
    on email_verification_token (token_hash);
