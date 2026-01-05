package com.vention.medfast.email.model;

import com.vention.medfast.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "email_verification_token")
public class EmailVerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "users_id", nullable = false)
	private User user;

	@Column(name = "token", nullable = false, length = 64)
	private String tokenHash;

	@Column(name = "exp_date", nullable = false)
	private Instant expiresAt;

	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "create_date", nullable = false, insertable = false, updatable = false)
	private Instant createdAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 25)
	private EmailTokenType type;

	@Column(name = "code")
	private Integer code;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 15)
	private EmailTokenStatus status;

    @Column(name = "resend_count", nullable = false)
    private int resendCount;

    @Column(name = "blocked_until")
    private Instant blockedUntil;

}
