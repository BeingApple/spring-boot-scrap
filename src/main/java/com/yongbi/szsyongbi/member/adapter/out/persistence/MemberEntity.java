package com.yongbi.szsyongbi.member.adapter.out.persistence;

import com.yongbi.szsyongbi.deduction.adapter.out.persistence.DeductionEntity;
import com.yongbi.szsyongbi.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "MEMBER")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String userId;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String regNo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "memberId")
    @OrderBy("year, month desc")
    private List<DeductionEntity> deductionEntities;

    protected MemberEntity() {}

    public MemberEntity(String userId, String password, String name, String regNo, LocalDateTime createdAt) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
        this.createdAt = createdAt;
    }

    public MemberEntity(Member member) {
        this.id = member.id();
        this.userId = member.userId();
        this.password = member.password();
        this.name = member.name();
        this.regNo = member.regNo();
        this.createdAt = member.createdAt();
    }

    public Member domain() {
        return new Member(this.id, this.userId, this.password, this.name, this.regNo, this.createdAt);
    }
}
