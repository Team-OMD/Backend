package com.onmydesk.backend.setup.domain;

import com.onmydesk.backend.global.BaseEntity;
import com.onmydesk.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Builder
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE setup SET is_deleted = true WHERE setup_id=?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Setup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setup_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", updatable = false)
    private Member member;

    @Column(name = "setup_name", length = 20, nullable = false)
    private String setupName;

    @Column(name = "post_total_price", nullable = false)
    private int postTotalPrice;

    @OneToMany(mappedBy = "setup", cascade = CascadeType.REMOVE)
    private List<SetupProduct> setupProducts;

    public void update(String setupName, int postTotalPrice) {
        this.setupName = setupName;
        this.postTotalPrice = postTotalPrice;
    }
}
